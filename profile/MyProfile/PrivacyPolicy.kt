package com.inpower.webguruz.profile.MyProfile

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.RadioButton
import androidx.core.content.ContextCompat
import com.inpower.webguruz.R
import android.widget.RadioGroup



//fragment to show privacy policy
class PrivacyPolicy : AppCompatActivity() {
    var iv_back: ImageView? = null
    var iv_mmg_off: ImageView? = null
    var iv_mmg_fmfo: ImageView? = null
    var iv_mmg_fe: ImageView? = null
    var iv_gif_off: ImageView? = null
    var iv_gif_fmfo: ImageView? = null
    var iv_gif_fe: ImageView? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.privacy_policy)

        val window: Window = getWindow()
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.white))
        }
        iv_back=findViewById(R.id.iv_back)
        iv_back!!.setOnClickListener(View.OnClickListener {
            onBackPressed()
        })


        iv_mmg_off=findViewById(R.id.iv_mmg_off)
        iv_mmg_fmfo=findViewById(R.id.iv_mmg_fmfo)
        iv_mmg_fe=findViewById(R.id.iv_mmg_fe)
        iv_gif_off=findViewById(R.id.iv_gif_off)
        iv_gif_fmfo=findViewById(R.id.iv_gif_fmfo)
        iv_gif_fe=findViewById(R.id.iv_gif_fe)

        iv_mmg_off!!.setOnClickListener(View.OnClickListener {
            iv_mmg_off!!.setImageResource(R.drawable.custom_radio_button_active)
            iv_mmg_fmfo!!.setImageResource(R.drawable.custom_radio_button_deactive)
            iv_mmg_fe!!.setImageResource(R.drawable.custom_radio_button_deactive)
        })

        iv_mmg_fmfo!!.setOnClickListener(View.OnClickListener {
            iv_mmg_off!!.setImageResource(R.drawable.custom_radio_button_deactive)
            iv_mmg_fmfo!!.setImageResource(R.drawable.custom_radio_button_active)
            iv_mmg_fe!!.setImageResource(R.drawable.custom_radio_button_deactive)
        })

        iv_mmg_fe!!.setOnClickListener(View.OnClickListener {
            iv_mmg_off!!.setImageResource(R.drawable.custom_radio_button_deactive)
            iv_mmg_fmfo!!.setImageResource(R.drawable.custom_radio_button_deactive)
            iv_mmg_fe!!.setImageResource(R.drawable.custom_radio_button_active)
        })

        iv_gif_off!!.setOnClickListener(View.OnClickListener {
            iv_gif_off!!.setImageResource(R.drawable.custom_radio_button_active)
            iv_gif_fmfo!!.setImageResource(R.drawable.custom_radio_button_deactive)
            iv_gif_fe!!.setImageResource(R.drawable.custom_radio_button_deactive)
        })

        iv_gif_fmfo!!.setOnClickListener(View.OnClickListener {
            iv_gif_off!!.setImageResource(R.drawable.custom_radio_button_deactive)
            iv_gif_fmfo!!.setImageResource(R.drawable.custom_radio_button_active)
            iv_gif_fe!!.setImageResource(R.drawable.custom_radio_button_deactive)
        })

        iv_gif_fe!!.setOnClickListener(View.OnClickListener {
            iv_gif_off!!.setImageResource(R.drawable.custom_radio_button_deactive)
            iv_gif_fmfo!!.setImageResource(R.drawable.custom_radio_button_deactive)
            iv_gif_fe!!.setImageResource(R.drawable.custom_radio_button_active)
        })


    }
}