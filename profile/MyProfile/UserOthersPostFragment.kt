package com.inpower.webguruz.profile.MyProfile


import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Rect
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.PopupMenu
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import com.bumptech.glide.Glide
import com.example.graphql.SchemaReaction.CreateReactionsMutation
import com.example.graphql.SchemaReaction.DeleteReactionByUserPostIDRequestMutation
import com.example.graphql.postFetch.FeedListUserAnonymousPostsQuery
import com.example.graphql.postFetch.FeedListUserSavedPostsQuery
import com.gdacciaro.iOSDialog.iOSDialogBuilder
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import com.inpower.webguruz.R
import com.inpower.webguruz.adapter.AllFeedAdapter
import com.inpower.webguruz.adapter.CommentAdapter
import com.inpower.webguruz.adapter.ReplyAdapter
import com.inpower.webguruz.reactions.ReactionView
import com.inpower.webguruz.listeners.ApiListeners
import com.inpower.webguruz.listeners.RecyclerViewClickListeners
import com.inpower.webguruz.model.*
import com.inpower.webguruz.post.UpdatePostActivity
import com.inpower.webguruz.reactions.DisplayUtil
import com.inpower.webguruz.utilMethods.ApplicationConstant
import com.inpower.webguruz.utilMethods.CustomLoader
import com.inpower.webguruz.utilMethods.UtilFunctions
import com.wang.avi.AVLoadingIndicatorView
import com.webgurus.attendanceportal.ui.base.BaseFragment
import okhttp3.OkHttpClient
import org.jetbrains.annotations.NotNull
import java.io.ByteArrayOutputStream
import java.security.KeyStore
import java.security.SecureRandom
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager

//fragment to show users saved and anonymous feed list
@RequiresApi(Build.VERSION_CODES.HONEYCOMB)
class UserOthersPostFragment : BaseFragment(), RecyclerViewClickListeners, ApiListeners, PopupMenu.OnMenuItemClickListener, ReactionView.SelectedReaction {


    lateinit var loader: AVLoadingIndicatorView
    lateinit var sharedPreferences: SharedPreferences
    lateinit var editor: SharedPreferences.Editor

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
    private lateinit var iv_back: ImageView
    private lateinit var tv_title: TextView
    var  screen_name=""


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_users_others_post, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        listener()
        sharedPreferences = requireActivity().getSharedPreferences("MySession", Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()

         mListPosts    = ArrayList<ListPost>();

        if (sharedPreferences.getString("feedType", "").toString()=="Saved"){
            tv_title.text="Saved Posts"
            screen_name="Saved Posts"
            getAllSavedPosts(page, limit)
            setAllSavedFeedAdapter()
        }
        else if (sharedPreferences.getString("feedType", "").toString()=="Anonymous"){
            tv_title.text="My Anonymous Posts"
            screen_name="My Anonymous Posts"
            getAllAnonymousPosts(page, limit)
            setAllAnonymousFeedAdapter()
        }





        commentList   = ArrayList<GetCommentsIdModel>();
        replyList     = ArrayList<GetRepliesIdModel>();

    }

    //function of initialisation of view
    private fun initView() {
        iv_back             = requireView().findViewById(R.id.iv_back)
        tv_title             = requireView().findViewById(R.id.tv_title)
        rvFeeds             = requireView().findViewById(R.id.rv_feedlist)
        nestedScrollview    = requireView().findViewById(R.id.nestedScrollview)
    }

    // function of listeners
    private  fun listener(){
        iv_back!!.setOnClickListener(View.OnClickListener {
            requireActivity().onBackPressed()
        })
    }

    //function to get all feed list
    private fun getAllSavedPosts(page: Int, limit: Int) {

        val customLoader = CustomLoader(context, R.style.CustomLoaderTheme)
        if (Loader) customLoader.show()

        val BASE_URL = ApplicationConstant.INSTANCE.GETALLPOST
        var sharedPreferences = context?.getSharedPreferences("MySession", Context.MODE_PRIVATE)
        var feedListUserSavedPostsQuery = FeedListUserSavedPostsQuery(sharedPreferences?.getString("MyUserId", "").toString(),page, limit)
        OkHttpClient.Builder().build()
        val apolloClient = ApolloClient.builder().serverUrl(BASE_URL).okHttpClient(UtilFunctions.INSTANCE.getUnsafeOkHttpClient()!!).build()
        apolloClient.query(feedListUserSavedPostsQuery).enqueue(object : ApolloCall.Callback<FeedListUserSavedPostsQuery.Data?>() {
            override fun onResponse(@NotNull response: Response<FeedListUserSavedPostsQuery.Data?>) {
                if (customLoader.isShowing()) customLoader.dismiss()
                requireActivity().runOnUiThread(Runnable {
                    if (response.data != null) {
                        Log.e("AllFeedListData", Gson().toJson(response.data))
                        val data = Gson().fromJson(Gson().toJson(response.data), Data::class.java)
                        mListPosts.addAll(data.feedListUserSavedPosts.list)
                        editor.putString("PostData", Gson().toJson(mListPosts)).apply()
                        hasNext = response.data?.feedListUserSavedPosts?.hasNext!!
                        allFeedAdapter.notifyDataSetChanged()
//                        if (mListPosts.size < 5) {
//                            getAllSavedPosts(page + 1, limit)
//                        } else {
//                            if (customLoader.isShowing()) customLoader.dismiss()
//                            allFeedAdapter.notifyDataSetChanged()
//                        }
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
    private fun setAllSavedFeedAdapter() {
        allFeedAdapter              = AllFeedAdapter(requireActivity(), this, mListPosts,screen_name)
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
                    getAllSavedPosts(page, limit)
                }

            }
        })
    }

    //function to get feed adapter
    private fun getAllAnonymousPosts(page: Int, limit: Int) {

        val customLoader = CustomLoader(context, R.style.CustomLoaderTheme)
        if (Loader) customLoader.show()

        val BASE_URL = ApplicationConstant.INSTANCE.GETALLPOST
        var sharedPreferences = context?.getSharedPreferences("MySession", Context.MODE_PRIVATE)
        var feedListUserAnonymousPostsQuery = FeedListUserAnonymousPostsQuery(sharedPreferences?.getString("MyUserId", "").toString(),page, limit)
        OkHttpClient.Builder().build()
        val apolloClient = ApolloClient.builder().serverUrl(BASE_URL).okHttpClient(UtilFunctions.INSTANCE.getUnsafeOkHttpClient()!!).build()
        apolloClient.query(feedListUserAnonymousPostsQuery).enqueue(object : ApolloCall.Callback<FeedListUserAnonymousPostsQuery.Data?>() {
            override fun onResponse(@NotNull response: Response<FeedListUserAnonymousPostsQuery.Data?>) {
                if (customLoader.isShowing()) customLoader.dismiss()
                requireActivity().runOnUiThread(Runnable {
                    if (response.data != null) {
                        Log.e("AllFeedListData", Gson().toJson(response.data))
                        val data = Gson().fromJson(Gson().toJson(response.data), Data::class.java)
                        mListPosts.addAll(data.feedListUserAnonymousPosts.list)
                        editor.putString("PostData", Gson().toJson(mListPosts)).apply()
                        hasNext = response.data?.feedListUserAnonymousPosts?.hasNext!!
                        allFeedAdapter.notifyDataSetChanged()
//                        if (mListPosts.size < 5) {
//                            getAllSavedPosts(page + 1, limit)
//                        } else {
//                            if (customLoader.isShowing()) customLoader.dismiss()
//                            allFeedAdapter.notifyDataSetChanged()
//                        }
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
    private fun setAllAnonymousFeedAdapter() {
        allFeedAdapter              = AllFeedAdapter(requireActivity(), this, mListPosts,"Other post screen")
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
                    getAllAnonymousPosts(page, limit)
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

    //function to implement click reaction
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

    //function to implement select reaction
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

    //function to implement create reaction
    fun createReaction(context: Activity, reactType: String, postId: String, userId: String, receiverId: String, receiverName: String, receiverPic: String) {
        val customLoader = CustomLoader(context, R.style.CustomLoaderTheme)
        customLoader.show()
        Log.d("createreactions","data: " +postId +","+ userId )
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

    //function to implement delete reaction
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
        apolloClient.mutate(upvotePostMutation).enqueue(
            object : ApolloCall.Callback<DeleteReactionByUserPostIDRequestMutation.Data>() {
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

    //function to show dialog
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

    //function to show image dialog
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


}

