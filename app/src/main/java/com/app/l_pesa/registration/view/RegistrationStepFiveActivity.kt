package com.app.l_pesa.registration.view

import android.Manifest
import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Typeface
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.app.l_pesa.R
import com.app.l_pesa.common.SharedPref
import io.fotoapparat.Fotoapparat
import io.fotoapparat.log.logcat
import io.fotoapparat.log.loggers
import io.fotoapparat.parameter.ScaleType
import io.fotoapparat.selector.back
import kotlinx.android.synthetic.main.activity_registration_step_five.*
import kotlinx.android.synthetic.main.layout_registration_step_five.*
import java.io.File

class RegistrationStepFiveActivity : AppCompatActivity() {

    private lateinit var fotoapparat    : Fotoapparat
    private lateinit var photoState     : PhotoState
    private lateinit var cameraStatus   : CameraState
    private lateinit var flashState     : FlashState
    private lateinit var imageFile      : File
    private lateinit var progressDialog : ProgressDialog

    private var permissions = arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration_step_five)
        setSupportActionBar(toolbar)
        toolbarFont(this@RegistrationStepFiveActivity)

        initData()
    }

    private fun initData()
    {
        initLoader()


        fotoapparat = Fotoapparat(
                context = this,
                view = camera_view,
                scaleType = ScaleType.CenterCrop,
                lensPosition = back(),
                logger = loggers(
                        logcat()
                ),
                cameraErrorCallback = {

                }
        )

        cameraStatus    = CameraState.BACK
        flashState      = FlashState.OFF
        photoState      = PhotoState.OFF

        imageSave.setOnClickListener {

            takePhoto()
        }
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

            val imgDirectory = File ((getExternalFilesDir(Environment.DIRECTORY_PICTURES)).toString())
            if (!imgDirectory.exists())
            {
                imgDirectory.mkdirs()
            }

            imageFile= File(imgDirectory,  "personal.png")
            progressDialog.show()
            fotoapparat.takePicture().saveToFile(imageFile)

            Handler().postDelayed({
                val sharedPref= SharedPref(this@RegistrationStepFiveActivity)
                sharedPref.imagePathPersonal=imageFile.absolutePath
                startActivity(Intent(this@RegistrationStepFiveActivity, RegistrationStepFourActivity::class.java))
                dismiss()
                overridePendingTransition(R.anim.left_in, R.anim.right_out)

            }, 5000)


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
        //fotoapparat!!.stop()
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
}
