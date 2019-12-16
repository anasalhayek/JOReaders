package com.example.prototype2_0.mainActivity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.prototype2_0.HomeActivity
import com.example.prototype2_0.R
import kotlinx.android.synthetic.main.about_layout.*
import kotlinx.android.synthetic.main.app_bar_home.*


class About : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.about_layout, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (activity as HomeActivity).home_page_search_bar.visibility = View.GONE

        about_fdb.setOnClickListener {
            fragmentManager!!.beginTransaction().replace(R.id.screen_area, AddFeedback())
                .addToBackStack("tag")
                .commit()
//            activity!!.finish()
        }
        about_pp.setOnClickListener {
            val openURL = Intent(Intent.ACTION_VIEW)
            openURL.data = Uri.parse("https://library123456.000webhostapp.com/privacypolicy.html")
            startActivity(openURL)
        }
        about_ts.setOnClickListener {
            val openURL = Intent(Intent.ACTION_VIEW)
            openURL.data = Uri.parse("https://library123456.000webhostapp.com/termsofservice.html")
            startActivity(openURL)
        }
    }
}