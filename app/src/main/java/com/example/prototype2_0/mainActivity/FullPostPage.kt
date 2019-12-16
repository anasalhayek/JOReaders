package com.example.prototype2_0.mainActivity

import android.content.Context
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
import com.example.prototype2_0.HomeActivity
import com.example.prototype2_0.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.app_bar_home.*
import kotlinx.android.synthetic.main.full_post_page.*
import kotlinx.android.synthetic.main.profile_layout.*
import org.json.JSONArray
import org.json.JSONObject
import java.util.ArrayList

class FullPostPage : Fragment() {

    var postId = ""
    var postDate = ""
    var postText = ""
    var user_id = ""
    var user_name = ""
    var user_image = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.full_post_page, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        fullp_swipe_refresh.setOnRefreshListener {
            getComments()
            fullp_swipe_refresh.isRefreshing=false
        }

        postId = this.arguments!!.getString("post_id")!!
        postDate = this.arguments!!.getString("post_date")!!
        postText = this.arguments!!.getString("post_text")!!
        user_id = this.arguments!!.getString("user_id")!!
        user_name = this.arguments!!.getString("user_name")!!
        user_image = this.arguments!!.getString("user_image")!!

        user_name_post_full_post_page.text = user_name
        if (user_image!="null")
        Picasso.get().load("https://library123456.000webhostapp.com/images/$user_image").into(profile_photo_full_post_page)
        date_post_full_post_page.text = postDate
        text_post_full_post_page.text = postText

        user_name_post_full_post_page.setOnClickListener {
            val popupMenu = PopupMenu(context, user_name_post_full_post_page)
            popupMenu.menuInflater.inflate(R.menu.view_profile, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.view_profile_item ->{
                        val bundle = Bundle()
                        bundle.putString("user_id", user_id)
                        bundle.putString("user_name", user_name)
                        bundle.putString("user_image", user_image)

                        val nextFragment = ProfilePageVisit()
                        nextFragment.arguments = bundle

                        val fragmentManager = (context as AppCompatActivity).supportFragmentManager
                        fragmentManager.beginTransaction().replace(R.id.screen_area, nextFragment)
                            .addToBackStack("tag").commit()}
                }
                true
            }
            popupMenu.show()
        }
        profile_photo_full_post_page.setOnClickListener {
            val popupMenu = PopupMenu(context, profile_photo_full_post_page)
            popupMenu.menuInflater.inflate(R.menu.view_profile, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.view_profile_item ->{
                        val bundle = Bundle()
                        bundle.putString("user_id", user_id)
                        bundle.putString("user_name", user_name)
                        bundle.putString("user_image", user_image)

                        val nextFragment = ProfilePageVisit()
                        nextFragment.arguments = bundle

                        val fragmentManager = (context as AppCompatActivity).supportFragmentManager
                        fragmentManager.beginTransaction().replace(R.id.screen_area, nextFragment)
                            .addToBackStack("tag").commit()}
                }
                true
            }
            popupMenu.show()
        }

        getComments()

        add_comment_btn.setOnClickListener {
            comments_PB.visibility = View.VISIBLE
            if (comment_text_box?.text.toString().trim().isNotEmpty()) {
                addComment()
                comment_text_box.hideKeyboard()
            }else{
                Toast.makeText(activity, "Empty Comment", Toast.LENGTH_LONG).show()
                comment_text_box.hideKeyboard()
                comments_PB.visibility = View.GONE
            }
        }
    }

    fun View.hideKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }

    private fun addComment() {
        val url = "https://library123456.000webhostapp.com/AddComment.php"
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
                        comment_text_box.text!!.clear()
                        comment_text_box.clearFocus()
                        getComments()
                    } else {
                        Toast.makeText(activity, "Error", Toast.LENGTH_SHORT).show()
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
                params["comment_post_id"] = postId
                params["comment_user_id"] = userId.toString()
                params["comment_text"] = comment_text_box.text.toString()
                return params
            }
        }

        postRequest.retryPolicy =
            DefaultRetryPolicy(
                0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
            )
        queue.add(postRequest)
    }

    private fun getComments() {
        val url = "https://library123456.000webhostapp.com/GetComments.php"
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
                            post_comments_list.layoutManager = LinearLayoutManager(activity)
                            post_comments_list.adapter =
                                CommentAdapter(activity!!,user_id ,user_name, user_image, comment_text,comment_id)
                            no_comment_TV.visibility=View.GONE
                            comments_PB.visibility=View.GONE
                        } catch (e: Exception) {
                            Toast.makeText(activity, e.message, Toast.LENGTH_SHORT).show()
                            comments_PB.visibility=View.GONE
                        }

                    } else {
                        no_comment_TV.visibility=View.VISIBLE
                        comments_PB.visibility=View.GONE
                    }

                } catch (e: Exception) {
                    Toast.makeText(activity, e.message, Toast.LENGTH_LONG).show()
                    comments_PB.visibility=View.GONE
                }
            },
            Response.ErrorListener {
                Toast.makeText(activity, it.message, Toast.LENGTH_LONG).show()
            }
        ) {
            override fun getParams(): Map<String, String> {
                //Creating HashMap
                val params = HashMap<String, String>()
                params["post_id"] = postId
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

class CommentAdapter(
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
                    R.id.delete_comment_item ->{
//                        posts.posts_pb.visibility = View.VISIBLE
                        DeletePostComment(comment_id[p1],p0.comment_card_view)
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
        val sharedPreference = context.getSharedPreferences("myPrefs",Context.MODE_PRIVATE)
        val user_role=sharedPreference.getString("user_role","")
        if (user_role=="2"){
            p0.comment_more.visibility=View.VISIBLE
        }
    }

    override fun getItemCount(): Int {
        return user_name.size
    }
    private fun DeletePostComment(comment_id: String,comment_card_view:CardView) {
        val url = "https://library123456.000webhostapp.com/DeletePostComment.php"
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
                params["comment_id"] =comment_id
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