package com.inpower.webguruz.intro

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.gdacciaro.iOSDialog.iOSDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson
import com.hbb20.CountryCodePicker
import com.inpower.webguruz.R
import com.inpower.webguruz.home.HomeActivity
import com.inpower.webguruz.utilMethods.ApplicationConstant
import com.inpower.webguruz.utilMethods.CustomLoader
import com.inpower.webguruz.utilMethods.UtilFunctions
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*

//activity to signup user
class SignUpActivity : Activity() {

    lateinit var btn_continue: TextView
    lateinit var et_fullname: TextInputEditText
    lateinit var et_phone: TextInputEditText
    lateinit var tv_login:TextView
    lateinit var tv_signup:TextView
//    lateinit var tv_or:TextView
    lateinit var tv_message:TextView
//    lateinit var country_code: CountryCodePicker
    private var requestQueue: RequestQueue? = null
    var sharedPreferences: SharedPreferences? = null
    var editor: SharedPreferences.Editor? = null
    lateinit var customLoader: CustomLoader
//    lateinit var rv_facebook: RelativeLayout
    lateinit var callbackManager: CallbackManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_newsignup)
        printHashKey(this)


        callbackManager = CallbackManager.Factory.create();

        sharedPreferences = this.getSharedPreferences("MySession", Context.MODE_PRIVATE)
        editor = sharedPreferences?.edit()
        requestQueue = Volley.newRequestQueue(this)
        customLoader = CustomLoader(this, R.style.CustomLoaderTheme)

        initView()
        listeners()
    }

    //function of initialisation of view
    private fun initView() {
        btn_continue = findViewById(R.id.btn_continue)
//        country_code = findViewById(R.id.country_code)
//        rv_facebook = findViewById(R.id.rv_facebook)
        tv_login=findViewById(R.id.tv_login)
        tv_signup=findViewById(R.id.tv_signup)
        tv_message=findViewById(R.id.tv_message)
//        tv_or=findViewById(R.id.tv_or)
        et_phone = findViewById(R.id.et_phone)
        et_fullname = findViewById(R.id.et_fullname)
        btn_continue.setEnabled(false);
        btn_continue.background = resources.getDrawable(R.drawable.register_background_drawable);


    }

    //function of listeners
    private fun listeners() {
        et_fullname.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if(et_fullname.text.toString().length>4 && et_phone.text.toString().length>9){
                    btn_continue.setEnabled(true);
                    btn_continue.setTextColor(getResources().getColor(R.color.white));
                    btn_continue.background = resources.getDrawable(R.drawable.register_background_drawable_active);
                }
                else{
                    btn_continue.setEnabled(false);
                    btn_continue.background = resources.getDrawable(R.drawable.register_background_drawable);
                }
            }
        })

        et_phone.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if(et_fullname.text.toString().length>4 && et_phone.text.toString().length>9){
                    btn_continue.setEnabled(true);
                    btn_continue.setTextColor(getResources().getColor(R.color.white));
                    btn_continue.background = resources.getDrawable(R.drawable.register_background_drawable_active);

                }
                else{
                    btn_continue.setEnabled(false);
                    btn_continue.background = resources.getDrawable(R.drawable.register_background_drawable);
                }
            }
        })

        btn_continue.setOnClickListener(View.OnClickListener {
//            Log.e("countryCode","+"+country_code.selectedCountryCode+et_phone.text.toString());
            //UtilFunctions.INSTANCE.fireBaseCreateUser(this,"+"+country_Cide.selectedCountryCode+et_phone.text.toString().plus("@gmail.com"),ApplicationConstant.INSTANCE.firebasePassword)
            if(et_fullname.text.toString().length<4){
                UtilFunctions.INSTANCE.errorToast(this,"Name is too short")
            }
            else if(et_phone.text.toString().length<10){
                UtilFunctions.INSTANCE.errorToast(this,"Invalid mobile number")
            }
            else{
                if (UtilFunctions.INSTANCE.isNetworkAvailable(this)) {
                    registrationFlow(this)
                } else {
                    UtilFunctions.INSTANCE.errorToast(this, ApplicationConstant.INSTANCE.networkConnectionError)
                }

            }


        })

//        rv_facebook.setOnClickListener(View.OnClickListener {
//            if (UtilFunctions.INSTANCE.isNetworkAvailable(this)) {
//                facebookLogin()
//            } else {
//                UtilFunctions.INSTANCE.errorToast(this, ApplicationConstant.INSTANCE.networkConnectionError)
//            }
//
//        })

        tv_login.setOnClickListener(View.OnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
            finish()
        })
    }

    //function to register
    private fun registrationFlow(context: Activity) {
        customLoader.show()
        val url = ApplicationConstant.INSTANCE.BASEURLLOGIN
        registration(url, "POST" )

        // for testing

//        val intent = Intent(this, HomeActivity::class.java)
//        startActivity(intent)

//        val intent = Intent(context, MyPronounsActivity::class.java)
//        context.startActivity(intent)

    }

    private fun registration(url: String, method: String) {
        var methodRequest = 0
        if (method.equals("POST")) {
            methodRequest = Request.Method.POST
        } else {
            Request.Method.GET
        }
        val params = JSONObject()
        var i = 0
//        params.put("phone","+"+country_code.selectedCountryCode+et_phone.text.toString())

        Log.e("registrationArray", Gson().toJson(params));

        var request = JsonObjectRequest(methodRequest, url, params, { response ->
                      Log.e("registrationResponse",response.toString())
                    try {

                        Log.d("singuptest","signres" +response.toString() )
                        Log.d("signuptest","testsignx" +Gson().toJson(response) )
                        val rescode = response.get("phone").toString()
                        Log.d("signuptestcode","testx :" + rescode )

                        if (rescode.length>9){

                            val intent = Intent(this, VerifyNumberActivity::class.java)
//                            intent.putExtra("mobileno", "+"+country_code.selectedCountryCode+et_phone.text.toString())
                            intent.putExtra("screen", "signUp")
                            intent.putExtra("Fullname", et_fullname.text.toString())
                            startActivity(intent)

                        }else{
                            UtilFunctions.INSTANCE.errorToast(this, "Something went wrong")
                        }

//                        val authToken = response.getJSONObject("identity").get("id").toString()
//                        val session_token = response.get("session_token").toString();
//                        editor?.putString("session_token", session_token)?.apply()
//                        editor?.putString("authToken", authToken)?.apply()
//                        editor?.putString("Phone","+"+country_code.selectedCountryCode + et_phone.text.toString())?.apply()
//                        editor?.putString("Fullname", et_fullname.text.toString())?.apply()

                       // UtilFunctions.INSTANCE.sendOtp(this, "+"+country_code.selectedCountryCode +et_phone.text.toString(), "signUp")
                    }
                    catch (e: Exception) {
                        e.printStackTrace()
                        if (customLoader.isShowing()) customLoader.dismiss()
                    }
                },

            { error ->
                if (customLoader.isShowing()) customLoader.dismiss()
                Log.e("registrationErrorResponse",error.toString())
              //  showAlreadyUserAlert(this)
                Log.d("signuperrorres"," :" +  error.toString())
                UtilFunctions.INSTANCE.errorToast(this, "Something went wrong")
            }
            )
        requestQueue?.add(request)
        requestQueue!!.addRequestFinishedListener<Any> { requestQueue!!.cache.clear() }

    }

    //function to show dialof
    private fun showAlreadyUserAlert(activity: Activity){
        var d = iOSDialogBuilder(activity);
        d.setTitle("SignUp Failed")
            .setSubtitle("The give contact number is already registered.")
            .setBoldPositiveLabel(false)
            .setCancelable(false)
            .setPositiveListener("Login") { dialog ->
                val intent = Intent(activity, LoginActivity::class.java)
                startActivity(intent)
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
                finish()
                dialog.dismiss()
            }
           .setNegativeListener("Cancel") { dialog -> dialog.dismiss() }
            .build().show()
    }

    private fun printHashKey(pContext: Context) {
        try {
            val info = pContext.packageManager.getPackageInfo(
                pContext.packageName,
                PackageManager.GET_SIGNATURES
            )
            for (signature in info.signatures) {
                val md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                val hashKey = String(Base64.encode(md.digest(), 0))
                Log.i(TAG, "printHashKey() Hash Key: $hashKey")
            }
        } catch (e: NoSuchAlgorithmException) {
            Log.e(TAG, "printHashKey()", e)
        } catch (e: Exception) {
            Log.e(TAG, "printHashKey()", e)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager.onActivityResult(requestCode, resultCode, data)
        Log.e("result", requestCode?.toString())
    }

    //function to login in facebook
    private fun facebookLogin() {
        val customLoader = CustomLoader(this, R.style.CustomLoaderTheme)
        customLoader.show()

        //LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("user_photos", "email", "public_profile", "user_posts"))
        LoginManager.getInstance().logInWithPublishPermissions(this, Arrays.asList("publish_actions"))
        LoginManager.getInstance().registerCallback(callbackManager, object :
            FacebookCallback<LoginResult?> {
                override fun onCancel() {
                    customLoader.dismiss()
//                    rv_facebook.isEnabled=true
                }
                override fun onError(exception: FacebookException) {
                    customLoader.dismiss()
//                    rv_facebook.isEnabled=true
                }
                override fun onSuccess(result: LoginResult?) {
                    customLoader.dismiss()
                    Log.e("resultonSuccess", result?.accessToken?.token!!)
                    val request = GraphRequest.newMeRequest(result?.accessToken) { `object`, response ->
                        et_fullname.setText(`object`?.getString("name").toString())
                        tv_signup.setText(R.string.Phone)
                        tv_message.setText(R.string.Please_verify)
//                        tv_or.visibility=View.GONE
//                        rv_facebook.visibility=View.GONE
                    }
                    val parameters = Bundle()
                    parameters.putString("fields", "id,name,email,gender, birthday")
                    request.parameters = parameters
                    request.executeAsync()
                }
            })
    }
}