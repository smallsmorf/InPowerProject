package com.inpower.webguruz.profile.MyProfile

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.CompoundButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.appcompat.widget.SwitchCompat
import androidx.core.app.NotificationManagerCompat
import com.gdacciaro.iOSDialog.iOSDialogBuilder
import com.inpower.webguruz.R
import com.inpower.webguruz.database.DatabaseHandler
import com.inpower.webguruz.intro.HelloBeautyActivity
import com.inpower.webguruz.utilMethods.UtilFunctions


//fragment to show setting
class Setting : Activity() {


    var iv_back: ImageView? = null
    var rl_saved: RelativeLayout? = null
    var rl_myGroup: RelativeLayout? = null
    var rl_myBadges: RelativeLayout? = null
    var rl_myAnonymousPost: RelativeLayout? = null
    var rl_blocked: RelativeLayout? = null
    var rl_privacy_policy: RelativeLayout? = null
    var logout: LinearLayout? = null
    var sharedPreferences: SharedPreferences? = null
    lateinit var sb_private: SwitchCompat
    lateinit var sb_notification: SwitchCompat
    var  load="load"
    lateinit var editor:SharedPreferences.Editor


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.privacy_setting)

        sharedPreferences = getSharedPreferences("MySession", Context.MODE_PRIVATE)
        editor = sharedPreferences?.edit()!!


        initView()
        listeners()


    }

    private fun initView() {
        iv_back=findViewById(R.id.iv_back)
        rl_saved=findViewById(R.id.rl_saved)
        rl_myGroup=findViewById(R.id.rl_myGroup)
        rl_myBadges=findViewById(R.id.rl_myBadges)
        rl_myAnonymousPost=findViewById(R.id.rl_myAnonymousPost)
        rl_blocked=findViewById(R.id.rl_blocked)
        rl_privacy_policy=findViewById(R.id.rl_privacy_policy)
        logout=findViewById(R.id.logout)
        sb_private=findViewById(R.id.sb_private)
        sb_notification=findViewById(R.id.sb_notification)

    }

    private fun listeners() {
        iv_back!!.setOnClickListener(View.OnClickListener {
            onBackPressed()
        })
        rl_saved!!.setOnClickListener(View.OnClickListener {
            editor?.putString("feedType","Saved")?.apply()
            val intent = Intent(this, ProfileSettings::class.java)
            intent.putExtra("title", "UserFeeds")
            startActivity(intent)
            overridePendingTransition(R.anim.fade_in,R.anim.fade_out)
        })
        rl_myGroup!!.setOnClickListener(View.OnClickListener {
            val intent = Intent(this, ProfileSettings::class.java)
            intent.putExtra("title", "MyGroups")
            startActivity(intent)
            overridePendingTransition(R.anim.fade_in,R.anim.fade_out)
        })
        rl_myBadges!!.setOnClickListener(View.OnClickListener {
//            val intent = Intent(this, ProfileSettings::class.java)
//            intent.putExtra("title", "Badges")
//            startActivity(intent)
//            overridePendingTransition(R.anim.fade_in,R.anim.fade_out)
        })
        rl_myAnonymousPost!!.setOnClickListener(View.OnClickListener {
            editor?.putString("feedType","Anonymous")?.apply()
            val intent = Intent(this, ProfileSettings::class.java)
            intent.putExtra("title", "UserFeeds")
            startActivity(intent)
            overridePendingTransition(R.anim.fade_in,R.anim.fade_out)
        })
        rl_blocked!!.setOnClickListener(View.OnClickListener {
            val intent = Intent(this, ProfileSettings::class.java)
            intent.putExtra("title", "Blocked")
            startActivity(intent)
            overridePendingTransition(R.anim.fade_in,R.anim.fade_out)
        })

        rl_privacy_policy!!.setOnClickListener(View.OnClickListener {
            val intent = Intent(this, PrivacyPolicy::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.fade_in,R.anim.fade_out)
        })
        logout!!.setOnClickListener(View.OnClickListener {
            iOSDialogBuilder(this)
                .setTitle("Logout?")
                .setSubtitle("Do you really want to logout?")
                .setBoldPositiveLabel(false)
                .setCancelable(false)
                .setPositiveListener("Yes") { dialog ->

                    UtilFunctions.INSTANCE.deleteFirebaseToken(this)

                    val databaseHandler = DatabaseHandler(this)
                    databaseHandler.deleteTable()

                    var sharedPreferences: SharedPreferences? = getSharedPreferences("MySession", Context.MODE_PRIVATE)
                    var editor: SharedPreferences.Editor? = sharedPreferences?.edit()
                    if (editor != null) {
                        editor.clear()
                        editor.apply()
                    }

                    val intent = Intent(this, HelloBeautyActivity::class.java)
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    startActivity(intent)
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
                    finish()
                    dialog.dismiss()
                }
                .setNegativeListener("Cancel") { dialog -> dialog.dismiss() }
                .build().show()
        })


        val accountVisibility= sharedPreferences?.getString("accountVisibility", "").toString()
        Log.e("accountVisibility",accountVisibility)

        val editor = sharedPreferences?.edit()
        if (sharedPreferences!!.getString("accountVisibility", "").equals("private")){
            sb_private.setChecked(true)
        }
        sb_private.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                editor!!.putString("accountVisibility", "private").apply()
                Log.e("accountVisibility", sharedPreferences?.getString("accountVisibility", "").toString())
                UtilFunctions.INSTANCE.editProfile(
                    this,
                    sharedPreferences?.getString("Email", "").toString(),
                    sharedPreferences?.getString("MyUserId", "").toString(),
                    sharedPreferences?.getString("Fullname", "").toString(),
                    true,
                    sharedPreferences?.getString("Bio", "").toString(),
                    sharedPreferences?.getString("Phone", "").toString(),
                    sharedPreferences?.getString("userPic64","").toString(),
                    sharedPreferences?.getString("userPic64","").toString(),
                    sharedPreferences?.getString("Pronoun","").toString(),
                    sharedPreferences?.getString("pronounVisibility","").toString(),
                    sharedPreferences?.getString("accountVisibility", "").toString(),
                    "setting")
            }
            else {
                editor!!.putString("accountVisibility", "public").apply()
                Log.e("accountVisibility", sharedPreferences?.getString("accountVisibility", "").toString())
                UtilFunctions.INSTANCE.editProfile(
                    this,
                    sharedPreferences?.getString("Email", "").toString(),
                    sharedPreferences?.getString("MyUserId", "").toString(),
                    sharedPreferences?.getString("Fullname", "").toString(),
                    true,
                    sharedPreferences?.getString("Bio", "").toString(),
                    sharedPreferences?.getString("Phone", "").toString(),
                    sharedPreferences?.getString("userPic64","").toString(),
                    sharedPreferences?.getString("userPic64","").toString(),
                    sharedPreferences?.getString("Pronoun","").toString(),
                    sharedPreferences?.getString("pronounVisibility","").toString(),
                    sharedPreferences?.getString("accountVisibility", "").toString(),
                    "setting")
            }
        })


        if(!NotificationManagerCompat.from(this).areNotificationsEnabled()){
            sb_notification.isChecked=false
            Log.e("Notification","on")
        }
        else{
            sb_notification.isChecked=true
            Log.e("Notification","off")
        }


        sb_notification.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
            if (!NotificationManagerCompat.from(this).areNotificationsEnabled()) {
                sb_notification.isChecked = false


                Log.e("NotificationC", "off")
            } else if(NotificationManagerCompat.from(this).areNotificationsEnabled()){
                sb_notification.isChecked = true

                Log.e("NotificationC", "on")
            }

            if (sb_notification.isChecked) {
                sb_notification.isChecked = false
            } else if (!sb_notification.isChecked) {
                sb_notification.isChecked = true

            }

            if (load.equals("load")){
                iOSDialogBuilder(this)
                    .setTitle("Settings")
                    .setSubtitle("Go to App Settings and turn on/off notifications")
                    .setBoldPositiveLabel(false)
                    .setCancelable(false)
                    .setPositiveListener("Settings") { dialog ->
                        dialog.dismiss()
                        load="Setting"
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        val uri: Uri = Uri.fromParts("package", packageName, null)
                        intent.data = uri
                        startActivity(intent)
                    }
                    .setNegativeListener("Cancel") { dialog ->
                        dialog.dismiss()
                    }
                    .build().show()
            }


        })

    }

    @Override
    override fun onBackPressed() {
        finish()
        overridePendingTransition(R.anim.fade_in,R.anim.fade_out)
    }


    override fun onResume() {
        super.onResume()
        if(!NotificationManagerCompat.from(this).areNotificationsEnabled()){
            sb_notification.isChecked=false
            load="load"
            Log.e("NotificationC", "on")
        }
        else {
            sb_notification.isChecked=true
            Log.e("NotificationC", "off")
            load="load"
        }

    }


}