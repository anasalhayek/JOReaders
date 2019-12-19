package com.example.prototype2_0.mainActivity

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.prototype2_0.HomeActivity
import com.example.prototype2_0.R
import kotlinx.android.synthetic.main.add_post_layout.*
import kotlinx.android.synthetic.main.app_bar_home.*
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class AddPostPage : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.add_post_layout, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        (activity as HomeActivity).home_page_search_bar.visibility = View.GONE

        add_post_pb.visibility = View.GONE

        add_post_btn.setOnClickListener {
            add_post_pb.visibility = View.VISIBLE
            if (post_text_add_new_post_page.text.toString().trim().isNotEmpty()) {
                addPost()
                post_text_add_new_post_page.hideKeyboard()

//                activity!!.onBackPressed()
//                val fragmentManager = activity!!.supportFragmentManager
//                fragmentManager.beginTransaction().replace(R.id.screen_area, PostsPage()).addToBackStack("ok").commit()
            } else {
                Toast.makeText(activity, "Empty Post", Toast.LENGTH_LONG).show()
                post_text_add_new_post_page.hideKeyboard()
                add_post_pb.visibility = View.GONE
            }
        }
    }

    private fun addPost() {
        val url = "https://library123456.000webhostapp.com/AddPost.php"
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
                    var status: Any = ""
                    for (i in 0 until jsonArray.length()) {
                        val jsonInner: JSONObject = jsonArray.getJSONObject(i)
                        status = jsonInner.get("status")
                    }
                    if (status == "ok") {
                        try {
                            Toast.makeText(activity, "Post Added", Toast.LENGTH_LONG).show()
                            post_text_add_new_post_page.text.clear()
                            add_post_pb.visibility = View.GONE
                        }catch (e:Exception){
                            Toast.makeText(activity, e.message, Toast.LENGTH_LONG).show()

                        }
                        } else {
                            Toast.makeText(activity, "Error", Toast.LENGTH_SHORT).show()
                            add_post_pb.visibility = View.GONE
                        }

                    } catch (e: Exception) {
                        Toast.makeText(activity, e.message, Toast.LENGTH_LONG).show()
//                        add_post_pb.visibility = View.GONE
                    }
                },
                Response.ErrorListener {
                    Toast.makeText(activity, it.message, Toast.LENGTH_LONG).show()
//                    add_post_pb.visibility = View.GONE
                }
                ) {

                val sharedPreference = activity!!.getSharedPreferences(
                    "myPrefs",
                    Context.MODE_PRIVATE
                )
                val userId = sharedPreference.getString("user_id", "")

                val sdf = SimpleDateFormat("dd/MM/yyyy")
                val currentDate = sdf.format(Date())
                override fun getParams(): Map<String, String> {
                    //Creating HashMap
                    val params = HashMap<String, String>()
                    params["post_text"] = post_text_add_new_post_page.text.toString()
                    params["post_date"] = currentDate
                    params["post_image"] = ""
                    params["post_user"] = userId.toString()
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
                val imm =
                    context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(windowToken, 0)
            }
    }