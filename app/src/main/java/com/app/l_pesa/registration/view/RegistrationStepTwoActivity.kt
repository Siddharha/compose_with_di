package com.app.l_pesa.registration.view

import android.Manifest
import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.graphics.Typeface
import android.media.MediaScannerConnection
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.exifinterface.media.ExifInterface
import com.app.l_pesa.R
import com.app.l_pesa.common.CommonMethod
import com.app.l_pesa.common.SharedPref
import com.app.l_pesa.main.view.MainActivity
import com.app.l_pesa.profile.inter.ICallBackUpload
import com.app.l_pesa.registration.inter.ICallBackRegisterTwo
import com.kaopiz.kprogresshud.KProgressHUD
import io.fotoapparat.Fotoapparat
import io.fotoapparat.log.logcat
import io.fotoapparat.log.loggers
import io.fotoapparat.parameter.ScaleType
import io.fotoapparat.result.transformer.scaled
import io.fotoapparat.selector.front
import kotlinx.android.synthetic.main.activity_registration_step_two.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*


class RegistrationStepTwoActivity : AppCompatActivity(), ICallBackUpload, ICallBackRegisterTwo {

    private lateinit var progressDialog  : ProgressDialog
    private lateinit var fotoapparat    : Fotoapparat
    private lateinit var photoState     : PhotoState
    private lateinit var cameraStatus   : CameraState
    private lateinit var flashState     : FlashState
    private lateinit var imageFile      : File

    private var permissions = arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration_step_two)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbarFont(this@RegistrationStepTwoActivity)

        initLoader()
        initCamera()

        cameraStatus    = CameraState.BACK
        flashState      = FlashState.OFF
        photoState      = PhotoState.OFF

        buttonContinue.setOnClickListener {

            takePhoto()
        }

    }


    private fun initCamera(){


        fotoapparat = Fotoapparat(
                context         = this,
                view            = cameraView,
                scaleType       = ScaleType.CenterCrop,
                lensPosition    = front(),
                logger          = loggers(
                                logcat()
                ),
                cameraErrorCallback = {

                }
        )

        val imgDirectory = File ((getExternalFilesDir(Environment.DIRECTORY_PICTURES)).toString())
        if (!imgDirectory.exists())
        {
            imgDirectory.mkdirs()
        }
        imageFile= File(imgDirectory,  "selfie.png")
    }

    enum class CameraState{
        FRONT, BACK
    }

    enum class FlashState{
        TORCH, OFF
    }

    enum class PhotoState{
        ON, OFF
    }



    private fun takePhoto() {
        if (hasNoPermissions()) {

            val permissions = arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
            ActivityCompat.requestPermissions(this, permissions,0)
        }else{
            progressDialog.show()
            val photoResult = fotoapparat
                    .autoFocus()
                    .takePicture()

            photoResult.saveToFile(imageFile)
            photoResult
                    .toBitmap(scaled(scaleFactor = 0.25f))
                    .whenAvailable { photo ->
                        photo
                                ?.let {
                                    val sharedPref=SharedPref(this@RegistrationStepTwoActivity)
                                    sharedPref.imagePath=imageFile.absolutePath
                                    startActivity(Intent(this@RegistrationStepTwoActivity, RegistrationStepThreeActivity::class.java))
                                    overridePendingTransition(R.anim.right_in, R.anim.left_out)
                                    dismiss()
                                }

                    }
           /* progressDialog.show()
            fotoapparat.takePicture().saveToFile(imageFile)

            Handler().postDelayed({
                val sharedPref=SharedPref(this@RegistrationStepTwoActivity)
                sharedPref.imagePath=imageFile.absolutePath
                dismiss()
                startActivity(Intent(this@RegistrationStepTwoActivity, RegistrationStepThreeActivity::class.java))
                overridePendingTransition(R.anim.right_in, R.anim.left_out)
               }, 3000)*/


        }
    }


    @RequiresApi(Build.VERSION_CODES.M)
    override fun onStart() {
        super.onStart()

        if (hasNoPermissions()) {
            requestPermission()
        }else{
            fotoapparat.start()
            photoState = PhotoState.ON

        }
    }

    private fun hasNoPermissions(): Boolean{
        return ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED  && ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
    }

    fun requestPermission(){
        ActivityCompat.requestPermissions(this, permissions,0)
    }

    override fun onStop() {
        super.onStop()
        fotoapparat.stop()
        PhotoState.OFF
    }

    override fun onPause() {
        super.onPause()
        println("OnPause")
    }

    override fun onResume() {
        super.onResume()

       /* if(!hasNoPermissions() && fotoapparatState == FotoapparatState.OFF){
            val intent = Intent(baseContext, RegistrationStepTwoActivity::class.java)
            startActivity(intent)
            finish()
        }*/
    }




    private fun initLoader()
    {
        progressDialog = ProgressDialog(this@RegistrationStepTwoActivity,R.style.MyAlertDialogStyle)
        progressDialog.isIndeterminate = true
        progressDialog.setMessage(resources.getString(R.string.saving))
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

  /*  private fun onSubmit()
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
                val imgFile=CommonMethod.fileCompress(photoFile)
                presenterAWSProfile.uploadProfileImageRegistration(this@RegistrationStepTwoActivity,this,imgFile)

            }
            else
            {
                CommonMethod.customSnackBarError(rootLayout,this@RegistrationStepTwoActivity,resources.getString(R.string.no_internet))
            }
        }
    }*/

    override fun onSuccessUploadAWS(url: String) {

        uploadData(url)
    }

    override fun onFailureUploadAWS(string: String) {

        dismiss()
        Toast.makeText(this@RegistrationStepTwoActivity,string,Toast.LENGTH_SHORT).show()
    }

    private fun uploadData(imageURL: String)
    {

        /*val jsonObject = JsonObject()
        jsonObject.addProperty("name",etName.text.toString())
        jsonObject.addProperty("image",imageURL)
        jsonObject.addProperty("otp",mobileOtp)

        val presenterRegistrationTwo= PresenterRegistrationTwo()
        presenterRegistrationTwo.doRegistrationStepTwo(this@RegistrationStepTwoActivity,jsonObject,this)*/


    }

    override fun onSuccessRegistrationTwo() {

        startActivity(Intent(this@RegistrationStepTwoActivity, RegistrationStepThreeActivity::class.java))
        overridePendingTransition(R.anim.right_in, R.anim.left_out)
    }

    override fun onErrorRegistrationTwo(jsonMessage: String) {

        dismiss()
        CommonMethod.customSnackBarError(rootLayout,this@RegistrationStepTwoActivity,jsonMessage)
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


}
