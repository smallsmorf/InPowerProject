package com.inpower.webguruz.profile.MyProfile

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.inpower.webguruz.R
import com.inpower.webguruz.profile.UsersGroupFragment

//fragment to show profile setting
class ProfileSettings : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_settings)

        val key=intent.getStringExtra("title")

        if(key.equals("UserFeeds")){
            setCurrentFragment(UserOthersPostFragment(), "SavedFeedsFragment")
        }
        else if(key.equals("MyGroups")){
            var sharedPreferences: SharedPreferences = getSharedPreferences("MySession", Context.MODE_PRIVATE)
            sharedPreferences.edit().putString("UserId",sharedPreferences.getString("MyUserId", "")).apply()
            sharedPreferences.edit().putString("UserName",sharedPreferences.getString("Fullname", "")).apply()
            setCurrentFragment(UsersGroupFragment(), "MyGroupFragment")
        }
        else if(key.equals("Blocked")){
            setCurrentFragment(BlockUserList(), "BlockedUserFragment")
        }
        else if(key.equals("Badges")){
            setCurrentFragment(MyBadges(), "BadgesFragment")
        }



    }

    private fun setCurrentFragment(fragment: Fragment, tag: String) =
        supportFragmentManager.beginTransaction().apply {
            add(R.id.flFragment, fragment,tag)
            commit()
        }
}