package com.example.prototype2_0

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.example.prototype2_0.ui.main.SectionsPagerAdapter
import com.google.android.material.tabs.TabLayout
import com.facebook.login.LoginResult
import com.facebook.login.LoginManager
import com.facebook.*
import com.facebook.GraphRequest
import android.util.Log
import android.view.View
import android.widget.Toast
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.prototype2_0.data.DatasourceHelper
import kotlinx.android.synthetic.main.login_layout.*
import org.json.JSONArray
import org.json.JSONObject
import java.net.URL


class MainActivity : AppCompatActivity() {

    var callbackManager: CallbackManager? = null

    val datasource = DatasourceHelper(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme_NoActionBar)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val sectionsPagerAdapter =
            SectionsPagerAdapter( this, supportFragmentManager)
        val viewPager: ViewPager = findViewById(R.id.view_pager)
        viewPager.adapter = sectionsPagerAdapter
        val tabs: TabLayout = findViewById(R.id.tabs)
        tabs.setupWithViewPager(viewPager)

        callbackManager = CallbackManager.Factory.create()
        LoginManager.getInstance().registerCallback(callbackManager,
            object : FacebookCallback<LoginResult> {
                override fun onSuccess(loginResult: LoginResult) {
                    println(loginResult)
                    val request = GraphRequest.newMeRequest(
                        loginResult.accessToken
                    ) { `object`, response ->
                        Log.v("LoginActivity", response.toString())

                        // Application code
                        val id = `object`.getString("id")
                        val email = `object`.getString("email")
                        val name = `object`.getString("name")
                        val picture = `object`.getString("picture")
                        val profilePicture =URL("https://graph.facebook.com/$id/picture?width=200&height=200")
//                        if(profilePicture.toString().isNotEmpty()) {
//                            Picasso.get().load(profilePicture.toString()).into(image_view)
//                        }

                        loginWithFacebook(email,name,profilePicture.toString(), id)
                    }
                    val parameters = Bundle()
                    parameters.putString("fields", "id,name,email,picture.type(large)")
                    request.parameters = parameters
                    request.executeAsync()
                }

                override fun onCancel() {
                    println("")
                    // App code
                }

                override fun onError(exception: FacebookException) {
                    println("")
                    // App code
                }
            })
    }

    override fun onStart() {
        super.onStart()
        val sharedPreference = getSharedPreferences(
            "myPrefs",
            Context.MODE_PRIVATE
        )
        val user_id = sharedPreference.getString("user_id", "")
        if (user_id != "") {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            this.finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager?.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun loginWithFacebook(email: String, name: String, profilePicture: String, id:String) {

        val exists = datasource.checkFbUser(email)

        if (!exists) {
            val url = "https://library123456.000webhostapp.com/LoginWithFacebook.php"
            val queue = Volley.newRequestQueue(this)
            val postRequest = object : StringRequest(
                Method.POST, url, Response.Listener<String>
                {
                    // Getting Response from Server
                        response ->
                    try {
                        val strResp = response.toString()
                        val jsonObj = JSONObject(strResp)
                        val jsonArray: JSONArray = jsonObj.getJSONArray("dishs")
                        var status: Any = ""
                        for (i in 0 until jsonArray.length()) {
                            val jsonInner: JSONObject = jsonArray.getJSONObject(i)
                            status = jsonInner.get("status")
                        }
                        if (status == "ok") {
                            val edit = this.getSharedPreferences(
                                "myPrefs",
                                Context.MODE_PRIVATE
                            ).edit()
                            edit.apply()
                            edit.putString("user_id", id)
                            startHomeActivity()
                            login_pb.visibility = View.GONE
                        } else {
                            startHomeActivity()
                            login_pb.visibility = View.GONE
                        }


                    } catch (e: Exception) {
                        Toast.makeText(this, e.message, Toast.LENGTH_LONG).show()
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener {
                    Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
                }
            ) {
                override fun getParams(): Map<String, String> {
                    //Creating HashMap
                    val params = HashMap<String, String>()
                    params["user_name"] = name
                    params["user_email"] = email
                    params["user_image"] = profilePicture
                    return params
                }
            }

            postRequest.retryPolicy =
                DefaultRetryPolicy(
                    0,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                )
            queue.add(postRequest)
            postRequest.setShouldCache(false)
        } else {
            startHomeActivity()
            login_pb.visibility = View.GONE
        }
    }

    private fun startHomeActivity() {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        this.finish()
        login_pb.visibility = View.GONE
    }

}