package com.example.basic_nice_lib

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import org.json.JSONArray
import org.json.JSONObject
import javax.security.auth.callback.Callback



class Survey_Library {
//   init {
//        System.loadLibrary("keys")
//        }
    fun callToInitiate(context : Context, user_info:JSONObject)
    {
        initiate(context,user_info)
    }
    @SuppressLint("RestrictedApi")
    private fun initiate(context: Context, user_info: JSONObject){

            //external fun getconvAPI():String
        //call "https:/<NPX_HOST>/npxapi/conversation/v1.0/initiate" api here with a post request

        // VOLLEY GET API CODE
        // --- Volley Code for initiate API Starts

        val url="https://qa15app.3gqa.satmetrix.com/npxapi/conversation/v1.0/initiate?selectedLanguage=en_US"
       // val url = Base64.decode(getconvAPI(), Base64.DEFAULT).toString()
        val jsonObjectRequest =object : JsonObjectRequest(
            Method.POST, url, user_info,
            { response ->

                Log.d("response1",response.toString())
                var intent = Intent(context, SurveyActivity::class.java)
                intent.putExtra("json_response", response.toString())

                val b = Bundle()
                b.putString("json_response", response.toString())
                intent.putExtra("json_bundle", b)
                startActivity(context, intent, b)

            },
            { error ->
                Log.d("Error:", error.toString())
                //TODO: Handle error
            }
        )
        {    @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String>
            {
                val headers = HashMap<String, String>()
                headers["Authorization"] = "Basic" 
                headers["tenantId"] = "JHQIOPU"
                return headers
            }

        }

        Volley.newRequestQueue(context).add(jsonObjectRequest)




    }

    }
