package com.inpower.webguruz.intro

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.CompoundButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.ContextCompat
import com.inpower.webguruz.R

//activity to show pronoun
class MyPronounsActivity : Activity() {

    var sharedPreferences: SharedPreferences? = null
    var editor: SharedPreferences.Editor? = null
    lateinit var ll_she: LinearLayout
    lateinit var ll_they: LinearLayout
    lateinit var tv_they: TextView
    lateinit var tv_she: TextView
    lateinit var switch_button: SwitchCompat
    var screen: String = ""
    var pronounVisibility="false"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mypronouns)
        sharedPreferences = this.getSharedPreferences("MySession", Context.MODE_PRIVATE)
        editor = sharedPreferences?.edit()

        initView()
        listeners()
    }

    //function to initialisation of views
    private fun initView() {

        ll_she = findViewById(R.id.ll_she)
        ll_they = findViewById(R.id.ll_they)
        tv_she = findViewById(R.id.tv_she)
        tv_they = findViewById(R.id.tv_they)
        switch_button=findViewById(R.id.switch_button)

    }

    //function of listeners
    private fun listeners() {
        ll_they.setOnClickListener(View.OnClickListener {
            ll_they.setBackgroundDrawable(resources.getDrawable(R.drawable.drawable_green))
            ll_she.setBackgroundDrawable(resources.getDrawable(R.drawable.drawable_bg))
            tv_they.setTextColor(resources.getColor(R.color.white))
            tv_she.setTextColor(resources.getColor(R.color.bcolor))

            editor?.putString("Pronoun","They/Them")?.apply()
            editor?.putString("pronounVisibility", pronounVisibility)?.apply()

            val intent = Intent(this, SelfieTimeActivity::class.java)
            intent.putExtra("screen","Registration")
            startActivity(intent)
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)

        })
        ll_she.setOnClickListener(View.OnClickListener {
            ll_they.setBackgroundDrawable(resources.getDrawable(R.drawable.drawable_bg))
            ll_she.setBackgroundDrawable(resources.getDrawable(R.drawable.drawable_green))
            tv_she.setTextColor(resources.getColor(R.color.white))
            tv_they.setTextColor(resources.getColor(R.color.bcolor))

            editor?.putString("Pronoun","She/Her")?.apply()
            editor?.putString("pronounVisibility", pronounVisibility)?.apply()
            val intent = Intent(this, SelfieTimeActivity::class.java)
            intent.putExtra("screen","Registration")
            startActivity(intent)
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        })

        switch_button.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                pronounVisibility="true"
                editor!!.putString("pronounVisibility", "true").apply()
            }
            else {
                pronounVisibility="false"
                editor!!.putString("pronounVisibility", "false").apply()
            }
        })
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