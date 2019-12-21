package com.example.prototype2_0.mainActivity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.prototype2_0.HomeActivity
import com.example.prototype2_0.R
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.app_bar_home.*
import kotlinx.android.synthetic.main.edit_profile.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream

class EditProfile : Fragment() {
    var encodImg = ""
    var bitmap: Bitmap? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.edit_profile, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (activity as HomeActivity).home_page_search_bar.visibility = View.GONE

        loadInfo()

        change_photo.setOnClickListener { startGallery() }

        save_btn.setOnClickListener {
            checkInfo()
        }
    }

    private fun loadInfo() {
        val sharedPreference = activity!!.getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        val user_id = sharedPreference.getString("user_id", "")
        val userName = sharedPreference.getString("user_name", "")
        val userImage = sharedPreference.getString("user_image", "")
        val decodedImage = sharedPreference.getString("user_image", "")
        val picasso = sharedPreference.getString("picasso", "")
        val userUn = sharedPreference.getString("user_un", "")
        val userPass = sharedPreference.getString("user_pass", "")

        activity?.runOnUiThread {
            replaceText(user_name_edit_page, userName)
            replaceText(unnum_edit_page, userUn)
            replaceText(pass_edit_page, userPass)
            replaceText(confirm_pass_edit_page, userPass)
            if (picasso?.isNotEmpty()!!) {
                Picasso.get().load(picasso)
                    .into(change_profile_photo)
            }
            val navigationView: NavigationView = activity!!.findViewById(R.id.nav_view)
            val headerView: View = navigationView.getHeaderView(0)
            val navUserName: TextView = headerView.findViewById(R.id.user_name_header)
            val navUserUn: TextView = headerView.findViewById(R.id.user_un_header)
            navUserName.text = user_name_edit_page?.text.toString()
            navUserUn.text = unnum_edit_page.text.toString()
        }
    }

    private fun replaceText(editText: TextInputEditText, value: String?) {
        editText.text?.apply {
            clear()
            append(value)
        }
    }

    private fun checkInfo() {
        if (pass_edit_page.text.toString() == confirm_pass_edit_page.text.toString()) {
            Toast.makeText(activity, "OK", Toast.LENGTH_LONG).show()
            updatePhoto()
        } else {
            pass_edit_page.error = "Passwords don't match"
            confirm_pass_edit_page.error = "Passwords don't match"
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
            val sharedPreference = activity!!.getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
            val uri: Uri? = i!!.data
            change_profile_photo.setImageURI(uri)
            sharedPreference.edit().apply {
                putString("picasso", uri.toString())
                apply()
            }
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
        encodImg = Base64.encodeToString(b, Base64.DEFAULT)
//        }
    }

    private fun updatePhoto() {
        val url = "https://library123456.000webhostapp.com/EditProfile.php"
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
                        Snackbar.make(view!!, "تم", Snackbar.LENGTH_LONG).show()

                        val sharedPreference =
                            activity!!.getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
                        val picasso = sharedPreference.getString("picasso", null)

                        val navigationView: NavigationView = activity!!.findViewById(R.id.nav_view)
                        val headerView: View = navigationView.getHeaderView(0)
                        val navUserImage: CircleImageView =
                            headerView.findViewById(R.id.profile_photo_nav)
                        if (picasso?.isNotBlank()!!) {
                            val loaded = Picasso.get().load(picasso)
                            loaded.into(navUserImage)
                        }
                        loadInfo()
                    } else {
                        Snackbar.make(view!!, "لم يتم التسجيل !", Snackbar.LENGTH_LONG)
                            .setAction("UNDO", null).show()
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
            val user_id = sharedPreference.getString("user_id", "")
            val userName = sharedPreference.getString("user_name", "")
            val userImage = sharedPreference.getString("user_image", "")
            val decodedImage = sharedPreference.getString("user_image", "")
            val userUn = sharedPreference.getString("user_un", "")
            val userPass = sharedPreference.getString("user_pass", "")

            override fun getParams(): Map<String, String> {
                //Creating HashMap
                val params = HashMap<String, String>()
                params["user_id"] = user_id.toString()
                val editor = sharedPreference.edit()

                if (user_name_edit_page?.text!!.isNotEmpty()) {
                    params["user_name"] = user_name_edit_page?.text.toString()
                    editor.putString("user_name", user_name_edit_page?.text.toString())
                } else {
                    params["user_name"] = userName.toString()
                }

                if (unnum_edit_page?.text!!.isNotEmpty()) {
                    params["user_un"] = unnum_edit_page?.text.toString()
                    editor.putString("user_un", unnum_edit_page?.text.toString())
                } else {
                    params["user_un"] = userUn.toString()
                }

                if (pass_edit_page?.text!!.isNotEmpty()) {
                    params["user_pass"] = pass_edit_page?.text.toString()
                    editor.putString("user_pass", pass_edit_page?.text.toString())

                } else {
                    params["user_pass"] = userPass.toString()
                }

                if (bitmap == null) {
                    params["user_image"] = decodedImage!!
                } else {
                    params["user_image"] = encodImg

                    val sharedPreference = activity!!.getSharedPreferences(
                        "myPrefs",
                        Context.MODE_PRIVATE
                    )
                    editor.putString("user_image", encodImg)
                }
                editor.putString("decoded_image", userImage)
                editor.apply()
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