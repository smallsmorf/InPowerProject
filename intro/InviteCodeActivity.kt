package com.inpower.webguruz.intro

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.Window
import android.view.WindowManager
import androidx.core.content.ContextCompat
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.exception.ApolloException
import com.example.graphql.createGroup.MyGroupsQuery
import com.google.gson.Gson
import com.inpower.webguruz.R
import com.inpower.webguruz.utilMethods.UtilFunctions
import com.inpower.webguruz.customviews.CustomOtpView
import com.inpower.webguruz.home.HomeActivity
import com.inpower.webguruz.utilMethods.ApplicationConstant
import com.inpower.webguruz.utilMethods.CustomLoader
import okhttp3.OkHttpClient
import java.security.KeyStore
import java.security.SecureRandom
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager

//activity to invite code
class InviteCodeActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.itstime_layout)

        initView()
        setOtp(this)
    }


    //function to send otp
    private fun setOtp(context: Activity) {
        var custom_invitecode = findViewById(R.id.firstPinView3) as CustomOtpView
        custom_invitecode.itemWidth = getResources().getDimensionPixelSize(R.dimen._32sdp)
        custom_invitecode.itemSpacing = getResources().getDimensionPixelSize(R.dimen._8sdp)
        custom_invitecode.itemHeight = getResources().getDimensionPixelSize(R.dimen._32sdp)
        custom_invitecode.setAnimationEnable(true)// start animation when adding text
        custom_invitecode.setCursorVisible(true)
        custom_invitecode.itemCount = 6
        custom_invitecode.setAnimationEnable(true)// start animation when adding text
        custom_invitecode.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                custom_invitecode.setTypeface(custom_invitecode.getTypeface(), Typeface.BOLD_ITALIC)
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                custom_invitecode.setTypeface(custom_invitecode.getTypeface(), Typeface.BOLD_ITALIC)
                if (s.toString().length == 6) {
                    //UtilFunctions.INSTANCE.getAllGroups(this@InviteCodeActivity)
                    getMyGroups(this@InviteCodeActivity)
                }
            }
            override fun afterTextChanged(s: Editable?) {
                custom_invitecode.setTypeface(custom_invitecode.getTypeface(), Typeface.BOLD_ITALIC)
            }
        })

    }


    private fun initView() {

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
        val apolloClient = ApolloClient.builder().serverUrl(BASE_URL).okHttpClient(UtilFunctions.INSTANCE.getUnsafeOkHttpClient()!!).build()
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
                        UtilFunctions.INSTANCE.getAllGroups(context)
                        val intent = Intent(context, IntroGroupActivity::class.java)
                        startActivity(intent)
                        overridePendingTransition(R.anim.fade_in,R.anim.fade_out)
                    }


                }
            }
        })
    }



}
