package com.example.prototype2_0.data

import android.app.Activity
import android.content.Context
import android.widget.Toast
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import org.json.JSONObject

class DatasourceHelper(val activity: Activity) {
    public fun checkFbUser(mail:String): Boolean {

        var user_email = ""
        var user_pass = ""
        val params = HashMap<String, String>()

        val url = "https://library123456.000webhostapp.com/LoginPage.php"
        val queue = Volley.newRequestQueue(activity)
        var exists = false
        val postRequest = object : StringRequest(
            Method.POST, url, Response.Listener<String>
            {
                // Getting Response from Server
                    response ->
                try {

                    val strResp = response.toString()
                    val jsonObj = JSONObject(strResp)
                    val jsonArray: JSONArray = jsonObj.getJSONArray("dishs")
                    var user_id = ""
                    var user_name = ""
                    var user_image = ""
                    var user_un = ""
                    var user_role = ""

                    var status: Any = ""
                    for (i in 0 until jsonArray.length()) {
                        val jsonInner: JSONObject = jsonArray.getJSONObject(i)
                        user_id = jsonInner.get("user_id").toString()
                        user_email = jsonInner.get("user_email").toString()
                        user_pass = jsonInner.get("user_pass").toString()
                        user_name = jsonInner.get("user_name").toString()
                        user_image = jsonInner.get("user_image").toString()
                        user_un = jsonInner.get("user_un").toString()
                        user_role = jsonInner.get("user_role").toString()

                        status = jsonInner.get("status")
                    }
                    if (status == "ok") {

                        val sharedPreference = activity!!.getSharedPreferences(
                            "myPrefs",
                            Context.MODE_PRIVATE
                        )
                        val editor = sharedPreference.edit()
                        editor.putString("user_id", user_id)
                        editor.putString("user_email", user_email)
                        editor.putString("user_pass", user_pass)
                        editor.putString("user_name", user_name)
                        editor.putString("user_image", user_image)
                        editor.putString("user_un", user_un)
                        editor.putString("user_role", user_role)
                        editor.clear()
                        editor.apply()
                        exists = true

//                Toast.makeText(activity, "ok", Toast.LENGTH_LONG).show()
                    } else {
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
                val params = HashMap<String, String>()
                params["user_email"] = mail
                params["user_pass"] = mail
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
        return exists
    }
}