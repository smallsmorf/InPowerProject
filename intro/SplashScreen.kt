package com.inpower.webguruz.intro

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.inpower.webguruz.home.HomeActivity
import com.inpower.webguruz.R
import com.inpower.webguruz.utilMethods.UtilFunctions

//activity to show splash screen
class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splashscreen)

        initView()
    }

    // function of initialisation
//    private fun initView() {
//        var sharedPreferences = getSharedPreferences("MySession", Context.MODE_PRIVATE)
//        Handler().postDelayed({
//            if (!sharedPreferences.getBoolean("isLogin", false)) {
//                val intent = Intent(this, HelloBeautyActivity::class.java)
//                startActivity(intent)
//                overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
//            }
//            else if(sharedPreferences.getBoolean("isLogin", false)) {
//                UtilFunctions.INSTANCE.getMyProfile(this,sharedPreferences.getString("MyUserId","").toString(),"Login")
//                val intent = Intent(this, HomeActivity::class.java)
//                intent.putExtra("key", "");
//                startActivity(intent)
//                overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
//                finish()
//            }
//        }, 3000)
//
//    }

    private fun initView() {
        val sharedPreferences = getSharedPreferences("MySession", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean("isLogin", true)
        editor.putString("MyUserId", "dummy_user_id")
        editor.apply()

        Handler().postDelayed({
            val intent = Intent(this, WelcomeDiscoverActivity::class.java)
            intent.putExtra("key", "")
            startActivity(intent)
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
            finish()
        }, 3000)
    }
}