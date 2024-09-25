package com.inpower.webguruz.intro

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.inpower.webguruz.R

//activity of showing selfie approval status
class SelfieApproveStatus : AppCompatActivity() {
    lateinit var  tv_message : TextView
    lateinit var  tv_action_message : TextView
    lateinit var  tv_action : TextView
    lateinit var  tv_reason: TextView
    lateinit var sharedPreferences: SharedPreferences
    lateinit var editor: SharedPreferences.Editor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_selfie_approve_status)

        sharedPreferences =getSharedPreferences("MySession", Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()

        initView()
    }

    //function of initialstion of views
    private fun initView() {
        tv_message=findViewById(R.id.tv_message)
        tv_action_message=findViewById(R.id.tv_action_message)
        tv_action=findViewById(R.id.tv_action)
        tv_reason=findViewById(R.id.tv_reason)


        if(getIntent().getStringExtra("status").toString().equals("declined")){
            tv_message.text         =  getString(R.string.selfie_decline_msg)
            tv_action_message.text  =  getString(R.string.selfie_decline_action_msg)
            tv_action.text          =  getString(R.string.selfie_decline_action)

            tv_action.setOnClickListener(View.OnClickListener {
                val intent = Intent(this, SelfieTimeActivity::class.java)
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                intent.putExtra("screen","Edit")
                startActivity(intent)
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
                finish()
            })
        }
        else if(getIntent().getStringExtra("status").toString().equals("pending")){
            tv_message.text         =  getString(R.string.selfie_pending_msg)
            tv_action_message.text  =  getString(R.string.selfie_pending_action_msg)
            tv_action.text          =  getString(R.string.selfie_pending_action)

            tv_action.setOnClickListener(View.OnClickListener {
                val intent = Intent(this, LoginActivity::class.java)
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
                finish()
            })
        }
        else if(getIntent().getStringExtra("status").toString().contains("suspect") || getIntent().getStringExtra("status").toString().contains("suspend")){
            var reason=getIntent().getStringExtra("status").toString()
            tv_message.text         =  getString(R.string.selfie_suspect_msg)
            tv_action_message.text  =  getString(R.string.selfie_suspect_action_msg)
            tv_action.text          =  getString(R.string.selfie_suspect_action)
            tv_reason.text           =reason
            tv_reason.visibility=View.VISIBLE

            tv_action.setOnClickListener(View.OnClickListener {
                val intent = Intent(Intent.ACTION_SEND)
                intent.data = Uri.parse("email")
                val s = arrayOf("inpower@gmail.com")
                intent.putExtra(Intent.EXTRA_EMAIL, s)
                intent.putExtra(Intent.EXTRA_SUBJECT, "Query regarding suspended account")
                intent.putExtra(Intent.EXTRA_TEXT, "I want to enquire from you that my following account got suspended \n\n"+" Name :"+" " +sharedPreferences?.getString("Fullname", "").toString()+"\n"+" Phone :"+" " +sharedPreferences?.getString("Phone", "").toString()+"\n\n Reason : "+reason)
                intent.type = "message/rfc822"
                val chooser = Intent.createChooser(intent, "Please Launch Email")
                startActivity(chooser)
            })
        }

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