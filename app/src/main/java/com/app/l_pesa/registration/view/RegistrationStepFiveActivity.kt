package com.app.l_pesa.registration.view

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.app.l_pesa.R
import com.app.l_pesa.common.SharedPref
import io.fotoapparat.Fotoapparat
import io.fotoapparat.log.logcat
import io.fotoapparat.log.loggers
import io.fotoapparat.parameter.ScaleType
import io.fotoapparat.result.transformer.scaled
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
        initCamera()

        cameraStatus    = CameraState.BACK
        flashState      = FlashState.OFF
        photoState      = PhotoState.OFF


        buttonComplete.setOnClickListener {

            takePhoto()
        }
    }

    private fun initCamera(){


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

        val imgDirectory = File ((getExternalFilesDir(Environment.DIRECTORY_PICTURES)).toString())
        if (!imgDirectory.exists())
        {
            imgDirectory.mkdirs()
        }
        imageFile= File(imgDirectory,  "personal.png")
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

       val photoResult = fotoapparat
                .autoFocus()
                .takePicture()



        photoResult.saveToFile(imageFile)
        photoResult
                .toBitmap(scaled(scaleFactor = 0.25f))
                .whenAvailable { photo ->
                    photo
                            ?.let {

                                Toast.makeText(this@RegistrationStepFiveActivity,"Success",Toast.LENGTH_SHORT).show()
                            }

                }
    }



    @RequiresApi(Build.VERSION_CODES.M)
    override fun onStart() {
        super.onStart()
            fotoapparat.start()
            photoState = PhotoState.ON

    }


    override fun onStop() {
        super.onStop()
       // fotoapparat.stop()
        PhotoState.OFF
    }

    override fun onPause() {
        super.onPause()
        println("OnPause")
    }

    override fun onResume() {
        super.onResume()

        /*if(photoState == PhotoState.OFF){
             val intent = Intent(baseContext, RegistrationStepFiveActivity::class.java)
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
