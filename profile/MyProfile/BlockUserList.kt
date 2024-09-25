package com.inpower.webguruz.profile.MyProfile
import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Canvas
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.exception.ApolloException
import com.example.graphql.service1.BlockUserMutation
import com.example.graphql.service1.GetUserBlockedUsersQuery
import com.gdacciaro.iOSDialog.iOSDialogBuilder
import com.google.gson.Gson
import com.inpower.webguruz.R
import com.inpower.webguruz.adapter.*
import com.inpower.webguruz.listeners.RecyclerViewClickListeners
import com.inpower.webguruz.model.*
import com.inpower.webguruz.recyclerviewswip.SimpleDividerItemDecoration
import com.inpower.webguruz.recyclerviewswip.SwipeHelper
import com.inpower.webguruz.utilMethods.ApplicationConstant
import com.inpower.webguruz.utilMethods.CustomLoader
import com.inpower.webguruz.utilMethods.UtilFunctions
import com.webgurus.attendanceportal.ui.base.BaseFragment
import okhttp3.OkHttpClient
import java.security.KeyStore
import java.security.SecureRandom
import javax.net.ssl.*

//fragment to list blocked users
@RequiresApi(Build.VERSION_CODES.HONEYCOMB)
class BlockUserList : BaseFragment(), RecyclerViewClickListeners{

    private lateinit var iv_back: ImageView
    var rv_blocked: RecyclerView? = null
    lateinit var mListBlocked :MutableList<ListBlock>
    lateinit var blockedUserAdapter: BlockedUserAdapter
    lateinit var data: Data
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_block_user_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedPreferences =requireActivity().getSharedPreferences("MySession", Context.MODE_PRIVATE)


        initView()
        listener()

        mListBlocked=ArrayList<ListBlock>();
        getBlockUserList(requireActivity())
        setRecyclerview()


    }

    //function of initialisation
    private fun initView() {
        iv_back = requireView().findViewById(R.id.iv_back)
        rv_blocked = requireView().findViewById(R.id.rv_blocked)
    }

    //function of click listeners
    private  fun listener(){
        iv_back!!.setOnClickListener(View.OnClickListener {
            requireActivity().onBackPressed()
        })
    }

    //function to set recyclerview
    private fun setRecyclerview() {
        rv_blocked = requireView().findViewById(R.id.rv_blocked)
        blockedUserAdapter = BlockedUserAdapter(requireActivity(),requireFragmentManager(),mListBlocked)
        rv_blocked!!.setHasFixedSize(true)
        rv_blocked!!.setLayoutManager(LinearLayoutManager(activity))
        rv_blocked!!.addItemDecoration(SimpleDividerItemDecoration(requireContext()))
        rv_blocked!!.setAdapter(blockedUserAdapter)
        fragmentManager


        object : SwipeHelper(requireContext(), rv_blocked, false) {

            override fun instantiateUnderlayButton(viewHolder: RecyclerView.ViewHolder?, underlayButtons: MutableList<UnderlayButton>?) {
                // Archive Button
                underlayButtons?.add(
                    //UtilFunctions.INSTANCE.errorToast(requireContext(),"hii")
                    SwipeHelper.UnderlayButton("Unblock",
                    null,
                    Color.parseColor("#FF476C"),
                    Color.parseColor("#ffffff"))
                    { pos: Int ->
                        val myListData = mListBlocked.get(pos)
                        var d = iOSDialogBuilder(requireContext());
                        d.setTitle("Unblock?")
                            .setSubtitle("Are you sure you want to unblock this user?")
                            .setBoldPositiveLabel(false)
                            .setCancelable(false)
                            .setPositiveListener("Yes") { dialog ->
                                unblockUser(requireContext() as Activity,myListData.id,pos)
                                dialog.dismiss()
                            }
                            .setNegativeListener("Cancel") { dialog -> dialog.dismiss() }
                            .build().show()
                    })
            }
        }
    }

    override fun onClick(position: Int, view: View) {
        TODO("Not yet implemented")
    }

    override fun onClick(position: Int, view: View, id: String) {
        TODO("Not yet implemented")
    }

    //function to get blocked user list
    private fun getBlockUserList(context: Activity) {
        val customLoader = CustomLoader(context, R.style.CustomLoaderTheme)
        customLoader.show()

        var getUserBlockedUsersQuery = GetUserBlockedUsersQuery(
            userId = sharedPreferences.getString("MyUserId", "").toString(),
            pageNo=1,
            perPage = 20
        )

        val BASE_URL = ApplicationConstant.INSTANCE.BLOCKUSERLIST
        val okHttpClient = OkHttpClient.Builder().build()
        val apolloClient = ApolloClient.builder().serverUrl(BASE_URL).okHttpClient(UtilFunctions.INSTANCE.getUnsafeOkHttpClient()!!).build()
        apolloClient.query(getUserBlockedUsersQuery).enqueue(
            object : ApolloCall.Callback<GetUserBlockedUsersQuery.Data>() {
                override fun onFailure(e: ApolloException) {
                    if (customLoader.isShowing()) customLoader.dismiss()
                }

                override fun onResponse(response: com.apollographql.apollo.api.Response<GetUserBlockedUsersQuery.Data>) {
                    if (customLoader.isShowing()) customLoader.dismiss()
                    context.runOnUiThread(Runnable {
                        if (response.data != null) {
                            Log.e("BlockedUserList", response.data!!.getUserBlockedUsers.toString())
                            //editor?.putString("blockuserlist", Gson().toJson(response.data))?.apply()
                            //var blockuser = sharedPreferences.getString("blockuserlist", "").toString()
                            val data = Gson().fromJson(Gson().toJson(response.data), Data::class.java)
                            mListBlocked.clear()
                            mListBlocked.addAll(data.getUserBlockedUsers.list)
                            blockedUserAdapter.notifyDataSetChanged()
                        }
                    })
                }
            })
    }

    //function to unblock user
    fun unblockUser(context: Activity,userId: String,position:Int) {
        val customLoader = CustomLoader(context, R.style.CustomLoaderTheme)
        customLoader.show()
        var sharedPreferences = context.getSharedPreferences("MySession", Context.MODE_PRIVATE)
        var editor = sharedPreferences?.edit()

        var blockUserMutation = BlockUserMutation(
            userId = sharedPreferences.getString("MyUserId", "").toString(),
            blockUserId = userId)

        val BASE_URL = ApplicationConstant.INSTANCE.BLOCKUSER
        OkHttpClient.Builder().build()
        val apolloClient = ApolloClient.builder().serverUrl(BASE_URL).okHttpClient(UtilFunctions.INSTANCE.getUnsafeOkHttpClient()!!).build()
        apolloClient.mutate(blockUserMutation).enqueue(
            object : ApolloCall.Callback<BlockUserMutation.Data>() {

                override fun onFailure(e: ApolloException) {
                    if (customLoader.isShowing()) customLoader.dismiss()
                }

                override fun onResponse(response: com.apollographql.apollo.api.Response<BlockUserMutation.Data>) {
                    if (customLoader.isShowing()) customLoader.dismiss()
                    context.runOnUiThread(Runnable {
                        if (response.data != null) {
                            if (response.data!!.blockUser.equals("User unblocked successfully")){
                                mListBlocked.removeAt(position)
                                blockedUserAdapter.notifyDataSetChanged()
                            }
                        }
                    })
                }
            })
    }


}

