package com.example.prototype2_0.mainActivity

import android.app.AlertDialog
import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.prototype2_0.R
import com.example.prototype2_0.Utitlities
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.book_layout.*
import org.json.JSONArray
import org.json.JSONObject
import java.util.*
import kotlin.collections.HashMap
import kotlin.collections.Map
import kotlin.collections.set

class BookPage : Fragment() {
    var utitlities = Utitlities()

    var bookId = ""
    var categoryId = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.book_layout, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        bookc_swipe_refresh.setOnRefreshListener {
            getBookComments()
            getBook()
            bookc_swipe_refresh.isRefreshing = false
        }

        bookId = this.arguments!!.getString("bookId")!!
        categoryId = this.arguments!!.getString("categoryId")!!

        getBook()
        getBookComments()
        getSuggestions()

        val sharedPreference = activity!!.getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        val userUn = sharedPreference.getString("user_un", "")

        borrow_btn.setOnClickListener {
            if (userUn != "") {
//                if (bookStatus == "0") {
//                    dialogBox("جميع نسخ الكتاب معارة")
//                }else
                getBookStatus()
            }
            else dialogBox("لا يمكنك الإستعارة بدون رقم جامعي , \nعدل الرقم الجامعي من الصفحة الشخصية")
        }


        book_add_comment_btn.setOnClickListener {
            book_comments_PB.visibility = View.VISIBLE
            if (book_comment_text_box?.text.toString().trim().isNotEmpty()) {
                addBookComment()
                book_comment_text_box.hideKeyboard()
            } else {
                Toast.makeText(activity, "Empty Comment", Toast.LENGTH_LONG).show()
                book_comment_text_box.hideKeyboard()
                book_comments_PB.visibility = View.GONE
            }
        }

        add_book_to_list.setOnClickListener {
            val popupMenu = PopupMenu(activity, add_book_to_list)
            popupMenu.menuInflater.inflate(R.menu.shelves_popup, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.currently_reading ->
                        currentlyReading()

                    R.id.want_to_read ->
                        wantToRead()

                    R.id.done_reading ->
                        doneReading()
                }
                true
            }
            popupMenu.show()
        }
    }

    fun View.hideKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }

    private fun addBorrowed() {
        val url = "https://library123456.000webhostapp.com/AddBorrowed.php"
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
                        dialogBox("سوف يتم مراجعة طلبك راجع المكتبة للاستلام")
                    } else {
                        dialogBox("تم تقديم طلب الاستعارة مسبقاً \n راجع المكتبة")
                    }
                } catch (e: Exception) {
                    Toast.makeText(activity, e.message, Toast.LENGTH_LONG).show()
                }
            },
            Response.ErrorListener {
                Toast.makeText(activity, it.message, Toast.LENGTH_LONG).show()
            }
        ) {

            val sharedPreference = activity!!.getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
            val userId = sharedPreference.getString("user_id", "")

            override fun getParams(): Map<String, String> {
                //Creating HashMap
                val params = HashMap<String, String>()
                params["borrow_book_id"] = bookId
                params["borrow_user_id"] = userId.toString()
                return params
            }
        }

        postRequest.retryPolicy =
            DefaultRetryPolicy(
                0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
            )
        queue.add(postRequest)
    }

    private fun dialogBox(text: String) {
        val builder = AlertDialog.Builder(activity!!)
//            builder.setTitle("30 ثانية لتسجيل الدخول")
        builder.setMessage(text)
        builder.setPositiveButton("حسناً") { dialog, which ->

        }
//        builder.setNegativeButton("إغلاق") { dialog, which ->
//            activity!!.onBackPressed()
//        }
        val dialog: AlertDialog = builder.create()
        dialog.setCanceledOnTouchOutside(false)
        dialog.show()
    }

    private fun getBook() {
        val url = "https://library123456.000webhostapp.com/GetBooks.php"
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
//                    var book_id = ""
                    var book_image = ""
//                    var book_category = ""
                    var book_name = ""
                    var book_author = ""
                    var book_status = ""
                    var book_description = ""
                    var category = ""
                    var status: Any = ""

                    for (i in 0 until jsonArray.length()) {
                        val jsonInner: JSONObject = jsonArray.getJSONObject(i)
//                        book_id = (jsonInner.get("book_id").toString())
                        book_image = (jsonInner.get("book_image").toString())
//                        book_category = (jsonInner.get("book_category_id").toString())
                        book_name = (jsonInner.get("book_name").toString())
                        book_author = (jsonInner.get("book_author").toString())
                        book_status = (jsonInner.get("book_status").toString())
                        book_description = (jsonInner.get("book_description").toString())
                        category = (jsonInner.get("category").toString())
                        status = jsonInner.get("status")
                    }
                    if (status == "ok") {

                        if (book_image!="null")
                        Picasso.get()
                            .load("https://library123456.000webhostapp.com/BookImage/$book_image")
                            .into(book_image_book_page)

                        book_name_book_page.text = book_name
                        author_name_book_page.text = book_author
                        book_status_book_page.text = book_status
                        book_category_book_page.text = category
                        book_description_book_page.text = book_description

                        val sharedPreference = activity!!.getSharedPreferences(
                            "myPrefs",
                            Context.MODE_PRIVATE
                        )
                        val editor = sharedPreference.edit()
                        editor.putString("book_status", book_status)
                        editor.commit()

                        if (book_status.toInt()==0){
                            book_status_book_page.text="الكتاب معار"
                            }
                        else book_status_book_page.text= "عدد النسخ المتوفرة : $book_status"

                        pdf.setOnClickListener {
                            val url = "$book_name pdf"
                            val i = Intent(Intent.ACTION_WEB_SEARCH)
                            i.putExtra(SearchManager.QUERY, url)
                            startActivity(i)
                        }

                        audio.setOnClickListener {
                            val url = "$book_name audio book"
                            val i = Intent(Intent.ACTION_WEB_SEARCH)
                            i.putExtra(SearchManager.QUERY, url)
                            startActivity(i)
                        }

                    } else {
                        Toast.makeText(activity, "no man", Toast.LENGTH_SHORT).show()
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
                params["bookId"] = bookId
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

    private fun addBookComment() {
        val url = "https://library123456.000webhostapp.com/AddBooks_comments.php"
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
                        Toast.makeText(activity, "Added", Toast.LENGTH_LONG).show()
                        book_comment_text_box.text!!.clear()
                        getBookComments()
                    } else {
                        Toast.makeText(activity, "ERROR", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(activity, e.message, Toast.LENGTH_LONG).show()
                }
            },
            Response.ErrorListener {
                Toast.makeText(activity, it.message, Toast.LENGTH_LONG).show()
            }
        ) {

            val sharedPreference = activity!!.getSharedPreferences(
                "myPrefs",
                Context.MODE_PRIVATE
            )
            val userId = sharedPreference.getString("user_id", "")

            override fun getParams(): Map<String, String> {
                //Creating HashMap
                val params = HashMap<String, String>()
                params["comment_book_id"] = bookId
                params["comment_user_id"] = userId.toString()
                params["comment_text"] = book_comment_text_box.text.toString()
                return params
            }
        }

        postRequest.retryPolicy =
            DefaultRetryPolicy(
                0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
            )
        queue.add(postRequest)
    }

    private fun getBookComments() {
        val url = "https://library123456.000webhostapp.com/GetBooks_comments.php"
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
                    val user_id = ArrayList<String>()
                    val user_name = ArrayList<String>()
                    val user_image = ArrayList<String>()
                    val comment_text = ArrayList<String>()
                    val comment_id = ArrayList<String>()
                    var status: Any = ""
                    user_id.clear()
                    user_name.clear()
                    user_image.clear()
                    comment_text.clear()
                    comment_id.clear()
                    for (i in 0 until jsonArray.length()) {
                        val jsonInner: JSONObject = jsonArray.getJSONObject(i)
                        user_id.add(jsonInner.get("user_id").toString())
                        user_name.add(jsonInner.get("user_name").toString())
                        user_image.add(jsonInner.get("user_image").toString())
                        comment_text.add(jsonInner.get("comment_text").toString())
                        comment_id.add(jsonInner.get("comment_id").toString())
                        status = jsonInner.get("status")
                    }
                    if (status == "ok") {
                        try {
                            book_comments_list.layoutManager = LinearLayoutManager(activity)
                            book_comments_list.adapter =
                                BookCommentAdapter(
                                    activity!!,
                                    user_id,
                                    user_name,
                                    user_image,
                                    comment_text,
                                    comment_id
                                )
                            book_no_comment_TV.visibility = View.GONE
                            book_comments_PB.visibility = View.GONE
                        } catch (e: Exception) {
                            Toast.makeText(activity, e.message, Toast.LENGTH_SHORT).show()
                            book_comments_PB.visibility = View.GONE
                        }

                    } else {
                        book_no_comment_TV.visibility = View.VISIBLE
                        book_comments_PB.visibility = View.GONE
                    }

                } catch (e: Exception) {
                    Toast.makeText(activity, e.message, Toast.LENGTH_LONG).show()
                    book_comments_PB.visibility = View.GONE
                }
            },
            Response.ErrorListener {
                Toast.makeText(activity, it.message, Toast.LENGTH_LONG).show()
            }
        ) {
            override fun getParams(): Map<String, String> {
                //Creating HashMap
                val params = HashMap<String, String>()
                params["book_id"] = bookId
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

    private fun getSuggestions() {
        val utitlities = Utitlities()
        val url = utitlities.base_url + "GetSuggestions.php"
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
                    val book_id = ArrayList<String>()
                    val book_image = ArrayList<String>()
                    val book_name = ArrayList<String>()
                    var status: Any = ""
                    book_id.clear()
                    book_image.clear()
                    book_name.clear()
                    for (i in 0 until jsonArray.length()) {
                        val jsonInner: JSONObject = jsonArray.getJSONObject(i)
                        book_id.add(jsonInner.get("book_id").toString())
                        book_image.add(jsonInner.get("book_image").toString())
                        book_name.add(jsonInner.get("book_name").toString())
                        status = jsonInner.get("status")
                    }
                    if (status == "ok") {
                        try {
                            suggestions.layoutManager = LinearLayoutManager(context)
                            suggestions.layoutManager =
                                LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

                            suggestions.adapter =
                                SuggestionsAdapter(
                                    activity!!, book_id, book_image, book_name, categoryId
                                )
//                            inner_PB.visibility=View.GONE
                        } catch (e: Exception) {
                            Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
//                            inner_PB.visibility=View.GONE
                        }

                    } else {
//                        catalog_PB.visibility=View.GONE
                    }

                } catch (e: Exception) {
                    Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
//                    catalog_PB.visibility=View.GONE
                }
            },
            Response.ErrorListener {
                Toast.makeText(context, it.message, Toast.LENGTH_LONG).show()
            }
        ) {
            override fun getParams(): Map<String, String> {
                //Creating HashMap
                val params = HashMap<String, String>()
                params["cat_id"] = categoryId
                params["book_id"] = bookId
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

    private fun getBookStatus() {
        val utitlities = Utitlities()
        val url = utitlities.base_url + "GetBookState.php"
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
                    val book_status = ArrayList<String>()
                    var status: Any = ""
                    book_status.clear()
                    for (i in 0 until jsonArray.length()) {
                        val jsonInner: JSONObject = jsonArray.getJSONObject(i)
                        book_status.add(jsonInner.get("book_status").toString())
                        status = jsonInner.get("status")
                    }
                    if (status == "ok") {
                        try {

                            if (book_status.toString() == "0") {
                                dialogBox("جميع نسخ الكتاب معارة")
                            }
                            else addBorrowed()

                        } catch (e: Exception) {
                            Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
//                            inner_PB.visibility=View.GONE
                        }

                    } else {
//                        catalog_PB.visibility=View.GONE
                    }

                } catch (e: Exception) {
                    Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
//                    catalog_PB.visibility=View.GONE
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

    private fun doneReading() {
        val url = "https://library123456.000webhostapp.com/AddDoneReading.php"
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
                        Toast.makeText(activity, "Added", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(activity, "Already added", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(activity, e.message, Toast.LENGTH_LONG).show()
                }
            },
            Response.ErrorListener {
                Toast.makeText(activity, it.message, Toast.LENGTH_LONG).show()
            }
        ) {

            val sharedPreference = activity!!.getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
            val userId = sharedPreference.getString("user_id", "").toString()

            override fun getParams(): Map<String, String> {
                //Creating HashMap
                val params = HashMap<String, String>()
                params["book_id"] = bookId
                params["user_id"] = userId
                return params
            }
        }

        postRequest.retryPolicy =
            DefaultRetryPolicy(
                0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
            )
        queue.add(postRequest)
    }

    private fun currentlyReading() {
        val url = "https://library123456.000webhostapp.com/AddCurrentlyReading.php"
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
                        Toast.makeText(activity, "Added", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(activity, "Already added", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(activity, e.message, Toast.LENGTH_LONG).show()
                }
            },
            Response.ErrorListener {
                Toast.makeText(activity, it.message, Toast.LENGTH_LONG).show()
            }
        ) {

            val sharedPreference = activity!!.getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
            val userId = sharedPreference.getString("user_id", "").toString()

            override fun getParams(): Map<String, String> {
                //Creating HashMap
                val params = HashMap<String, String>()
                params["book_id"] = bookId
                params["user_id"] = userId
                return params
            }
        }

        postRequest.retryPolicy =
            DefaultRetryPolicy(
                0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
            )
        queue.add(postRequest)
    }

    private fun wantToRead() {
        val url = "https://library123456.000webhostapp.com/AddWantToRead.php"
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
                        Toast.makeText(activity, "Added", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(activity, "Already added", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(activity, e.message, Toast.LENGTH_LONG).show()
                }
            },
            Response.ErrorListener {
                Toast.makeText(activity, it.message, Toast.LENGTH_LONG).show()
            }
        ) {

            val sharedPreference = activity!!.getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
            val userId = sharedPreference.getString("user_id", "").toString()

            override fun getParams(): Map<String, String> {
                //Creating HashMap
                val params = HashMap<String, String>()
                params["book_id"] = bookId
                params["user_id"] = userId
                return params
            }
        }

        postRequest.retryPolicy =
            DefaultRetryPolicy(
                0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
            )
        queue.add(postRequest)
    }

}

class BookCommentAdapter(
    val context: Context,
    val user_id: ArrayList<String>,
    val user_name: ArrayList<String>,
    val user_image: ArrayList<String>,
    val comment_text: ArrayList<String>,
    val comment_id: ArrayList<String>
) : RecyclerView.Adapter<ViewHolder>() {

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.comment_look, p0, false))
    }

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        p0.user_name_comment.text = user_name[p1]
        p0.text_comment.text = comment_text[p1]
        if (user_image[p1]!="null")
        Picasso.get().load("https://library123456.000webhostapp.com/images/${user_image[p1]}")
            .into(p0.comment_profile_photo)

        p0.comment_more.setOnClickListener {
            val popupMenu = PopupMenu(context, p0.comment_more)
            popupMenu.menuInflater.inflate(R.menu.comment, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.delete_comment_item -> {
//                        posts.posts_pb.visibility = View.VISIBLE
                        DeleteBookComment(comment_id[p1], p0.comment_card_view)
//                        posts.getPost()
                    }
                }
                true
            }
            popupMenu.show()
        }
        p0.user_name_comment.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("user_id", user_id[p1])
            bundle.putString("user_name", user_name[p1])
            bundle.putString("user_image", user_image[p1])

            val nextFragment = ProfilePageVisit()
            nextFragment.arguments = bundle

            val fragmentManager = (context as AppCompatActivity).supportFragmentManager
            fragmentManager.beginTransaction().replace(R.id.screen_area, nextFragment)
                .addToBackStack("tag").commit()
        }
        p0.comment_profile_photo.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("user_id", user_id[p1])
            bundle.putString("user_name", user_name[p1])
            bundle.putString("user_image", user_image[p1])

            val nextFragment = ProfilePageVisit()
            nextFragment.arguments = bundle

            val fragmentManager = (context as AppCompatActivity).supportFragmentManager
            fragmentManager.beginTransaction().replace(R.id.screen_area, nextFragment)
                .addToBackStack("tag").commit()
        }
        val sharedPreference = context.getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        val user_role = sharedPreference.getString("user_role", "")
        if (user_role == "2") {
            p0.comment_more.visibility = View.VISIBLE
        }
    }

    override fun getItemCount(): Int {
        return user_name.size
    }

    private fun DeleteBookComment(comment_id: String, comment_card_view: CardView) {
        val url = "https://library123456.000webhostapp.com/DeleteBookComment.php"
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
                        Toast.makeText(context, "تم الحذف", Toast.LENGTH_LONG).show()
                        comment_card_view.visibility = View.GONE
                    } else {
                        Toast.makeText(context, "لم يتم الحذف !", Toast.LENGTH_LONG).show()

//                        posts.posts_pb.visibility = View.GONE
                    }

                } catch (e: Exception) {
                    Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
//                    posts.posts_pb.visibility = View.GONE
                }
            },
            Response.ErrorListener {
                Toast.makeText(context, it.message, Toast.LENGTH_LONG).show()
//                posts.posts_pb.visibility = View.GONE
            }
        ) {
            override fun getParams(): Map<String, String> {
                //Creating HashMap
                val params = HashMap<String, String>()
                params["comment_id"] = comment_id
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
    }

}


class SuggestionsAdapter(
    val context: Context,
    private val bookId: ArrayList<String>,
    private val bookImg: ArrayList<String>,
    private val bookName: ArrayList<String>,
    private val categoryId: String
) : RecyclerView.Adapter<ViewHolder>() {
    val utitlities = Utitlities()
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.book_look, p0, false))
    }

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        p0.book_name_book_look.text = bookName[p1]
//        if(bookImg[p1]!=="null")
        Picasso.get().load(utitlities.book_image_url + bookImg[p1]).into(p0.book_image_book_look)
        p0.book_layout.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("bookId", bookId[p1])
            bundle.putString("categoryId", categoryId)

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