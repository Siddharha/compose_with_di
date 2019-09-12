package com.app.l_pesa.registration.view

import android.Manifest
import android.app.Activity
import android.app.ProgressDialog
import android.content.ClipData
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.graphics.Typeface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.provider.Settings
import android.view.MenuItem
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
import com.app.l_pesa.common.CommonMethod
import com.app.l_pesa.common.SharedPref
import kotlinx.android.synthetic.main.activity_registration_step_two.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.HashMap
import kotlin.collections.ArrayList
import kotlin.collections.set


class RegistrationStepTwoActivity : AppCompatActivity() {

    private lateinit var progressDialog : ProgressDialog
   /* private lateinit var fotoapparat    : Fotoapparat
    private lateinit var photoState     : PhotoState
    private lateinit var cameraStatus   : CameraState
    private lateinit var flashState     : FlashState*/
    //private lateinit var imageFile      : File
    private val  requestPhoto               = 12
    private var  captureImageStatus         : Boolean    = false
    private lateinit var photoFile          : File
    private lateinit var captureFilePath    : Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration_step_two)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbarFont(this@RegistrationStepTwoActivity)

        initLoader()
        imageProfile.setOnClickListener {

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

        buttonContinue.setOnClickListener {

           if(captureImageStatus)
           {
               progressDialog.show()
               Handler().postDelayed({
                   dismiss()
                   val intent = Intent(this@RegistrationStepTwoActivity, RegistrationStepThreeActivity::class.java)
                   startActivity(intent)
                   overridePendingTransition(R.anim.right_in, R.anim.left_out)
               }, 2000)

           }
            else
           {
               CommonMethod.customSnackBarError(rootLayout, this@RegistrationStepTwoActivity, resources.getString(R.string.required_profile_image))
           }
        }

    }


    private fun initCamera(){

        val captureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val imagePath = File(filesDir, "images")
        photoFile = File(imagePath, "user.jpg")
        if (photoFile.exists()) {
            photoFile.delete()
        } else {
            photoFile.parentFile!!.mkdirs()
        }
        captureFilePath = FileProvider.getUriForFile(this@RegistrationStepTwoActivity, BuildConfig.APPLICATION_ID + ".provider", photoFile)

        captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, captureFilePath)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            captureIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        } else {
            val clip = ClipData.newUri(this@RegistrationStepTwoActivity.contentResolver, "user photo", captureFilePath)
            captureIntent.clipData = clip
            captureIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        }

        startActivityForResult(captureIntent, requestPhoto)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            requestPhoto ->

                if (resultCode == Activity.RESULT_OK)
                {
                    setImage()
                }

        }
    }

    private fun setImage() {

        val photoPath: Uri  = captureFilePath
        try {
            if(photoPath!=Uri.EMPTY)
            {
                    progressDialog.show()
                    handleRotation(photoFile.absolutePath)
                    Handler().postDelayed({
                    dismiss()
                    imageProfile.setImageURI(null)
                    imageProfile.setImageURI(photoPath)
                    captureImageStatus       = true
                    val sharedPref=SharedPref(this@RegistrationStepTwoActivity)
                    sharedPref.imagePath=photoFile.absolutePath
                }, 1000)
            }
            else
            {
                Toast.makeText(this@RegistrationStepTwoActivity,"Retake Photo", Toast.LENGTH_SHORT).show()
            }

        }
        catch (exp:Exception)
        {
            Toast.makeText(this@RegistrationStepTwoActivity,"Retake Photo", Toast.LENGTH_SHORT).show()
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
                            else -> origin                  //origin.rotate(270f)
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
        AlertDialog.Builder(this@RegistrationStepTwoActivity,R.style.MyAlertDialogTheme)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", okListener)
                .create()
                .show()
    }

    private fun permissionDialog(msg: String) {
        val dialog = AlertDialog.Builder(this@RegistrationStepTwoActivity,R.style.MyAlertDialogTheme)
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
