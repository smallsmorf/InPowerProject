package com.inpower.webguruz.intro

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.inpower.webguruz.model.CategoryData
import com.inpower.webguruz.model.Data
import com.inpower.webguruz.R
import com.inpower.webguruz.utilMethods.UtilFunctions
import com.inpower.webguruz.adapter.IntroGroupCategoryAdapter
import com.inpower.webguruz.listeners.ApiListeners
import java.util.*

//activity to load intro group
class IntroGroupActivity : AppCompatActivity(),ApiListeners {

    lateinit var rl_continuetochoose: RelativeLayout
    lateinit var rv_group: RecyclerView
    lateinit var tv_buttontext: TextView
    lateinit var tv_user: TextView
    lateinit var Group: Data
    lateinit var GetCategories: List<CategoryData>
    var joined: ArrayList<String> = ArrayList()
    var success: ArrayList<String> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.intro_group_layout)

        initview()
        listeners()
        UtilFunctions.INSTANCE.getCategories(this,this)

        GetCategories= ArrayList();

        var sharedPreferences = getSharedPreferences("MySession", Context.MODE_PRIVATE)
        var editor = sharedPreferences?.edit()
        var a = sharedPreferences.getString("groups", "").toString()
        Group = Gson().fromJson(a, Data::class.java)
        Log.d("firstgroupintro","" + a)

        var join = sharedPreferences.getString("joined", "").toString()
        var succ = sharedPreferences.getString("success", "").toString()
        var Fullname = sharedPreferences.getString("Fullname", "").toString()
        tv_user.text="Hi "+Fullname+"\uD83D\uDC4B"

        if (join.equals("")) {
            editor?.putString("joined", Gson().toJson(joined))?.apply()
        } else if (join.equals("[]")) {
            editor?.putString("joined", Gson().toJson(joined))?.apply()
        } else {
            val arrayType = object : TypeToken<ArrayList<String>>() {}.type
            joined = Gson().fromJson<ArrayList<String>>(join, arrayType)
            editor?.putString("joined", Gson().toJson(joined))?.apply()
            Log.e("joinedjoined", sharedPreferences.getString("joined", "").toString())
        }
        if (succ.equals("")) {
            editor?.putString("success", Gson().toJson(success))?.apply()
        } else if (succ.equals("[]")) {
            editor?.putString("success", Gson().toJson(success))?.apply()
        } else {
            val arrayType = object : TypeToken<ArrayList<String>>() {}.type
            success = Gson().fromJson<ArrayList<String>>(succ, arrayType)
            editor?.putString("success", Gson().toJson(success))?.apply()
        }
        if (!succ.equals("")) {
            val arrayType = object : TypeToken<ArrayList<String>>() {}.type
            success = Gson().fromJson<ArrayList<String>>(succ, arrayType)
            if (success.size >2) {
                Log.d("firstgroupinside","" + success)
                rl_continuetochoose.setEnabled(true);
                tv_buttontext.setTextColor(getResources().getColor(R.color.white));
                tv_buttontext.setText("Continue");
                rl_continuetochoose.background = resources.getDrawable(R.drawable.register_background_drawable_active);
            }
        }
        editor?.putString("success", Gson().toJson(success))?.apply()

        Log.d("firstgroupintrolessthan","" + succ)
    }


    //function of listeners
    private fun listeners() {
        rl_continuetochoose.setOnClickListener(View.OnClickListener {
            if (UtilFunctions.INSTANCE.isNetworkAvailable(this)) {
                UtilFunctions.INSTANCE.setToken(this);
            }

        })
    }

    lateinit var groupCategoryAdapter:IntroGroupCategoryAdapter;

    //function of initialisation of views
    private fun initview() {
        rl_continuetochoose = findViewById(R.id.rl_continuetochoose)
        rv_group = findViewById(R.id.rv_group)
        tv_user = findViewById(R.id.tv_user)
        tv_buttontext = findViewById(R.id.tv_buttontext)
        rl_continuetochoose.setEnabled(false);

    }

    override fun onResult(response: String) {
        rl_continuetochoose.visibility=View.VISIBLE
        val arrayType = object : TypeToken<List<CategoryData>>() {}.type
        GetCategories= Gson().fromJson<List<CategoryData>>(response, arrayType)
        Log.d("introdata","get categories : " + Group.groups.list)
        groupCategoryAdapter = IntroGroupCategoryAdapter(this, GetCategories,Group.groups.list, rl_continuetochoose, tv_buttontext,1,supportFragmentManager )
        rv_group.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        fragmentManager
        rv_group.adapter = groupCategoryAdapter
        rv_group.setHasFixedSize(true)
    }

    override fun onResultType(response: String, type: String) {
        TODO("Not yet implemented")
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

    override fun onResume() {
        super.onResume()
        //UtilFunctions.INSTANCE.introGetMyGroups(this,tv_buttontext,rl_continuetochoose)
    }
}