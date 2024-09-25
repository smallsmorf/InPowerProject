package com.inpower.webguruz.profile.EditProfile

import android.app.Activity
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.TextView
import com.inpower.webguruz.R
import com.inpower.webguruz.customviews.CustomOtpView

//activity to edit mobile number
class UpdateMobileNumber : AppCompatActivity() {
    var tv_cancel: TextView?=null
    lateinit var tv_nocode: TextView
    lateinit var tv_try: TextView
    lateinit var customOtpView: CustomOtpView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_mobile_number)

        tv_cancel=findViewById(R.id.tv_cancel)

        tv_cancel!!.setOnClickListener(View.OnClickListener {
            finish()
        })


        tv_nocode = findViewById(R.id.tv_nocode) as TextView
        tv_try = findViewById(R.id.tv_try) as TextView
        customOtpView = findViewById(R.id.firstPinView3) as CustomOtpView

        tv_try.setOnClickListener(View.OnClickListener {
            customOtpView.setText("")
        })

        setOtp(this)

    }

    //funtion to send OTPc
    private fun setOtp(context: Activity) {
        customOtpView.setTypeface(null, Typeface.BOLD)
//        customOtpView.setTextColor(ResourcesCompat.getColor(getResources(), R.color.textcolor, getTheme()))
//        customOtpView.setTextColor(ResourcesCompat.getColorStateList(getResources(), R.color.textcolor, getTheme()))
//        customOtpView.setLineColor(ResourcesCompat.getColor(getResources(), R.color.textcolor, getTheme()))
//        customOtpView.setLineColor(ResourcesCompat.getColorStateList(getResources(), R.color.textcolor, getTheme()))

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
//                    if (UtilFunctions.INSTANCE.isNetworkAvailable(context)) {
////                        UtilFunctions.INSTANCE.verifyOtp(
////                            context,
////                            s.toString(),
////                            getIntent().getStringExtra("number").toString(),screen
////                        )
//                    } else {
//                        UtilFunctions.INSTANCE.Error(context, ApplicationConstant.INSTANCE.BASEURL)
//                    }
                }

                else{


                }
            }

            override fun afterTextChanged(s: Editable?) {
                customOtpView.setTypeface(customOtpView.getTypeface(), Typeface.BOLD_ITALIC)
            }
        })

    }
}