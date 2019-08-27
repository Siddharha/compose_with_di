package com.app.l_pesa.registration.view

import android.Manifest
import android.app.Activity
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Typeface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.app.l_pesa.R
import com.app.l_pesa.common.SharedPref
import io.fotoapparat.Fotoapparat
import io.fotoapparat.log.logcat
import io.fotoapparat.log.loggers
import io.fotoapparat.parameter.ScaleType
import io.fotoapparat.result.transformer.scaled
import io.fotoapparat.selector.front
import kotlinx.android.synthetic.main.activity_registration_step_two.*
import java.io.File
import java.util.HashMap
import kotlin.collections.ArrayList
import kotlin.collections.set


class RegistrationStepTwoActivity : AppCompatActivity() {

    private lateinit var progressDialog : ProgressDialog
    private lateinit var fotoapparat    : Fotoapparat
    private lateinit var photoState     : PhotoState
    private lateinit var cameraStatus   : CameraState
    private lateinit var flashState     : FlashState
    private lateinit var imageFile      : File


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration_step_two)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbarFont(this@RegistrationStepTwoActivity)

        initLoader()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            checkAndRequestPermissions()
        }
        else{
            initCamera()
        }


        cameraStatus    = CameraState.BACK
        flashState      = FlashState.OFF
        photoState      = PhotoState.OFF

        buttonContinue.setOnClickListener {

            takePhoto()
        }

    }


    private fun initCamera(){


     fotoapparat = Fotoapparat(
                context         = this@RegistrationStepTwoActivity,
                view            = cameraView,
                scaleType       = ScaleType.CenterCrop,
                lensPosition    = front(),
                logger          = loggers(
                                  logcat()
                ),
                cameraErrorCallback = {

                }
        )

        fotoapparat.start()
        photoState = PhotoState.ON

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

        progressDialog.show()
        val photoResult = fotoapparat
                .autoFocus()
                .takePicture()

        photoResult.saveToFile(imageFile)
        photoResult
                .toBitmap(scaled(scaleFactor = 0.25f))
                .whenAvailable { photo ->
                    photo?.let {
                            val sharedPref=SharedPref(this@RegistrationStepTwoActivity)
                            sharedPref.imagePath=imageFile.absolutePath
                            startActivity(Intent(this@RegistrationStepTwoActivity, RegistrationStepThreeActivity::class.java))
                            overridePendingTransition(R.anim.right_in, R.anim.left_out)
                            dismiss()

                            }

                }

    }


    override fun onStop() {
        super.onStop()
        //fotoapparat.stop()
        PhotoState.OFF
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

    private fun checkAndRequestPermissions(): Boolean {

        val permissionCamera        = ContextCompat.checkSelfPermission(this@RegistrationStepTwoActivity, Manifest.permission.CAMERA)
        val permissionStorage       = ContextCompat.checkSelfPermission(this@RegistrationStepTwoActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE)

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
        else
        {
            initCamera()
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
                        if (perms[Manifest.permission.CAMERA]                       == PackageManager.PERMISSION_GRANTED
                            && perms[Manifest.permission.WRITE_EXTERNAL_STORAGE]    == PackageManager.PERMISSION_GRANTED) {

                        initCamera()
                    } else {

                        if (ActivityCompat.shouldShowRequestPermissionRationale(this@RegistrationStepTwoActivity, Manifest.permission.CAMERA)
                                || ActivityCompat.shouldShowRequestPermissionRationale(this@RegistrationStepTwoActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
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
        val dialog = AlertDialog.Builder(this@RegistrationStepTwoActivity)
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

    override fun onResume() {
        super.onResume()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            checkAndRequestPermissions()
        }
        else{
            initCamera()
        }
    }


}
