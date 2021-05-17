package com.app.l_pesa.registration.view

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.TextUtils
import android.text.style.RelativeSizeSpan
import android.util.Log
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.app.l_pesa.R
import com.app.l_pesa.application.MyApplication
import com.app.l_pesa.common.CommonMethod
import com.app.l_pesa.common.CustomTypeFaceSpan
import com.app.l_pesa.common.SharedPref
import com.app.l_pesa.profile.inter.ICallBackUpload
import com.app.l_pesa.profile.model.ResUserInfo
import com.app.l_pesa.profile.presenter.PresenterAWSProfile
import com.app.l_pesa.registration.inter.ICallBackRegisterTwo
import com.app.l_pesa.registration.model.ReqNameVerify
import com.app.l_pesa.registration.presenter.PresenterRegistrationTwo
import com.bumptech.glide.Glide
import id.zelory.compressor.Compressor
import kotlinx.android.synthetic.main.activity_registration_step_three.*
import kotlinx.android.synthetic.main.layout_registration_step_three.*
import java.io.File


class RegistrationStepThreeActivity : AppCompatActivity(), ICallBackUpload, ICallBackRegisterTwo {


    private lateinit var progressDialog: ProgressDialog
    private lateinit var imageFile: File
    private var sImage: String? = null
    private var sName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration_step_three)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbarFont(this@RegistrationStepThreeActivity)

        sImage = intent.getStringExtra("social_image")
        sName = intent.getStringExtra("social_name")

        etName.setText(sName)

        initLoader()
        initData()


    }

    private fun initData() {
        val sharedPref = SharedPref(this@RegistrationStepThreeActivity)
        imageFile = File(sharedPref.imagePath)
        if (imageFile.exists()) {
            imageFile = Compressor(this@RegistrationStepThreeActivity).compressToFile(imageFile)
            val imagePath = BitmapFactory.decodeFile(imageFile.absolutePath)
            imageView.setImageBitmap(imagePath)
        } else {
            Glide.with(this@RegistrationStepThreeActivity)
                    .load(sImage)
                    .into(imageView)
        }

        imageEdit.setOnClickListener {
            onBackPressed()
            overridePendingTransition(R.anim.left_in, R.anim.right_out)
        }

        btnSubmit.setOnClickListener {
            hideKeyboard()
            if (TextUtils.isEmpty(etName.text.toString())) {
                CommonMethod.customSnackBarError(rootLayout, this@RegistrationStepThreeActivity, resources.getString(R.string.required_name))
            } else {
                if (CommonMethod.isNetworkAvailable(this@RegistrationStepThreeActivity)) {

                    if (imageFile.exists()) {
                        progressDialog.show()
                        val presenterAWSProfile = PresenterAWSProfile()
                        presenterAWSProfile.uploadProfileImageRegistration(this@RegistrationStepThreeActivity, this, imageFile)
                    } else {
                        progressDialog.show()
                        uploadInformation(sImage!!)
                    }


                } else {
                    CommonMethod.customSnackBarError(rootLayout, this@RegistrationStepThreeActivity, resources.getString(R.string.no_internet))
                }
            }
        }

    }

    private fun hideKeyboard() {

        try {
            CommonMethod.hideKeyboardView(this@RegistrationStepThreeActivity)
        } catch (exp: Exception) {

        }

    }

    private fun initLoader() {
        progressDialog = ProgressDialog(this@RegistrationStepThreeActivity, R.style.MyAlertDialogStyle)
        val message = SpannableString(resources.getString(R.string.loading))
        val face = Typeface.createFromAsset(assets, "fonts/Montserrat-Regular.ttf")
        message.setSpan(RelativeSizeSpan(1.0f), 0, message.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        message.setSpan(CustomTypeFaceSpan("", face!!, Color.parseColor("#535559")), 0, message.length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
        progressDialog.isIndeterminate = true
        progressDialog.setMessage(message)
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        progressDialog.setCancelable(false)
        progressDialog.setCanceledOnTouchOutside(false)

    }

    override fun onSuccessUploadAWS(url: String) {

        uploadInformation(url)
       // "$url".toast(this@RegistrationStepThreeActivity)
    }

    override fun onFailureUploadAWS(string: String) {
        dismiss()
        CommonMethod.customSnackBarError(rootLayout, this@RegistrationStepThreeActivity, string)
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

    private fun uploadInformation(url: String) {
        /* val jsonObject = JsonObject()
         jsonObject.addProperty("name", etName.text.toString())
         jsonObject.addProperty("image", url)
         jsonObject.addProperty("otp", etCode.text.toString())
         val presenterRegistrationTwo = PresenterRegistrationTwo()
         presenterRegistrationTwo.doRegistrationStepTwo(this@RegistrationStepThreeActivity, jsonObject, this)*/

        //"$url".toast(this@RegistrationStepThreeActivity)

        val reqNameVerify = ReqNameVerify(
                etName.text.toString().trim(), url
        )

        Log.i("name request : ", reqNameVerify.toString())

        val sharedPref = SharedPref(this@RegistrationStepThreeActivity)

        PresenterRegistrationTwo().doNameVerify(
                this@RegistrationStepThreeActivity,
                sharedPref.accessToken,
                ReqNameVerify(etName.text.toString().trim(), url),
                this)

    }

    override fun onSuccessRegistrationTwo() {
        dismiss()
        doContinue()
    }

    override fun onErrorRegistrationTwo(jsonMessage: String) {
        dismiss()
        CommonMethod.customSnackBarError(rootLayout, this@RegistrationStepThreeActivity, jsonMessage)
    }

    private fun dismiss() {
        if (progressDialog.isShowing) {
            progressDialog.dismiss()
        }
    }


    private fun doContinue() {
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

    public override fun onResume() {
        super.onResume()
        MyApplication.getInstance().trackScreenView(this@RegistrationStepThreeActivity::class.java.simpleName)

    }

}
