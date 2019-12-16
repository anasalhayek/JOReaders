package com.example.prototype2_0.mainActivity

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.prototype2_0.R
import com.example.prototype2_0.Utitlities
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.book_category.*
import org.json.JSONArray
import org.json.JSONObject
import java.util.ArrayList

class book_category : Fragment() {
    val utilities = Utitlities()
    var categoryIdV = ""
    var categoryNameV = ""
    var libraryId=""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.book_category, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        libraryId=this.arguments!!.getString("libraryId").toString()
        categoryIdV = this.arguments!!.getString("categoryId").toString()
        categoryNameV = this.arguments!!.getString("categoryName").toString()

        cat_name_TV.text = categoryNameV
        getBooksUsingCategory()
    }

    private fun getBooksUsingCategory() {
        val utitlities = Utitlities()
        val url = utitlities.base_url + "GetBooksUsingCategory.php"
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
                    val categoryId = ArrayList<String>()
                    var status: Any = ""
                    book_id.clear()
                    book_image.clear()
                    book_name.clear()
                    for (i in 0 until jsonArray.length()) {
                        val jsonInner: JSONObject = jsonArray.getJSONObject(i)
                        book_id.add(jsonInner.get("book_id").toString())
                        book_image.add(jsonInner.get("book_image").toString())
                        book_name.add(jsonInner.get("book_name").toString())
                        categoryId.add(jsonInner.get("categoryId").toString())
                        status = jsonInner.get("status")
                    }
                    if (status == "ok") {
                        try {
                            category_recycler_view.layoutManager = GridLayoutManager(activity, 3)
                            category_recycler_view.adapter =
                                BooksCategoryAdapter(
                                    activity!!,
                                    book_id,
                                    book_image,
                                    book_name,
                                    categoryId
                                )
                            category_PB.visibility=View.GONE
                        } catch (e: Exception) {
                            Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
                            category_PB.visibility=View.GONE
                        }

                    } else {
                        category_PB.visibility=View.GONE
                    }

                } catch (e: Exception) {
                    Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
                    category_PB.visibility=View.GONE
                }
            },
            Response.ErrorListener {
                Toast.makeText(context, it.message, Toast.LENGTH_LONG).show()
            }
        ) {
            override fun getParams(): Map<String, String> {
                //Creating HashMap
                val params = HashMap<String, String>()
                params["cat_id"] = categoryIdV
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

class BooksCategoryAdapter(
    val context: Context,
    private val bookId: ArrayList<String>,
    private val bookImg: ArrayList<String>,
    private val bookName: ArrayList<String>,
    private val categoryId: ArrayList<String>
) : RecyclerView.Adapter<ViewHolder>() {
    val utilities = Utitlities()

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.book_look, p0, false))
    }

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        p0.book_name_book_look.text = bookName[p1]
//        if(bookImg[p1]!=="null")
        Picasso.get().load(utilities.book_image_url + bookImg[p1]).into(p0.book_image_book_look)
        p0.book_layout.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("bookId", bookId[p1])
            bundle.putString("categoryId", categoryId[p1])

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