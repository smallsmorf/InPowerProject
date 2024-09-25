package com.inpower.webguruz.intro

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.inpower.webguruz.home.HomeActivity
import com.inpower.webguruz.R
import com.inpower.webguruz.utilMethods.ApplicationConstant
import com.inpower.webguruz.utilMethods.UtilFunctions

//activity to load welcome page
class WelcomeInPowerActivity: Activity() {

   lateinit var rl_letgo : TextView
    lateinit var sharedPreferences:SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.welcome_inpower)

        UtilFunctions.INSTANCE.checkAndRequestPermissions(this)


        sharedPreferences =getSharedPreferences("MySession", Context.MODE_PRIVATE)

        val userId = sharedPreferences.getString("MyUserId", "").toString()
        UtilFunctions.INSTANCE.getFirebaseToken(this)
        UtilFunctions.INSTANCE.firebaseSignIn(this,userId+"@gmail.com", ApplicationConstant.INSTANCE.firebasePassword)


        initview()
        listeners()

    }

    //function of listeners
    private fun listeners() {
        rl_letgo.setOnClickListener(View.OnClickListener {

            var editor = sharedPreferences?.edit()
            editor?.putBoolean("isLogin",true)?.apply()

            UtilFunctions.INSTANCE.getAllGroups(this);
            UtilFunctions.INSTANCE.getMyGroups(this);

            val intent = Intent(this, HomeActivity::class.java)
            intent.putExtra("key", "");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
            finish()
        })
    }
    //initialisation of views
    private fun initview() {
        rl_letgo=findViewById(R.id.rl_letgo)
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

}