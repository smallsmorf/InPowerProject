package com.inpower.webguruz.intro

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.inpower.webguruz.R

//activity to load hellow buties
class HelloBeautyActivity : Activity() {

    lateinit var tv_login: TextView
    lateinit var rl_signup: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hellobeautiues)

        initView()
        listeners()
    }

    //function to initialisation
    private fun initView() {
        rl_signup = findViewById(R.id.rl_signup)
        tv_login = findViewById(R.id.tv_login)

    }

    //function of click listeners
    private fun listeners() {
        tv_login.setOnClickListener(View.OnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        })
        rl_signup.setOnClickListener(View.OnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        })
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        finish()
    }

}