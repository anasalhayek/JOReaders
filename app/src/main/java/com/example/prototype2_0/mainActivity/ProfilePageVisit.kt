package com.example.prototype2_0.mainActivity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.prototype2_0.HomeActivity
import com.example.prototype2_0.R
import com.example.prototype2_0.Utitlities
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.app_bar_home.*
import kotlinx.android.synthetic.main.profile_layout.*
import org.json.JSONArray
import org.json.JSONObject
import java.util.*
import kotlin.collections.HashMap
import kotlin.collections.Map
import kotlin.collections.set

class ProfilePageVisit : Fragment() {

    var user_id = ""
    var user_name = ""
    var user_image = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.profile_layout, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (activity as HomeActivity).home_page_search_bar.visibility = View.GONE

        edit_profile_text.isVisible=false

        user_id = this.arguments!!.getString("user_id")!!
        user_name = this.arguments!!.getString("user_name")!!
        user_image = this.arguments!!.getString("user_image")!!

//        val sharedPreference = activity!!.getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
//        val user_name = sharedPreference.getString("user_name", "")
//        val user_image = sharedPreference.getString("user_image", "")
        if (user_image!="null")
        Picasso.get().load("https://library123456.000webhostapp.com/images/${user_image}").into(profile_photo)
        user_name_profile.text=user_name

        getCurrentlyReading(user_id)
        getWantToRead(user_id)
        getDoneReading(user_id)

    }

    private fun getCurrentlyReading(user_id: String) {
        val utitlities = Utitlities()
        val url = utitlities.base_url + "GetCurrentlyReading.php"
        val queue = Volley.newRequestQueue(context)
        val postRequest = object : StringRequest(
            Method.POST, url, Response.Listener<String>
            {
                // Getting Response from Server
                    response ->
                try {
                    val strResp = response.toString()
                    val jsonObj = JSONObject(strResp)
                    val jsonArray: JSONArray = jsonObj.getJSONArray("dishs")
                    val bookId = ArrayList<String>()
                    val bookImage = ArrayList<String>()
                    val bookName = ArrayList<String>()
                    val book_category_id = ArrayList<String>()
                    val empty = ArrayList<String>()
                    var status: Any = ""
                    bookId.clear()
                    bookImage.clear()
                    bookName.clear()
                    book_category_id.clear()
                    for (i in 0 until jsonArray.length()) {
                        val jsonInner: JSONObject = jsonArray.getJSONObject(i)
                        bookId.add(jsonInner.get("book_id").toString())
                        bookImage.add(jsonInner.get("book_image").toString())
                        bookName.add(jsonInner.get("book_name").toString())
                        book_category_id.add(jsonInner.get("book_category_id").toString())
                        status = jsonInner.get("status")
                    }
                    if (status == "ok") {
                        try {
                            currently_reading_view.layoutManager = LinearLayoutManager(context)
                            currently_reading_view.layoutManager =
                                LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                            currently_reading_view.adapter =
                                ShelvesAdapter(activity!!, bookId, bookImage, bookName,empty,book_category_id,"")
                            shelves_pb.visibility=View.GONE
                            no_books_cr.visibility=View.GONE
                        } catch (e: Exception) {
                            Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
                            shelves_pb.visibility=View.GONE
                        }
                    } else {
                        shelves_pb.visibility=View.GONE
                        no_books_cr.visibility=View.VISIBLE
                    }
                } catch (e: Exception) {
                    Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
                    shelves_pb.visibility=View.GONE
                    no_books_cr.visibility=View.GONE
                }
            },
            Response.ErrorListener {
                Toast.makeText(context, it.message, Toast.LENGTH_LONG).show()
            }
        ) {
            override fun getParams(): Map<String, String> {
                //Creating HashMap
                val params = HashMap<String, String>()
                params["user_id"] = user_id
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
    private fun getWantToRead(user_id: String) {
        val utitlities = Utitlities()
        val url = utitlities.base_url + "GetWantToRead.php"
        val queue = Volley.newRequestQueue(context)
        val postRequest = object : StringRequest(
            Method.POST, url, Response.Listener<String>
            {
                // Getting Response from Server
                    response ->
                try {
                    val strResp = response.toString()
                    val jsonObj = JSONObject(strResp)
                    val jsonArray: JSONArray = jsonObj.getJSONArray("dishs")
                    val bookId = ArrayList<String>()
                    val bookImage = ArrayList<String>()
                    val bookName = ArrayList<String>()
                    val book_category_id = ArrayList<String>()
                    val empty = ArrayList<String>()
                    var status: Any = ""
                    bookId.clear()
                    bookImage.clear()
                    bookName.clear()
                    book_category_id.clear()
                    for (i in 0 until jsonArray.length()) {
                        val jsonInner: JSONObject = jsonArray.getJSONObject(i)
                        bookId.add(jsonInner.get("book_id").toString())
                        bookImage.add(jsonInner.get("book_image").toString())
                        bookName.add(jsonInner.get("book_name").toString())
                        book_category_id.add(jsonInner.get("book_category_id").toString())
                        status = jsonInner.get("status")
                    }
                    if (status == "ok") {
                        try {
                            want_to_read_view.layoutManager = LinearLayoutManager(context)
                            want_to_read_view.layoutManager =
                                LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                            want_to_read_view.adapter =
                                ShelvesAdapter(activity!!, bookId, bookImage, bookName,empty,book_category_id,"")
                            shelves_pb.visibility=View.GONE
                            no_books_wr.visibility=View.GONE
                        } catch (e: Exception) {
                            Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
                            shelves_pb.visibility=View.GONE
                        }
                    } else {
                        shelves_pb.visibility=View.GONE
                        no_books_wr.visibility=View.VISIBLE
                    }
                } catch (e: Exception) {
                    Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
                    shelves_pb.visibility=View.GONE
                    no_books_wr.visibility=View.GONE
                }
            },
            Response.ErrorListener {
                Toast.makeText(context, it.message, Toast.LENGTH_LONG).show()
            }
        ) {

            override fun getParams(): Map<String, String> {
                //Creating HashMap
                val params = HashMap<String, String>()
                params["user_id"] = user_id
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
    private fun getDoneReading(user_id: String) {
        val utitlities = Utitlities()
        val url = utitlities.base_url + "GetDoneReading.php"
        val queue = Volley.newRequestQueue(context)
        val postRequest = object : StringRequest(
            Method.POST, url, Response.Listener<String>
            {
                // Getting Response from Server
                    response ->
                try {
                    val strResp = response.toString()
                    val jsonObj = JSONObject(strResp)
                    val jsonArray: JSONArray = jsonObj.getJSONArray("dishs")
                    val bookId = ArrayList<String>()
                    val bookImage = ArrayList<String>()
                    val bookName = ArrayList<String>()
                    val book_category_id = ArrayList<String>()
                    val empty = ArrayList<String>()
                    var status: Any = ""
                    bookId.clear()
                    bookImage.clear()
                    bookName.clear()
                    book_category_id.clear()
                    for (i in 0 until jsonArray.length()) {
                        val jsonInner: JSONObject = jsonArray.getJSONObject(i)
                        bookId.add(jsonInner.get("book_id").toString())
                        bookImage.add(jsonInner.get("book_image").toString())
                        bookName.add(jsonInner.get("book_name").toString())
                        book_category_id.add(jsonInner.get("book_category_id").toString())
                        status = jsonInner.get("status")
                    }
                    if (status == "ok") {
                        try {
                            done_reading_view.layoutManager = LinearLayoutManager(context)
                            done_reading_view.layoutManager =
                                LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                            done_reading_view.adapter =
                                ShelvesAdapter(activity!!, bookId, bookImage, bookName,empty,book_category_id,"")
                            shelves_pb.visibility=View.GONE
                            no_books_dr.visibility=View.GONE
                        } catch (e: Exception) {
                            Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
                            shelves_pb.visibility=View.GONE
                        }
                    } else {
                        shelves_pb.visibility=View.GONE
                        no_books_dr.visibility=View.VISIBLE
                    }
                } catch (e: Exception) {
                    Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
                    shelves_pb.visibility=View.GONE
                    no_books_dr.visibility=View.GONE
                }
            },
            Response.ErrorListener {
                Toast.makeText(context, it.message, Toast.LENGTH_LONG).show()
            }
        ) {
//            val sharedPreference = activity!!.getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
//            val userId = sharedPreference.getString("user_id", "").toString()

            override fun getParams(): Map<String, String> {
                //Creating HashMap
                val params = HashMap<String, String>()
                params["user_id"] = user_id
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