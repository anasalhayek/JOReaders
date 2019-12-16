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
import kotlinx.android.synthetic.main.book_look.*
import kotlinx.android.synthetic.main.books_catalogue2.*
import kotlinx.android.synthetic.main.society_layout.*
import org.json.JSONArray
import org.json.JSONObject
import java.util.ArrayList

class SearchBooks : Fragment() {

    var query = ""
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.books_catalogue2, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        (activity as HomeActivity).home_page_search_bar.visibility = View.VISIBLE

        query = this.arguments!!.getString("query")!!
        getBooksFromSearch(query)
    }

    private fun getBooksFromSearch(query: String) {
        val url = "https://library123456.000webhostapp.com/GetBooksFromSearch.php"
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
                    val book_id = ArrayList<String>()
                    val book_image = ArrayList<String>()
                    val book_name = ArrayList<String>()
                    val book_category_id = ArrayList<String>()
                    var status: Any = ""

                    book_id.clear()
                    book_image.clear()
                    book_name.clear()
                    for (i in 0 until jsonArray.length()) {
                        val jsonInner: JSONObject = jsonArray.getJSONObject(i)
                        book_id.add(jsonInner.get("book_id").toString())
                        book_image.add(jsonInner.get("book_image").toString())
                        book_name.add(jsonInner.get("book_name").toString())
                        book_category_id.add(jsonInner.get("book_category_id").toString())
                        status = jsonInner.get("status")
                    }
                    if (status == "ok") {
                        cataloge_list.layoutManager = LinearLayoutManager(activity)
                        cataloge_list.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                        cataloge_list.adapter = BooksCatalogAdapter(activity!!, book_id, book_image, book_name,book_category_id)
                        catalog_PB.visibility = View.GONE
                    } else {
                        Toast.makeText(activity, "Not Found", Toast.LENGTH_SHORT).show()
                        catalog_PB.visibility = View.GONE
                    }

                } catch (e: Exception) {
                    Toast.makeText(activity, e.message, Toast.LENGTH_LONG).show()
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