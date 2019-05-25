package com.app.l_pesa.registration.view

import android.app.Activity
import android.content.ClipData
import android.content.ContentUris
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.content.FileProvider
import android.support.v7.app.AlertDialog
import android.text.TextUtils
import android.widget.ImageView
import android.widget.Toast
import com.app.l_pesa.BuildConfig
import com.app.l_pesa.R
import com.app.l_pesa.common.BitmapResize
import com.app.l_pesa.common.CommonMethod
import com.app.l_pesa.profile.inter.ICallBackUpload
import com.app.l_pesa.profile.presenter.PresenterAWSProfile
import com.app.l_pesa.registration.inter.ICallBackRegisterTwo
import com.app.l_pesa.registration.presenter.PresenterRegistrationTwo
import com.google.gson.JsonObject
import com.kaopiz.kprogresshud.KProgressHUD
import kotlinx.android.synthetic.main.activity_registration_step_two.*
import java.io.File
import java.lang.Exception


class RegistrationStepTwoActivity : AppCompatActivity(), ICallBackUpload, ICallBackRegisterTwo {

    private lateinit              var progressDialog: KProgressHUD
    private var captureImageStatus : Boolean    = false
    private var photoFile          : File?      = null
    private var captureFilePath    : Uri?       = null
    private var mobileOtp           = ""
    private val requestPhoto        = 14
    private val requestGallery      = 15


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration_step_two)

        val bundle      = intent.extras
        mobileOtp       = bundle!!.getString("OTP")!!

        initLoader()
        imgProfilePhoto.setOnClickListener {

            selectImage()
        }

        btnSubmit.setOnClickListener {

            onSubmit()
        }


    }

    private fun initLoader()
    {
        progressDialog=KProgressHUD.create(this@RegistrationStepTwoActivity)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setCancellable(false)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f)

    }

    private fun dismiss()
    {
        if(progressDialog.isShowing)
        {
            progressDialog.dismiss()
        }
    }

    private fun onSubmit()
    {
        CommonMethod.hideKeyboardView(this@RegistrationStepTwoActivity)
        if(!captureImageStatus)
        {
            CommonMethod.customSnackBarError(rootLayout,this@RegistrationStepTwoActivity,resources.getString(R.string.required_profile_image))
        }
        else if(TextUtils.isEmpty(etName.text.toString()))
        {
            CommonMethod.customSnackBarError(rootLayout,this@RegistrationStepTwoActivity,resources.getString(R.string.required_name))
        }
        else if (TextUtils.isEmpty(etOTP.text.toString()))
        {
            CommonMethod.customSnackBarError(rootLayout,this@RegistrationStepTwoActivity,resources.getString(R.string.required_otp))
        }
        else if(etOTP.text.toString()!=mobileOtp)
        {
            CommonMethod.customSnackBarError(rootLayout,this@RegistrationStepTwoActivity,resources.getString(R.string.otp_not_match))
        }
        else
        {
            if(CommonMethod.isNetworkAvailable(this@RegistrationStepTwoActivity))
            {
                progressDialog.show()
                btnSubmit.isClickable=false

                val presenterAWSProfile= PresenterAWSProfile()
                presenterAWSProfile.uploadProfileImageRegistration(this@RegistrationStepTwoActivity,this,photoFile)

            }
            else
            {
                CommonMethod.customSnackBarError(rootLayout,this@RegistrationStepTwoActivity,resources.getString(R.string.no_internet))
            }
        }
    }

    override fun onSuccessUploadAWS(url: String) {

        uploadData(url)
    }

    override fun onFailureUploadAWS(string: String) {

        dismiss()
        btnSubmit.isClickable=true
        Toast.makeText(this@RegistrationStepTwoActivity,string,Toast.LENGTH_SHORT).show()
    }

    private fun uploadData(imageURL: String)
    {

        val jsonObject = JsonObject()
        jsonObject.addProperty("name",etName.text.toString())
        jsonObject.addProperty("image",imageURL)
        jsonObject.addProperty("otp",mobileOtp)

        val presenterRegistrationTwo= PresenterRegistrationTwo()
        presenterRegistrationTwo.doRegistrationStepTwo(this@RegistrationStepTwoActivity,jsonObject,this)


    }

    override fun onSuccessRegistrationTwo() {

        startActivity(Intent(this@RegistrationStepTwoActivity, RegistrationStepThreeActivity::class.java))
        overridePendingTransition(R.anim.right_in, R.anim.left_out)
    }

    override fun onErrorRegistrationTwo(jsonMessage: String) {

        dismiss()
        btnSubmit.isClickable=true
        CommonMethod.customSnackBarError(rootLayout,this@RegistrationStepTwoActivity,jsonMessage)
    }

    private fun selectImage() {

        val items = arrayOf<CharSequence>("Camera", "Gallery", "Cancel") // array list
        val dialogView = AlertDialog.Builder(this@RegistrationStepTwoActivity)
        dialogView.setTitle("Choose Options")

        dialogView.setItems(items) { dialog, item ->

            when {
                items[item] == "Camera" -> // open camera
                    cameraClick() // open default camera
                items[item] == "Gallery" -> // open gallery
                    galleryClick() // open default gallery
                items[item] == "Cancel" -> // close dialog
                    dialog.dismiss()
            }
        }
        dialogView.show()
    }

    private fun cameraClick()
    {

        cacheDir.deleteRecursively()
        val captureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val imagePath = File(filesDir, "images")
        photoFile = File(imagePath, "user.jpg")
        if (photoFile!!.exists()) {
            photoFile!!.delete()
        } else {
            photoFile!!.parentFile.mkdirs()
        }
        captureFilePath = FileProvider.getUriForFile(this@RegistrationStepTwoActivity, BuildConfig.APPLICATION_ID + ".provider", photoFile!!)

        captureIntent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, captureFilePath)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            captureIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        } else {
            val clip = ClipData.newUri(this@RegistrationStepTwoActivity.contentResolver, "user photo", captureFilePath)
            captureIntent.clipData = clip
            captureIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        }

        startActivityForResult(captureIntent, requestPhoto)
    }

      private fun galleryClick()
    {
        val checkSelfPermission = ContextCompat.checkSelfPermission(this@RegistrationStepTwoActivity, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if (checkSelfPermission != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this@RegistrationStepTwoActivity, arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
        }
        else{
            openAlbum()
        }
    }

    private fun openAlbum(){
        val intent = Intent("android.intent.action.GET_CONTENT")
        intent.type = "image/*"
        startActivityForResult(intent, requestGallery)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            1 ->
                if (grantResults.isNotEmpty() && grantResults[0] ==PackageManager.PERMISSION_GRANTED){
                    openAlbum()
                }
                else {
                    Toast.makeText(this@RegistrationStepTwoActivity, "You denied the permission", Toast.LENGTH_SHORT).show()
                }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            requestPhoto ->

                if (resultCode == Activity.RESULT_OK)
                {
                    setImage()
                }
            requestGallery->
                if (resultCode == Activity.RESULT_OK) {
                    handleImage(data)

                }
        }
    }


    private fun setImage() {

        try {
            val imgSize = File(captureFilePath.toString())
            val length  = imgSize.length() / 1024
            if(length>3000) // Max Size Under 3MB
            {
                captureImageStatus = false
                Toast.makeText(this@RegistrationStepTwoActivity, "Image size maximum 3Mb", Toast.LENGTH_SHORT).show()
            }
            else {
                val photoPath: Uri = captureFilePath ?: return
                imgProfilePhoto.post {
                    val pictureBitmap = BitmapResize.shrinkBitmap(
                            this@RegistrationStepTwoActivity,
                            photoPath,
                            imgProfilePhoto.width,
                            imgProfilePhoto.height
                    )
                    imgProfilePhoto.setImageBitmap(pictureBitmap)
                    imgProfilePhoto.scaleType = ImageView.ScaleType.CENTER_CROP
                    CommonMethod.fileCompress(photoFile!!)
                }

                captureImageStatus = true

            }
        }
        catch (exp:Exception)
        {

        }

    }

    private fun handleImage(data: Intent?) {
        var imagePath=""
        try
        {
            val uri = data!!.data
            when {
                DocumentsContract.isDocumentUri(this, uri) -> try {
                    val docId = DocumentsContract.getDocumentId(uri)
                    if ("com.android.providers.media.documents" == uri!!.authority){
                        val id = docId.split(":")[1]
                        val section = MediaStore.Images.Media._ID + "=" + id
                        imagePath = imagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, section)
                    } else if ("com.android.providers.downloads.documents" == uri.authority){
                        val contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), java.lang.Long.valueOf(docId))
                        imagePath = imagePath(contentUri, null)
                    }

                }
                catch (exp: Exception)
                {

                }
                "content".equals(uri!!.scheme, ignoreCase = true) -> imagePath = imagePath(uri, null)
                "file".equals(uri.scheme, ignoreCase = true) -> imagePath = uri.path!!
            }
             displayImage(imagePath)
        }
        catch (exp: Exception)
        {

        }
    }

    private fun imagePath(uri: Uri?, selection: String?): String {
        var path: String? = null
        val cursor = contentResolver.query(uri!!, null, selection, null, null )
        if (cursor != null){
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA))
            }
            cursor.close()
        }
        return path!!
    }

    private fun displayImage(imagePath: String?){
        if (imagePath != null) {
            val imgSize = File(imagePath)
            val length  = imgSize.length() / 1024
            if(length>3000) // Max Size Under 3MB
            {
                Toast.makeText(this@RegistrationStepTwoActivity, "Image size maximum 3MB", Toast.LENGTH_SHORT).show()
            }
            else
            {
                captureImageStatus=true
                val bitmap = BitmapFactory.decodeFile(imagePath)
                imgProfilePhoto?.setImageBitmap(bitmap)
                photoFile=imgSize
                CommonMethod.fileCompress(photoFile!!)
            }

        }
        else {
            Toast.makeText(this@RegistrationStepTwoActivity, "Failed to get image", Toast.LENGTH_SHORT).show()
        }
    }



    override fun onBackPressed() {


    }


}
