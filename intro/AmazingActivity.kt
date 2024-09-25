package com.inpower.webguruz.intro

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.inpower.webguruz.R


//actvity to laod amazing page
class AmazingActivity : Activity() {

    lateinit var  tv_login : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_amazing)
        initView()
        listeners()

    }

    //function of initialisation
    private fun initView() {
        tv_login=findViewById(R.id.tv_login)
    }

    //function of listeners
    private fun listeners() {
        tv_login.setOnClickListener(View.OnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
            finish()
        })
    }



}