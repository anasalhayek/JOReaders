package com.example.prototype2_0.mainActivity

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
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
import kotlinx.android.synthetic.main.books_catalogue2.*
import org.json.JSONArray
import org.json.JSONObject
import java.util.*
import kotlin.collections.HashMap
import kotlin.collections.Map
import kotlin.collections.set

class BooksCatalogue : Fragment() {
    val utilities = Utitlities()

    var libraryId=""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.books_catalogue2, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        libraryId=this.arguments!!.getString("libraryId").toString()

        getCategories()

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

                val nextFragment = SearchBooks()
                nextFragment.arguments = bundle

                val fragmentManager = activity!!.supportFragmentManager
                fragmentManager.beginTransaction().replace(R.id.screen_area, nextFragment)
                    .addToBackStack("tag").commit()
                return false
            }
        })

    }


    private fun getCategories() {
        val url = utilities.base_url + "GetCategories.php"
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
                    val categoryId = ArrayList<String>()
                    val category = ArrayList<String>()
                    var status: Any = ""
                    categoryId.clear()
                    category.clear()
                    for (i in 0 until jsonArray.length()) {
                        val jsonInner: JSONObject = jsonArray.getJSONObject(i)
                        categoryId.add(jsonInner.get("category_id").toString())
                        category.add(jsonInner.get("category").toString())
                        status = jsonInner.get("status")
                    }
                    if (status == "ok") {
                        try {
                            cataloge_list.layoutManager = LinearLayoutManager(activity)
                            cataloge_list.adapter =CatalogeAdapter(activity!!, categoryId, category,libraryId)

                            //btns
                            cat_btns_recycler.layoutManager = LinearLayoutManager(activity)
                            cat_btns_recycler.layoutManager =LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                            cat_btns_recycler.adapter =CategoriesBtnsAdapter(activity!!, categoryId, category,libraryId)

                            catalog_PB.visibility = View.GONE
                        } catch (e: Exception) {
                            Toast.makeText(activity, e.message, Toast.LENGTH_SHORT).show()
                            catalog_PB.visibility = View.GONE
                        }

                    } else {
                        catalog_PB.visibility = View.GONE
                    }

                } catch (e: Exception) {
                    Toast.makeText(activity, e.message, Toast.LENGTH_LONG).show()
                    catalog_PB.visibility = View.GONE
                }
            },
            Response.ErrorListener {
                Toast.makeText(activity, it.message, Toast.LENGTH_LONG).show()
            }
        ) {
            override fun getParams(): Map<String, String> {
                //Creating HashMap
                val params = HashMap<String, String>()
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

class CatalogeAdapter(
    val context: Context,
    private val categoryId: ArrayList<String>,
    private val category: ArrayList<String>,
    private  val libraryId:String
) : RecyclerView.Adapter<ViewHolder>() {

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.inner_recycler_view,
                p0,
                false
            )
        )
    }

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        p0.inner_category_name.text = category[p1]
        getBooksUsingCategory(p0.inner_recycler_view, categoryId[p1])
    }

    override fun getItemCount(): Int {
        return categoryId.size
    }

    private fun getBooksUsingCategory(inner_recycler_view: RecyclerView, categoryId: String) {
        val utitlities = Utitlities()
        val url = "${utitlities.base_url}GetBooksUsingCategory.php"
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
                            inner_recycler_view.layoutManager = LinearLayoutManager(context)
                            inner_recycler_view.layoutManager =
                                LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

                            inner_recycler_view.adapter = SuggestionsAdapter(
                                context,
                                book_id,
                                book_image,
                                book_name,
                                categoryId
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
                params["libraryId"] = libraryId
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

class BooksCatalogAdapter(
    val context: Context,
    private val bookId: ArrayList<String>,
    private val bookImg: ArrayList<String>,
    private val bookName: ArrayList<String>,
    private val categoriesId: ArrayList<String>
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
            bundle.putString("categoryId", categoriesId[p1])

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

class CategoriesBtnsAdapter(
    val context: Context,
    private val categoryId: ArrayList<String>,
    private val category: ArrayList<String>,
    private val libraryId: String
) : RecyclerView.Adapter<ViewHolder>() {

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.category_btn_look, p0, false
            )
        )
    }

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        p0.category_btn.text = category[p1]
        p0.category_btn.setOnClickListener {
            val bundel = Bundle()
            bundel.putString("categoryId", categoryId[p1])
            bundel.putString("categoryName", category[p1])
            bundel.putString("libraryId", libraryId)

            val nextFragment = book_category()
            nextFragment.arguments = bundel

            val fragmentManager = (context as AppCompatActivity).supportFragmentManager
            fragmentManager.beginTransaction().replace(R.id.screen_area, nextFragment)
                .addToBackStack("tag").commit()
        }
    }

    override fun getItemCount(): Int {
        return categoryId.size
    }

}


