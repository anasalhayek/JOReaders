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
import kotlinx.android.synthetic.main.borrow_requests.*
import org.json.JSONArray
import org.json.JSONObject
import java.util.ArrayList

class SearchBRequests: Fragment() {

    var query = ""
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.borrow_requests, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        (activity as HomeActivity).home_page_search_bar.visibility = View.VISIBLE

        query = this.arguments!!.getString("query")!!
        getBRequestsFromSearch(query)
    }

    private fun getBRequestsFromSearch(query: String) {
        val url = "https://library123456.000webhostapp.com/GetBRequestsFromSearch.php"
        val queue = Volley.newRequestQueue(activity)
        val postRequest = object : StringRequest(
            Method.POST, url, Response.Listener<String>
            {
                // Getting Response from Server
                    response ->
                try {
                    //book_id,book_image,book_name,book_status,user_name,user_un
                    val strResp = response.toString()
                    val jsonObj = JSONObject(strResp)
                    val jsonArray: JSONArray = jsonObj.getJSONArray("dishs")
                    val borrow_id = ArrayList<String>()
                    val book_id = ArrayList<String>()
                    val book_image = ArrayList<String>()
                    val book_name = ArrayList<String>()
                    val book_status = ArrayList<String>()
                    val user_name = ArrayList<String>()
                    val user_un = ArrayList<String>()
                    var status: Any = ""
                    book_id.clear()
                    book_image.clear()
                    book_name.clear()
                    for (i in 0 until jsonArray.length()) {
                        val jsonInner: JSONObject = jsonArray.getJSONObject(i)
                        borrow_id.add(jsonInner.get("borrow_id").toString())
                        book_id.add(jsonInner.get("book_id").toString())
                        book_image.add(jsonInner.get("book_image").toString())
                        book_name.add(jsonInner.get("book_name").toString())
                        book_status.add(jsonInner.get("book_status").toString())
                        user_name.add(jsonInner.get("user_name").toString())
                        user_un.add(jsonInner.get("user_un").toString())
                        status = jsonInner.get("status")
                    }
                    if (status == "ok") {
                        try {
                            borrow_requests_recycler_view.layoutManager =
                                LinearLayoutManager(activity)
                            borrow_requests_recycler_view.adapter =
                                BooksBorrowAdapter(
                                    activity!!,
                                    book_id,
                                    borrow_id,
                                    book_image,
                                    book_name,
                                    user_name,
                                    user_un,
                                    book_status
                                )

                            borrow_requests_PB.visibility = View.GONE
                        } catch (e: Exception) {
                            Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
                            borrow_requests_PB.visibility = View.GONE
                        }

                    } else {
                        borrow_requests_PB.visibility = View.GONE
                        Toast.makeText(context, "Not Found", Toast.LENGTH_LONG).show()
                    }

                } catch (e: Exception) {
                    Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
                    borrow_requests_PB.visibility = View.GONE
                }
            },
            Response.ErrorListener {
                Toast.makeText(context, it.message, Toast.LENGTH_LONG).show()
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