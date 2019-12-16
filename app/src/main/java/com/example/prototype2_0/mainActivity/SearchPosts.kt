package com.example.prototype2_0.mainActivity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.prototype2_0.HomeActivity
import com.example.prototype2_0.R
import kotlinx.android.synthetic.main.app_bar_home.*
import kotlinx.android.synthetic.main.society_layout.*
import org.json.JSONArray
import org.json.JSONObject
import java.util.ArrayList

class SearchPosts : Fragment() {

    var query = ""
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.society_layout, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        (activity as HomeActivity).home_page_search_bar.visibility = View.VISIBLE

        query = this.arguments!!.getString("query")!!
        getPostFromSearch(query)
    }

    private fun getPostFromSearch(query: String) {
        val url = "https://library123456.000webhostapp.com/GetPostFromSearch.php"
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
                    val post_id = ArrayList<String>()
                    val post_text = ArrayList<String>()
                    val post_date = ArrayList<String>()
                    val post_image = ArrayList<String>()
                    val user_id = ArrayList<String>()
                    val user_name = ArrayList<String>()
                    val user_image = ArrayList<String>()
                    var status: Any = ""

                    post_id.clear()
                    post_text.clear()
                    post_date.clear()
                    post_image.clear()
                    user_id.clear()
                    user_name.clear()
                    user_image.clear()
                    for (i in 0 until jsonArray.length()) {
                        val jsonInner: JSONObject = jsonArray.getJSONObject(i)
                        post_id.add(jsonInner.get("post_id").toString())
                        post_text.add(jsonInner.get("post_text").toString())
                        post_date.add(jsonInner.get("post_date").toString())
                        post_image.add(jsonInner.get("post_image").toString())
                        user_id.add(jsonInner.get("user_id").toString())
                        user_name.add(jsonInner.get("user_name").toString())
                        user_image.add(jsonInner.get("user_image").toString())
                        status = jsonInner.get("status")
                    }
                    if (status == "ok") {
                        post_list_home_page.layoutManager = LinearLayoutManager(activity)
                        post_list_home_page.adapter = PostAdapter(
                            activity!!,
                            post_id,post_text,post_date,
                            user_id,user_name,user_image
                        )
                        posts_pb.visibility = View.GONE
                    } else {
                        Toast.makeText(activity, "Not Found", Toast.LENGTH_SHORT).show()
                        posts_pb.visibility = View.GONE
                    }

                } catch (e: Exception) {
                    Toast.makeText(activity, e.message, Toast.LENGTH_LONG).show()
//                    posts_pb.visibility=View.GONE
                }
            },
            Response.ErrorListener {
                Toast.makeText(activity, it.message, Toast.LENGTH_LONG).show()
            }
        ) {
            override fun getParams(): Map<String, String> {
                //Creating HashMap
                val params = HashMap<String, String>()
                params["query"] = query
                return params
            }
        }

        postRequest.retryPolicy =
            DefaultRetryPolicy(
                0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
            )
        postRequest.setShouldCache(false)
        queue.add(postRequest)
    }
}