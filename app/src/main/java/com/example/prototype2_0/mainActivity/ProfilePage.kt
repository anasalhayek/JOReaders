package com.example.prototype2_0.mainActivity

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.prototype2_0.HomeActivity
import com.example.prototype2_0.R
import com.example.prototype2_0.Utitlities
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.app_bar_home.*
import kotlinx.android.synthetic.main.book_look.*
import kotlinx.android.synthetic.main.profile_layout.*
import org.json.JSONArray
import org.json.JSONObject
import java.util.ArrayList

class ProfilePage : Fragment() {

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

        edit_profile_text.setOnClickListener {
            val fragmentManager = activity!!.supportFragmentManager
            fragmentManager.beginTransaction().replace(R.id.screen_area, EditProfile())
                .addToBackStack("tag")
                .commit()
        }

        val sharedPreference = activity!!.getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        val user_name = sharedPreference.getString("user_name", "")
        val user_image = sharedPreference.getString("user_image", "")

        if (user_image!="null")
            Picasso.get().load("https://library123456.000webhostapp.com/images/$user_image").into(profile_photo)
        val userName: TextView = user_name_profile.findViewById(R.id.user_name_profile)
        userName.text = user_name

        getCurrentlyReading()
        getWantToRead()
        getDoneReading()
        getBorrowedRequests()
    }

    private fun getCurrentlyReading() {
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
                                ShelvesAdapter(
                                    activity!!,
                                    bookId,
                                    bookImage,
                                    bookName,
                                    empty,
                                    book_category_id
                                )
                            shelves_pb.visibility = View.GONE
                            no_books_cr.visibility = View.GONE
                        } catch (e: Exception) {
                            Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
                            shelves_pb.visibility = View.GONE
                        }
                    } else {
                        shelves_pb.visibility = View.GONE
                        no_books_cr.visibility = View.VISIBLE
                    }
                } catch (e: Exception) {
                    Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
                    shelves_pb.visibility = View.GONE
                    no_books_cr.visibility = View.GONE
                }
            },
            Response.ErrorListener {
                Toast.makeText(context, it.message, Toast.LENGTH_LONG).show()
            }
        ) {
            val sharedPreference = activity!!.getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
            val userId = sharedPreference.getString("user_id", "").toString()

            override fun getParams(): Map<String, String> {
                //Creating HashMap
                val params = HashMap<String, String>()
                params["user_id"] = userId
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

    private fun getWantToRead() {
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
                                ShelvesAdapter(
                                    activity!!,
                                    bookId,
                                    bookImage,
                                    bookName,
                                    empty,
                                    book_category_id
                                )
                            shelves_pb.visibility = View.GONE
                            no_books_wr.visibility = View.GONE
                        } catch (e: Exception) {
                            Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
                            shelves_pb.visibility = View.GONE
                        }
                    } else {
                        shelves_pb.visibility = View.GONE
                        no_books_wr.visibility = View.VISIBLE
                    }
                } catch (e: Exception) {
                    Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
                    shelves_pb.visibility = View.GONE
                    no_books_wr.visibility = View.GONE
                }
            },
            Response.ErrorListener {
                Toast.makeText(context, it.message, Toast.LENGTH_LONG).show()
            }
        ) {
            val sharedPreference = activity!!.getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
            val userId = sharedPreference.getString("user_id", "").toString()

            override fun getParams(): Map<String, String> {
                //Creating HashMap
                val params = HashMap<String, String>()
                params["user_id"] = userId
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

    private fun getDoneReading() {
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
                                ShelvesAdapter(
                                    activity!!,
                                    bookId,
                                    bookImage,
                                    bookName,
                                    empty,
                                    book_category_id
                                )
                            shelves_pb.visibility = View.GONE
                            no_books_dr.visibility = View.GONE
                        } catch (e: Exception) {
                            Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
                            shelves_pb.visibility = View.GONE
                        }
                    } else {
                        shelves_pb.visibility = View.GONE
                        no_books_dr.visibility = View.VISIBLE
                    }
                } catch (e: Exception) {
                    Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
                    shelves_pb?.visibility = View.GONE
                    no_books_dr?.visibility = View.GONE
                }
            },
            Response.ErrorListener {
                Toast.makeText(context, it.message, Toast.LENGTH_LONG).show()
            }
        ) {
            val sharedPreference = activity!!.getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
            val userId = sharedPreference.getString("user_id", "").toString()

            override fun getParams(): Map<String, String> {
                //Creating HashMap
                val params = HashMap<String, String>()
                params["user_id"] = userId
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

    private fun getBorrowedRequests() {
        val utitlities = Utitlities()
        val url = utitlities.base_url + "GetUserBorrowed.php"
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
                    val borrowDate = ArrayList<String>()
                    val book_category_id = ArrayList<String>()
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
                        borrowDate.add(jsonInner.get("borrow_date").toString())
                        book_category_id.add(jsonInner.get("book_category_id").toString())
                        status = jsonInner.get("status")
                    }
                    if (status == "ok") {
                        try {
                            borrowed_requests_view.layoutManager = LinearLayoutManager(context)
                            borrowed_requests_view.layoutManager =
                                LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                            borrowed_requests_view.adapter = ShelvesBAdapter(
                                activity!!,
                                bookId,
                                bookImage,
                                bookName,
                                borrowDate,
                                book_category_id
                            )
                            shelves_pb.visibility = View.GONE
                            no_books_br.visibility = View.GONE
                        } catch (e: Exception) {
                            Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
                            shelves_pb.visibility = View.GONE
                        }
                    } else {
                        shelves_pb.visibility = View.GONE
                        no_books_br.visibility = View.VISIBLE
                    }
                } catch (e: Exception) {
                    Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
                    shelves_pb.visibility = View.GONE
                    no_books_br.visibility = View.GONE
                }
            },
            Response.ErrorListener {
                Toast.makeText(context, it.message, Toast.LENGTH_LONG).show()
            }
        ) {
            val sharedPreference = activity!!.getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
            val userId = sharedPreference.getString("user_id", "").toString()

            override fun getParams(): Map<String, String> {
                //Creating HashMap
                val params = HashMap<String, String>()
                params["user_id"] = userId
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

class ShelvesBAdapter(
    val context: Context,
    private val bookId: ArrayList<String>,
    private val bookImg: ArrayList<String>,
    private val bookName: ArrayList<String>,
    private val borrowDate: ArrayList<String>,
    private val book_category_id: ArrayList<String>

) : RecyclerView.Adapter<ViewHolder>() {
    val utitlities = Utitlities()
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.book_look, p0, false))
    }

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {


        try {
            if (borrowDate[p1] != "") {
                p0.book_date_book_look.visibility = View.VISIBLE
                p0.book_date_book_look.text = borrowDate[p1]
            }
        } catch (e: Exception) {
//            Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
        }
        p0.book_name_book_look.text = bookName[p1]
//        if(bookImg[p1]!=="null")
        Picasso.get().load(utitlities.book_image_url + bookImg[p1]).into(p0.book_image_book_look)
        p0.book_layout.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("bookId", bookId[p1])
            bundle.putString("categoryId", book_category_id[p1])

            val nextFragment = BookPage()
            nextFragment.arguments = bundle

            val fragmentManager = (context as AppCompatActivity).supportFragmentManager
            fragmentManager.beginTransaction().replace(R.id.screen_area, nextFragment)
                .addToBackStack("tag").commit()
        }
    }

    override fun getItemCount(): Int {
        return bookId.size
    }
}


class ShelvesAdapter(
    val context: Context,
    private val bookId: ArrayList<String>,
    private val bookImg: ArrayList<String>,
    private val bookName: ArrayList<String>,
    private val borrowDate: ArrayList<String>,
    private val book_category_id: ArrayList<String>

) : RecyclerView.Adapter<ViewHolder>() {
    val utitlities = Utitlities()
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.book_look, p0, false))
    }

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        p0.more_shelves_book.visibility=View.VISIBLE
        p0.more_shelves_book.setOnClickListener {
            val popupMenu = PopupMenu(context, p0.more_shelves_book)
            popupMenu.menuInflater.inflate(R.menu.delete_from_shelve, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.delete_shelve_book ->{

                    }
                }
                true
            }
            popupMenu.show()
        }
        try {
            if (borrowDate[p1] != "") {
                p0.book_date_book_look.visibility = View.VISIBLE
                p0.book_date_book_look.text = borrowDate[p1]
            }
        } catch (e: Exception) {
//            Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
        }
        p0.book_name_book_look.text = bookName[p1]
//        if(bookImg[p1]!=="null")
        Picasso.get().load(utitlities.book_image_url + bookImg[p1]).into(p0.book_image_book_look)
        p0.book_layout.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("bookId", bookId[p1])
            bundle.putString("categoryId", book_category_id[p1])

            val nextFragment = BookPage()
            nextFragment.arguments = bundle

            val fragmentManager = (context as AppCompatActivity).supportFragmentManager
            fragmentManager.beginTransaction().replace(R.id.screen_area, nextFragment)
                .addToBackStack("tag").commit()
        }
    }

    override fun getItemCount(): Int {
        return bookId.size
    }
}