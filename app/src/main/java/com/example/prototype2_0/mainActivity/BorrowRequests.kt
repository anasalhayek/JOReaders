package com.example.prototype2_0.mainActivity

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.DefaultRetryPolicy
import com.android.volley.DefaultRetryPolicy.DEFAULT_MAX_RETRIES
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.prototype2_0.HomeActivity
import com.example.prototype2_0.R
import com.example.prototype2_0.Utitlities
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.app_bar_home.*
import kotlinx.android.synthetic.main.borrow_requests.*
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class BorrowRequests : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.borrow_requests, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        activity!!.home_page_search_bar.visibility = View.VISIBLE
        (activity as HomeActivity).home_page_search_bar.setQuery("", false)
        (activity as HomeActivity).home_page_search_bar.clearFocus()
        (activity as HomeActivity).home_page_search_bar.setOnQueryTextListener(object :
            SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String): Boolean {
                return false
            }
            override fun onQueryTextSubmit(query: String): Boolean {
                // task HERE
                val bundle = Bundle()
                bundle.putString("query", query)

                val nextFragment = SearchBRequests()
                nextFragment.arguments = bundle

                val fragmentManager = activity!!.supportFragmentManager
                fragmentManager.beginTransaction().replace(R.id.screen_area, nextFragment)
                    .addToBackStack("tag").commit()
                return false
            }
        })

        getBorrowedBooks()
        borrow_requests_swipe_refresh.setOnRefreshListener {
            getBorrowedBooks()
            borrow_requests_swipe_refresh.isRefreshing = false
        }
    }

    private fun getBorrowedBooks() {
        val utitlities = Utitlities()
        val url = utitlities.base_url + "GetBorrowed.php"
        val queue = Volley.newRequestQueue(context)
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
//                params["cat_id"] = categoryIdV
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

class BooksBorrowAdapter(
    val context: Context,
    private val book_id: ArrayList<String>,
    private val borrowId: ArrayList<String>,
    private val bookImg: ArrayList<String>,
    private val bookName: ArrayList<String>,
    private val userName: ArrayList<String>,
    private val userUn: ArrayList<String>,
    private val bookStatus: ArrayList<String>
) : RecyclerView.Adapter<ViewHolder>() {
    val utilities = Utitlities()

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.borrow_look, p0, false))
    }

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
//        if(bookImg[p1]!=="null")
        Picasso.get().load(utilities.book_image_url + bookImg[p1]).into(p0.borrow_book_book_image)

        p0.borrow_book_book_name.text = bookName[p1]
        p0.borrow_book_user_name.text = userName[p1]
        p0.borrow_book_user_un.text = userUn[p1]
        p0.borrow_book_more.setOnClickListener {
            val popupMenu = PopupMenu(context, p0.borrow_book_more)
            popupMenu.menuInflater.inflate(R.menu.approve_request, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.approve_item -> {
                        Toast.makeText(context, "تحديد تاريخ الإرجاع", Toast.LENGTH_LONG).show()

                        val c = Calendar.getInstance()
                        val year = c.get(Calendar.YEAR)
                        val month = c.get(Calendar.MONTH)
                        val day = c.get(Calendar.DAY_OF_MONTH)

                        val dpd = DatePickerDialog(
                            context,
                            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                                // Display Selected date in Toast
                                val date = "$dayOfMonth/${monthOfYear + 1}/$year"
                                acceptRequest(book_id[p1], borrowId[p1], date, bookStatus[p1],p0.borrow_book_card_view)
                            }, year, month, day
                        )
                        dpd.datePicker.minDate= System.currentTimeMillis() -1000
                        dpd.show()
                    }
                    R.id.decline_item -> {
                        deleteRequest(borrowId[p1],p0.borrow_book_card_view)
                    }
                }
                true
            }
            popupMenu.show()
        }
    }

    override fun getItemCount(): Int {
        return bookName.size
    }

    private fun acceptRequest(bookId: String,borrowId: String,borrowDate: String,bookStatus: String,borrow_book_card_view:CardView) {
        val url = "https://library123456.000webhostapp.com/AcceptBorrow.php"
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
                    var status: Any = ""
                    for (i in 0 until jsonArray.length()) {
                        val jsonInner: JSONObject = jsonArray.getJSONObject(i)
                        status = jsonInner.get("status")
                    }
                    if (status == "ok") {
                        Toast.makeText(context, "تم", Toast.LENGTH_SHORT).show()
                        borrow_book_card_view.visibility = View.GONE
                    } else {
                        Toast.makeText(context, "لم يتم", Toast.LENGTH_SHORT).show()
                    }

                } catch (e: Exception) {
                    Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
                }
            },
            Response.ErrorListener {
                Toast.makeText(context, it.message, Toast.LENGTH_LONG).show()
            }
        ) {
            override fun getParams(): Map<String, String> {
                //Creating HashMap
                val params = HashMap<String, String>()
                params["book_id"] = bookId
                params["borrow_id"] = borrowId
                params["borrow_date"] = borrowDate
                params["borrow_state"] = "1"
                params["book_status"] = (bookStatus.toInt()-1).toString()
                return params
            }
        }

        postRequest.retryPolicy =
            DefaultRetryPolicy(
                0, DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
            )
        queue.add(postRequest)
        postRequest.setShouldCache(false)
    }

    private fun deleteRequest(borrowId: String,borrow_book_card_view:CardView) {
        val url = "https://library123456.000webhostapp.com/DeleteBorrow.php"
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
                    var status: Any = ""
                    for (i in 0 until jsonArray.length()) {
                        val jsonInner: JSONObject = jsonArray.getJSONObject(i)
                        status = jsonInner.get("status")
                    }
                    if (status == "ok") {
                        Toast.makeText(context, "تم", Toast.LENGTH_SHORT).show()
                        borrow_book_card_view.visibility = View.GONE
                    } else {
                        Toast.makeText(context, "لم يتم", Toast.LENGTH_SHORT).show()
                    }

                } catch (e: Exception) {
                    Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
                }
            },
            Response.ErrorListener {
                Toast.makeText(context, it.message, Toast.LENGTH_LONG).show()
            }
        ) {
            override fun getParams(): Map<String, String> {
                //Creating HashMap
                val params = HashMap<String, String>()
                params["borrow_Id"] = borrowId
                return params
            }
        }

        postRequest.retryPolicy =
            DefaultRetryPolicy(
                0, DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
            )
        queue.add(postRequest)
        postRequest.setShouldCache(false)
    }

}