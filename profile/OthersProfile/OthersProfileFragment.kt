package com.inpower.webguruz.profile.OthersProfile

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.PorterDuff
import android.graphics.Rect
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.*
import android.view.ViewGroup.MarginLayoutParams
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.annotation.IdRes
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.BitmapImageViewTarget
import com.example.graphql.SchemaReaction.CreateReactionsMutation
import com.example.graphql.SchemaReaction.DeleteReactionByUserPostIDRequestMutation
import com.example.graphql.postFetch.FeedListUserPostsQuery
import com.example.graphql.service1.*
import com.gdacciaro.iOSDialog.iOSDialogBuilder
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.tabs.TabLayout
import com.google.gson.Gson
import com.inpower.webguruz.model.*
import com.inpower.webguruz.R
import com.inpower.webguruz.reactions.DisplayUtil
import com.inpower.webguruz.reactions.ReactionView
import com.inpower.webguruz.utilMethods.ApplicationConstant
import com.inpower.webguruz.utilMethods.CustomLoader
import com.inpower.webguruz.utilMethods.UtilFunctions
import com.inpower.webguruz.adapter.*
import com.inpower.webguruz.profile.ConnectionsFragment
import com.inpower.webguruz.customviews.ShowMoreTextView
import com.inpower.webguruz.listeners.ApiListeners
import com.inpower.webguruz.listeners.RecyclerViewClickListeners
import com.inpower.webguruz.post.UpdatePostActivity
import com.inpower.webguruz.profile.UsersGroupFragment
import com.inpower.webguruz.profile.MyProfile.Setting
import com.wang.avi.AVLoadingIndicatorView
import de.hdodenhof.circleimageview.CircleImageView
import okhttp3.OkHttpClient
import org.jetbrains.annotations.NotNull
import java.io.ByteArrayOutputStream
import java.util.*

//fragment to shoe others profile
class OthersProfileFragment : Fragment(),  RecyclerViewClickListeners, ApiListeners, android.widget.PopupMenu.OnMenuItemClickListener, ReactionView.SelectedReaction{

    lateinit var loader: AVLoadingIndicatorView
    lateinit var sharedPreferences: SharedPreferences
    lateinit var editor: SharedPreferences.Editor
    var tabLayout: TabLayout? = null
    var viewPager: ViewPager? = null

    lateinit var profileDataResponse: Data;
    var tv_title: TextView? = null
    var iv_action: ImageView? = null
    var iv_back: ImageView? = null
    var iv_pic: CircleImageView? = null
    var tv_name: TextView? = null
    var tv_pronoun: TextView? = null
    var tv_connection_count: TextView? = null
    var ll_connection: LinearLayout? = null
    var tv_group_count: TextView? = null
    var ll_group: LinearLayout? = null
    var tv_bio: ShowMoreTextView? = null
    var rl_edit_profile: RelativeLayout? = null
    var tv_edit:TextView?=null
    var rv_post:RecyclerView?=null
    var base64=""

    var page: Int = 1
    var limit: Int = 5
    var hasNext: Boolean = true
    lateinit var nestedScrollview: NestedScrollView
    private lateinit var rvFeeds: RecyclerView
    private lateinit var allFeedAdapter: AllFeedAdapter
    lateinit var mListPosts: MutableList<ListPost>


    var popupWindow: PopupWindow? = null


    lateinit var commentAdapter: CommentAdapter
    lateinit var commentDialog: BottomSheetDialog


    private val MY_CAMERA_PERMISSION_CODE = 100
    private val OPERATION_CAPTURE_PHOTO = 1
    private val OPERATION_CHOOSE_PHOTO = 2

    var iv_addComment: EditText? = null
    var ll: LinearLayout? = null
    var iv_camera: LinearLayout? = null
    var iv_send: LinearLayout? = null
    var ll_lenear: LinearLayout? = null
    var iv_cameraimage: ImageView? = null
    var cv: CardView? = null
    var iv_remove: ImageView? = null

    var replyid: String = "0"
    var commentId: String = "0"
    var position: Int = 0
    var postId: String = ""
    var postUserId: String = ""
    var comments: String = ""
    var replyies: String = ""
    var PostDetail: String = ""
    var commentsUserId: String = ""
    var id_like: ImageView? = null

    var isupdate: Boolean = false
    var isupdateReply: Boolean = false
    var iv_addReply: EditText? = null


    lateinit var replyAdapter: ReplyAdapter

    lateinit var replyDialog: BottomSheetDialog

    lateinit var commentList: MutableList<GetCommentsIdModel>
    lateinit var replyList: MutableList<GetRepliesIdModel>
    private lateinit var tv_commentCount: TextView

    var postUserName: String = ""
    var postUserPic: String = ""
    var base64String: String = ""

    var x: Int = 0
    var flag: Int = 2
    var flagImage: Int = 0
    var Loader: Boolean = true

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_profile_others, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedPreferences = requireActivity().getSharedPreferences("MySession", Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()

        var profileData = sharedPreferences?.getString("profile", "")

        profileDataResponse = Gson().fromJson(profileData, Data::class.java)

        initView()
        setProfileData()
        setTab()
        listener()

        Log.d("profileotherprofileuser","profile section")
        UtilFunctions.INSTANCE.getUserImpacting(requireActivity(),profileDataResponse.profile.id,tv_connection_count!!)
        //UtilFunctions.INSTANCE.getUserInspired(requireActivity(),profileDataResponse.profile.id,tv_connection_count!!)
        UtilFunctions.INSTANCE.getUserGroups(requireActivity(),profileDataResponse.profile.id,tv_group_count!!)
        mListPosts    = ArrayList<ListPost>();
        getAllPosts(page, limit)
        setAllFeedAdapter()

        checkFollowStatus(requireActivity(),profileDataResponse.profile.id)



        commentList   = ArrayList<GetCommentsIdModel>();
        replyList     = ArrayList<GetRepliesIdModel>();

    }

    //function of initialisation of view
    private fun initView() {

        tabLayout = requireView().findViewById<TabLayout>(R.id.tabLayout)
        viewPager = requireView().findViewById<ViewPager>(R.id.viewPager)
        nestedScrollview = requireView().findViewById<NestedScrollView>(R.id.nestedScrollview)
        tv_title = requireView().findViewById<TextView>(R.id.tv_title)
        iv_action = requireView().findViewById<ImageView>(R.id.iv_action)
        iv_back = requireView().findViewById<ImageView>(R.id.iv_back)

        iv_pic = requireView().findViewById<CircleImageView>(R.id.iv_pic)
        tv_name = requireView().findViewById<TextView>(R.id.tv_name)
        tv_pronoun = requireView().findViewById<TextView>(R.id.tv_pronoun)
        tv_connection_count = requireView().findViewById<TextView>(R.id.tv_connection_count)
        tv_group_count = requireView().findViewById<TextView>(R.id.tv_group_count)
        ll_connection = requireView().findViewById<LinearLayout>(R.id.ll_connection)
        ll_group = requireView().findViewById<LinearLayout>(R.id.ll_group)
        tv_bio = requireView().findViewById<ShowMoreTextView>(R.id.tv_bio)
        tv_bio!!.setShowingLine(2);
        tv_bio!!.addShowMoreText("see more")
        tv_bio!!.addShowLessText("see less")
        rl_edit_profile = requireView().findViewById<RelativeLayout>(R.id.rl_edit_profile)
        tv_edit=requireView().findViewById(R.id.tv_edit)


        rvFeeds             = requireView().findViewById(R.id.rv_feedlist)
        nestedScrollview    = requireView().findViewById(R.id.nestedScrollview)


    }

    // function of listeners
    private fun listener() {
        iv_action!!.setOnClickListener(View.OnClickListener {

//            val intent = Intent(context, Setting::class.java)
//            requireContext().startActivity(intent)
//            requireActivity().overridePendingTransition(R.anim.fade_in,R.anim.fade_out)

            if (profileDataResponse.profile.id.equals(sharedPreferences?.getString("MyUserId", "").toString())) {
                val intent = Intent(context, Setting::class.java)
                requireContext().startActivity(intent)
                requireActivity().overridePendingTransition(R.anim.fade_in,R.anim.fade_out)
                Log.d("otherprofile","clicked :" )
            }
            else{
                checkBlockStatus(requireActivity(),profileDataResponse.profile.id)
                Log.d("otherprofileblocked","clicked :" )
            }

        })

        iv_back!!.setOnClickListener(View.OnClickListener {
            requireActivity().onBackPressed()

        })

        rl_edit_profile!!.setOnClickListener(View.OnClickListener {
            if(tv_edit!!.text=="Connected"){
                UtilFunctions.INSTANCE.unfollow(requireActivity(),profileDataResponse.profile.id,profileDataResponse.profile.id,rl_edit_profile,tv_edit!!,tv_connection_count!!)
            }
            else{
                UtilFunctions.INSTANCE.follow(requireActivity(),profileDataResponse.profile.id,profileDataResponse.profile.id,rl_edit_profile,tv_edit!!,tv_connection_count!!)
            }


        })

        ll_connection!!.setOnClickListener(View.OnClickListener {
            sharedPreferences.edit().putString("UserId",profileDataResponse.profile.id).apply()
            sharedPreferences.edit().putString("UserName",profileDataResponse.profile.fullname).apply()
            addFragment(R.id.flFragment, ConnectionsFragment(), "MutualFragment")
        })

        ll_group!!.setOnClickListener(View.OnClickListener {
            sharedPreferences.edit().putString("UserId",profileDataResponse.profile.id).apply()
            sharedPreferences.edit().putString("UserName",profileDataResponse.profile.fullname).apply()
            addFragment(R.id.flFragment, UsersGroupFragment(), "MutualFragment")
        })

    }

    // function to set tab
    private fun setTab() {
        val tabIcons = intArrayOf(R.drawable.ic_table, R.drawable.saved)
        tabLayout!!.addTab(tabLayout!!.newTab().setIcon(tabIcons[0]))
        // tabLayout!!.addTab(tabLayout!!.newTab().setIcon(tabIcons[1]))
        tabLayout!!.tabGravity = TabLayout.GRAVITY_FILL
        val adapter = TabAdapter(requireContext(), requireFragmentManager(), tabLayout!!.tabCount)
        viewPager!!.adapter = adapter
        viewPager!!.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabLayout))
        wrapTabIndicatorToTitle(tabLayout!!, 50, 50)
        tabLayout!!.setOnTabSelectedListener(object : TabLayout.ViewPagerOnTabSelectedListener(viewPager) {
            override fun onTabSelected(tab: TabLayout.Tab) {
                super.onTabSelected(tab)
                val tabIconColor = ContextCompat.getColor(context!!, R.color.black)
                tab.icon!!.setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN)
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
                super.onTabUnselected(tab)
                val tabIconColor =
                    ContextCompat.getColor(context!!, R.color.icon_color_tab)
                tab.icon!!.setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN)
            }

            override fun onTabReselected(tab: TabLayout.Tab) {
                super.onTabReselected(tab)
                val tabIconColor =
                    ContextCompat.getColor(context!!, R.color.black)
                tab.icon!!.setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN)
            }
        })

    }

    private fun wrapTabIndicatorToTitle(tabLayout: TabLayout, externalMargin: Int, internalMargin: Int) {
        val tabStrip = tabLayout.getChildAt(0)
        if (tabStrip is ViewGroup) {
            val childCount = tabStrip.childCount
            for (i in 0 until childCount) {
                val tabView = tabStrip.getChildAt(i)
                //set minimum width to 0 for instead for small texts, indicator is not wrapped as expected
                tabView.minimumWidth = 0
                // set padding to 0 for wrapping indicator as title
                tabView.setPadding(0, tabView.paddingTop, 0, tabView.paddingBottom)
                // setting custom margin between tabs
                if (tabView.layoutParams is MarginLayoutParams) {
                    val layoutParams = tabView.layoutParams as MarginLayoutParams
                    if (i == 0) {
                        // left
                        settingMargin(layoutParams, externalMargin, internalMargin)
                    } else if (i == childCount - 1) {
                        // right
                        settingMargin(layoutParams, internalMargin, externalMargin)
                    } else {
                        // internal
                        settingMargin(layoutParams, internalMargin, internalMargin)
                    }
                }
            }
            tabLayout.requestLayout()
        }
    }

    private fun settingMargin(layoutParams: MarginLayoutParams, start: Int, end: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            layoutParams.marginStart = start
            layoutParams.marginEnd = end
            layoutParams.leftMargin = start
            layoutParams.rightMargin = end
        } else {
            layoutParams.leftMargin = start
            layoutParams.rightMargin = end
        }
    }

    //function to set profile data
    private fun setProfileData() {

        tv_title?.text = profileDataResponse.profile.fullname
        tv_name?.text = profileDataResponse.profile.fullname
        tv_pronoun?.text = profileDataResponse.profile.pronoun
        tv_bio?.text =profileDataResponse.profile.bio
        tv_bio!!.setShowingLine(2);
        tv_bio!!.addShowMoreText("see more")
        tv_bio!!.addShowLessText("see less")
        iv_pic?.let {
            Glide.with(iv_pic!!)
                .asBitmap()
                .load(profileDataResponse.profile.profilePhoto?.replace("hostname     ", ""))
                .placeholder(R.drawable.default_user_placeholder)
                .into(object : BitmapImageViewTarget(iv_pic) {
                    override fun setResource(resource: Bitmap?) {
                        val byteArrayOutputStream = ByteArrayOutputStream()
                        resource?.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
                        val imageBytes: ByteArray = byteArrayOutputStream.toByteArray()
                        var imageString: String = Base64.encodeToString(imageBytes, Base64.DEFAULT)
                        imageString = imageString.replace("\\n".toRegex(), "")
                        base64 = "data:image/png;base64," + imageString
                        super.setResource(resource)
                    }
                })
            iv_pic!!

//        userId = response.profile.id
//        tv_title?.text = response.profile.fullname
//        fullname?.text = response.profile.fullname
//        pronoun?.text = response.profile.pronoun
//        tv_totalconnection?.text = "0"
//        tv_totalconnection?.text = sharedPreferences?.getInt("impactingSize", 0).toString()
//        tv_profile_intro?.text = response.profile.bio
//        if(response.profile.bio.isNullOrEmpty()){
//            tv_profile_intro?.visibility =View.GONE
//        }
//        Log.e("profile", response.profile.profilePhoto.replace("hostname     ", ""))
//        userpic?.let {
//            Glide.with(userpic!!)
//                .asBitmap()
//                .load(response.profile.selfieImg.replace("hostname     ", ""))
//                .placeholder(R.drawable.profilepicplace)
//                .into(object : BitmapImageViewTarget(userpic) {
//                    override fun setResource(resource: Bitmap?) {
//                        val byteArrayOutputStream = ByteArrayOutputStream()
//                        resource?.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
//                        val imageBytes: ByteArray = byteArrayOutputStream.toByteArray()
//                        var imageString: String = Base64.encodeToString(imageBytes, Base64.DEFAULT)
//                        imageString = imageString.replace("\\n".toRegex(), "")
//                        base64 = "data:image/png;base64," + imageString
//                        super.setResource(resource)
//                    }
//                })
//            userpic!!
//        }
        }
    }

    override fun onResume() {
        super.onResume()
        setProfileData()
    }

    private fun addFragment(@IdRes containerViewId: Int, fragment: Fragment, fragmentTag: String) {
        requireActivity().supportFragmentManager.beginTransaction()
            .add(containerViewId, fragment, fragmentTag)
            .addToBackStack("MutualFragment")
            .commit()
    }

    //function to get all feed list
    private fun getAllPosts(page: Int, limit: Int) {

        val customLoader = CustomLoader(context, R.style.CustomLoaderTheme)
        if (Loader) customLoader.show()

        val BASE_URL = ApplicationConstant.INSTANCE.GETALLPOST
        var sharedPreferences = context?.getSharedPreferences("MySession", Context.MODE_PRIVATE)
        var feedListUserPostsQuery = FeedListUserPostsQuery(sharedPreferences?.getString("MyUserId", "").toString(),profileDataResponse.profile.id, page, limit)
        OkHttpClient.Builder().build()
        val apolloClient = ApolloClient.builder().serverUrl(BASE_URL).okHttpClient(UtilFunctions.INSTANCE.getUnsafeOkHttpClient()!!).build()
        apolloClient.query(feedListUserPostsQuery).enqueue(object : ApolloCall.Callback<FeedListUserPostsQuery.Data?>() {
            override fun onResponse(@NotNull response: Response<FeedListUserPostsQuery.Data?>) {
                if (customLoader.isShowing()) customLoader.dismiss()
                requireActivity().runOnUiThread(Runnable {
                    if (response.data != null) {
                        Log.e("AllFeedListData", Gson().toJson(response.data))
                        val data = Gson().fromJson(Gson().toJson(response.data), Data::class.java)
                        mListPosts.addAll(data.feedListUserPosts.list)
                        editor.putString("PostData", Gson().toJson(mListPosts)).apply()
                        hasNext = response.data?.feedListUserPosts?.hasNext!!
                        allFeedAdapter.notifyDataSetChanged()
                    }
                })
            }
            override fun onFailure(@NotNull e: ApolloException) {
                Log.e("AllFeedListError", e.toString())
                if (customLoader.isShowing()) customLoader.dismiss()

            }
        })
    }

    //function to set feed adapter
    private fun setAllFeedAdapter() {
        allFeedAdapter              = AllFeedAdapter(requireActivity(), this, mListPosts,"My profile screen")
        rvFeeds.layoutManager       = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        rvFeeds.adapter             = allFeedAdapter
        rvFeeds.setHasFixedSize(true)
        nestedScrollview.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            // on scroll change we are checking when users scroll as bottom.
            if (scrollY == v.getChildAt(0).measuredHeight - v.measuredHeight) {
                // in this method we are incrementing page number,
                // making progress bar visible and calling get data method.
                page++
                //  loadingPB.setVisibility(View.VISIBLE)
                if (hasNext) {
                    Loader = true
                    getAllPosts(page, limit)
                }

            }
        })
    }

    override fun onClick(position: Int, view: View, id: String) {

        if (view.id == R.id.ivUser) {
            UtilFunctions.INSTANCE.getProfileData(requireActivity(), requireFragmentManager(), id, 2);
        }

        if (view.id == R.id.tvGroup) {
            UtilFunctions.INSTANCE.getGroup(requireActivity(), id, requireFragmentManager());
        }

        if (view.id == R.id.llGroups) {
            UtilFunctions.INSTANCE.getGroup(requireActivity(), id, requireFragmentManager());
        }

        if (view.id == R.id.ivLike) {
            this.position = position;
            postId = id.split(",")[0]
            postUserId = id.split(",")[1]
            postUserName = id.split(",")[2]
            postUserPic = id.split(",")[3]
            var iv = view as ImageView
            id_like = iv;

            if (iv.tag.equals("heart")) {
                onReactionClicked(view as ImageView)
            } else {
                iv?.setImageResource(R.drawable.ic_heart_gray)
                iv?.tag = "heart"
                deleteReaction(requireActivity(), postId,sharedPreferences.getString("MyUserId", "").toString())
            }
        }

        if (view.id == R.id.ivComment) {
            postId = id.split(",")[0]
            postUserId = id.split(",")[1]
            postUserName = id.split(",")[2]
            postUserPic = id.split(",")[3]
            showCommentDialog(postId)
        }

        if (view.id == R.id.tv_reply) {
            // commentDialog.dismiss()
            showReplyDialog(id, postId)
        }

        if (view.id == R.id.ivThreeDot) {
            this.position = position;
            postId = id.split(",")[0]
            commentsUserId = id.split(",")[1]
            showActionPickerDialog("Post");
        }

        if (view.id == R.id.iv_threedotComent) {
            commentId = id.split("~")[0];
            comments = id.split("~")[1];
            commentsUserId = id.split("~")[2];
            this.position = position;
            showActionPickerDialog("Comment");
        }

        if (view.id == R.id.iv_threedot) {
            replyid = id.split("~")[0];
            replyies = id.split("~")[1]
            commentsUserId = id.split("~")[2];
            this.position = position;
            showActionPickerDialog("Reply");
        }
    }

    override fun onClick(position: Int, view: View) {
    }

    //function to implement reaction
    private fun onReactionClicked(anchorView: ImageView) {
        this.position = position
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
            showReactionPopup(anchorView)
        } else {
            showReactionPopupPreLollipop(anchorView)
        }
    }

    //function to implement reaction
    private fun showReactionPopupPreLollipop(anchorView: View) {

        val popUpView = layoutInflater.inflate(R.layout.popup_view, null)
        val width = LinearLayout.LayoutParams.MATCH_PARENT
        val height = LinearLayout.LayoutParams.WRAP_CONTENT
        val focusable = true
        popupWindow = PopupWindow(popUpView, width, height, focusable)
        popupWindow!!.setOutsideTouchable(true)
        popupWindow!!.setFocusable(true)
        popupWindow!!.setTouchable(true)
        val layout = ReactionView(requireContext(), this)
        val relativeLayout = popUpView.findViewById<View>(R.id.rlpopuplayout) as RelativeLayout
        relativeLayout.addView(layout)
        if (popupWindow != null && popupWindow!!.isShowing()) {
            popupWindow!!.dismiss()
        } else {
            var a = Resources.getSystem().displayMetrics
            var height = 2.4 * anchorView.height
            if (a.heightPixels < 1400) {
                height = 3.1 * anchorView.height
            }
            popupWindow!!.showAsDropDown(
                anchorView, 0, -DisplayUtil.dpToPx((height).toInt())
            )
        }
    }

    //function to implement reaction
    private fun showReactionPopup(anchorView: ImageView) {
        id_like = anchorView;
        val layout = ReactionView(requireContext(), this)
        val width = LinearLayout.LayoutParams.MATCH_PARENT
        val height = LinearLayout.LayoutParams.WRAP_CONTENT
        val focusable = true
        popupWindow = PopupWindow(layout, width, height, focusable)
        popupWindow!!.setOutsideTouchable(true)
        popupWindow!!.setFocusable(true)
        popupWindow!!.setTouchable(true)
        if (popupWindow != null && popupWindow!!.isShowing()) {
            popupWindow!!.dismiss()
        } else {

            var a = Resources.getSystem().displayMetrics
            var height = 2.4 * anchorView.height
            if (a.heightPixels < 1400) {
                height = 3.1 * anchorView.height
            }
            popupWindow!!.showAsDropDown(
                anchorView,
                0,
                -DisplayUtil.dpToPx((height).toInt())
            )
        }
    }

    //function to select reaction
    override fun onSelectReaction(selectedReaction: String?, drwable: Int) {
        Glide.with(id_like!!).load(drwable).into(id_like!!)
        id_like?.tag = selectedReaction
        if (UtilFunctions.INSTANCE.isNetworkAvailable(requireContext())) {
            var a = "love";
            createReaction(
                requireActivity(),
                a,
                postId,
                sharedPreferences.getString("MyUserId", "").toString(),
                postUserId,
                postUserName,
                postUserPic
            )
        }
        if (popupWindow != null && popupWindow!!.isShowing()) {
            popupWindow!!.dismiss()
        }

    }

    //function to create reaction
    fun createReaction(context: Activity, reactType: String, postId: String, userId: String, receiverId: String, receiverName: String, receiverPic: String) {
        val customLoader = CustomLoader(context, R.style.CustomLoaderTheme)
        customLoader.show()
        val upvotePostMutation = CreateReactionsMutation(
            reactType = reactType,
            postId = postId,
            userId = userId
        )
        val BASE_URL = ApplicationConstant.INSTANCE.BASEURLREACTION
        OkHttpClient.Builder().build()
        val apolloClient = ApolloClient.builder().serverUrl(BASE_URL).okHttpClient(UtilFunctions.INSTANCE.getUnsafeOkHttpClient()!!).build()
        apolloClient.mutate(upvotePostMutation).enqueue(
            object : ApolloCall.Callback<CreateReactionsMutation.Data>() {
                override fun onFailure(e: ApolloException) {
                    Log.e("createReactionError", e.toString())
                    context.runOnUiThread(Runnable {
                        UtilFunctions.INSTANCE.errorToast(context, e.toString())
                        if (customLoader.isShowing()) customLoader.dismiss()
                    })
                }

                override fun onResponse(response: Response<CreateReactionsMutation.Data>) {
                    Log.e("createReactionResponse", Gson().toJson(response.data).toString())
                    context.runOnUiThread(Runnable {
                        if (customLoader.isShowing()) customLoader.dismiss()
                        if(response.data?.createReactions?.reactType?.equals("love") == true){
                            val count=mListPosts[position].totalReactions.toInt()+1
                            mListPosts[position].totalReactions=count.toString()
                            allFeedAdapter.notifyDataSetChanged()
                        }
                        var sharedPreferences = context.getSharedPreferences("MySession", Context.MODE_PRIVATE)
                        var others=sharedPreferences.getString("MyUserId", "").toString()+", "+sharedPreferences.getString("Fullname", "").toString()+", "+sharedPreferences.getString("ProfilePic", "").toString()+", "+postId+", like"
                        Log.e("othersData",others)

                        UtilFunctions.INSTANCE.sendNotificationToServer(context, postId, receiverName, receiverId, others,"postLike");

                    })
                }
            })
    }

    //function to delete reaction
    private fun deleteReaction(context: Activity, postId: String, userId:String) {
        val customLoader = CustomLoader(context, R.style.CustomLoaderTheme)
        customLoader.show()
        val upvotePostMutation = DeleteReactionByUserPostIDRequestMutation(
            postId = postId,
            userId = userId
        )
        val BASE_URL = ApplicationConstant.INSTANCE.BASEURLREACTION
        val okHttpClient = OkHttpClient.Builder().build()
        val apolloClient = ApolloClient.builder().serverUrl(BASE_URL).okHttpClient(UtilFunctions.INSTANCE.getUnsafeOkHttpClient()!!).build()
        apolloClient.mutate(upvotePostMutation).enqueue(object : ApolloCall.Callback<DeleteReactionByUserPostIDRequestMutation.Data>() {
                override fun onFailure(e: ApolloException) {
                    context.runOnUiThread(Runnable {
                        UtilFunctions.INSTANCE.errorToast(context, e.toString())
                        if (customLoader.isShowing()) customLoader.dismiss()
                    })
                }

                override fun onResponse(response: com.apollographql.apollo.api.Response<DeleteReactionByUserPostIDRequestMutation.Data>) {
                    context.runOnUiThread(Runnable {
                        if (customLoader.isShowing()) customLoader.dismiss()
                        if(response.data?.deleteReactionByUserPostIDRequest?.equals("Deleted") == true){
                            val count=mListPosts[position].totalReactions.toInt()-1
                            mListPosts[position].totalReactions=count.toString()
                            allFeedAdapter.notifyDataSetChanged()
                        }
                    })
                }
            })

    }

    //function to show comment dialog
    private fun showCommentDialog(id: String) {
        commentDialog = BottomSheetDialog(requireContext(), R.style.AppBottomSheetDialogTheme)
        commentDialog.setContentView(R.layout.sheet_commentbottom)

        val parentContainer = commentDialog.findViewById<ScrollView>(R.id.bottomsheet)
        val mRootWindow: Window = requireActivity().getWindow()
        val mRootView: View = mRootWindow.decorView.findViewById(android.R.id.content)


        val recyclerView: RecyclerView = commentDialog.findViewById(R.id.rv_comment)!!
        val iv_close: ImageView = commentDialog.findViewById(R.id.iv_close)!!
        iv_cameraimage = commentDialog.findViewById(R.id.iv_cameraimage)!!
        ll = commentDialog.findViewById(R.id.lower)!!
        cv = commentDialog.findViewById(R.id.cv)!!
        iv_remove = commentDialog.findViewById(R.id.iv_remove)!!
        ll_lenear = commentDialog.findViewById(R.id.ll_lenear)!!
        iv_send = commentDialog.findViewById(R.id.iv_send)!!
        iv_addComment = commentDialog.findViewById(R.id.iv_addComment)!!
        tv_commentCount = commentDialog.findViewById(R.id.tv_commentCount)!!
        iv_camera = commentDialog.findViewById(R.id.iv_camera)!!

        loader = commentDialog.findViewById(R.id.loader)!!

        commentDialog?.setOnShowListener {
            val dialogParent = parentContainer?.parent as View
            BottomSheetBehavior.from(dialogParent).peekHeight = parentContainer.height
            dialogParent.requestLayout()
        }
        iv_close.setOnClickListener(View.OnClickListener {
            commentDialog.dismiss()
        })
        iv_camera?.setOnClickListener(View.OnClickListener {
            showImagePickerDialog(requireContext())
        })
        iv_remove?.setOnClickListener(View.OnClickListener {
            cv?.visibility = View.GONE
            base64String =""

        })
        ll_lenear?.setOnClickListener(View.OnClickListener {
            commentDialog.dismiss()
        })
        iv_send?.setOnClickListener(View.OnClickListener {
            if (iv_addComment?.text?.isNotEmpty() == true) {
                loader.visibility = View.VISIBLE
                if (isupdate) {
                    isupdate = false
                    UtilFunctions.INSTANCE.updateComment(requireActivity(), postId, commentId, iv_addComment?.text.toString(), this, loader, base64String)
                } else {
                    UtilFunctions.INSTANCE.sendComment(requireActivity(), postId, iv_addComment?.text.toString(), this, loader, postUserName, postUserId, postUserPic, base64String)
                }

                iv_addComment?.setText("")
                cv?.visibility = View.GONE
                base64String = ""
            } else {
                iv_addComment?.setError("Please write comment")
            }
        })

        commentAdapter = CommentAdapter(requireActivity(), this, commentList)
        recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        parentContainer?.getViewTreeObserver()?.addOnGlobalLayoutListener(ViewTreeObserver.OnGlobalLayoutListener {
            val r = Rect()
            parentContainer?.getWindowVisibleDisplayFrame(r)
            val screenHeight: Int = parentContainer?.getRootView().getHeight()
            val keypadHeight = screenHeight - r.bottom
            if (keypadHeight > screenHeight * 0.15) {
                val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                params.setMargins(0, 0, 0, keypadHeight - getResources().getDimension(R.dimen._110sdp).toInt())
                ll?.setLayoutParams(params)
            } else {
                val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                params.setMargins(0, 0, 0, 0)
                ll?.setLayoutParams(params)
            }
        })
        recyclerView.adapter = commentAdapter
        commentDialog.show()
        loader.visibility = View.VISIBLE
        commentList.clear()
        UtilFunctions.INSTANCE.getComments(requireActivity(), id, this, loader)

    }

    //function to show reply dialog
    private fun showReplyDialog(commentId: String, postId: String) {
        replyDialog = BottomSheetDialog(requireContext(), R.style.AppBottomSheetDialogTheme)
        replyDialog.setContentView(R.layout.post_comment_reply)
        val parentContainer = replyDialog.findViewById<ScrollView>(R.id.bottomsheet)


        iv_cameraimage = replyDialog.findViewById(R.id.iv_cameraimage)!!
        cv = replyDialog.findViewById(R.id.cv)!!
        iv_remove = replyDialog.findViewById(R.id.iv_remove)!!
        iv_send = replyDialog.findViewById(R.id.iv_send)!!
        iv_camera = replyDialog.findViewById(R.id.iv_camera)!!
        ll = replyDialog.findViewById(R.id.lower)!!
        val recyclerView: RecyclerView = replyDialog.findViewById(R.id.rv_reply)!!
        val loader: AVLoadingIndicatorView = replyDialog.findViewById(R.id.loader)!!
        val iv_back_arrow: ImageView = replyDialog.findViewById(R.id.iv_back_arrow)!!
        iv_addReply = replyDialog.findViewById(R.id.iv_addComment)!!
        tv_commentCount = replyDialog.findViewById(R.id.tv_replyCount)!!

        replyDialog?.setOnShowListener {
            val dialogParent = parentContainer?.parent as View
            BottomSheetBehavior.from(dialogParent).peekHeight = parentContainer.height
            dialogParent.requestLayout()
        }


        replyAdapter = ReplyAdapter(requireActivity(), replyList, this)
        recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        parentContainer?.getViewTreeObserver()?.addOnGlobalLayoutListener(ViewTreeObserver.OnGlobalLayoutListener {
            val r = Rect()
            parentContainer?.getWindowVisibleDisplayFrame(r)
            val screenHeight: Int = parentContainer?.getRootView().getHeight()
            val keypadHeight = screenHeight - r.bottom
            if (keypadHeight > screenHeight * 0.15) {
                val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                params.setMargins(
                    0,
                    0,
                    0,
                    keypadHeight - getResources().getDimension(R.dimen._40sdp).toInt()
                )
                ll?.setLayoutParams(params)
            } else {
                val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                params.setMargins(0, 0, 0, 0)
                ll?.setLayoutParams(params)
            }
        })
        iv_back_arrow.setOnClickListener(View.OnClickListener {
            replyDialog.dismiss()
        })
        iv_camera?.setOnClickListener(View.OnClickListener {
            showImagePickerDialog(requireContext())
        })
        iv_remove?.setOnClickListener(View.OnClickListener {
            cv?.visibility = View.GONE
            base64String =" "
        })
        iv_send?.setOnClickListener(View.OnClickListener {
            if (iv_addReply?.text?.isNotEmpty() == true) {
                loader.visibility = View.VISIBLE
                if (isupdateReply) {
                    isupdateReply = false
                    UtilFunctions.INSTANCE.updateReply(
                        requireActivity(),
                        postId,
                        commentId,
                        replyid,
                        iv_addReply?.text.toString(),
                        this,
                        loader,
                        base64String
                    )
                } else {
                    UtilFunctions.INSTANCE.sendReply(
                        requireActivity(),
                        postId,
                        commentId,
                        iv_addReply?.text.toString(),
                        this,
                        loader,
                        postUserName,
                        postUserId,
                        postUserPic,
                        base64String
                    )
                }
                cv?.visibility = View.GONE
                base64String = ""
                iv_addReply?.setText("")
            } else {
                iv_addReply?.setError("Please write reply")
            }
        })


        recyclerView.adapter = replyAdapter
        replyDialog.show()
        loader.visibility = View.VISIBLE
        replyList.clear()
        UtilFunctions.INSTANCE.getReplies(requireActivity(), commentId, this, loader)

    }

    //function to show action picker dialog
    private fun showActionPickerDialog(flag: String) {

        val actionDialog: BottomSheetDialog = BottomSheetDialog(requireContext(), R.style.AppBottomSheetDialogThemeTransparent)
        actionDialog.setContentView(R.layout.action_choser_sheet)
        actionDialog.show()

        val tv_delete: TextView = actionDialog.findViewById(R.id.tv_delete)!!
        val tv_update: TextView = actionDialog.findViewById(R.id.tv_update)!!
        val tv_report: TextView = actionDialog.findViewById(R.id.tv_report)!!
        val tv_cancel: TextView = actionDialog.findViewById(R.id.tv_cancel)!!
        val v1: View = actionDialog.findViewById(R.id.v1)!!
        val v2: View = actionDialog.findViewById(R.id.v2)!!

        if (commentsUserId == sharedPreferences.getString("MyUserId", "").toString()) {
            tv_delete.visibility = View.VISIBLE
            tv_update.visibility = View.VISIBLE
            tv_report.visibility = View.GONE
            v1.visibility = View.GONE
            tv_update.setBackground(requireActivity().getDrawable(R.drawable.popupbottom))
        }
        else {
            tv_delete.visibility = View.GONE
            tv_update.visibility = View.GONE
            tv_report.visibility = View.VISIBLE
            v1.visibility = View.GONE
            v2.visibility = View.GONE
            tv_report.setBackground(requireActivity().getDrawable(R.drawable.popupmiddle))
        }

        tv_delete.setOnClickListener(View.OnClickListener {

            var msg="Are you sure?"
            when (flag) {
                "Post" -> {
                    msg="Are you sure you want to delete this post?"
                }
                "Comment" -> {
                    msg="Are you sure you want to delete this comment?"
                }
                "Reply" -> {
                    msg="Are you sure you want to delete this reply?"
                }
            }

            var d = iOSDialogBuilder(requireContext());
            d.setTitle("Delete?")
                .setSubtitle(msg)
                .setBoldPositiveLabel(false)
                .setCancelable(false)
                .setPositiveListener("Yes") { dialog ->
                    when (flag) {
                        "Post" -> {
                            UtilFunctions.INSTANCE.deletePost(requireActivity(), postId)
                            mListPosts.removeAt(position)
                            allFeedAdapter.notifyDataSetChanged()
                        }
                        "Comment" -> {
                            UtilFunctions.INSTANCE.deleteComment(requireActivity(), commentId)
                            commentList.removeAt(position)
                            commentAdapter.notifyDataSetChanged()
                            tv_commentCount.text=commentList.size.toString()+" Comments"


                        }
                        "Reply" -> {
                            UtilFunctions.INSTANCE.deleteReply(requireActivity(), replyid)
                            replyList.removeAt(position)
                            replyAdapter.notifyDataSetChanged()
                            tv_commentCount.text=replyList.size.toString()+" Comments"

                        }
                    }
                    dialog.dismiss()
                }
                .setNegativeListener(
                    "Cancel"
                ) { dialog -> dialog.dismiss() }
                .build().show()


            actionDialog.dismiss()
        })
        tv_update.setOnClickListener(View.OnClickListener {
            when (flag) {
                "Post" -> {
                    var bundle = Bundle()
                    bundle.putString("PostDetail", PostDetail)
                    var fragment = UpdatePostActivity()
                    fragment.arguments = bundle
                    UtilFunctions.INSTANCE.setCurrentFragment(fragment, "updateActivity", requireFragmentManager())
                    actionDialog.dismiss()
                }
                "Comment" -> {
                    isupdate = true
                    iv_addComment?.setText(comments.toString())
                    if(commentList.get(position).medias[0].status.equals("True")){
                        cv?.visibility = View.VISIBLE
                        Glide.with(iv_cameraimage!!)
                            .load(commentList.get(position).medias[0].mediaUrl)
                            .fitCenter()
                            .thumbnail(0.1f)
                            .into(iv_cameraimage!!)
                    }

                    iv_addComment?.requestFocus();
                    val imm: InputMethodManager? = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
                    imm?.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
                    actionDialog.dismiss()

                }
                "Reply" -> {
                    isupdateReply = true
                    iv_addReply?.setText(replyies.toString())
                    if(replyList.get(position).medias[0].status.equals("True")){
                        cv?.visibility = View.VISIBLE
                        Glide.with(iv_cameraimage!!)
                            .load(replyList.get(position).medias[0].mediaUrl)
                            .fitCenter()
                            .thumbnail(0.1f)
                            .into(iv_cameraimage!!)
                    }
                    iv_addReply?.requestFocus();
                    val imm: InputMethodManager? = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
                    imm?.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
                    actionDialog.dismiss()
                }
            }
        })
        tv_report.setOnClickListener(View.OnClickListener {
            when (flag) {
                "Post" -> {
                    actionDialog.dismiss()
                    UtilFunctions.INSTANCE.getPostReport(requireActivity(),postId)
                }
                "Comment" -> {
                    actionDialog.dismiss()
                    UtilFunctions.INSTANCE.getCommentReport(requireActivity(),commentId)

                }
                "Reply" -> {
                    actionDialog.dismiss()
                    UtilFunctions.INSTANCE.getCommentReport(requireActivity(),replyid)
                }
            }
        })
        tv_cancel.setOnClickListener(View.OnClickListener { actionDialog.dismiss() })

    }

    override fun onResult(response: String) {
        // No TODO here
        try {
            commentList.clear()
            loader.visibility = View.GONE
            Log.e("callback", response);
            if (response != null) {
                var comment = Gson().fromJson(response, Comment::class.java)
                commentList.addAll(comment.getCommentsId)
                commentAdapter.notifyDataSetChanged()
                tv_commentCount.text = comment.getCommentsId.size.toString() + " Comments"
            } else {
                tv_commentCount.text = "0" + " Comments"
            }
        } catch (e: Exception) {
            tv_commentCount.text = "0" + " Comments"
        }
    }

    override fun onResultType(response: String, type: String) {
        //TODO("Not yet implemented")
        if (type.equals("reply")) {
            replyList.clear()
            loader.visibility = View.GONE
            Log.e("callback", response);
            if (response != null) {
                var comment = Gson().fromJson(response, Comment::class.java)
                if (comment.getRepyId != null) {
                    replyList.addAll(comment.getRepyId)
                    replyAdapter.notifyDataSetChanged()
                    tv_commentCount.text = comment.getRepyId.size.toString() + " Replies"
                } else {
                    tv_commentCount.text = "0" + " Replies"
                }
            } else {
                tv_commentCount.text = "0" + " Replies"
            }
        }
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        return when (item!!.itemId) {
            R.id.deletePost -> {
                if (UtilFunctions.INSTANCE.isNetworkAvailable(requireContext())) {
                    UtilFunctions.INSTANCE.deleteReply(requireActivity(), replyid)
                    replyList.removeAt(position)
                    replyAdapter.notifyDataSetChanged()
                } else {

                }
                true
            }

//            R.id.updatePost ->                 // do your code
//                true
            else -> false
        }
    }

    //function to show image picker dialog
    private fun showImagePickerDialog(context: Context) {
        var choserDialog: BottomSheetDialog = BottomSheetDialog(requireContext(), R.style.AppBottomSheetDialogThemeTransparent)
        choserDialog.setContentView(R.layout.image_picker_sheet)
        choserDialog.show()


        val tv_gallery: TextView = choserDialog.findViewById(R.id.tv_gallery)!!
        val tv_camera: TextView = choserDialog.findViewById(R.id.tv_camera)!!
        val tv_cancel: TextView = choserDialog.findViewById(R.id.tv_cancel)!!
        val v1: View = choserDialog.findViewById(R.id.v1)!!


        tv_gallery.setOnClickListener(View.OnClickListener {
            flagImage = 1
            val checkSelfPermission = ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
            if (checkSelfPermission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(requireActivity(), arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
            } else {
                openGallery()
            }
            choserDialog.dismiss()
        })
        tv_camera.setOnClickListener(View.OnClickListener {
            flagImage = 2
            val checkSelfPermission = ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.CAMERA
            )
            if (checkSelfPermission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(requireActivity(), arrayOf(android.Manifest.permission.CAMERA), 1)
            } else {
                capturePhoto()
            }
            choserDialog.dismiss()

        })
        tv_cancel.setOnClickListener(View.OnClickListener {
            choserDialog.dismiss()
        })


    }

    @SuppressLint("WrongConstant")
    private fun capturePhoto() {
        if (PermissionChecker.checkSelfPermission(requireActivity(), Manifest.permission.CAMERA) !== PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.CAMERA), MY_CAMERA_PERMISSION_CODE)
        } else {
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(cameraIntent, OPERATION_CAPTURE_PHOTO)
        }
    }

    private fun openGallery() {
        if (Build.VERSION.SDK_INT < 19) {
            var intent = Intent()
            intent.type = "image/*"
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false)
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent, "Choose Pictures"), OPERATION_CHOOSE_PHOTO)
        } else {
            // For latest versions API LEVEL 19+
            var intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.type = "image/*"
            startActivityForResult(intent, OPERATION_CHOOSE_PHOTO);
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            OPERATION_CAPTURE_PHOTO ->
                if (resultCode == Activity.RESULT_OK) {
                    val bitmap = data!!.extras!!.get("data") as Bitmap
                    iv_cameraimage!!.setImageBitmap(bitmap)
                    cv?.visibility = View.VISIBLE
                    val byteArrayOutputStream = ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
                    val imageBytes: ByteArray = byteArrayOutputStream.toByteArray()
                    var imageString: String = Base64.encodeToString(imageBytes, Base64.DEFAULT)
                    imageString = imageString.replace("\\n".toRegex(), "")
                    Log.e("new", "data:image/png;base64," + imageString)
                    base64String = "data:image/png;base64," + imageString

                }
            OPERATION_CHOOSE_PHOTO ->
                if (resultCode == Activity.RESULT_OK) {
                    var imageUri: Uri = data?.data!!
                    val photoBmp: Bitmap = MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, imageUri)
                    iv_cameraimage!!.setImageBitmap(photoBmp)
                    cv?.visibility = View.VISIBLE
                    val byteArrayOutputStream = ByteArrayOutputStream()
                    photoBmp.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
                    val imageBytes: ByteArray = byteArrayOutputStream.toByteArray()
                    var imageString: String = Base64.encodeToString(imageBytes, Base64.DEFAULT)
                    imageString = imageString.replace("\\n".toRegex(), "")
                    base64String = "data:image/png;base64," + imageString
                }
        }
    }

    //function to show action dialog
    private fun showActionPopup(isBlocked:Boolean){

        val actionDialog: BottomSheetDialog = BottomSheetDialog(requireContext(), R.style.AppBottomSheetDialogThemeTransparent)
        actionDialog.setContentView(R.layout.action_choser_sheet)
        actionDialog.show()

        val tv_delete: TextView = actionDialog.findViewById(R.id.tv_delete)!!
        val tv_update: TextView = actionDialog.findViewById(R.id.tv_update)!!
        val tv_report: TextView = actionDialog.findViewById(R.id.tv_report)!!
        val tv_block :TextView  =actionDialog.findViewById(R.id.tv_block)!!
        val tv_cancel: TextView = actionDialog.findViewById(R.id.tv_cancel)!!
        val v1: View = actionDialog.findViewById(R.id.v1)!!
        val v2: View = actionDialog.findViewById(R.id.v2)!!

        Log.e("isBlocked",isBlocked.toString())

        if (isBlocked){
            tv_block!!.text="Unblock"
        }
        else{
            tv_block!!.text="Block"

        }


        tv_delete.visibility=View.GONE
        tv_update.visibility=View.GONE
        //tv_report.visibility=View.GONE
        v1.visibility=View.GONE
        v2.visibility=View.GONE
        tv_report.setBackground(requireActivity().getDrawable(R.drawable.popupmiddle))
        tv_block?.visibility=View.VISIBLE


        tv_report.setOnClickListener(View.OnClickListener {
            actionDialog.dismiss()
            UtilFunctions.INSTANCE.getProfileReport(requireActivity(),profileDataResponse.profile.id)
        })
        tv_block!!.setOnClickListener(View.OnClickListener {
            UtilFunctions.INSTANCE.blockUnblockUser(requireActivity(),profileDataResponse.profile.id,tv_block!!)
            actionDialog.dismiss() })
        tv_cancel.setOnClickListener(View.OnClickListener { actionDialog.dismiss() })

    }

    //function to check blocked status
    private fun checkBlockStatus(context: Activity,blockUserId: String) {
        val customLoader = CustomLoader(context, R.style.CustomLoaderTheme)
        customLoader.show()
        Log.d("otherprofileblocked","checking :" + blockUserId + "   myuserid: " + sharedPreferences.getString("MyUserId", "").toString() )
        var sharedPreferences = context.getSharedPreferences("MySession", Context.MODE_PRIVATE)
        var editor = sharedPreferences?.edit()

        var checkUserBlockedOrNotQuery = CheckUserBlockedOrNotQuery(
            userId = sharedPreferences.getString("MyUserId", "").toString(),
            blockUserId = blockUserId)

        val BASE_URL = ApplicationConstant.INSTANCE.BLOCKUSER
        val okHttpClient = OkHttpClient.Builder().build()
        val apolloClient = ApolloClient.builder().serverUrl(BASE_URL).okHttpClient(UtilFunctions.INSTANCE.getUnsafeOkHttpClient()!!).build()
        apolloClient.query(checkUserBlockedOrNotQuery).enqueue(
            object : ApolloCall.Callback<CheckUserBlockedOrNotQuery.Data>() {

                override fun onFailure(e: ApolloException) {
                    if (customLoader.isShowing()) customLoader.dismiss()
                    Log.e("BlockedUserStatus", e.toString())
                    Log.d("otherprofileblocked","failure :" )

                }

                override fun onResponse(response: com.apollographql.apollo.api.Response<CheckUserBlockedOrNotQuery.Data>) {
                    if (customLoader.isShowing()) customLoader.dismiss()
                    Log.d("otherprofileblocked","onres :" )
                    context.runOnUiThread(Runnable {
                        if (response.data != null) {
                            Log.d("otherprofileblocked","if not null :" )
                            if (response.data!!.checkUserBlockedOrNot == true){
                                //tv_block?.text="Unblock"
                                showActionPopup(true)
                                Log.d("otherprofileblocked","blocked true :" )
                            }
                            else{
                                //tv_block?.text="Block"
                                showActionPopup(false)
                                Log.d("otherprofileblocked","blocked false :" )

                            }
                            Log.e("BlockedUserStatus", response.data!!.checkUserBlockedOrNot.toString())
                        }
                    })
                }


            })

    }

    //function to check follow status
    private fun checkFollowStatus(context: Activity, followerId: String) {
        val customLoader = CustomLoader(context, R.style.CustomLoaderTheme)
        customLoader.show()
        Log.d("otherprofileusercheck"," checkFollowStatus  " + followerId + "userid  "+ sharedPreferences.getString("MyUserId", "").toString())
        // id revered because api not working for right condition
        var checkUsersConnectedOrNotQuery = CheckUsersConnectedOrNotQuery(
//            userId = sharedPreferences.getString("MyUserId", "").toString(),
//            followerUserId = followerId)
        userId =followerId ,
        followerUserId = sharedPreferences.getString("MyUserId", "").toString())

        val BASE_URL = ApplicationConstant.INSTANCE.BLOCKUSER
        val okHttpClient = OkHttpClient.Builder().build()
        val apolloClient = ApolloClient.builder().serverUrl(BASE_URL).okHttpClient(UtilFunctions.INSTANCE.getUnsafeOkHttpClient()!!).build()
        apolloClient.query(checkUsersConnectedOrNotQuery).enqueue(object : ApolloCall.Callback<CheckUsersConnectedOrNotQuery.Data>() {
                override fun onFailure(e: ApolloException) {
                    if (customLoader.isShowing()) customLoader.dismiss()
                    Log.d("otherprofileusercheck"," on failure")
                }

                override fun onResponse(response: com.apollographql.apollo.api.Response<CheckUsersConnectedOrNotQuery.Data>) {
                    if (customLoader.isShowing()) customLoader.dismiss()
                    Log.d("otherprofileusercheck"," on response")
                    context.runOnUiThread(Runnable {
                        if (response.data != null) {
                            if (response.data!!.checkUsersConnectedOrNot == true){
                                tv_edit?.text = getString(R.string.connected)
                                tv_edit?.setTextColor(ContextCompat.getColor(context, R.color.black));
                                rl_edit_profile?.setBackgroundResource(R.drawable.background_curve_buttton)
                                Log.d("otherprofileusercheck"," Connected")
                            }
                            else{
                                tv_edit?.text = getString(R.string.connect)
                                tv_edit?.setTextColor(ContextCompat.getColor(context, R.color.white));
                                rl_edit_profile?.setBackgroundResource(R.drawable.pink_button_drawable_curve)
                                Log.d("otherprofileusercheck"," Connect")

                            }
                            Log.e("FollowedUserStatus", response.data!!.checkUsersConnectedOrNot.toString())
                            Log.d("otherprofileusercheck"," not null ")
                        }
                    })
                }
            })

    }


}