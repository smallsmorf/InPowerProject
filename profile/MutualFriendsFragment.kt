package com.inpower.webguruz.profile

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.exception.ApolloException
import com.example.graphql.service1.GetImpactingQuery
import com.example.graphql.service1.GetInspiredQuery
import com.google.gson.Gson
import com.inpower.webguruz.model.*
import com.inpower.webguruz.R
import com.inpower.webguruz.utilMethods.ApplicationConstant
import com.inpower.webguruz.utilMethods.CustomLoader
import com.inpower.webguruz.utilMethods.UtilFunctions
import com.inpower.webguruz.adapter.ImpactAdapter
import com.inpower.webguruz.adapter.InspiredAdapter
import com.inpower.webguruz.recyclerviewswip.SimpleDividerItemDecoration
import okhttp3.OkHttpClient
import java.security.KeyStore
import java.security.SecureRandom
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager

//fragment to load mutual friend
class MutualFriendsFragment  : Fragment(), View.OnClickListener {

    var inspired_recyclerView: RecyclerView? = null
    var impact_recyclerView: RecyclerView? = null
    var iv_backfriends: ImageView? = null
    var tv_user_name: TextView? = null
    var Inpacting: TextView? = null
    var inspired: TextView? = null
    var tv_Inspired: LinearLayout? = null
    var tv_Inpacting: LinearLayout? = null
    lateinit var profileDataResponse: Data;
    var sharedPreferences: SharedPreferences? = null
    lateinit var impacts: MutableList<GetImpacting>
    lateinit var impactAdapter: ImpactAdapter
    lateinit var insspired: MutableList<GetInspired>
    lateinit var inspiredAdapter: InspiredAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_connections, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedPreferences = requireActivity().getSharedPreferences("MySession", Context.MODE_PRIVATE)
        var profileData = sharedPreferences?.getString("profile", "")
        profileDataResponse = Gson().fromJson(profileData, Data::class.java)
        initview()

        listeners()

        impacts = ArrayList<GetImpacting>();
        setImpactingRecyclerview()
        getImpacting(requireActivity())
        insspired = ArrayList<GetInspired>();
        setInspiredRecyclerview()
        getInspiring(requireActivity())
    }

    //function to implement listener
    private fun listeners() {
        iv_backfriends!!.setOnClickListener(this)
        tv_Inspired!!.setOnClickListener(this)
        tv_Inpacting!!.setOnClickListener(this)

    }

    //function to initialisation of view
    private fun initview() {

        iv_backfriends = requireView().findViewById(R.id.iv_backfriends)

        tv_user_name = requireView().findViewById(R.id.tv_user_name)
        tv_Inspired = requireView().findViewById(R.id.tv_Inspired)
        tv_Inpacting = requireView().findViewById(R.id.tv_Inpacting)
        inspired = requireView().findViewById(R.id.inspired)
        Inpacting = requireView().findViewById(R.id.Inpacting)
        tv_user_name?.text = profileDataResponse.profile.fullname
    }

    //function to set inspire adapter
    private fun setInspiredRecyclerview() {
        inspired_recyclerView = requireView().findViewById(R.id.inspired_recyclerView)
        inspiredAdapter = InspiredAdapter(requireActivity(), requireFragmentManager(), insspired)
        inspired_recyclerView!!.setHasFixedSize(true)
        inspired_recyclerView!!.setLayoutManager(LinearLayoutManager(activity))
        inspired_recyclerView!!.addItemDecoration(SimpleDividerItemDecoration(requireContext()))
        inspired_recyclerView!!.setAdapter(inspiredAdapter)
        fragmentManager

    }

    //function to set impact adapter
    private fun setImpactingRecyclerview() {
        impact_recyclerView = requireView().findViewById(R.id.impact_recyclerView)
        impactAdapter = ImpactAdapter(requireActivity(), requireFragmentManager(), impacts)
        impact_recyclerView!!.setHasFixedSize(true)
        impact_recyclerView!!.setLayoutManager(LinearLayoutManager(activity))
        impact_recyclerView!!.addItemDecoration(SimpleDividerItemDecoration(requireContext()))
        impact_recyclerView!!.setAdapter(impactAdapter)
        fragmentManager

    }

    //function to get impacting list
    public fun getImpacting(context: Activity) {
        impacts.clear()
        val customLoader = CustomLoader(context, R.style.CustomLoaderTheme)
        customLoader.show()

        var sharedPreferences = context.getSharedPreferences("MySession", Context.MODE_PRIVATE)
        //var editor = sharedPreferences?.edit()
        val BASE_URL = ApplicationConstant.INSTANCE.CREATEPROFILE
        var getGroup = GetImpactingQuery(id = sharedPreferences.getString("userID", "").toString())
        Log.e("ImpactUserId", sharedPreferences.getString("userID", "").toString())
        val okHttpClient = OkHttpClient.Builder().build()
        val apolloClient = ApolloClient.builder().serverUrl(BASE_URL).okHttpClient(UtilFunctions.INSTANCE.getUnsafeOkHttpClient()!!).build()
        apolloClient.query(getGroup).enqueue(
            object : ApolloCall.Callback<GetImpactingQuery.Data>() {

                override fun onFailure(e: ApolloException) {
                    if (customLoader.isShowing()) customLoader.dismiss()
                    context.runOnUiThread(Runnable {
                        UtilFunctions.INSTANCE.errorToast(context, e.toString())
                    })
                    Log.e("ImpactError", Gson().toJson(GetInspiredQuery.Data))
                }

                override fun onResponse(response: com.apollographql.apollo.api.Response<GetImpactingQuery.Data>) {
                    if (customLoader.isShowing()) customLoader.dismiss()
                    requireActivity().runOnUiThread(Runnable {
                        if (response.data != null) {
                            Log.e("ImpactSuccess", Gson().toJson(response.data))
                            val data = Gson().fromJson(Gson().toJson(response.data), Impacts::class.java)
                            impacts.addAll(data.getImpacting)
                            impactAdapter.notifyDataSetChanged()
                        } else {
                            requireActivity().runOnUiThread(Runnable {
                                if (customLoader.isShowing()) customLoader.dismiss()
                                UtilFunctions.INSTANCE.errorToast(requireActivity(), response.errors?.get(0)?.message)
                            })
                        }
                    })
                }
            })
    }

    //function to get inspired list
    public fun getInspiring(context: Activity) {
        insspired.clear()
        val customLoader = CustomLoader(context, R.style.CustomLoaderTheme)
        customLoader.show()

        var sharedPreferences = context.getSharedPreferences("MySession", Context.MODE_PRIVATE)
        val BASE_URL = ApplicationConstant.INSTANCE.CREATEPROFILE
        var getGroup = GetInspiredQuery(id = sharedPreferences.getString("userID", "").toString())

        Log.e("InspiredId", sharedPreferences.getString("userID", "").toString())

        val okHttpClient = OkHttpClient.Builder().build()
        val apolloClient = ApolloClient.builder().serverUrl(BASE_URL).okHttpClient(UtilFunctions.INSTANCE.getUnsafeOkHttpClient()!!).build()
        apolloClient.query(getGroup).enqueue(
            object : ApolloCall.Callback<GetInspiredQuery.Data>() {
                override fun onFailure(e: ApolloException) {
                    if (customLoader.isShowing()) customLoader.dismiss()
                    context.runOnUiThread(Runnable {
                        UtilFunctions.INSTANCE.errorToast(context, e.toString())
                    })
                    Log.e("InspiredError", Gson().toJson(GetInspiredQuery.Data))
                }

                override fun onResponse(response: com.apollographql.apollo.api.Response<GetInspiredQuery.Data>) {
                    if (customLoader.isShowing()) customLoader.dismiss()
                    requireActivity().runOnUiThread(Runnable {
                        if (response.data != null) {
                            Log.e("InspiredSuccess", Gson().toJson(response.data))
                            val data = Gson().fromJson(Gson().toJson(response.data), Inspired::class.java)
                            insspired.addAll(data.getInspired)
                            inspiredAdapter.notifyDataSetChanged()
                        } else {
                            requireActivity().runOnUiThread(Runnable {
                                if (customLoader.isShowing()) customLoader.dismiss()
                                UtilFunctions.INSTANCE.errorToast(requireActivity(), response.errors?.get(0)?.message)
                            })
                        }
                    })
                }
            })
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.iv_backfriends -> {
                requireActivity().onBackPressed()
            }
            R.id.tv_Inspired -> {
                getInspiring(requireActivity())
                tv_Inpacting?.background = null
                Inpacting?.setTextColor(resources.getColor(R.color.bcolor))
                inspired?.setTextColor(resources.getColor(R.color.textcolor))
                tv_Inspired?.background = resources.getDrawable(R.drawable.drawable_public_private)
                inspired_recyclerView?.visibility = View.VISIBLE
                impact_recyclerView?.visibility = View.GONE
            }
            R.id.tv_Inpacting -> {
                getImpacting(requireActivity())
                tv_Inspired?.background = null
                inspired?.setTextColor(resources.getColor(R.color.bcolor))
                Inpacting?.setTextColor(resources.getColor(R.color.textcolor))
                tv_Inpacting?.background = resources.getDrawable(R.drawable.drawable_public_private)
                impact_recyclerView?.visibility = View.VISIBLE
                inspired_recyclerView?.visibility = View.GONE
            }
        }
    }


}