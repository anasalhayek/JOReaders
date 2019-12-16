package com.example.prototype2_0.mainActivity

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
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.prototype2_0.HomeActivity
import com.example.prototype2_0.R
import com.example.prototype2_0.Utitlities
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.app_bar_home.*
import kotlinx.android.synthetic.main.borrowed_books.*
import org.json.JSONArray
import org.json.JSONObject
import java.util.*
import kotlin.collections.HashMap

class BorrowedBooks : Fragment() {
        override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
        ): View? {
            return inflater.inflate(R.layout.borrowed_books, container, false)
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

                    val nextFragment = SearchBBooks()
                    nextFragment.arguments = bundle

                    val fragmentManager = activity!!.supportFragmentManager
                    fragmentManager.beginTransaction().replace(R.id.screen_area, nextFragment)
                        .addToBackStack("tag").commit()
                    return false
                }
            })

            getBorrowedBooks()
            borrowed_books_swipe_refresh.setOnRefreshListener {
                getBorrowedBooks()
                borrowed_books_swipe_refresh.isRefreshing = false
            }
        }
    private fun getBorrowedBooks() {
        val utitlities = Utitlities()
        val url = utitlities.base_url + "GetBorrowedBooks.php"
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
                            borrowed_books_recycler_view.layoutManager =LinearLayoutManager(activity)
                            borrowed_books_recycler_view.adapter =
                                BooksBorrowedAdapter(
                                    activity!!,
                                    book_id,
                                    borrow_id,
                                    book_image,
                                    book_name,
                                    user_name,
                                    book_status,
                                    user_un)

                            borrowed_books_PB.visibility = View.GONE
                        } catch (e: Exception) {
                            Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
                            borrowed_books_PB.visibility = View.GONE
                        }

                    } else {
                        borrowed_books_PB.visibility = View.GONE
                    }

                } catch (e: Exception) {
                    Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
                    borrowed_books_PB.visibility = View.GONE
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

class BooksBorrowedAdapter(
    val context: Context,
    private val book_id: ArrayList<String>,
    private val borrowId: ArrayList<String>,
    private val bookImg: ArrayList<String>,
    private val bookName: ArrayList<String>,
    private val userName: ArrayList<String>,
    private val bookStatus: ArrayList<String>,
    private val userUn: ArrayList<String>
) : RecyclerView.Adapter<ViewHolder>() {
    val utilities = Utitlities()

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.borrow_look, p0, false))
    }

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        Picasso.get().load(utilities.book_image_url + bookImg[p1]).into(p0.borrow_book_book_image)
        p0.borrow_book_book_name.text = bookName[p1]
        p0.borrow_book_user_name.text = userName[p1]
        p0.borrow_book_user_un.text = userUn[p1]
        p0.borrow_book_more.setOnClickListener {
            val popupMenu = PopupMenu(context, p0.borrow_book_more)
            popupMenu.menuInflater.inflate(R.menu.borrow_available, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.borrow_available_item -> {
                        ReturnBorrow(book_id[p1], borrowId[p1],p0.borrow_book_card_view,bookStatus[p1])
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

    private fun ReturnBorrow(bookId: String,borrowId: String,borrow_book_card_view:CardView,bookStatus:String) {
        val url = "https://library123456.000webhostapp.com/ReturnBorrow.php"
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
                params["book_status"] = (bookStatus.toInt()+1).toString()
                return params
            }
        }

        postRequest.retryPolicy =
            DefaultRetryPolicy(
                0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
            )
        queue.add(postRequest)
        postRequest.setShouldCache(false)
    }

}