package com.app.l_pesa.registration.view

import `in`.creativelizard.creativecam.CamUtil
import `in`.creativelizard.creativecam.CamViewActivity
import android.Manifest
import android.app.Activity
import android.app.ProgressDialog
import android.content.ClipData
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.*
import android.hardware.camera2.CameraCharacteristics
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.provider.Settings
import android.text.Spannable
import android.text.SpannableString
import android.text.style.RelativeSizeSpan
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.exifinterface.media.ExifInterface
import com.app.l_pesa.BuildConfig
import com.app.l_pesa.R
import com.app.l_pesa.application.MyApplication
import com.app.l_pesa.common.CommonMethod
import com.app.l_pesa.common.CustomTypeFaceSpan
import com.app.l_pesa.common.SharedPref
import com.app.l_pesa.login.view.LoginActivity
import com.app.l_pesa.profile.inter.ICallBackUpload
import com.app.l_pesa.profile.model.ResUserInfo
import com.app.l_pesa.profile.presenter.PresenterAWSProfile
import com.app.l_pesa.registration.inter.ICallBackRegisterThree
import com.app.l_pesa.registration.presenter.PresenterRegistrationThree
import com.bumptech.glide.Glide
import com.facebook.appevents.AppEventsConstants
import com.facebook.appevents.AppEventsLogger
import com.google.gson.JsonObject
import id.zelory.compressor.Compressor
import kotlinx.android.synthetic.main.activity_registration_step_five.*
import kotlinx.android.synthetic.main.layout_registration_step_five.*
import org.jetbrains.anko.longToast
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.HashMap
import kotlin.collections.ArrayList
import kotlin.collections.set

class RegistrationStepFiveActivity : AppCompatActivity(), ICallBackUpload, ICallBackRegisterThree {

    private val  requestPhoto               = 16
    private var  captureImageStatus         : Boolean    = false
    private lateinit var photoFile          : File
    private lateinit var captureFilePath    : Uri
    private lateinit var progressDialog     : ProgressDialog


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration_step_five)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbarFont(this@RegistrationStepFiveActivity)

        initData()
    }

    private fun initData()
    {
        initLoader()

        imageCard.setOnClickListener {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                if(checkAndRequestPermissions())
                {
                    initCamera()
                }
                else
                {
                    checkAndRequestPermissions()
                }
            }
            else{
                initCamera()
            }
        }


        buttonComplete.setOnClickListener {

            if (captureImageStatus)
            {
                doContinue()

            }
            else
            {
                CommonMethod.customSnackBarError(rootLayout, this@RegistrationStepFiveActivity, resources.getString(R.string.required_id_image))
            }
        }


    }

    private fun initCamera(){

//        val captureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//        val imagePath = File(filesDir, "images")
//        photoFile = File(imagePath, "personal.jpg")
//        if (photoFile.exists()) {
//            photoFile.delete()
//        } else {
//            photoFile.parentFile!!.mkdirs()
//        }
//        captureFilePath = FileProvider.getUriForFile(this@RegistrationStepFiveActivity, BuildConfig.APPLICATION_ID, photoFile)
//
//        captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, captureFilePath)
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            captureIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
//        } else {
//            val clip = ClipData.newUri(this@RegistrationStepFiveActivity.contentResolver, "id photo", captureFilePath)
//            captureIntent.clipData = clip
//            captureIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
//        }
//
//        startActivityForResult(captureIntent, requestPhoto)

            val intent_cam = Intent(this, CamViewActivity::class.java)
            //intent_cam.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
            intent_cam.putExtra(CamUtil.CAM_FACING, 1)
//        intent_cam.putExtra(CamUtil.CAM_SWITCH_OPT,false)
//        intent_cam.putExtra(CamUtil.CAPTURE_BTN_COLOR,"#00695c")
//        intent_cam.putExtra(CamUtil.CAPTURE_CONTROL_COLOR,"#ffffff")

        intent_cam.putExtra(CamUtil.CAPTURE_BTN_COLOR,ContextCompat.getColor(this,R.color.colorApp))
        intent_cam.putExtra(CamUtil.CAPTURE_BTN_ICON_COLOR,ContextCompat.getColor(this,R.color.colorLightBlack))
        intent_cam.putExtra(CamUtil.CAPTURE_CONTROL_COLOR,ContextCompat.getColor(this,R.color.screenBackground))
        intent_cam.putExtra(CamUtil.TARGET_BOX, View.VISIBLE)
        intent_cam.putExtra(CamUtil.TARGET_COLOR, Color.parseColor("#00bcd4"))
        intent_cam.putExtra(CamUtil.TARGET_WIDTH, 5)
            startActivityForResult(intent_cam, requestPhoto)


//        val intent_cam = Intent(this, CamViewActivity::class.java)
//       // intent_cam.flags = Intent.FLAG_ACTIVITY_NEW_TASK /*or Intent.FLAG_ACTIVITY_SINGLE_TOP*/
//        intent_cam.putExtra(CamUtil.CAM_FACING,1)
//        intent_cam.putExtra(CamUtil.CAM_SWITCH_OPT,true)
//        intent_cam.putExtra(CamUtil.CAPTURE_BTN_COLOR,ContextCompat.getColor(this,R.color.colorApp))
//        intent_cam.putExtra(CamUtil.CAPTURE_BTN_ICON_COLOR,Color.WHITE)
//        intent_cam.putExtra(CamUtil.CAPTURE_CONTROL_COLOR,Color.WHITE)
//
//        startActivityForResult(intent_cam,requestPhoto)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            requestPhoto ->

                if (resultCode == Activity.RESULT_OK)
                {
                    setImage(data?.getStringExtra(CamUtil.IMG_FILE_PATH)!!)
                }

        }
    }

    private fun setImage(filepath:String) {
        photoFile = File(filepath)
      //  val photoPath: Uri  = captureFilePath
        val photoPath: Uri = Uri.fromFile(photoFile)//captureFilePath
        try {
            if(photoPath!=Uri.EMPTY)
            {
                //progressDialog.show()
                handleRotation(photoFile.absolutePath)
                imageCard.setBackgroundResource(R.drawable.bg_button_green)
                // imageCard.setImageURI(null)
                //imageCard.setImageURI(photoPath)
                Glide.with(this).load(photoFile).into(imageCard)
                captureImageStatus       = true
                photoFile   = Compressor(this@RegistrationStepFiveActivity).compressToFile(photoFile)
//                Handler(Looper.getMainLooper()).postDelayed({
//                    dismiss()
//
//                }, 2000)

            }
            else
            {
                Toast.makeText(this@RegistrationStepFiveActivity,"Retake Photo", Toast.LENGTH_SHORT).show()
            }

        }
        catch (exp:Exception)
        {
            Toast.makeText(this@RegistrationStepFiveActivity,"Retake Photo", Toast.LENGTH_SHORT).show()
        }

    }

    private fun handleRotation(imgPath: String) {
        BitmapFactory.decodeFile(imgPath)?.let { origin ->
            try {
                ExifInterface(imgPath).apply {
                    getAttributeInt(
                            ExifInterface.TAG_ORIENTATION,
                            ExifInterface.ORIENTATION_UNDEFINED
                    ).let { orientation ->
                        when (orientation) {
                            ExifInterface.ORIENTATION_ROTATE_90 -> origin.rotate(90f)
                            ExifInterface.ORIENTATION_ROTATE_180 -> origin.rotate(180f)
                            ExifInterface.ORIENTATION_ROTATE_270 -> origin.rotate(270f)
                            ExifInterface.ORIENTATION_NORMAL -> origin
                            else ->origin          //origin.rotate(270f)
                        }.also { bitmap ->
                            //Update the input file with the new bytes.
                            try {
                                FileOutputStream(imgPath).use { fos ->
                                    bitmap.compress(
                                            Bitmap.CompressFormat.JPEG,
                                            100,
                                            fos
                                    )
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun Bitmap.rotate(degrees: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(degrees)
        val scaledBitmap = Bitmap.createScaledBitmap(this, width, height, true)
        return Bitmap.createBitmap(
                scaledBitmap,
                0,
                0,
                scaledBitmap.width,
                scaledBitmap.height,
                matrix,
                true
        )
    }



    private fun doContinue()
    {

        if(CommonMethod.isNetworkAvailable(this@RegistrationStepFiveActivity))
        {
            progressDialog.show() 
            val presenterAWSProfile= PresenterAWSProfile()
            presenterAWSProfile.uploadPersonalID(this@RegistrationStepFiveActivity,this,photoFile)
        }
        else
        {
            CommonMethod.customSnackBarError(rootLayout, this@RegistrationStepFiveActivity, resources.getString(R.string.no_internet))
        }
    }

    override fun onSuccessUploadAWS(url: String) {

        uploadInformation(url)
    }

    override fun onFailureUploadAWS(string: String) {
        dismiss()
        CommonMethod.customSnackBarError(rootLayout, this@RegistrationStepFiveActivity, string)
    }

    override fun onSucessDeleteUploadAWS(UserIdsPersonalInfo: ResUserInfo.UserIdsPersonalInfo, pos: Int) {
        //
    }

    override fun onFailureDeleteAWS(message: String) {
        //
    }

    override fun onSucessProfileImgDeleteAWS() {
        //
    }

    private fun uploadInformation(url: String)
    {
        val bundle     = intent.extras
        val type       = bundle!!.getString("id_type")
        val number     = bundle.getString("id_number")
        val zoopId     = bundle.getString("zoop_id")
        val jsonObject = JsonObject()
        jsonObject.addProperty("id_image",url)
        jsonObject.addProperty("type_id",type)
        jsonObject.addProperty("id_number",number)

        if(zoopId?.isNotEmpty()!!){
        jsonObject.addProperty("zoop_ref_id",zoopId)
        }

        val presenterRegistrationThree= PresenterRegistrationThree()
        presenterRegistrationThree.doRegistrationStepThree(this@RegistrationStepFiveActivity,jsonObject,this)
    }

    override fun onSuccessRegistrationThree() {

        val logger = AppEventsLogger.newLogger(this@RegistrationStepFiveActivity)
        val params =  Bundle()
        params.putString(AppEventsConstants.EVENT_PARAM_REGISTRATION_METHOD, "Registration")
        logger.logEvent(AppEventsConstants.EVENT_NAME_COMPLETED_REGISTRATION, params)

        MyApplication.getInstance().getGoogleAnalyticsLogger("Sign Up",0) //0 for sign up
        dismiss()
        SharedPref(this@RegistrationStepFiveActivity).removeImagePath()
        Toast.makeText(this@RegistrationStepFiveActivity,resources.getString(R.string.sent_pin_via_sms),Toast.LENGTH_LONG).show()
        startActivity(Intent(this@RegistrationStepFiveActivity, LoginActivity::class.java))
        overridePendingTransition(R.anim.right_in, R.anim.left_out)
    }

    override fun onErrorRegistrationThree(jsonMessage: String) {
       dismiss()
       CommonMethod.customSnackBarError(rootLayout, this@RegistrationStepFiveActivity, jsonMessage)
    }

    private fun initLoader()
    {
        progressDialog = ProgressDialog(this@RegistrationStepFiveActivity,R.style.MyAlertDialogStyle)
        val message=   SpannableString(resources.getString(R.string.saving))
        val face = Typeface.createFromAsset(assets, "fonts/Montserrat-Regular.ttf")
        message.setSpan(RelativeSizeSpan(1.0f), 0, message.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        message.setSpan(CustomTypeFaceSpan("", face!!, Color.parseColor("#535559")), 0, message.length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
        progressDialog.isIndeterminate = true
        progressDialog.setMessage(message)
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        progressDialog.setCancelable(false)
        progressDialog.setCanceledOnTouchOutside(false)

    }

    private fun dismiss()
    {
        if(progressDialog.isShowing)
        {
            progressDialog.dismiss()
        }
    }

    private fun checkAndRequestPermissions(): Boolean {

        val permissionCamera        = ContextCompat.checkSelfPermission(this@RegistrationStepFiveActivity, Manifest.permission.CAMERA)
        val permissionStorage       = ContextCompat.checkSelfPermission(this@RegistrationStepFiveActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE)

        val listPermissionsNeeded = ArrayList<String>()

        if (permissionCamera != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA)
        }
        if (permissionStorage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
        if (listPermissionsNeeded.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toTypedArray(), REQUEST_ID_PERMISSIONS)
            return false
        }

        return true
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            REQUEST_ID_PERMISSIONS -> {

                val perms = HashMap<String, Int>()
                perms[Manifest.permission.CAMERA]                   = PackageManager.PERMISSION_GRANTED
                perms[Manifest.permission.WRITE_EXTERNAL_STORAGE]   = PackageManager.PERMISSION_GRANTED

                if (grantResults.isNotEmpty()) {
                    for (i in permissions.indices)
                        perms[permissions[i]] = grantResults[i]
                    if (perms[Manifest.permission.CAMERA]                           == PackageManager.PERMISSION_GRANTED
                            && perms[Manifest.permission.WRITE_EXTERNAL_STORAGE]    == PackageManager.PERMISSION_GRANTED) {

                        initCamera()
                    } else {

                        if (ActivityCompat.shouldShowRequestPermissionRationale(this@RegistrationStepFiveActivity, Manifest.permission.CAMERA)
                                || ActivityCompat.shouldShowRequestPermissionRationale(this@RegistrationStepFiveActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                            showDialogOK("Permissions are required for L-Pesa",
                                    DialogInterface.OnClickListener { _, which ->
                                        when (which) {
                                            DialogInterface.BUTTON_POSITIVE -> checkAndRequestPermissions()
                                            DialogInterface.BUTTON_NEGATIVE ->

                                                finish()
                                        }
                                    })
                        } else {
                            permissionDialog("You need to give some mandatory permissions to continue. Do you want to go to app settings?")

                        }
                    }
                }
            }
        }

    }

    private fun showDialogOK(message: String, okListener: DialogInterface.OnClickListener) {
        AlertDialog.Builder(this@RegistrationStepFiveActivity,R.style.MyAlertDialogTheme)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", okListener)
                .create()
                .show()
    }

    private fun permissionDialog(msg: String) {
        val dialog = AlertDialog.Builder(this@RegistrationStepFiveActivity,R.style.MyAlertDialogTheme)
        dialog.setMessage(msg)
                .setPositiveButton("Yes") { _, _ ->
                    startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:com.app.l_pesa")))
                }
                .setNegativeButton("Cancel") { _, _ -> finish() }
        dialog.show()
    }

    companion object {

        private const  val REQUEST_ID_PERMISSIONS = 1

    }


    private fun toolbarFont(context: Activity) {

        for (i in 0 until toolbar.childCount) {
            val view = toolbar.getChildAt(i)
            if (view is TextView) {
                val titleFont = Typeface.createFromAsset(context.assets, "fonts/Montserrat-Regular.ttf")
                if (view.text == toolbar.title) {
                    view.typeface = titleFont
                    break
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                overridePendingTransition(R.anim.left_in, R.anim.right_out)
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }
    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.left_in, R.anim.right_out)
    }

    public override fun onResume() {
        super.onResume()
        MyApplication.getInstance().trackScreenView(this@RegistrationStepFiveActivity::class.java.simpleName)

    }

}
