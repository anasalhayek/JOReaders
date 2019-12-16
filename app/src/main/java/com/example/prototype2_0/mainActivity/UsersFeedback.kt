package com.example.prototype2_0.mainActivity

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.prototype2_0.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.app_bar_home.*
import kotlinx.android.synthetic.main.users_feedback.*
import org.json.JSONArray
import org.json.JSONObject
import java.util.ArrayList

class UsersFeedback : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.users_feedback, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        activity!!.home_page_search_bar.visibility = View.GONE
        getFdb()
    }
    private fun getFdb() {
        val url = "https://library123456.000webhostapp.com/GetFdb.php"
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
                    val fdb_id = ArrayList<String>()
                    val fdb_text = ArrayList<String>()
                    val fdb_date = ArrayList<String>()
                    val user_id = ArrayList<String>()
                    val user_name = ArrayList<String>()
                    val user_image = ArrayList<String>()
                    var status: Any = ""

                    fdb_id.clear()
                    fdb_text.clear()
                    fdb_date.clear()
                    user_id.clear()
                    user_name.clear()
                    user_image.clear()
//                    post_image.clear()
                    for (i in 0 until jsonArray.length()) {
                        val jsonInner: JSONObject = jsonArray.getJSONObject(i)
                        fdb_id.add(jsonInner.get("fdb_id").toString())
                        fdb_text.add(jsonInner.get("fdb_text").toString())
                        fdb_date.add(jsonInner.get("fdb_date").toString())
                        user_id.add(jsonInner.get("user_id").toString())
                        user_name.add(jsonInner.get("user_name").toString())
                        user_image.add(jsonInner.get("user_image").toString())
                        status = jsonInner.get("status")
                    }
                    if (status == "ok") {
                        fdb_list.layoutManager = LinearLayoutManager(activity)
                        fdb_list.layoutManager =
                            LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
                        fdb_list.adapter = FdbAdapter(
                            activity!!,
                            fdb_id,
                            fdb_text,
                            fdb_date,
                            user_id,
                            user_name,
                            user_image
                        )
                        users_fdb_pb.visibility = View.GONE

                    } else {
//                        Toast.makeText(activity, "no man", Toast.LENGTH_SHORT).show()
                        users_fdb_pb.visibility = View.GONE
                    }

                } catch (e: Exception) {
                    Toast.makeText(activity, e.message, Toast.LENGTH_LONG).show()
                    users_fdb_pb.visibility = View.GONE
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

class FdbAdapter(
    val context: Context,
    val fdb_id: ArrayList<String>,
    val fdb_text: ArrayList<String>,
    val fdb_date: ArrayList<String>,
    val user_id: ArrayList<String>,
    val user_name: ArrayList<String>,
    val user_image: ArrayList<String>
) : RecyclerView.Adapter<ViewHolder>() {

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.post_look, p0, false))
    }

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        p0.user_name_post.text = user_name[p1]
        p0.date_post.text = fdb_date[p1]
        p0.text_post.text = fdb_text[p1]
        if (user_image[p1]!="null")
        Picasso.get().load("https://library123456.000webhostapp.com/images/${user_image[p1]}")
            .into(p0.post_profile_photo)

        p0.user_name_post.setOnClickListener {
            val popupMenu = PopupMenu(context, p0.user_name_post)
            popupMenu.menuInflater.inflate(R.menu.view_profile, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.view_profile_item ->{
                        val bundle = Bundle()
                        bundle.putString("user_id", user_id[p1])
                        bundle.putString("user_name", user_name[p1])
                        bundle.putString("user_image", user_image[p1])

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
        p0.post_profile_photo.setOnClickListener {
            val popupMenu = PopupMenu(context, p0.post_profile_photo)
            popupMenu.menuInflater.inflate(R.menu.view_profile, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.view_profile_item ->{
                        val bundle = Bundle()
                        bundle.putString("user_id", user_id[p1])
                        bundle.putString("user_name", user_name[p1])
                        bundle.putString("user_image", user_image[p1])

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

    }

    override fun getItemCount(): Int {
        return fdb_id.size
    }

}