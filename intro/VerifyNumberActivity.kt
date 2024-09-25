package com.inpower.webguruz.intro

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Typeface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.exception.ApolloException
import com.example.graphql.createGroup.MyGroupsQuery
import com.google.gson.Gson
import com.inpower.webguruz.R
import com.inpower.webguruz.utilMethods.*
import com.inpower.webguruz.customviews.CustomOtpView
import okhttp3.OkHttpClient
import java.security.KeyStore
import java.security.SecureRandom
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager

//activity to verify mobile mobile number
class VerifyNumberActivity : Activity() {
    var sharedPreferences: SharedPreferences? = null
    var editor: SharedPreferences.Editor? = null
    lateinit var tv_nocode: TextView
    lateinit var tv_try: TextView
    var screen: String = ""
    var mob: String = ""
    var code: String = ""
    var fullname: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verfiyphonenumber)

        sharedPreferences = this.getSharedPreferences("MySession", Context.MODE_PRIVATE)
        editor = sharedPreferences?.edit()
        initview()
        setOtp(this)
        screen=getIntent().getStringExtra("screen").toString()
        mob =getIntent().getStringExtra("mobileno").toString()
        fullname =getIntent().getStringExtra("Fullname").toString()



       Log.d("mobile","no :" + mob)
        if (screen == "Login") {
            UtilFunctions.INSTANCE.getAllGroups(this)

        }

        listeners()
    }

    //function of listeners
    private fun listeners() {
        tv_nocode.setOnClickListener(View.OnClickListener {
            customOtpView.setText("")
        })
        tv_try.setOnClickListener(View.OnClickListener {
            customOtpView.setText("")
       })
    }

    lateinit var customOtpView:CustomOtpView

    //function to send otp
    private fun initviews() {

        UtilFunctions.INSTANCE.verifyOtps(this, code.toString(),mob,screen)
        UtilFunctions.INSTANCE.getCountryModel(this,screen)

        UtilFunctions.INSTANCE.getCountries(this,screen)
//        if (screen == "Login") {
//            getMyGroups(this@VerifyNumberActivity)
//        }

        var sharedPreferencess :SharedPreferences= getSharedPreferences("MySession", Context.MODE_PRIVATE)
        var editor = sharedPreferencess.edit()
        editor?.putString("Fullname", fullname)?.apply()

    }
    private fun setOtp(context: Activity) {
        tv_nocode = findViewById(R.id.tv_nocode) as TextView
        tv_try = findViewById(R.id.tv_try) as TextView
        customOtpView = findViewById(R.id.firstPinView3) as CustomOtpView
        customOtpView.setTypeface(null, Typeface.BOLD)

        customOtpView.itemCount = 6
        customOtpView.typeface = Typeface.DEFAULT_BOLD
        customOtpView.itemWidth=getResources().getDimensionPixelSize(R.dimen._32sdp)
        customOtpView.itemSpacing=getResources().getDimensionPixelSize(R.dimen._8sdp)
        customOtpView.itemHeight=getResources().getDimensionPixelSize(R.dimen._32sdp)
        customOtpView.setCursorVisible(true)
        customOtpView.setAnimationEnable(true)// start animation when adding text
        customOtpView.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                customOtpView.setTypeface(customOtpView.getTypeface(), Typeface.BOLD_ITALIC)
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                customOtpView.setTypeface(customOtpView.getTypeface(), Typeface.BOLD_ITALIC)
                if (s.toString().length == 6) {
                    val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
                    code =s.toString()

                    initviews()


//                    if(s.toString()=="123456"){
//
//                        // if invite code not skip
//                       // getMyGroups(this@VerifyNumberActivity)
//                       // UtilFunctions.INSTANCE.verifyOtp(context, s.toString(), sharedPreferences?.getString("Phone","").toString(),screen)
//
//                       //skip invite code
//                        if (screen == "Login") {
//                            getMyGroups(this@VerifyNumberActivity)
//                        } else if(screen == "signUp"){
//                            val intent = Intent(context, MyPronounsActivity::class.java)
//                            context.startActivity(intent)
//                            context.overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
//                            context.finish()
//                        }
//                    }else{
//                        CustomToast("Incorrect Code")
//                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {
                customOtpView.setTypeface(customOtpView.getTypeface(), Typeface.BOLD_ITALIC)


            }
        })



    }

    //function to show custom toast
    fun CustomToast(message: String) {
        var toast = Toast(this)
        val inflater = LayoutInflater.from(this)
        val toastRoot: View = inflater.inflate(R.layout.customtoast, null)
        var custom_toast_message = toastRoot.findViewById<TextView>(R.id.custom_toast_message)
        toast.setView(toastRoot)
        custom_toast_message.text = message
        toast.setGravity(Gravity.CENTER_HORIZONTAL or Gravity.BOTTOM, 0, Math.round(resources.getDimension(R.dimen._100sdp)))
        val yOffset = Math.max(0,toast.yOffset-100)
        toast.setGravity(Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL, 0, yOffset)
        toast.duration = Toast.LENGTH_SHORT
        toast.show()
    }

    //function of initialisation of view
    private fun initview() {

//        if (intent!!.getStringExtra("mobileno").equals("login")) {
//            screen = intent!!.getStringExtra("screen")!!
//        } else {
//            screen = intent!!.getStringExtra("screen")!!
//        }
        val window: Window = getWindow()
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.white))
        }
        tv_nocode = findViewById(R.id.tv_nocode) as TextView
        tv_try = findViewById(R.id.tv_try) as TextView
        val customOtpView = findViewById(R.id.firstPinView3) as CustomOtpView
    }

    @Override
    override fun onBackPressed() {
    }

    override fun onKeyDown(key_code: Int, key_event: KeyEvent?): Boolean {
        if (key_code == KeyEvent.KEYCODE_BACK) {
            super.onKeyDown(key_code, key_event)
            return true
        }
        return false
    }

    // calling from splash screen, lets go,
    public fun getMyGroups(context: Activity) {
        val customLoader = CustomLoader(context, R.style.CustomLoaderTheme)
        customLoader.show()
        var sharedPreferences = context.getSharedPreferences("MySession", Context.MODE_PRIVATE)
        var editor = sharedPreferences?.edit()
        var getGroup = MyGroupsQuery(
            userId = sharedPreferences.getString("MyUserId", "").toString(),
            pageNo = 1,
            perPage = 100
        )

        val BASE_URL = ApplicationConstant.INSTANCE.BASEURLGROUP
        val okHttpClient = OkHttpClient.Builder().build()
        val apolloClient = ApolloClient.builder().serverUrl(BASE_URL).okHttpClient(getUnsafeOkHttpClient()!!).build()
        apolloClient.query(getGroup).enqueue(object : ApolloCall.Callback<MyGroupsQuery.Data>() {
            override fun onFailure(e: ApolloException) {
                if (customLoader.isShowing()) customLoader.dismiss()
                context.runOnUiThread(Runnable {
                    // UtilFunctions.INSTANCE.errorToast(context, e.toString())
                })
            }
            override fun onResponse(response: com.apollographql.apollo.api.Response<MyGroupsQuery.Data>) {
                if (customLoader.isShowing()) customLoader.dismiss()
                if (response.data != null) {
                    Log.e("myGroup", Gson().toJson(response.data))
                    editor?.putString("mygroups", Gson().toJson(response.data))?.apply()
                    editor?.putString("myGroupSize", Gson().toJson(response.data))?.apply()

                    if(response.data!!.myGroups?.list?.size!! > 2){
                        val intent = Intent(context, WelcomeInPowerActivity::class.java)
                        startActivity(intent)
                        overridePendingTransition(R.anim.fade_in,R.anim.fade_out)
                    }
                    else{
                        val intent = Intent(context, IntroGroupActivity::class.java)
                        startActivity(intent)
                        overridePendingTransition(R.anim.fade_in,R.anim.fade_out)
                    }


                }
            }
        })
    }



    public fun getUnsafeOkHttpClient(): OkHttpClient? {
        return try {
            // Create a trust manager that does not validate certificate chains
            val trustAllCerts = arrayOf<TrustManager>(
                object :
                    X509TrustManager { override fun checkClientTrusted(chain: Array<out java.security.cert.X509Certificate>?, authType: String?) {
                    // TODO("Not yet implemented")
                }
                    override fun checkServerTrusted(chain: Array<out java.security.cert.X509Certificate>?, authType: String?) {
                        // TODO("Not yet implemented")
                    }
                    override fun getAcceptedIssuers(): Array<out java.security.cert.X509Certificate>? {
                        return arrayOf()
                    }
                }
            )

            // Install the all-trusting trust manager
            val sslContext = SSLContext.getInstance("SSL")
            sslContext.init(null, trustAllCerts, SecureRandom())
            // Create an ssl socket factory with our all-trusting manager
            val sslSocketFactory = sslContext.socketFactory
            val trustManagerFactory: TrustManagerFactory =
                TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
            trustManagerFactory.init(null as KeyStore?)
            val trustManagers: Array<TrustManager> =
                trustManagerFactory.trustManagers
            check(!(trustManagers.size != 1 || trustManagers[0] !is X509TrustManager)) {
                "Unexpected default trust managers:" + trustManagers.contentToString()
            }

            val trustManager =
                trustManagers[0] as X509TrustManager
            val builder = OkHttpClient.Builder()
            builder.sslSocketFactory(sslSocketFactory, trustManager)
            builder.hostnameVerifier(HostnameVerifier { _, _ -> true })
            builder.build()
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

}