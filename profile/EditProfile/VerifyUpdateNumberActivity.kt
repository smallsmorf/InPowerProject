package com.inpower.webguruz.profile.EditProfile

import android.app.Activity
import android.graphics.Typeface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.inpower.webguruz.R
import com.inpower.webguruz.customviews.CustomOtpView

//activity to verify mobile number
class VerifyUpdateNumberActivity : Activity() {

    lateinit var tv_nocode: TextView
    lateinit var tv_try: TextView
    lateinit var but_ok: TextView
    lateinit var tv_cancel: TextView
    lateinit var ll_ok: LinearLayout
    lateinit var ll_nocode: LinearLayout
    lateinit var ll_otp: LinearLayout
    var screen: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verify_update_number)
        initview()
        setOtp(this)
        screen=getIntent().getStringExtra("screen").toString()
        tv_cancel=findViewById(R.id.tv_cancel)
        tv_nocode = findViewById(R.id.tv_nocode) as TextView
        tv_try = findViewById(R.id.tv_try) as TextView
        listeners()
    }

    //fucntion of listeners
    private fun listeners() {

        tv_cancel.setOnClickListener(View.OnClickListener {
               finish()
        })

        tv_nocode.setOnClickListener(View.OnClickListener {
            customOtpView.setText("")
//            if (screen.equals("login")) {
//                val intent = Intent(this, InviteCodeActivity::class.java)
//                startActivity(intent)
//            } else {
//                val intent = Intent(this, SelfieTimeActivity::class.java)
//                startActivity(intent)
//            }
        })
        tv_try.setOnClickListener(View.OnClickListener {
            customOtpView.setText("")
//            if (screen.equals("login")) {
//                val intent = Intent(this, InviteCodeActivity::class.java)
//                startActivity(intent)
//            } else {
//                val intent = Intent(this, SelfieTimeActivity::class.java)
//                startActivity(intent)
//            }
       })

        tv_cancel!!.setOnClickListener(View.OnClickListener {
            finish()
        })
    }
    lateinit var customOtpView:CustomOtpView

    //function to send otp
    private fun setOtp(context: Activity) {
        tv_nocode = findViewById(R.id.tv_nocode) as TextView
        tv_try = findViewById(R.id.tv_try) as TextView
        customOtpView = findViewById(R.id.firstPinView3) as CustomOtpView
        customOtpView.setTypeface(null, Typeface.BOLD)
        //        customOtpView.setTextColor(ResourcesCompat.getColor(getResources(), R.color.textcolor, getTheme()))
//        customOtpView.setTextColor(ResourcesCompat.getColo                                         rStateList(getResources(), R.color.textcolor, getTheme()))
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
                    val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
                   if(s.toString()=="123456"){
                       ll_ok.visibility=View.VISIBLE
                       ll_otp.visibility=View.GONE
                       ll_nocode.visibility=View.GONE
                      // CustomToastGreen("Your mobile was successfully changed")
                   }else{
                       CustomToast("Incorrect Code")
                   }
                }
            }

            override fun afterTextChanged(s: Editable?) {
                customOtpView.setTypeface(customOtpView.getTypeface(), Typeface.BOLD_ITALIC)
            }
        })

    }

    //function to send custom OTP
    fun CustomToast(message: String) {
        var toast = Toast(this)
        val inflater = LayoutInflater.from(this)
        val toastRoot: View = inflater.inflate(R.layout.customtoast, null)
        var custom_toast_message = toastRoot.findViewById<TextView>(R.id.custom_toast_message)
        toast.setView(toastRoot)
        custom_toast_message.text = message
        toast.setGravity(
            Gravity.CENTER_HORIZONTAL or Gravity.BOTTOM,
            0, Math.round(resources.getDimension(R.dimen._100sdp))
        )
        val yOffset = Math.max(0,toast.yOffset-100)
        toast.setGravity(
            Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL,
            0,
            yOffset)
        toast.duration = Toast.LENGTH_SHORT
        toast.show()
    }

    fun CustomToastGreen(message: String) {
        tv_nocode?.setVisibility(View.GONE)
        tv_try?.setVisibility(View.GONE)

        var toast = Toast(this)
        val inflater = LayoutInflater.from(this)
        val toastRoot: View = inflater.inflate(R.layout.customtoast_green, null)
        var custom_toast_message = toastRoot.findViewById<TextView>(R.id.custom_toast_message)
        toast.setView(toastRoot)
        custom_toast_message.text = message
        toast.setGravity(
            Gravity.CENTER_HORIZONTAL or Gravity.BOTTOM,
            0, Math.round(resources.getDimension(R.dimen._100sdp))
        )
        val yOffset = Math.max(0,toast.yOffset-100)
        toast.setGravity(
            Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL,
            0,
            yOffset)

//        toast.setGravity(
//            Gravity.CENTER or Gravity.CENTER,
//            0, Math.round(resources.getDimension(R.dimen._100sdp))
//        )
        toast.duration = Toast.LENGTH_SHORT
        toast.show()
    }

    //function of initialisation
    private fun initview() {
        val window: Window = getWindow()
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.white))
        }
        tv_nocode = findViewById(R.id.tv_nocode) as TextView
        tv_try = findViewById(R.id.tv_try) as TextView
        ll_ok = findViewById(R.id.ll_ok) as LinearLayout
        ll_otp = findViewById(R.id.ll_otp) as LinearLayout
        but_ok = findViewById(R.id.but_ok) as TextView
        ll_nocode = findViewById(R.id.ll_nocode) as LinearLayout
        but_ok?.setOnClickListener(View.OnClickListener {
            finish()
        })
        val customOtpView = findViewById(R.id.firstPinView3) as CustomOtpView
    }

}