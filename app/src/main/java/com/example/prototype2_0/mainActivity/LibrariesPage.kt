package com.example.prototype2_0.mainActivity

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.example.prototype2_0.Utitlities
import kotlinx.android.synthetic.main.app_bar_home.*
import kotlinx.android.synthetic.main.libraries_layout.*
import kotlinx.android.synthetic.main.library_design.view.*
import org.json.JSONArray
import org.json.JSONObject
import java.util.ArrayList

class LibrariesPage : Fragment() {
    var utitlities = Utitlities()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.libraries_layout, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        activity!!.home_page_search_bar.visibility = View.GONE

        getLibraries()
    }

    private fun getLibraries() {
        val url = "${utitlities.base_url}GetLibraries.php"
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
                    val libraryId = ArrayList<String>()
                    val libraryName = ArrayList<String>()
                    var status: Any = ""
                    libraryId.clear()
                    libraryName.clear()

                    for (i in 0 until jsonArray.length()) {
                        val jsonInner: JSONObject = jsonArray.getJSONObject(i)
                        libraryId.add(jsonInner.get("UniversityId").toString())
                        libraryName.add(jsonInner.get("UniversityName").toString())
                        status = jsonInner.get("status")
                    }
                    if (status == "ok") {
                        try {
                            universities_list.layoutManager = LinearLayoutManager(activity)
                            universities_list.adapter =
                                LibrariesAdapter(activity!!, libraryId, libraryName)
                        } catch (e: Exception) {
                            Toast.makeText(activity, e.message, Toast.LENGTH_SHORT).show()
                        }

                    } else {
                        //TODO:TV -> nothing to show
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

class LibrariesAdapter(
    var context: Context,
    var libraryId: ArrayList<String>,
    var libraryName: ArrayList<String>
) : RecyclerView.Adapter<LibrariesViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LibrariesViewHolder {
        return LibrariesViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.library_design, parent, false
            )
        )
    }

    override fun getItemCount(): Int {
        return libraryId.size
    }

    override fun onBindViewHolder(holder: LibrariesViewHolder, position: Int) {

        holder.libraryName.text = libraryName[position]

        holder.libraryCard.setOnClickListener {
            val bundel = Bundle()
            bundel.putString("libraryId", libraryId[position])
            bundel.putString("libraryName", libraryName[position])

            val nextFragment = BooksCatalogue()
            nextFragment.arguments = bundel

            val fragmentManager = (context as AppCompatActivity).supportFragmentManager
            fragmentManager.beginTransaction().replace(R.id.screen_area, nextFragment)
                .addToBackStack("tag").commit()
        }
    }
}

class LibrariesViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    var libraryCard = view.library_card
    var libraryIcon = view.library_icon
    var libraryName = view.library_name
}