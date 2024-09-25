package com.inpower.webguruz.intro

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.*
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.Matrix
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import androidx.exifinterface.media.ExifInterface
import com.apollographql.apollo.ApolloClient
import com.inpower.webguruz.R
import com.inpower.webguruz.utilMethods.ApplicationConstant
import com.inpower.webguruz.utilMethods.UtilFunctions
import okhttp3.OkHttpClient
import java.io.ByteArrayOutputStream
import java.io.File


//activity to upload selfie
class SelfieTimeActivity : Activity() {

    lateinit var rl_next: TextView
    private val MY_CAMERA_PERMISSION_CODE = 100

    private var mImageView: ImageView? = null
    private var tv_retry: TextView? = null

    private lateinit var tv_choosePic: TextView
    var encoded :String=""
    private val OPERATION_CAPTURE_PHOTO = 1
    var sharedPreferences: SharedPreferences? = null
    var base64String:String?=null
    var flag:Int?=0
    var okHttpClient: OkHttpClient? = null
    var apolloClient: ApolloClient? = null

    lateinit var    outPutfileUri:Uri


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_selfie)
        UtilFunctions.INSTANCE.checkAndRequestPermissions(this)



        sharedPreferences = this.getSharedPreferences("MySession", Context.MODE_PRIVATE)
        Log.e("pronounVisibility",sharedPreferences?.getString("pronounVisibility", "").toString())


        initView()
        listeners()
    }

    //function of initialising view
    private fun initView() {
        rl_next = findViewById(R.id.rl_next)
        tv_choosePic = findViewById(R.id.rl_next)
        tv_retry = findViewById(R.id.tv_retry)
        mImageView = findViewById(R.id.mImageView)
        okHttpClient = OkHttpClient.Builder().build()
        apolloClient = ApolloClient.builder().serverUrl(ApplicationConstant.INSTANCE.CREATEPROFILE)
            .okHttpClient(UtilFunctions.INSTANCE.getUnsafeOkHttpClient()!!).build()
    }

    //function of listeners
    private fun listeners() {
        rl_next.setOnClickListener(View.OnClickListener {
            if(rl_next.text.equals(resources.getString(R.string.takePhoto))){
                val checkSelfPermission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
                if (checkSelfPermission != PackageManager.PERMISSION_GRANTED) {
                    //Requests permissions to be granted to this application at runtime
                    ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.CAMERA), 1)
                } else {
                    capturePhoto()
                }
            }else{
                if(getIntent().getStringExtra("screen").toString().equals("Registration")) {

                    Log.e("valueauthToken",sharedPreferences?.getString("authToken", "").toString())
                    Log.e("valuePhone",sharedPreferences?.getString("Phone", "").toString())
                    Log.e("valueFullname",sharedPreferences?.getString("Fullname", "").toString())
                    Log.e("valuePronoun",sharedPreferences?.getString("Pronoun", "").toString())
                    Log.e("valuepronounVisibility",sharedPreferences?.getString("pronounVisibility", "").toString())

                    UtilFunctions.INSTANCE.createProfile(
                        this,
                        sharedPreferences?.getString("authToken", "").toString(),
                        sharedPreferences?.getString("Phone", "").toString(),
                        sharedPreferences?.getString("Fullname", "").toString(),
                        base64String!!,
                        sharedPreferences?.getString("Pronoun", "").toString(),
                        sharedPreferences?.getString("pronounVisibility", "").toString()
                    )
                }
                else if(getIntent().getStringExtra("screen").toString().equals("Edit")){
                    UtilFunctions.INSTANCE.editProfileSelfie(
                        this,
                        sharedPreferences?.getString("Email", "").toString(),
                        sharedPreferences?.getString("MyUserId", "").toString(),
                        sharedPreferences?.getString("Fullname", "").toString(),
                        true,
                        sharedPreferences?.getString("Bio", "").toString(),
                        sharedPreferences?.getString("Phone", "").toString(),
                        base64String!!, base64String!!,
                        sharedPreferences?.getString("Pronoun", "").toString(),
                        sharedPreferences?.getString("pronounVisibility", "").toString()
                    )
                }
            }
        })
        tv_retry?.setOnClickListener(View.OnClickListener {
         //   showDialog(1)
//            rl_next.text=resources.getString(R.string.takePhoto)
//            tv_retry?.visibility=View.GONE
            capturePhoto()
        })

        mImageView?.setOnClickListener(View.OnClickListener {
            //showDialog(this)
//            rl_next.text=resources.getString(R.string.takePhoto)
//            tv_retry?.visibility=View.GONE
            capturePhoto()
        })

    }

    @SuppressLint("WrongConstant")
    private fun capturePhoto() {
        if (PermissionChecker.checkSelfPermission(this, Manifest.permission.CAMERA) !== PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.CAMERA), MY_CAMERA_PERMISSION_CODE)
        } else {
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(cameraIntent, OPERATION_CAPTURE_PHOTO)

            //  testing









        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantedResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantedResults)
        when (requestCode) {
            1 ->
                if (grantedResults.isNotEmpty() && grantedResults.get(0) == PackageManager.PERMISSION_GRANTED) {
                    capturePhoto()
                }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        var path: String?
        when (requestCode) {
            OPERATION_CAPTURE_PHOTO ->
                if (resultCode == Activity.RESULT_OK) {


                    val bitmap = data!!.extras!!.get("data") as Bitmap

                    Log.d("takepicbit","bitmap: " + bitmap)
                    val matrix = Matrix()

                    val tempUri: Uri = getImageUri(applicationContext, bitmap)
                    val finalFile = File(getRealPathFromURI(tempUri))
                    val ei = ExifInterface(finalFile.path)
                 //   val orientation: Int = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED)
                    val orientation: Int = 0

                    var rotatedBitmap: Bitmap? = null
                    when (orientation) {
                        ExifInterface.ORIENTATION_ROTATE_90 ->{
                            matrix.postRotate(0f)
                            Log.d("takepic","90")
                            val scaledBitmap = Bitmap.createScaledBitmap(bitmap, bitmap.width, bitmap.height, true)
                            rotatedBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.width, scaledBitmap.height, matrix, true)
                        }
                        ExifInterface.ORIENTATION_ROTATE_180 -> {
                            matrix.postRotate(0f)
                            Log.d("takepic","180")
                            val scaledBitmap = Bitmap.createScaledBitmap(bitmap, bitmap.width, bitmap.height, true)
                            rotatedBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.width, scaledBitmap.height, matrix, true)
                        }
                        ExifInterface.ORIENTATION_ROTATE_270 -> {
                            matrix.postRotate(0f)
                            Log.d("takepic","270")
                            val scaledBitmap = Bitmap.createScaledBitmap(bitmap, bitmap.width, bitmap.height, true)
                            rotatedBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.width, scaledBitmap.height, matrix, true)
                        }

                        ExifInterface.ORIENTATION_NORMAL -> rotatedBitmap = bitmap

                        else -> rotatedBitmap =bitmap

                    }

                    mImageView!!.setImageBitmap(rotatedBitmap)

                    val byteArrayOutputStream = ByteArrayOutputStream()
                    rotatedBitmap?.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
                    val imageBytes: ByteArray = byteArrayOutputStream.toByteArray()
                    var imageString: String = Base64.encodeToString(imageBytes, Base64.DEFAULT)
                    imageString = imageString.replace("\\n".toRegex(), "")
                    Log.e("new", "data:image/png;base64," + imageString)
                    base64String = "data:image/png;base64," + imageString


                    rl_next.text=resources.getString(R.string.next)
                    tv_retry?.visibility=View.VISIBLE

                }
        }
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

    //function to get image uri
    fun getImageUri(inContext: Context, inImage: Bitmap): Uri {
        val bytes = ByteArrayOutputStream()
        //inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(
            inContext.contentResolver, inImage, "IMG_" + System.currentTimeMillis(), null
        )
    //    val path = MediaStore.Images.Media.insertImage(inContext.contentResolver, inImage, "Title", null)
        return Uri.parse(path)
    }

    //function to get real path of image from uri
    fun getRealPathFromURI(uri: Uri?): String? {
        var path = ""
        if (contentResolver != null) {
            val cursor: Cursor? = contentResolver.query(uri!!, null, null, null, null)
            if (cursor != null) {
                cursor.moveToFirst()
                val idx: Int = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
                path = cursor.getString(idx)
                cursor.close()
            }
        }
        return path
    }
}