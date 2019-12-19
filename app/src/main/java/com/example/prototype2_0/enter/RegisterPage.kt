package com.example.prototype2_0.enter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request.Method.POST
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.prototype2_0.R
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.register_layout.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream

class RegisterPage :Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.register_layout, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        reg_pb.visibility = View.GONE

        reg_btn.setOnClickListener {
            reg_pb.visibility = View.VISIBLE
            checkinfo()
        }
    }

    private fun checkinfo(){
        if (user_name_register_page.text.toString().trim().isNotEmpty()){
            if (isValidEmail(user_email_register_page.text.toString())){
                if(user_password_register_page.text.toString().length>6 ){
                     if(confirm_user_password_register_page.text.toString() == user_password_register_page.text.toString()){
                        registerRequest()
                    }
                    else{confirm_user_password_register_page.error="كلمة المرور غير متطابقة"
                         reg_pb.visibility = View.GONE
                     }
                }
                else{user_password_register_page.error="كلمة المرور قصيرة"
                    reg_pb.visibility = View.GONE }
                }
            else{        user_email_register_page.error="البريد مطلوب"
                reg_pb.visibility = View.GONE
            }
        }
        else {        user_name_register_page.error="الإسم مطلوب"
            reg_pb.visibility = View.GONE
        }
    }

    private fun isValidEmail(email: String): Boolean {
    val pattern = Patterns.EMAIL_ADDRESS
    return pattern.matcher(email).matches()
}

    private fun registerRequest() {

        val url = "https://library123456.000webhostapp.com/RegisterPage.php"
        val queue = Volley.newRequestQueue(activity)
        val postRequest = object : StringRequest(
            POST, url, Response.Listener<String>
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

                        Snackbar.make(view!!, "تم التسجيل", Snackbar.LENGTH_LONG).setAction("UNDO", null).show()
                        remove()
                        reg_pb.visibility = View.GONE
                    } else {
                        Snackbar.make(view!!, "لم يتم التسجيل !", Snackbar.LENGTH_LONG)
                            .setAction("UNDO", null).show()
                        reg_pb.visibility = View.GONE
                    }

                } catch (e: Exception) {
                    Toast.makeText(activity, e.message, Toast.LENGTH_LONG).show()
                    reg_pb.visibility = View.GONE
                }
            },
            Response.ErrorListener {
                Toast.makeText(activity, it.message, Toast.LENGTH_LONG).show()
                reg_pb.visibility = View.GONE
            }
        ) {
            override fun getParams(): Map<String, String> {
                //Creating HashMap
                val params = HashMap<String, String>()
                params["user_name"] = user_name_register_page?.text.toString()
                params["user_email"] = user_email_register_page?.text.toString()
                params["user_un"] = user_unnum_register_page?.text.toString()
                params["user_pass"] = user_password_register_page?.text.toString()
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

    private fun remove() {
        user_name_register_page?.text?.clear()
        user_email_register_page?.text?.clear()
        user_unnum_register_page?.text?.clear()
        user_password_register_page?.text?.clear()
        confirm_user_password_register_page?.text?.clear()
    }

    fun View.hideKeyboard() {
        val imm =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }

}