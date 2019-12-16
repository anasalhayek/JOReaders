package com.example.prototype2_0.enter

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.prototype2_0.HomeActivity
import com.example.prototype2_0.MainActivity
import com.facebook.*
import com.facebook.login.LoginResult
import kotlinx.android.synthetic.main.login_layout.*
import org.json.JSONArray
import org.json.JSONObject
import java.util.*
import kotlin.collections.HashMap


class LoginPage : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(com.example.prototype2_0.R.layout.login_layout, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        login_pb.visibility = View.GONE

        login_btn.setOnClickListener {
            login_pb.visibility = View.VISIBLE
            user_password_login_page.hideKeyboard()
            user_email_login_page.hideKeyboard()
            loginRequest()
        }
        fb_login_button.setOnClickListener {
            login_pb.visibility = View.VISIBLE
        }
    }
    private fun loginRequest() {

        val url = "https://library123456.000webhostapp.com/LoginPage.php"
        val queue = Volley.newRequestQueue(activity)
        val postRequest = object : StringRequest(
            Method.POST, url, Response.Listener<String>
            {
                // Getting Response from Server
                    response ->
                try {

                    val strResp = response.toString()
                    val jsonObj = JSONObject(strResp)
                    val jsonArray: JSONArray = jsonObj.getJSONArray("dishs")
                    var user_id = ""
                    var user_email = ""
                    var user_pass = ""
                    var user_name = ""
                    var user_image = ""
                    var user_un = ""
                    var user_role = ""

                    var status: Any = ""
                    for (i in 0 until jsonArray.length()) {
                        val jsonInner: JSONObject = jsonArray.getJSONObject(i)
                        user_id = jsonInner.get("user_id").toString()
                        user_email = jsonInner.get("user_email").toString()
                        user_pass = jsonInner.get("user_pass").toString()
                        user_name = jsonInner.get("user_name").toString()
                        user_image = jsonInner.get("user_image").toString()
                        user_un = jsonInner.get("user_un").toString()
                        user_role = jsonInner.get("user_role").toString()

                        status = jsonInner.get("status")
                    }
                    if (status == "ok") {

                        val sharedPreference = activity!!.getSharedPreferences(
                            "myPrefs",
                            Context.MODE_PRIVATE
                        )
                        val editor = sharedPreference.edit()
                        editor.putString("user_id", user_id)
                        editor.putString("user_email", user_email)
                        editor.putString("user_pass", user_pass)
                        editor.putString("user_name", user_name)
                        editor.putString("user_image", user_image)
                        editor.putString("user_un", user_un)
                        editor.putString("user_role", user_role)
                        editor.clear()
                        editor.apply()

                        val intent = Intent(activity, HomeActivity::class.java)
                        startActivity(intent)
                        activity!!.finish()
                        login_pb.visibility = View.GONE

//                Toast.makeText(activity, "ok", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(activity, "Error", Toast.LENGTH_SHORT).show()
                        login_pb.visibility = View.GONE

                    }

                } catch (e: Exception) {
                    Toast.makeText(activity, e.message, Toast.LENGTH_LONG).show()
                    login_pb.visibility = View.GONE

                }
            },
            Response.ErrorListener {
                Toast.makeText(activity, it.message, Toast.LENGTH_LONG).show()
                login_pb.visibility = View.GONE
            }
        ) {
            override fun getParams(): Map<String, String> {
                //Creating HashMap
                val params = HashMap<String, String>()
                params["user_email"] = user_email_login_page.text.toString()
                params["user_pass"] = user_password_login_page.text.toString()
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
    }
    fun View.hideKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }

}