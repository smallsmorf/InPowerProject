package com.inpower.webguruz.profile.EditProfile

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.CompoundButton
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.ContextCompat
import com.google.gson.Gson
import com.inpower.webguruz.model.Data
import com.inpower.webguruz.model.Models
import com.inpower.webguruz.R
import com.inpower.webguruz.utilMethods.CustomLoadergrey
import com.inpower.webguruz.utilMethods.GetDataService
import com.inpower.webguruz.utilMethods.RetrofitClientInstance
import com.inpower.webguruz.utilMethods.UtilFunctions
import retrofit2.Call
import retrofit2.Callback

//Activity to update user profile
class UpdateProfileActivity : Activity() {

    var tv_cancel:TextView?=null
    var tv_save:TextView?=null
    var tv_they:TextView?=null
    var tv_she:TextView?=null
    var tv_title:TextView?=null
    var tv_mobile:TextView?=null
    var tv_dob:TextView?=null
    var tv_bio:TextView?=null
    var tv_wordCount:TextView?=null
    var tv_name:TextView?=null
    var tv_pronoun:TextView?=null

    var rl_name:RelativeLayout?=null
    var rl_pronoun:RelativeLayout?=null
    var rl_bio:LinearLayout?=null
    var ll_she:LinearLayout?=null
    var ll_they:LinearLayout?=null
    var rl_dob:RelativeLayout?=null
    var rl_mobile:RelativeLayout?=null
    var ll_pronoun:LinearLayout?=null
    var sharedPreferences: SharedPreferences? = null
    var Pronoun:String?=""
    var PronounVisibility:String?="false"
    lateinit var switch_button: SwitchCompat

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.update_profile_activity)
        sharedPreferences = getSharedPreferences("MySession", Context.MODE_PRIVATE)

        initview()
        setdata();
        listeners()
    }

    //function of initialisation
    private fun initview() {
        tv_cancel=findViewById(R.id.tv_cancel)
        tv_wordCount=findViewById(R.id.tv_wordCount)
        tv_save=findViewById(R.id.tv_save)
        tv_save!!.isEnabled=false


        tv_title=findViewById(R.id.tv_title)
        tv_name=findViewById(R.id.tv_name)
        tv_pronoun=findViewById(R.id.tv_pronoun)
        tv_bio=findViewById(R.id.tv_bio)
        tv_mobile=findViewById(R.id.tv_mobile)
        tv_she=findViewById(R.id.tv_she)
        tv_they=findViewById(R.id.tv_they)
        tv_dob=findViewById(R.id.tv_dob)
        ll_they=findViewById(R.id.ll_they)
        ll_she=findViewById(R.id.ll_she)


        tv_name?.text = sharedPreferences?.getString("Fullname", "").toString()
        tv_bio?.text = sharedPreferences?.getString("Bio", "").toString()
        tv_pronoun?.text = sharedPreferences?.getString("Pronoun", "").toString()
        Pronoun=sharedPreferences?.getString("Pronoun", "").toString()
        tv_mobile?.text = sharedPreferences?.getString("Phone", "").toString()
        tv_dob?.text = sharedPreferences?.getString("DOB", "").toString()

        rl_name=findViewById(R.id.rl_name)
        rl_pronoun=findViewById(R.id.rl_pronoun)
        rl_bio=findViewById(R.id.rl_bio)
        rl_dob=findViewById(R.id.rl_dob)
        rl_mobile=findViewById(R.id.rl_mobile)
        ll_pronoun=findViewById(R.id.ll_pronoun)

        switch_button=findViewById(R.id.switch_button)


    }

    //function of listeners
    private fun listeners() {
        tv_cancel!!.setOnClickListener(View.OnClickListener {
            finish()
            overridePendingTransition(R.anim.fade_in,R.anim.fade_out)
        })
        ll_she?.setOnClickListener(View.OnClickListener {
            ll_they?.setBackgroundDrawable(resources.getDrawable(R.drawable.drawable_bg))
            ll_she?.setBackgroundDrawable(resources.getDrawable(R.drawable.drawable_green))
            tv_she?.setTextColor(resources.getColor(R.color.white))
            tv_they?.setTextColor(resources.getColor(R.color.bcolor))
            Pronoun="She/Her"
            tv_save!!.setTextColor(resources.getColor(R.color.pinkcolor))
            tv_save!!.isEnabled=true
        })
        ll_they?.setOnClickListener(View.OnClickListener {
            ll_they?.setBackgroundDrawable(resources.getDrawable(R.drawable.drawable_green))
            tv_they?.setTextColor(resources.getColor(R.color.white))
            ll_she?.setBackgroundDrawable(resources.getDrawable(R.drawable.drawable_bg))
            tv_she?.setTextColor(resources.getColor(R.color.bcolor))
            Pronoun="They/Them"
            tv_save!!.setTextColor(resources.getColor(R.color.pinkcolor))
            tv_save!!.isEnabled=true
        })

        val editor = sharedPreferences?.edit()
//        if (sharedPreferences!!.getString("pronounVisibility", "false").toString().equals("true")){
//            switch_button.setChecked(true)
//        }
        if (sharedPreferences!!.getString("pronounVisibility", "").toString().equals("true")){
            switch_button.setChecked(true)
            PronounVisibility="true"
            Log.d("switchtest","switch : " +  sharedPreferences!!.getString("pronounVisibility", "").toString())
        }else{
            switch_button.setChecked(false)
            PronounVisibility="false"
            Log.d("switchtestelse","switch else: " +  sharedPreferences!!.getString("pronounVisibility", "").toString())
        }
        switch_button.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                editor!!.putString("pronounVisibility", "true").apply()
                PronounVisibility="true"
                tv_save!!.setTextColor(resources.getColor(R.color.pinkcolor))
                tv_save!!.isEnabled=true
            }
            else {
                editor!!.putString("pronounVisibility", "false").apply()
                PronounVisibility="false"
                tv_save!!.setTextColor(resources.getColor(R.color.pinkcolor))
                tv_save!!.isEnabled=true
            }
        })

        tv_name!!.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if(s.length>4){
                    tv_save!!.setTextColor(resources.getColor(R.color.pinkcolor))
                    tv_save!!.isEnabled=true
                }else{
                    tv_save!!.setTextColor(resources.getColor(R.color.bcolor))
                    tv_save!!.isEnabled=false
                }
            }
        })

        tv_bio!!.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if(s.length>10){
                    tv_save!!.setTextColor(resources.getColor(R.color.pinkcolor))
                    tv_save!!.isEnabled=true
                }else{
                    tv_save!!.setTextColor(resources.getColor(R.color.bcolor))
                    tv_save!!.isEnabled=false
                }


//                var wordCount=s.split(" ")
//                tv_wordCount!!.text = wordCount.size.toString() + "/50"

                tv_wordCount!!.text =tv_bio!!.text!!.length.toString() + "/250"


            }
        })


        tv_save!!.setOnClickListener(View.OnClickListener {
            val key=intent.getStringExtra("key")
            if(key.equals("mobile")){
                sendOtp(this,sharedPreferences?.getString("Phone", "").toString());
            }
            else{
                UtilFunctions.INSTANCE.editProfile(
                    this,
                    sharedPreferences?.getString("Email", "").toString(),
                    sharedPreferences?.getString("MyUserId", "").toString(),
                    tv_name?.text.toString(),
                    true,
                    tv_bio?.text.toString(),
                    tv_mobile?.text.toString(),
                    sharedPreferences?.getString("userPic64","").toString(),
                    sharedPreferences?.getString("userPic64","").toString(),
                    Pronoun!!,
                    PronounVisibility!!,
                    sharedPreferences?.getString("accountVisibility", "").toString(),
                    "Edit profile")
            }
        })
//        var wordCount=tv_bio?.text.toString().split(" ")
//        tv_wordCount!!.text = wordCount.size.toString() + "/250"
        tv_wordCount!!.text = tv_bio!!.text.length.toString() + "/250"

    }

    //function to set profile data
    private fun setdata() {
        val key=intent.getStringExtra("key")
        if(key.equals("name")){
            tv_title?.setText("Name")
            rl_name?.setVisibility(View.VISIBLE)
            rl_pronoun?.setVisibility(View.GONE)
            rl_bio?.setVisibility(View.GONE)
            rl_dob?.setVisibility(View.GONE)
            ll_pronoun?.setVisibility(View.GONE)
            rl_mobile?.setVisibility(View.GONE)
        }
        else if(key.equals("pronoun")){
            tv_title?.setText("")
            rl_name?.setVisibility(View.GONE)
            rl_pronoun?.setVisibility(View.GONE)
            rl_bio?.setVisibility(View.GONE)
            rl_dob?.setVisibility(View.GONE)
            ll_pronoun?.setVisibility(View.VISIBLE)
            rl_mobile?.setVisibility(View.GONE)
            if(!sharedPreferences?.getString("Pronoun", "").toString().contains("She")){
                ll_they?.setBackgroundDrawable(resources.getDrawable(R.drawable.drawable_green))
                tv_they?.setTextColor(resources.getColor(R.color.white))
                ll_she?.setBackgroundDrawable(resources.getDrawable(R.drawable.drawable_bg))
                tv_she?.setTextColor(resources.getColor(R.color.bcolor))
            }else{
                ll_they?.setBackgroundDrawable(resources.getDrawable(R.drawable.drawable_bg))
                ll_she?.setBackgroundDrawable(resources.getDrawable(R.drawable.drawable_green))
                tv_she?.setTextColor(resources.getColor(R.color.white))
                tv_they?.setTextColor(resources.getColor(R.color.bcolor))
            }

            if(sharedPreferences?.getString("pronounVisibility", "").toString().contains("false")){
                Log.e("pronounVisibility","false")
            }
            else if(sharedPreferences?.getString("pronounVisibility", "").toString().contains("true")){
                Log.e("pronounVisibility","true")
            }
        }
        else if(key.equals("bio")){
            tv_title?.setText("Bio")
            rl_name?.setVisibility(View.GONE)
            rl_pronoun?.setVisibility(View.GONE)
            rl_bio?.setVisibility(View.VISIBLE)
            rl_dob?.setVisibility(View.GONE)
            rl_mobile?.setVisibility(View.GONE)
            ll_pronoun?.setVisibility(View.GONE)
        }
        else if(key.equals("dob")){
            tv_title?.setText("Age")
            rl_name?.setVisibility(View.GONE)
            rl_pronoun?.setVisibility(View.GONE)
            rl_bio?.setVisibility(View.GONE)
            rl_dob?.setVisibility(View.VISIBLE)
            rl_mobile?.setVisibility(View.GONE)
            ll_she?.setVisibility(View.GONE)
            ll_they?.setVisibility(View.GONE)
        }
        else if(key.equals("mobile")){
            tv_title?.setText("Mobile")
            rl_name?.setVisibility(View.GONE)
            rl_pronoun?.setVisibility(View.GONE)
            rl_bio?.setVisibility(View.GONE)
            rl_dob?.setVisibility(View.GONE)
            rl_mobile?.setVisibility(View.VISIBLE)
            ll_pronoun?.setVisibility(View.GONE)
            tv_save?.setVisibility(View.GONE)

            tv_mobile?.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable) {
                    if(!s.contains("+")){
                        //UtilFunctions.INSTANCE.Error(this@UpdateBioActivity,"Enter country code +91 please")
                    }
                }
                override fun beforeTextChanged(s: CharSequence, start: Int,count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    tv_save?.setVisibility(View.VISIBLE)
                }
            })
        }
    }

    //function to send otp
    fun sendOtp(context: Activity, number: String) {
        val service: GetDataService = RetrofitClientInstance.getRetrofitInstance()!!.create(GetDataService::class.java)
        val customLoader = CustomLoadergrey(context, R.style.CustomLoaderTheme)
        customLoader.show()
        val call: Call<Models> = service.verifyOtp(number, "sms")
        call.enqueue(object : Callback<Models> {
            override fun onResponse(call: Call<Models>, response: retrofit2.Response<Models>) {
                Log.e("RegisterResponse", "is : " + Gson().toJson(response.body()).toString())
                if (customLoader.isShowing()) customLoader.dismiss()
                val intent = Intent(context, VerifyUpdateNumberActivity::class.java)
                context.startActivity(intent)
                context.finish()
            }

            override fun onFailure(call: Call<Models>, t: Throwable) {
                if (customLoader.isShowing()) customLoader.dismiss()
            }
        })
    }

    @Override
    override fun onBackPressed() {
        finish()
        overridePendingTransition(R.anim.fade_in,R.anim.fade_out)
    }

}