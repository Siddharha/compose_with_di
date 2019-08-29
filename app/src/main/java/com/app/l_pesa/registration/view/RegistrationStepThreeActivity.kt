package com.app.l_pesa.registration.view

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.graphics.Typeface
import android.os.Bundle
import android.text.TextUtils
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.exifinterface.media.ExifInterface
import com.app.l_pesa.R
import com.app.l_pesa.common.CommonMethod
import com.app.l_pesa.common.SharedPref
import com.app.l_pesa.profile.inter.ICallBackUpload
import com.app.l_pesa.profile.presenter.PresenterAWSProfile
import com.app.l_pesa.registration.inter.ICallBackRegisterTwo
import com.app.l_pesa.registration.presenter.PresenterRegistrationTwo
import com.google.gson.JsonObject
import id.zelory.compressor.Compressor
import kotlinx.android.synthetic.main.activity_registration_step_three.*
import kotlinx.android.synthetic.main.layout_registration_step_three.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


class RegistrationStepThreeActivity : AppCompatActivity(), ICallBackUpload, ICallBackRegisterTwo {


    private lateinit  var progressDialog    : ProgressDialog
    private lateinit  var imageFile         : File

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration_step_three)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbarFont(this@RegistrationStepThreeActivity)

        initLoader()
        initData()


    }

    private fun initData()
    {

        val sharedPref=SharedPref(this@RegistrationStepThreeActivity)
       // handleRotation(sharedPref.imagePath)
        imageFile   = File(sharedPref.imagePath)
        imageFile   = Compressor(this@RegistrationStepThreeActivity).compressToFile(imageFile)

        val imagePath = BitmapFactory.decodeFile(imageFile.absolutePath)
        imageView.setImageBitmap(imagePath)

        imageEdit.setOnClickListener {

            startActivity(Intent(this@RegistrationStepThreeActivity, RegistrationStepTwoActivity::class.java))
            overridePendingTransition(R.anim.left_in, R.anim.right_out)

        }

        btnSubmit.setOnClickListener {

            hideKeyboard()
            if (TextUtils.isEmpty(etName.text.toString()))
            {
                CommonMethod.customSnackBarError(rootLayout, this@RegistrationStepThreeActivity, resources.getString(R.string.required_name))
            } else if (TextUtils.isEmpty(etCode.text.toString()))
            {
                CommonMethod.customSnackBarError(rootLayout, this@RegistrationStepThreeActivity, resources.getString(R.string.required_verification_code))
            } else if (etCode.text.toString() != sharedPref.verificationCode)
            {
                CommonMethod.customSnackBarError(rootLayout, this@RegistrationStepThreeActivity, resources.getString(R.string.otp_not_match))
            } else
            {
                if (CommonMethod.isNetworkAvailable(this@RegistrationStepThreeActivity)) {

                    if(imageFile.exists())
                    {
                        progressDialog.show()
                        val presenterAWSProfile= PresenterAWSProfile()
                        presenterAWSProfile.uploadProfileImageRegistration(this@RegistrationStepThreeActivity,this,imageFile)
                    }
                    else
                    {
                        CommonMethod.customSnackBarError(rootLayout, this@RegistrationStepThreeActivity, resources.getString(R.string.image_not_found))
                    }


                } else {
                    CommonMethod.customSnackBarError(rootLayout, this@RegistrationStepThreeActivity, resources.getString(R.string.no_internet))
                }
            }
        }

    }

    private fun hideKeyboard()
    {

        try {
            CommonMethod.hideKeyboardView(this@RegistrationStepThreeActivity)
        } catch (exp: Exception) {

        }

    }

   /* private fun handleRotation(imgPath: String) {
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
                            else -> origin.rotate(270f)
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
    }*/


    private fun initLoader()
    {
        progressDialog = ProgressDialog(this@RegistrationStepThreeActivity,R.style.MyAlertDialogStyle)
        progressDialog.isIndeterminate = true
        progressDialog.setMessage(resources.getString(R.string.loading))
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        progressDialog.setCancelable(false)
        progressDialog.setCanceledOnTouchOutside(false)

    }

    override fun onSuccessUploadAWS(url: String) {

        uploadInformation(url)
    }

    override fun onFailureUploadAWS(string: String) {
        dismiss()
        CommonMethod.customSnackBarError(rootLayout, this@RegistrationStepThreeActivity, string)
    }

    private fun uploadInformation(url: String)
    {
        val jsonObject = JsonObject()
        jsonObject.addProperty("name",etName.text.toString())
        jsonObject.addProperty("image",url)
        jsonObject.addProperty("otp",etCode.text.toString())

        val presenterRegistrationTwo= PresenterRegistrationTwo()
        presenterRegistrationTwo.doRegistrationStepTwo(this@RegistrationStepThreeActivity,jsonObject,this)
    }

    override fun onSuccessRegistrationTwo() {
        dismiss()
        doContinue()
    }

    override fun onErrorRegistrationTwo(jsonMessage: String) {
        dismiss()
        CommonMethod.customSnackBarError(rootLayout, this@RegistrationStepThreeActivity, jsonMessage)
    }

    private fun dismiss()
    {
        if(progressDialog.isShowing)
        {
            progressDialog.dismiss()
        }
    }


    private fun doContinue()
    {
        startActivity(Intent(this@RegistrationStepThreeActivity, RegistrationStepFourActivity::class.java))
        overridePendingTransition(R.anim.right_in, R.anim.left_out)

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
