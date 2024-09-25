package com.inpower.webguruz.profile

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.widget.EditText
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

//Fragment to load connections
class ConnectionsFragment : Fragment() {

    var inspired_recyclerView: RecyclerView? = null
    var impact_recyclerView: RecyclerView? = null
    var iv_back: ImageView? = null
    var tv_user_name: TextView? = null
    var Inpacting: TextView? = null
    var inspired: TextView? = null
    var tv_Inspired: LinearLayout? = null
    var tv_Inpacting: LinearLayout? = null
    var sharedPreferences: SharedPreferences? = null
    lateinit var impacts: MutableList<GetImpacting>
    lateinit var impactAdapter: ImpactAdapter
    lateinit var insspired: MutableList<GetInspired>
    lateinit var inspiredAdapter: InspiredAdapter
    var  userId:String=""
    var  userName:String=""
    var et_search:EditText?=null
    var  load="inspired"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_connections, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        sharedPreferences = requireActivity().getSharedPreferences("MySession", Context.MODE_PRIVATE)

        userId=sharedPreferences!!.getString("UserId","").toString()
        userName=sharedPreferences!!.getString("UserName","").toString()

        initView()

        listeners()


        impacts = ArrayList<GetImpacting>();
        setImpactingRecyclerview()
        getImpacting(requireActivity())
        insspired = ArrayList<GetInspired>();
        setInspiringRecyclerview()
        getInspiring(requireActivity())
    }

    //function tto initialisation of view
    private fun initView() {

        iv_back= requireView().findViewById(R.id.iv_back)

        tv_user_name = requireView().findViewById(R.id.tv_user_name)
        tv_Inspired = requireView().findViewById(R.id.tv_Inspired)
        tv_Inpacting = requireView().findViewById(R.id.tv_Inpacting)
        inspired = requireView().findViewById(R.id.inspired)
        Inpacting = requireView().findViewById(R.id.Inpacting)
        et_search=requireView().findViewById(R.id.et_search)

        tv_user_name?.text = userName

        et_search?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
            }
            override fun beforeTextChanged(s: CharSequence, start: Int,count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (load=="inspired"){
                    inspiredAdapter.filter.filter(s)

                }
                else if(load=="impacted"){
                    impactAdapter.filter.filter(s.toString())
                }

            }
        })

    }

    //function to listeners
    private fun listeners() {

        iv_back!!.setOnClickListener(View.OnClickListener {
            requireActivity().onBackPressed()
        })
        tv_Inspired!!.setOnClickListener(View.OnClickListener {
            load="inspired"
            getInspiring(requireActivity())
            tv_Inpacting?.background = null
            Inpacting?.setTextColor(resources.getColor(R.color.bcolor))
            inspired?.setTextColor(resources.getColor(R.color.textcolor))
            tv_Inspired?.background = resources.getDrawable(R.drawable.drawable_public_private)
            inspired_recyclerView?.visibility = View.VISIBLE
            impact_recyclerView?.visibility = View.GONE
        })
        tv_Inpacting!!.setOnClickListener(View.OnClickListener {
            load="impacted"
            getImpacting(requireActivity())
            tv_Inspired?.background = null
            inspired?.setTextColor(resources.getColor(R.color.bcolor))
            Inpacting?.setTextColor(resources.getColor(R.color.textcolor))
            tv_Inpacting?.background = resources.getDrawable(R.drawable.drawable_public_private)
            impact_recyclerView?.visibility = View.VISIBLE
            inspired_recyclerView?.visibility = View.GONE
        })

    }

    //function set impacting list
    private fun setImpactingRecyclerview() {
        impact_recyclerView = requireView().findViewById(R.id.impact_recyclerView)
        impactAdapter = ImpactAdapter(requireActivity(), requireFragmentManager(), impacts)
        impact_recyclerView!!.setHasFixedSize(true)
        impact_recyclerView!!.setLayoutManager(LinearLayoutManager(activity))
        impact_recyclerView!!.addItemDecoration(SimpleDividerItemDecoration(requireContext()))
        impact_recyclerView!!.setAdapter(impactAdapter)
        fragmentManager

    }

    //function set inspired list
    private fun setInspiringRecyclerview() {
        inspired_recyclerView = requireView().findViewById(R.id.inspired_recyclerView)
        inspiredAdapter = InspiredAdapter(requireActivity(), requireFragmentManager(), insspired)
        inspired_recyclerView!!.setHasFixedSize(true)
        inspired_recyclerView!!.setLayoutManager(LinearLayoutManager(activity))
        inspired_recyclerView!!.addItemDecoration(SimpleDividerItemDecoration(requireContext()))
        inspired_recyclerView!!.setAdapter(inspiredAdapter)
        fragmentManager

    }

    //function to get impacting list
    private fun getImpacting(context: Activity) {
        impacts.clear()
        val customLoader = CustomLoader(context, R.style.CustomLoaderTheme)
        customLoader.show()

        var sharedPreferences = context.getSharedPreferences("MySession", Context.MODE_PRIVATE)
        val BASE_URL = ApplicationConstant.INSTANCE.CREATEPROFILE
        var getGroup = GetImpactingQuery(id = userId)

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
                        }
                    })
                }
            })
    }

    //function to get inspired list
    private fun getInspiring(context: Activity) {
        insspired.clear()
        val customLoader = CustomLoader(context, R.style.CustomLoaderTheme)
        customLoader.show()

        var sharedPreferences = context.getSharedPreferences("MySession", Context.MODE_PRIVATE)
        val BASE_URL = ApplicationConstant.INSTANCE.CREATEPROFILE
        var getGroup = GetInspiredQuery(id = userId)

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
                        }
                    })
                }
            })

    }


}