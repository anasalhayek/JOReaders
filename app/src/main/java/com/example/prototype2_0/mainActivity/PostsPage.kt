package com.example.prototype2_0.mainActivity

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.cardview.widget.CardView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.prototype2_0.HomeActivity
import com.example.prototype2_0.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.app_bar_home.*
import kotlinx.android.synthetic.main.post_look.*
import kotlinx.android.synthetic.main.society_layout.*
import org.json.JSONArray
import org.json.JSONObject
import java.util.*
import kotlin.collections.HashMap

class PostsPage : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.society_layout, container, false)
    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        swiperefresh.setOnRefreshListener {
            getPost()
            swiperefresh.isRefreshing=false
        }

        getPost()
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

                val nextFragment = SearchPosts()
                nextFragment.arguments = bundle

                val fragmentManager = activity!!.supportFragmentManager
                fragmentManager.beginTransaction().replace(R.id.screen_area, nextFragment)
                    .addToBackStack("tag").commit()
                return false
            }
        })

        fab.setOnClickListener {
            val fragmentManager = activity!!.supportFragmentManager
            fragmentManager.beginTransaction().replace(R.id.screen_area, AddPostPage())
                .addToBackStack("tag")
                .commit()
        }
    }

    public fun getPost() {
        val url = "https://library123456.000webhostapp.com/GetPost.php"
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
//                    val post_image = ArrayList<String>()
                    val user_id = ArrayList<String>()
                    val user_name = ArrayList<String>()
                    val user_image = ArrayList<String>()
                    var status: Any = ""

                    post_id.clear()
                    post_text.clear()
                    post_date.clear()
                    user_id.clear()
                    user_name.clear()
                    user_image.clear()
//                    post_image.clear()
                    for (i in 0 until jsonArray.length()) {
                        val jsonInner: JSONObject = jsonArray.getJSONObject(i)
                        post_id.add(jsonInner.get("post_id").toString())
                        post_text.add(jsonInner.get("post_text").toString())
                        post_date.add(jsonInner.get("post_date").toString())
//                        post_image.add(jsonInner.get("post_image").toString())
                        user_id.add(jsonInner.get("user_id").toString())
                        user_name.add(jsonInner.get("user_name").toString())
                        user_image.add(jsonInner.get("user_image").toString())
                        status = jsonInner.get("status")
                    }
                    if (status == "ok") {
                        post_list_home_page.layoutManager = LinearLayoutManager(activity)
                        post_list_home_page.layoutManager =
                            LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
                        post_list_home_page.adapter = PostAdapter(
                            activity!!,
                            post_id,
                            post_text,
                            post_date,
                            user_id,
                            user_name,
                            user_image
                        )
                        posts_pb.visibility = View.GONE

                    } else {
//                        Toast.makeText(activity, "no man", Toast.LENGTH_SHORT).show()
                        posts_pb.visibility = View.GONE
                    }

                } catch (e: Exception) {
                    Toast.makeText(activity, e.message, Toast.LENGTH_LONG).show()
//                    posts_pb.visibility = View.GONE
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

class PostAdapter(
    val context: Context,
    val post_id: ArrayList<String>,
    val post_text: ArrayList<String>,
    val post_date: ArrayList<String>,
//    val post_image: ArrayList<String>,
    val user_id: ArrayList<String>,
    val user_name: ArrayList<String>,
    val user_image: ArrayList<String>
) : RecyclerView.Adapter<ViewHolder>() {
    val posts = PostsPage()

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.post_look, p0, false))
    }

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        p0.user_name_post.text = user_name[p1]
        p0.date_post.text = post_date[p1]
        p0.text_post.text = post_text[p1]
        if (user_image[p1]!="null")
        Picasso.get().load("https://library123456.000webhostapp.com/images/${user_image[p1]}")
            .into(p0.post_profile_photo)

        p0.post_more.setOnClickListener {
            val popupMenu = PopupMenu(context, p0.post_more)
            popupMenu.menuInflater.inflate(R.menu.more_menu_delete, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.delete_post_item -> {
                        DeletePost(post_id[p1], p0.post_card_view)
//                        notifyItemRemoved(p1)
//                        posts.getPost()
                    }
                }
                true
            }
            popupMenu.show()
        }
        p0.post_card_view.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("post_id", post_id[p1])
            bundle.putString("post_text", post_text[p1])
            bundle.putString("post_date", post_date[p1])
            bundle.putString("user_id", user_id[p1])
            bundle.putString("user_name", user_name[p1])
            bundle.putString("user_image", user_image[p1])

            val nextFragment = FullPostPage()
            nextFragment.arguments = bundle

            val fragmentManager = (context as AppCompatActivity).supportFragmentManager
            fragmentManager.beginTransaction().replace(R.id.screen_area, nextFragment)
                .addToBackStack("tag").commit()
        }
        p0.user_name_post.setOnClickListener {
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
        p0.post_profile_photo.setOnClickListener {
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

        val sharedPreference = context.getSharedPreferences("myPrefs",Context.MODE_PRIVATE)
        val user_role=sharedPreference.getString("user_role","")
        if (user_role=="2"){
            p0.post_more.visibility=View.VISIBLE
        }
    }
    override fun getItemCount(): Int {
        return post_id.size
    }
    private fun DeletePost(post_id: String,post_card_view:CardView) {

        val url = "https://library123456.000webhostapp.com/DeletePost.php"
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
                        post_card_view.visibility = View.GONE
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
                params["post_id"] =post_id
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
