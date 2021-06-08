package com.app.l_pesa.registration.view

import `in`.creativelizard.creativecam.CamUtil
import `in`.creativelizard.creativecam.CamViewActivity
import android.Manifest
import android.app.Activity
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.*
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.text.Spannable
import android.text.SpannableString
import android.text.style.RelativeSizeSpan
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.exifinterface.media.ExifInterface
import com.app.l_pesa.R
import com.app.l_pesa.application.MyApplication
import com.app.l_pesa.common.*
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_registration_step_two.*
import kotlinx.android.synthetic.main.nav_header_main.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.HashMap
import kotlin.collections.ArrayList
import kotlin.collections.set


class RegistrationStepTwoActivity : AppCompatActivity() {

    private lateinit var progressDialog: ProgressDialog
    private val requestPhoto = 12
    private var captureImageStatus: Boolean = false
    private lateinit var photoFile: File
    private lateinit var captureFilePath: Uri

    private var socialImage: String? = null
    private var name: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration_step_two)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbarFont(this@RegistrationStepTwoActivity)

        socialImage = intent.getStringExtra("social_image")
        name = intent.getStringExtra("name")

        println("social img is $socialImage")

        if (socialImage != null){
            Glide.with(this@RegistrationStepTwoActivity)
                    .load(socialImage)
                    .into(imageProfile)
        }else{
            imageProfile.setImageURI(null)
        }


        initLoader()
        imageProfile.setOnClickListener {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkAndRequestPermissions()) {
                    initCamera()
                } else {
                    checkAndRequestPermissions()
                }
            } else {
                initCamera()
            }
        }

        buttonContinue.setOnClickListener {

            if (imageProfile.drawable == null) {
                CommonMethod.customSnackBarError(rootLayout, this@RegistrationStepTwoActivity, resources.getString(R.string.required_profile_image))
            } else {
                progressDialog.show()
                Handler().postDelayed({
                    dismiss()
                    val intent = Intent(this@RegistrationStepTwoActivity, RegistrationStepThreeActivity::class.java)
                    intent.putExtra("social_image", socialImage)
                    intent.putExtra("social_name", name)
                    startActivity(intent)
                    overridePendingTransition(R.anim.right_in, R.anim.left_out)
                }, 2000)

            }
        }

    }


    private fun initCamera() {

       /* val captureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
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
            captureIntent.putExtra("android.intent.extras.CAMERA_FACING",1)
        } else {
            val clip = ClipData.newUri(this@RegistrationStepTwoActivity.contentResolver, "user photo", captureFilePath)
            captureIntent.clipData = clip
            captureIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            captureIntent.putExtra("android.intent.extras.CAMERA_FACING",1)
        }

        startActivityForResult(captureIntent, requestPhoto)*/

        val intent_cam = Intent(this, CamViewActivity::class.java)
        intent_cam.putExtra(CamUtil.CAM_FACING,1)
        intent_cam.putExtra(CamUtil.CAM_SWITCH_OPT,false)
        intent_cam.putExtra(CamUtil.CAPTURE_BTN_COLOR,"#00695c")
        intent_cam.putExtra(CamUtil.CAPTURE_CONTROL_COLOR,"#ffffff")
        startActivityForResult(intent_cam,requestPhoto)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            requestPhoto ->

                if (resultCode == Activity.RESULT_OK) {
                   // setImage()
                    try {
                        setImage(data?.getStringExtra(CamUtil.IMG_FILE_PATH)!!)
                    }catch (e:Exception){
                        e.printStackTrace()
                    }
                }

        }
    }

    private fun setImage(filepath:String) {
        photoFile = File(filepath)
        //val photoPath: Uri = captureFilePath
        val photoPath: Uri = Uri.fromFile(photoFile)//captureFilePath
        try {
            if (photoPath != Uri.EMPTY) {
                progressDialog.show()
                handleRotation(photoFile.absolutePath)
                Handler().postDelayed({
                    dismiss()
                    imageProfile.setImageURI(null)
                    imageProfile.setImageURI(photoPath)
                    captureImageStatus = true
                    val sharedPref = SharedPref(this@RegistrationStepTwoActivity)
                    sharedPref.imagePath = photoFile.absolutePath
                }, 5000)


            } else {
                Toast.makeText(this@RegistrationStepTwoActivity, "Retake Photo", Toast.LENGTH_SHORT).show()
            }

        } catch (exp: Exception) {
            Toast.makeText(this@RegistrationStepTwoActivity, "Retake Photo", Toast.LENGTH_SHORT).show()
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


    private fun initLoader() {
        progressDialog = ProgressDialog(this@RegistrationStepTwoActivity, R.style.MyAlertDialogStyle)
        val message = SpannableString(resources.getString(R.string.saving))
        val face = Typeface.createFromAsset(assets, "fonts/Montserrat-Regular.ttf")
        message.setSpan(RelativeSizeSpan(1.0f), 0, message.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        message.setSpan(CustomTypeFaceSpan("", face!!, Color.parseColor("#535559")), 0, message.length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
        progressDialog.isIndeterminate = true
        progressDialog.setMessage(message)
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        progressDialog.setCancelable(false)
        progressDialog.setCanceledOnTouchOutside(false)

    }

    private fun dismiss() {
        if (progressDialog.isShowing) {
            progressDialog.dismiss()
        }
    }

    private fun checkAndRequestPermissions(): Boolean {

        val permissionCamera = ContextCompat.checkSelfPermission(this@RegistrationStepTwoActivity, Manifest.permission.CAMERA)
        val permissionStorage = ContextCompat.checkSelfPermission(this@RegistrationStepTwoActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE)

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
                perms[Manifest.permission.CAMERA] = PackageManager.PERMISSION_GRANTED
                perms[Manifest.permission.WRITE_EXTERNAL_STORAGE] = PackageManager.PERMISSION_GRANTED

                if (grantResults.isNotEmpty()) {
                    for (i in permissions.indices)
                        perms[permissions[i]] = grantResults[i]
                    if (perms[Manifest.permission.CAMERA] == PackageManager.PERMISSION_GRANTED
                            && perms[Manifest.permission.WRITE_EXTERNAL_STORAGE] == PackageManager.PERMISSION_GRANTED) {

                        initCamera()
                    } else {

                        if (ActivityCompat.shouldShowRequestPermissionRationale(this@RegistrationStepTwoActivity, Manifest.permission.CAMERA)
                                || ActivityCompat.shouldShowRequestPermissionRationale(this@RegistrationStepTwoActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
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
        AlertDialog.Builder(this@RegistrationStepTwoActivity, R.style.MyAlertDialogTheme)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", okListener)
                .create()
                .show()
    }

    private fun permissionDialog(msg: String) {
        val dialog = AlertDialog.Builder(this@RegistrationStepTwoActivity, R.style.MyAlertDialogTheme)
        dialog.setMessage(msg)
                .setPositiveButton("Yes") { _, _ ->
                    startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:com.app.l_pesa")))
                }
                .setNegativeButton("Cancel") { _, _ -> finish() }
        dialog.show()
    }

    companion object {

        private const val REQUEST_ID_PERMISSIONS = 1

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
        SharedPref(this@RegistrationStepTwoActivity).removeImagePath()
        super.onBackPressed()
        overridePendingTransition(R.anim.left_in, R.anim.right_out)
    }

    public override fun onResume() {
        super.onResume()
        MyApplication.getInstance().trackScreenView(this@RegistrationStepTwoActivity::class.java.simpleName)

    }


}
