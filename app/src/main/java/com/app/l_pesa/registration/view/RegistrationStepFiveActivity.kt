package com.app.l_pesa.registration.view

import android.Manifest
import android.app.Activity
import android.app.ProgressDialog
import android.content.ClipData
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Typeface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.app.l_pesa.BuildConfig
import com.app.l_pesa.R
import com.app.l_pesa.common.CommonMethod
import com.app.l_pesa.login.view.LoginActivity
import com.app.l_pesa.profile.inter.ICallBackUpload
import com.app.l_pesa.profile.presenter.PresenterAWSProfile
import com.app.l_pesa.registration.inter.ICallBackRegisterThree
import com.app.l_pesa.registration.presenter.PresenterRegistrationThree
import com.google.gson.JsonObject
import id.zelory.compressor.Compressor
import io.fotoapparat.Fotoapparat
import io.fotoapparat.log.logcat
import io.fotoapparat.log.loggers
import io.fotoapparat.parameter.ScaleType
import io.fotoapparat.result.transformer.scaled
import io.fotoapparat.selector.back
import kotlinx.android.synthetic.main.activity_registration_step_five.*
import kotlinx.android.synthetic.main.content_activity_view_file.*
import kotlinx.android.synthetic.main.content_registration_step_three.*
import kotlinx.android.synthetic.main.layout_registration_step_five.*
import kotlinx.android.synthetic.main.layout_registration_step_five.rootLayout
import java.io.File
import java.util.HashMap

class RegistrationStepFiveActivity : AppCompatActivity(), ICallBackUpload, ICallBackRegisterThree {

    private val  requestPhoto               = 12
    private var  captureImageStatus         : Boolean    = false
    private lateinit var photoFile          : File
    private lateinit var captureFilePath    : Uri
    private lateinit var imageFile      : File
    private lateinit var progressDialog : ProgressDialog


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


        val captureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val imagePath = File(filesDir, "images")
        photoFile = File(imagePath, "personal.jpg")
        if (photoFile.exists()) {
            photoFile.delete()
        } else {
            photoFile.parentFile!!.mkdirs()
        }
        captureFilePath = FileProvider.getUriForFile(this@RegistrationStepFiveActivity, BuildConfig.APPLICATION_ID + ".provider", photoFile)

        captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, captureFilePath)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            captureIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        } else {
            val clip = ClipData.newUri(this@RegistrationStepFiveActivity.contentResolver, "id photo", captureFilePath)
            captureIntent.clipData = clip
            captureIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        }

        startActivityForResult(captureIntent, requestPhoto)
    }




    private fun doContinue()
    {
        imageFile   = Compressor(this@RegistrationStepFiveActivity).compressToFile(imageFile)
        if(CommonMethod.isNetworkAvailable(this@RegistrationStepFiveActivity))
        {
            progressDialog.show()
            val presenterAWSProfile= PresenterAWSProfile()
            presenterAWSProfile.uploadPersonalID(this@RegistrationStepFiveActivity,this,imageFile)
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

    private fun uploadInformation(url: String)
    {
        val bundle     = intent.extras
        val type       = bundle!!.getString("id_type")
        val number     = bundle.getString("id_number")

        val jsonObject = JsonObject()
        jsonObject.addProperty("id_image",url)
        jsonObject.addProperty("type_id",type)
        jsonObject.addProperty("id_number",number)

        val presenterRegistrationThree= PresenterRegistrationThree()
        presenterRegistrationThree.doRegistrationStepThree(this@RegistrationStepFiveActivity,jsonObject,this)
    }

    override fun onSuccessRegistrationThree() {
        dismiss()
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
                            showDialogOK("Permissions are required for this app",
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
        AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", okListener)
                .create()
                .show()
    }

    private fun permissionDialog(msg: String) {
        val dialog = AlertDialog.Builder(this@RegistrationStepFiveActivity)
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

}
