package com.inpower.webguruz.profile.EditProfile

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.Matrix
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.MediaStore.Images
import android.util.Base64
import android.util.Log
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import androidx.exifinterface.media.ExifInterface
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.BitmapImageViewTarget
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.inpower.webguruz.R
import com.inpower.webguruz.utilMethods.UtilFunctions
import de.hdodenhof.circleimageview.CircleImageView
import java.io.ByteArrayOutputStream
import java.io.File

//fragment to edit user frofile
class EditProfile : AppCompatActivity() {

    var sharedPreferences: SharedPreferences? = null
    var editor = sharedPreferences?.edit()
    var iv_back: ImageView? = null

    var iv_pic: CircleImageView? = null
    var tv_name: TextView? = null
    var tv_pronoun: TextView? = null
    var tv_bio: TextView? = null
    var tv_dob: TextView? = null
    var tv_mobile: TextView? = null

    var rl_name: RelativeLayout? = null
    var rl_pronoun: RelativeLayout? = null
    var rl_bio: RelativeLayout? = null
    var rl_dob: RelativeLayout? = null
    var rl_mobile: RelativeLayout? = null


    private val OPERATION_CAPTURE_PHOTO = 1
    private val OPERATION_CHOOSE_PHOTO = 2
    private val MY_CAMERA_PERMISSION_CODE = 100
    var flag: Int? = 0
    var base64String: String? = null
    lateinit var switch_button: SwitchCompat
    var screen: String = ""
    var  base64=""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.editprofile_activity)

        UtilFunctions.INSTANCE.checkAndRequestPermissions(this)

        sharedPreferences = getSharedPreferences("MySession", Context.MODE_PRIVATE)
        editor = sharedPreferences?.edit()

        Log.d("myuseridtest"," :" + sharedPreferences?.getString("MyUserId", "").toString())


        initView()
        setProfileData()
        listener()

    }

    //function to set profile data
    private fun setProfileData() {

        tv_name?.text = sharedPreferences?.getString("Fullname", "").toString()
        tv_bio?.text = sharedPreferences?.getString("Bio", "").toString()
        tv_pronoun?.text = sharedPreferences?.getString("Pronoun", "").toString()
        tv_mobile?.text = sharedPreferences?.getString("Phone", "").toString()
        tv_dob?.text = sharedPreferences?.getString("DOB", "").toString()
        iv_pic?.let {
            Glide.with(iv_pic!!)
                .asBitmap()
                .load(sharedPreferences?.getString("ProfilePic", "")?.replace("hostname     ", ""))
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
        }

        if (sharedPreferences!!.getBoolean("triggerWarning", true)){
            switch_button.setChecked(true)
        }
        switch_button.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                editor!!.putBoolean("triggerWarning", true).apply()
                Log.e("triggerWarning", sharedPreferences?.getBoolean("triggerWarning", false).toString())
            }
            else { editor!!.putBoolean("triggerWarning", false).apply()
                Log.e("triggerWarning", sharedPreferences?.getBoolean("triggerWarning", false).toString())
            }
        })
    }

    //function of initialisation
    private fun initView() {
        iv_back = findViewById(R.id.iv_back)

        iv_pic=findViewById(R.id.iv_pic)
        tv_name = findViewById(R.id.tv_name)
        tv_bio = findViewById(R.id.tv_bio)
        tv_pronoun = findViewById(R.id.iv_pronouns)
        tv_dob = findViewById(R.id.tv_dob)
        tv_mobile = findViewById(R.id.tv_mobile)
        switch_button=findViewById(R.id.switch_button)


        rl_name = findViewById(R.id.rl_name)
        rl_pronoun = findViewById(R.id.rl_pronoun)
        rl_bio = findViewById(R.id.rl_bio)
        rl_dob = findViewById(R.id.rl_dob)
        rl_mobile = findViewById(R.id.rl_mobile)

    }

    //function of listeners
    private fun listener() {
        iv_back!!.setOnClickListener(View.OnClickListener {
            finish()
            overridePendingTransition(R.anim.fade_in,R.anim.fade_out)
        })

        iv_pic?.setOnClickListener(View.OnClickListener {
            showImagePickerDialog()
        })


        rl_name!!.setOnClickListener(View.OnClickListener {
            val intent = Intent(this, UpdateProfileActivity::class.java)
            intent.putExtra("key", "name")
            startActivity(intent)
            overridePendingTransition(R.anim.fade_in,R.anim.fade_out)
        })
        rl_pronoun!!.setOnClickListener(View.OnClickListener {
            val intent = Intent(this, UpdateProfileActivity::class.java)
            intent.putExtra("key", "pronoun")
            startActivity(intent)
            overridePendingTransition(R.anim.fade_in,R.anim.fade_out)
        })
        rl_bio!!.setOnClickListener(View.OnClickListener {
            val intent = Intent(this, UpdateProfileActivity::class.java)
            intent.putExtra("key", "bio")
            startActivity(intent)
            overridePendingTransition(R.anim.fade_in,R.anim.fade_out)
        })
        rl_dob!!.setOnClickListener(View.OnClickListener {
            //val intent = Intent(this, UpdateProfileActivity::class.java)
            //intent.putExtra("key", "dob")
            //startActivity(intent)
            // overridePendingTransition(R.anim.fade_in,R.anim.fade_out)
        })
        rl_mobile!!.setOnClickListener(View.OnClickListener {
//            val intent = Intent(this, UpdateProfileActivity::class.java)
//            intent.putExtra("key", "mobile")
//            startActivity(intent)
//            overridePendingTransition(R.anim.fade_in,R.anim.fade_out)
        })
    }

    override fun onResume() {
        super.onResume()
        setProfileData()
    }

    //function to pick images
    private fun showImagePickerDialog() {

        var choserDialog: BottomSheetDialog = BottomSheetDialog(this, R.style.AppBottomSheetDialogThemeTransparent)
        choserDialog.setContentView(R.layout.image_picker_sheet)
        choserDialog.show()

        val tv_gallery: TextView = choserDialog.findViewById(R.id.tv_gallery)!!
        val tv_camera: TextView = choserDialog.findViewById(R.id.tv_camera)!!
        val tv_cancel: TextView = choserDialog.findViewById(R.id.tv_cancel)!!
        val v1: View = choserDialog.findViewById(R.id.v1)!!


        tv_gallery.setOnClickListener(View.OnClickListener {
            flag = 1
            val checkSelfPermission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
            if (checkSelfPermission == PackageManager.PERMISSION_GRANTED) {
                openGallery()
            } else {
                UtilFunctions.INSTANCE.checkAndRequestPermissions(this)
            }
            choserDialog.dismiss()
        })
        tv_camera.setOnClickListener(View.OnClickListener {
            flag = 2
            val checkSelfPermission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
            if (checkSelfPermission == PackageManager.PERMISSION_GRANTED) {
                capturePhoto()
            }else {
                UtilFunctions.INSTANCE.checkAndRequestPermissions(this)
            }
            choserDialog.dismiss()

        })
        tv_cancel.setOnClickListener(View.OnClickListener { choserDialog.dismiss()})

    }

    private fun openGallery() {
        if (Build.VERSION.SDK_INT < 19) {
            val intent = Intent()
            intent.type = "image/*"
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false)
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent, "Choose Pictures"), OPERATION_CHOOSE_PHOTO)
        } else {
            // For latest versions API LEVEL 19+
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.type = "image/*"
            startActivityForResult(intent, OPERATION_CHOOSE_PHOTO);
        }
    }

    @SuppressLint("WrongConstant")
    private fun capturePhoto() {
        if (PermissionChecker.checkSelfPermission(this, Manifest.permission.CAMERA) !== PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.CAMERA), MY_CAMERA_PERMISSION_CODE)
        } else {
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(cameraIntent, OPERATION_CAPTURE_PHOTO)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        var path: String?
        when (requestCode) {
            OPERATION_CAPTURE_PHOTO ->
                if (resultCode == Activity.RESULT_OK) {

                    val bitmap = data!!.extras!!.get("data") as Bitmap
                    val matrix = Matrix()

                    val tempUri: Uri = getImageUri(applicationContext, bitmap)
                    val finalFile = File(getRealPathFromURI(tempUri))
                    val ei = ExifInterface(finalFile.path)
                    val orientation: Int = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED)

                    var rotatedBitmap: Bitmap? = null

                    when (orientation) {
                        ExifInterface.ORIENTATION_ROTATE_90 ->{
                            matrix.postRotate(90f)
                            val scaledBitmap = Bitmap.createScaledBitmap(bitmap, bitmap.width, bitmap.height, true)
                            rotatedBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.width, scaledBitmap.height, matrix, true)
                        }
                        ExifInterface.ORIENTATION_ROTATE_180 -> {
                            matrix.postRotate(180f)
                            val scaledBitmap = Bitmap.createScaledBitmap(bitmap, bitmap.width, bitmap.height, true)
                            rotatedBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.width, scaledBitmap.height, matrix, true)
                        }
                        ExifInterface.ORIENTATION_ROTATE_270 -> {
                            matrix.postRotate(270f)
                            val scaledBitmap = Bitmap.createScaledBitmap(bitmap, bitmap.width, bitmap.height, true)
                            rotatedBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.width, scaledBitmap.height, matrix, true)
                        }
                        ExifInterface.ORIENTATION_NORMAL -> rotatedBitmap = bitmap
                        else -> rotatedBitmap =bitmap
                    }

                    iv_pic!!.setImageBitmap(rotatedBitmap)

                    val byteArrayOutputStream = ByteArrayOutputStream()
                    rotatedBitmap?.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
                    val imageBytes: ByteArray = byteArrayOutputStream.toByteArray()
                    var imageString: String = Base64.encodeToString(imageBytes, Base64.DEFAULT)
                    imageString = imageString.replace("\\n".toRegex(), "")
                    Log.e("new", "data:image/png;base64," + imageString)
                    base64String = "data:image/png;base64," + imageString

                    UtilFunctions.INSTANCE.editProfile(
                        this,
                        sharedPreferences?.getString("Email", "").toString(),
                        sharedPreferences?.getString("MyUserId", "").toString(),
                        sharedPreferences?.getString("Fullname", "").toString(),
                        true,
                        sharedPreferences?.getString("Bio", "").toString(),
                        sharedPreferences?.getString("Phone", "").toString(),
                        base64String!!, base64String!!,
                        sharedPreferences?.getString("Pronoun", "").toString(),
                        sharedPreferences?.getString("pronounVisibility", "").toString(),
                        sharedPreferences?.getString("accountVisibility", "").toString(),
                        "Edit profile"
                    )

                }

            OPERATION_CHOOSE_PHOTO ->
                if (resultCode == Activity.RESULT_OK) {
                    var imageUri: Uri = data?.data!!
                    val photoBmp: Bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, imageUri)
                    iv_pic!!.setImageBitmap(photoBmp)
                    val byteArrayOutputStream = ByteArrayOutputStream()
                    photoBmp.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
                    val imageBytes: ByteArray = byteArrayOutputStream.toByteArray()
                    var imageString: String = Base64.encodeToString(imageBytes, Base64.DEFAULT)
                    imageString = imageString.replace("\\n".toRegex(), "")
                    base64String = "data:image/png;base64," + imageString

                    UtilFunctions.INSTANCE.editProfile(
                        this,
                        sharedPreferences?.getString("Email", "").toString(),
                        sharedPreferences?.getString("MyUserId", "").toString(),
                        sharedPreferences?.getString("Fullname", "").toString(),
                        true,
                        sharedPreferences?.getString("Bio", "").toString(),
                        sharedPreferences?.getString("Phone", "").toString(),
                        base64String!!, base64String!!,
                        sharedPreferences?.getString("Pronoun", "").toString(),
                        sharedPreferences?.getString("pronounVisibility", "").toString(),
                        sharedPreferences?.getString("accountVisibility", "").toString(),
                        "Edit profile"
                    )
                }

        }
    }

    //function to rotate image bitmap
    fun rotateImage(source: Bitmap, angle: Int): Bitmap? {
        val matrix = Matrix()
        matrix.postRotate(angle as Float)
        return Bitmap.createBitmap(
            source, 0, 0, source.width, source.height,
            matrix, true
        )
    }

    //funtion to get uri
    fun getImageUri(inContext: Context, inImage: Bitmap): Uri {
        val bytes = ByteArrayOutputStream()
        //inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = Images.Media.insertImage(inContext.contentResolver, inImage, "Title", null)
        return Uri.parse(path)
    }

    //function to get real path of uri
    fun getRealPathFromURI(uri: Uri?): String? {
        var path = ""
        if (contentResolver != null) {
            val cursor: Cursor? = contentResolver.query(uri!!, null, null, null, null)
            if (cursor != null) {
                cursor.moveToFirst()
                val idx: Int = cursor.getColumnIndex(Images.ImageColumns.DATA)
                path = cursor.getString(idx)
                cursor.close()
            }
        }
        return path
    }

    @Override
    override fun onBackPressed() {
        finish()
        overridePendingTransition(R.anim.fade_in,R.anim.fade_out)
    }


}