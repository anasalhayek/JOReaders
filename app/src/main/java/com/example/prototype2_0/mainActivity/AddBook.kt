package com.example.prototype2_0.mainActivity

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.prototype2_0.R
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.add_book.*
import kotlinx.android.synthetic.main.app_bar_home.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream

class AddBook : Fragment() {
    var cat = ""
    var lib = ""
    var encodImgb = ""
    var bitmap: Bitmap? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.add_book, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        activity!!.home_page_search_bar.visibility = View.GONE
        val catStrings=arrayOf("التاريخ","الأدب","علوم الحاسوب","الأبحاث العلمية")
        add_category.adapter=ArrayAdapter(activity!!,android.R.layout.simple_spinner_dropdown_item,catStrings)
        add_category.onItemSelectedListener=object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
                //To change body of created functions use File | Settings | File Templates.
            }
            override fun onItemSelected(parent: AdapterView<*>?,view: View?,position: Int,id: Long) {
//                add_book_btn.text=catStrings[position]
                if (catStrings[position]=="التاريخ")
                    cat="1"
                if (catStrings[position]=="الأدب")
                    cat="2"
                if (catStrings[position]=="علوم الحاسوب")
                    cat="3"
                if (catStrings[position]=="الأبحاث العلمية")
                    cat="4"
            }
        }

        val libStrings=arrayOf("المكتبة الهاشمية - ال البيت","مكتبة الجامعة الأردنية","مكتبة الجامعة اليرموك")
        add_library.adapter=ArrayAdapter(activity!!,android.R.layout.simple_spinner_dropdown_item,libStrings)
        add_library.onItemSelectedListener=object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
                //To change body of created functions use File | Settings | File Templates.
            }
            override fun onItemSelected(parent: AdapterView<*>?,view: View?,position: Int,id: Long) {
//                add_book_btn.text=libStrings[position]
                if (libStrings[position]=="المكتبة الهاشمية - ال البيت")
                    lib="1"
                if (libStrings[position]=="مكتبة الجامعة الأردنية")
                    lib="2"
                if (libStrings[position]=="مكتبة الجامعة اليرموك")
                    lib="3"
            }
        }

        add_book_pb.visibility=View.GONE
        add_book_image_btn.setOnClickListener { startGallery() }

        add_book_btn.setOnClickListener {
            add_book_pb.visibility=View.VISIBLE
            addBook()
        }
    }

    private fun startGallery() {
        val galleryIntent = Intent(Intent.ACTION_GET_CONTENT)
        galleryIntent.type = "image/*"
        if (galleryIntent.resolveActivity(activity!!.packageManager) != null) {
            startActivityForResult(galleryIntent, 1000)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, i: Intent?) {
        super.onActivityResult(requestCode, resultCode, i)

        if (resultCode == Activity.RESULT_OK) {
            val uri: Uri? = i!!.data
            book_image_add_book_page.setImageURI(uri)
            manageImageFromUri(i.data!!)

        } else {
            Toast.makeText(activity, "Error", Toast.LENGTH_LONG).show()
        }
    }

    private fun manageImageFromUri(imageUri: Uri) {
        val baos = ByteArrayOutputStream()
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
//            Snackbar.make(view!!, "ERROR", Snackbar.LENGTH_LONG)
//        } else {
        bitmap = MediaStore.Images.Media.getBitmap(activity?.contentResolver, imageUri)
        bitmap!!.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val b = baos.toByteArray()
        encodImgb = Base64.encodeToString(b, Base64.DEFAULT)
//        }
    }

    private fun addBook() {
        val url = "https://library123456.000webhostapp.com/AddBooks.php"
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
                        Snackbar.make(view!!, "تم", Snackbar.LENGTH_LONG).setAction("UNDO", null).show()
                        remove()
                        add_book_pb.visibility = View.GONE
                    } else {
                        Snackbar.make(view!!, "لم يتم !", Snackbar.LENGTH_LONG)
                            .setAction("UNDO", null).show()
                        add_book_pb.visibility = View.GONE
                    }

                } catch (e: Exception) {
                    Toast.makeText(activity, e.message, Toast.LENGTH_LONG).show()
                    add_book_pb.visibility = View.GONE
                }
            },
            Response.ErrorListener {
                Toast.makeText(activity, it.message, Toast.LENGTH_LONG).show()
                add_book_pb.visibility = View.GONE
            }
        ) {
            override fun getParams(): Map<String, String> {
                //Creating HashMap
                val params = HashMap<String, String>()
                params["book_image"] = encodImgb
                params["book_name"] = add_book_name?.text.toString()
                params["book_author"] = add_book_author?.text.toString()
                params["book_description"] = add_book_desc?.text.toString()
                params["book_category_id"] = cat
                params["book_uni_id"] = lib
                params["book_status"] = add_book_status?.text.toString()
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
    private fun remove(){
        add_book_name?.text?.clear()
        add_book_author?.text?.clear()
        add_book_desc?.text?.clear()
    }
}