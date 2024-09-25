package com.inpower.webguruz.profile


import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.exception.ApolloException
import com.example.graphql.createGroup.MyGroupsQuery
import com.example.graphql.createGroup.RemoveMemberMutation
import com.gdacciaro.iOSDialog.iOSDialogBuilder
import com.google.gson.Gson
import com.inpower.webguruz.database.DatabaseHandler
import com.inpower.webguruz.model.*
import com.inpower.webguruz.R
import com.inpower.webguruz.utilMethods.ApplicationConstant
import com.inpower.webguruz.utilMethods.CustomLoader
import com.inpower.webguruz.utilMethods.UtilFunctions
import com.inpower.webguruz.adapter.*
import com.inpower.webguruz.listeners.RecyclerViewClickListeners
import com.webgurus.attendanceportal.ui.base.BaseFragment
import okhttp3.OkHttpClient

//fragment to show users group
@RequiresApi(Build.VERSION_CODES.HONEYCOMB)
class UsersGroupFragment : BaseFragment(), RecyclerViewClickListeners {

    private lateinit var iv_back: ImageView
    private lateinit var rv_myGroups: RecyclerView
    private lateinit var tv_title:TextView

    lateinit var data: Data
    var hasNext: Boolean = true

    lateinit var editor: SharedPreferences.Editor

    var userId: String = ""
    var userName: String = ""
    lateinit var mListPosts: MutableList<Group>
    lateinit var sharedPreferences: SharedPreferences
    lateinit var usersGroupsAdapter: UsersGroupsAdapter;


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_user_group, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mListPosts = ArrayList<Group>();

        sharedPreferences = requireActivity().getSharedPreferences("MySession", Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()
        userId = sharedPreferences.getString("UserId", "").toString()
        userName = sharedPreferences.getString("UserName", "").toString()

        getMyGroup()
        initView()
        listener()
        setRecyclerview()


    }

    //function to initialisation of view
    private fun initView() {
        iv_back = requireView().findViewById(R.id.iv_back)
        tv_title = requireView().findViewById(R.id.tv_title)
        if (userId.equals(sharedPreferences.getString("MyUserId", "").toString())){
            tv_title!!.text="My Group"
        }
        else{
            tv_title!!.text=userName+"'s Group"
        }

        rv_myGroups = requireView().findViewById(R.id.rv_MyGroups)
    }

    //function of click listener
    private  fun listener(){
        iv_back!!.setOnClickListener(View.OnClickListener {
            requireActivity().onBackPressed()
        })
    }

    //function to set recyclerview
    private fun setRecyclerview() {
        usersGroupsAdapter = UsersGroupsAdapter(requireActivity(),this, mListPosts,requireFragmentManager())
        rv_myGroups.layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
        fragmentManager
        rv_myGroups.addItemDecoration(DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL))
        rv_myGroups.adapter = usersGroupsAdapter
        rv_myGroups.setHasFixedSize(true)
    }

    //function to implement api click lister
    override fun onClick(position: Int, view: View) {
        TODO("Not yet implemented")
    }

    //function to implement api click lister
    override fun onClick(position: Int, view: View, id: String) {
        var d = iOSDialogBuilder(context);
        d.setTitle("Leave?")
            .setSubtitle("Are you sure you want to leave this group?")
            .setBoldPositiveLabel(false)
            .setCancelable(false)
            .setPositiveListener("Yes") { dialog ->
                leaveGroup(requireActivity(), id,position);
                dialog.dismiss()
            }
            .setNegativeListener("Cancel") { dialog -> dialog.dismiss() }
            .build().show()
    }

    //function to get my group
    private fun getMyGroup() {

        val customLoader = CustomLoader(context, R.style.CustomLoaderTheme)
        customLoader.show()

        var sharedPreferences = context?.getSharedPreferences("MySession", Context.MODE_PRIVATE)
        var editor = sharedPreferences?.edit()
        var getGroup = MyGroupsQuery(userId = userId, pageNo = 1, perPage = 100)

        val BASE_URL = ApplicationConstant.INSTANCE.BASEURLGROUP
        val okHttpClient = OkHttpClient.Builder().build()
        val apolloClient = ApolloClient.builder().serverUrl(BASE_URL).okHttpClient(UtilFunctions.INSTANCE.getUnsafeOkHttpClient()!!).build()
        apolloClient.query(getGroup).enqueue(
            object : ApolloCall.Callback<MyGroupsQuery.Data>() {
                override fun onFailure(e: ApolloException) {
                }

                override fun onResponse(response: com.apollographql.apollo.api.Response<MyGroupsQuery.Data>) {
                    requireActivity().runOnUiThread(Runnable {
                        if (response.data != null) {
                            val data = Gson().fromJson(Gson().toJson(response.data),Data::class.java)
                            if (customLoader.isShowing()) customLoader.dismiss()
                            requireActivity().runOnUiThread(Runnable {
                                if (customLoader.isShowing()) customLoader.dismiss()
                                if (response.data != null) {
                                    Log.e("PostDataSaved", Gson().toJson(response.data))
                                    val data = Gson().fromJson(Gson().toJson(response.data), Data::class.java)
                                    mListPosts.addAll(data.myGroups.list)
                                    mListPosts.removeIf { a:Group-> !a.status.lowercase().equals("live")}
                                    hasNext = response.data?.myGroups?.hasNext!!
                                    usersGroupsAdapter.notifyDataSetChanged()
                                }
                            })
                        }
                    })
                }
            })

    }

    //function to leave a group
    private fun leaveGroup(context: Activity, groupId: String, position: Int) {
        val customLoader = CustomLoader(context, R.style.CustomLoaderTheme)
        customLoader.show()
        var sharedPreferences = context.getSharedPreferences("MySession", Context.MODE_PRIVATE)
        var getGroup = RemoveMemberMutation(
            userId = sharedPreferences.getString("MyUserId", "").toString(),
            groupId = groupId
        );

        val BASE_URL = ApplicationConstant.INSTANCE.BASEURLGROUP
        val apolloClient = ApolloClient.builder().serverUrl(BASE_URL).okHttpClient(UtilFunctions.INSTANCE.getUnsafeOkHttpClient()!!).build()
        apolloClient.mutate(getGroup).enqueue(
            object : ApolloCall.Callback<RemoveMemberMutation.Data>() {
                override fun onFailure(e: ApolloException) {
                }

                override fun onResponse(response: com.apollographql.apollo.api.Response<RemoveMemberMutation.Data>) {
                    context.runOnUiThread(Runnable {
                        if (customLoader.isShowing()) customLoader.dismiss()
                        Log.e("LeaveGroup", Gson().toJson(response.data))
                        context.runOnUiThread(Runnable {
                            mListPosts.removeAt(position)
                            usersGroupsAdapter.notifyDataSetChanged()
                            UtilFunctions.INSTANCE.getMyGroups(context);
                            UtilFunctions.INSTANCE.getAllGroups(context);
                            val databaseHandler = DatabaseHandler(context)
                            //databaseHandler.deleteTableRow(groupId)
                            //databaseHandler.deleteTable()

                        })
                    })
                }
            })
    }


}

