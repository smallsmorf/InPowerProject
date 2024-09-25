package com.inpower.webguruz.profile.MyProfile

import android.os.Build
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.demoproject.zoom.ZoomHelper
import com.inpower.webguruz.R
import com.inpower.webguruz.reactions.ReactionView
import com.inpower.webguruz.listeners.ApiListeners
import com.inpower.webguruz.listeners.RecyclerViewClickListeners
import com.webgurus.attendanceportal.ui.base.BaseFragment

//fragment ot list out my badges
@RequiresApi(Build.VERSION_CODES.HONEYCOMB)
class MyBadges : BaseFragment(), RecyclerViewClickListeners, ZoomHelper.OnZoomStateChangedListener, ApiListeners, PopupMenu.OnMenuItemClickListener, ReactionView.SelectedReaction {
    private lateinit var iv_back: ImageView
    private lateinit var rv_badges: RecyclerView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_my_badges, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        initview()
        listener()
        //setRecyclerview()
    }

    private fun initview() {
        iv_back = requireView().findViewById(R.id.iv_back)
        rv_badges = requireView().findViewById(R.id.rv_badges)

    }

    private  fun listener(){
        iv_back!!.setOnClickListener(View.OnClickListener {
            requireActivity().onBackPressed()
        })
    }


    override fun onPause() {
        super.onPause()
        ZoomHelper.getInstance().removeOnZoomStateChangedListener(this)
    }

    override fun onResume() {
        super.onResume()
        ZoomHelper.getInstance().addOnZoomStateChangedListener(this)
    }

    override fun onZoomStateChanged(zoomHelper: ZoomHelper, zoomableView: View, isZooming: Boolean) {
    }

    override fun onClick(position: Int, view: View) {
        TODO("Not yet implemented")
    }

    override fun onClick(position: Int, view: View, id: String) {
        TODO("Not yet implemented")
    }

    override fun onSelectReaction(selectedReaction: String?, drawable: Int) {
        TODO("Not yet implemented")
    }

    override fun onResult(response: String) {
        TODO("Not yet implemented")
    }

    override fun onResultType(response: String, type: String) {
        TODO("Not yet implemented")
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        TODO("Not yet implemented")
    }

}

