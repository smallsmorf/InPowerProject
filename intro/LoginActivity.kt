package com.inpower.webguruz.intro

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.facebook.*
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.hbb20.CountryCodePicker
import com.inpower.webguruz.R
import com.inpower.webguruz.utilMethods.ApplicationConstant
import com.inpower.webguruz.utilMethods.CustomLoader
import com.inpower.webguruz.utilMethods.UtilFunctions
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.*

//Activities of login
class LoginActivity : Activity() {

    lateinit var btn_continue : TextView
    lateinit var et_phone : TextInputEditText
    private var requestQueue: RequestQueue? = null
    lateinit var country_code: CountryCodePicker

    var sharedPreferences: SharedPreferences? = null
    var editor: SharedPreferences.Editor? = null
    lateinit var callbackManager: CallbackManager
    lateinit var rv_facebook: RelativeLayout
//    lateinit var tv_signup:TextView;
    lateinit var customLoader :CustomLoader

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_newlogin)

        callbackManager = CallbackManager.Factory.create();
        sharedPreferences = this.getSharedPreferences("MySession", Context.MODE_PRIVATE)
        editor = sharedPreferences?.edit()

        requestQueue = Volley.newRequestQueue(this)

        initView()
        listeners()

    }

    //function of initialisation of views
    private fun initView() {
        btn_continue=findViewById(R.id.btn_continue)
//        tv_signup = findViewById(R.id.tv_signup)
        et_phone=findViewById(R.id.et_phone)
        btn_continue.setEnabled(false);
        btn_continue.background = resources.getDrawable(R.drawable.register_background_drawable);

    }

    //function of listeners
    private fun listeners() {

        et_phone.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.isEmpty()) { et_phone.error = resources.getString(R.string.emptyfield) }
                else if (s.length < 10) {
                    btn_continue.setEnabled(false);
                    btn_continue.background = resources.getDrawable(R.drawable.register_background_drawable);
                } else {
                    btn_continue.setEnabled(true);
                    btn_continue.setTextColor(getResources().getColor(R.color.white));
                    btn_continue.background = resources.getDrawable(R.drawable.register_background_drawable_active);
                }
            }
        })

        btn_continue.setOnClickListener(View.OnClickListener {
            if(UtilFunctions.INSTANCE.isNetworkAvailable(this)){
                loginFlow(this)
            }
            else{ UtilFunctions.INSTANCE.errorToast(this, ApplicationConstant.INSTANCE.BASEURL) }

        })

////        tv_signup.setOnClickListener(View.OnClickListener {
//            val intent = Intent(this, SignUpActivity::class.java)
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
//            startActivity(intent)
//            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
//            finish()
//        })

    }


    //function of login flow
    private fun loginFlow(context: Activity) {
        customLoader = CustomLoader(context, R.style.CustomLoaderTheme)
        customLoader.show()

        val url = ApplicationConstant.INSTANCE.BASEURLLOGIN
        userLogin(url, "POST")
    }

    //function of user login
    private fun userLogin(url: String, method: String) {
        var methodRequest = 0
        if (method.equals("POST")) { methodRequest = Request.Method.POST }
        else { Request.Method.GET }

        val params = JSONObject()
        val jsonObject = JsonObject();
    //    val dataModel :DataModel = DataModel(phone = "+"+country_code.selectedCountryCode+et_phone.text.toString());
      //  params.put(fieldObject.get("phone").toString(), "+"+country_code.selectedCountryCode+et_phone.text.toString())
        params.put("phone","+"+country_code.selectedCountryCode+et_phone.text.toString())
        var i = 0

       // Log.e("loginArray", Gson().toJson(fields));
       //  Log.d("logintest","test" +Gson().toJson(fields) )
        val urllogin = ApplicationConstant.INSTANCE.BASEURLLOGIN
        val request = JsonObjectRequest(methodRequest, urllogin, params, { response ->
                     try {
                         Log.d("logintest","loginres" +response.toString() )
                         Log.d("logintest","testx" +Gson().toJson(response) )
                         val rescode = response.get("phone").toString()
                         Log.d("logintestcode","testx :" + rescode )
                         if (rescode.length>9){
                             val intent = Intent(this, VerifyNumberActivity::class.java)
                             intent.putExtra("mobileno", "+"+country_code.selectedCountryCode+et_phone.text.toString())
                             intent.putExtra("screen", "Login")
                             startActivity(intent)
                         }else{
                             UtilFunctions.INSTANCE.errorToast(this, "Something went wrong")
                         }
                        val id = response.getJSONObject("session").get("id").toString()
                        val authToken = response.getJSONObject("session").getJSONObject("identity").get("id").toString()
                        val username = response.getJSONObject("session").getJSONObject("identity").getJSONObject("traits").get("username").toString()
                        val schema_url = response.getJSONObject("session").getJSONObject("identity").get("schema_url").toString()
                        val MySession_token = response.get("session_token").toString();

                        editor?.putString("session_token", MySession_token)?.apply()
                        editor?.putString("authToken", authToken)?.apply()
                        editor?.putString("schema_url", schema_url.toString())?.apply()

                         // flow request
                      //  UtilFunctions.INSTANCE.getMyProfile(this,"","Login")


                    } catch (e: Exception) {
                         if (customLoader.isShowing()) customLoader.dismiss()
                         e.printStackTrace()
                    }
                },
            { error ->
                if (customLoader.isShowing()) customLoader.dismiss()
              //  UtilFunctions.INSTANCE.errorToast(this,"User does not exist")
                UtilFunctions.INSTANCE.errorToast(this,"Something went wrong")
                    Log.e("loginError", error.toString())
            })
        requestQueue?.add(request)
        requestQueue!!.addRequestFinishedListener<Any> { requestQueue!!.cache.clear() }

    }


    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        finish()
    }


}