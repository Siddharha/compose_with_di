package com.app.l_pesa.registration.view

import android.Manifest
import android.app.Activity
import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.graphics.SurfaceTexture
import android.hardware.camera2.*
import android.media.ImageReader
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.*
import android.provider.MediaStore
import android.text.TextUtils
import android.view.Surface
import android.view.TextureView
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.NumberPicker
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.app.l_pesa.BuildConfig
import com.app.l_pesa.R
import com.app.l_pesa.common.BitmapResize
import com.app.l_pesa.common.CommonMethod
import com.app.l_pesa.profile.inter.ICallBackUpload
import com.app.l_pesa.profile.presenter.PresenterAWSProfile
import com.app.l_pesa.registration.inter.ICallBackRegisterTwo
import com.app.l_pesa.registration.presenter.PresenterRegistrationTwo
import com.google.gson.JsonObject
import com.kaopiz.kprogresshud.KProgressHUD
import kotlinx.android.synthetic.main.activity_registration_step_two.*
import java.io.*
import java.util.*


class RegistrationStepTwoActivity : AppCompatActivity(), ICallBackUpload, ICallBackRegisterTwo {

    private lateinit              var progressDialog: KProgressHUD
    private var captureImageStatus : Boolean    = false
    private lateinit var photoFile          : File
    private lateinit var captureFilePath    : Uri
    private var mobileOtp           = ""
    private val requestPhoto        = 14
    private val requestGallery      = 15

    val PERMISION_CAMERA        = 200
    val PERMISION_WRITE_STORAGE = 1000
    val PERMISION_READ_STORAGE  = 1001


    private lateinit var previewView   : TextureView
    private lateinit var imageReader   : ImageReader


    private lateinit var previewRequestBuilder : CaptureRequest.Builder
    private lateinit var previewRequest        : CaptureRequest
    private var backgroundHandler              : Handler?                = null
    private var backgroundThread               : HandlerThread?          = null
    private var cameraDevice                   : CameraDevice?           = null
    private lateinit var captureSession        : CameraCaptureSession


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration_step_two)

        val bundle = intent.extras
        //mobileOtp       = bundle!!.getString("OTP")!!

        initLoader()
        /* imgProfilePhoto.setOnClickListener {

            selectImage()
        }

        btnSubmit.setOnClickListener {

            onSubmit()
        }*/


        val writePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        val readPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)

        if ((writePermission != PackageManager.PERMISSION_GRANTED) || (readPermission != PackageManager.PERMISSION_GRANTED)) {
            requestStoragePermission()
        }

        previewView = findViewById(R.id.mySurfaceView)
        previewView.surfaceTextureListener = surfaceTextureListener
        startBackgroundThread()


        btnSubmit.setOnClickListener {

            val appDir = File(Environment.getExternalStorageDirectory(), "Camera2Sample")

            if (!appDir.exists()) {
                appDir.mkdirs()
            }

            try {
                val filename = "picture.jpg"
                var savefile: File? = null


                captureSession.stopRepeating()
                if (previewView.isAvailable) {

                    savefile = File(appDir, filename)
                    val fos = FileOutputStream(savefile)
                    val bitmap: Bitmap = previewView.bitmap
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 85, fos)
                    fos.close()
                }

                if (savefile != null) {

                    Toast.makeText(this, "Saved: $savefile", Toast.LENGTH_SHORT).show()
                }

            } catch (e: CameraAccessException) {

            } catch (e: FileNotFoundException) {

            } catch (e: IOException) {

            }

            captureSession.setRepeatingRequest(previewRequest, null, null)
        }

        imgRotate.setOnClickListener {


        }
    }

    private fun startBackgroundThread() {
        backgroundThread = HandlerThread("CameraBackground").also { it.start() }
        backgroundHandler = Handler(backgroundThread!!.looper)
    }

    private val surfaceTextureListener = object : TextureView.SurfaceTextureListener
    {
        // TextureViewが有効になった
        override fun onSurfaceTextureAvailable(surface: SurfaceTexture, width: Int, height: Int)
        {
            imageReader = ImageReader.newInstance(width, height, ImageFormat.JPEG,2)
            openCamera()
        }

        // TextureViewのサイズが変わった
        override fun onSurfaceTextureSizeChanged(p0: SurfaceTexture?, p1: Int, p2: Int) { }

        // TextureViewが更新された
        override fun onSurfaceTextureUpdated(p0: SurfaceTexture?) { }

        // TextureViewが破棄された
        override fun onSurfaceTextureDestroyed(p0: SurfaceTexture?): Boolean
        {
            return false
        }
    }


    private fun openCamera() {

        val manager: CameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager

        try {

            val camerId: String = manager.cameraIdList[1]


            val permission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)

            if (permission != PackageManager.PERMISSION_GRANTED) {
                requestCameraPermission()
                return
            }


            manager.openCamera(camerId, stateCallback, null)



        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private fun requestCameraPermission() {
        if (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)
                } else {
                    TODO("VERSION.SDK_INT < M")
                }) {
            AlertDialog.Builder(baseContext)
                    .setMessage("Permission Check")
                    .setPositiveButton(android.R.string.ok) { _, _ ->
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            requestPermissions(arrayOf(Manifest.permission.CAMERA), PERMISION_CAMERA)
                        }
                    }
                    .setNegativeButton(android.R.string.cancel) { _, _ ->
                        finish()
                    }
                    .show()
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(arrayOf(Manifest.permission.CAMERA), PERMISION_CAMERA)
            }
        }
    }

    /**
     * カメラ状態取得コールバック関数
     */
    private val stateCallback = object : CameraDevice.StateCallback() {

        /**
         * カメラ接続完了
         */
        override fun onOpened(cameraDevice: CameraDevice) {
            this@RegistrationStepTwoActivity.cameraDevice = cameraDevice
            createCameraPreviewSession()
        }

        /**
         * カメラ切断
         */
        override fun onDisconnected(cameraDevice: CameraDevice) {
            cameraDevice.close()
            this@RegistrationStepTwoActivity.cameraDevice = null
        }

        /**
         * カメラエラー
         */
        override fun onError(cameraDevice: CameraDevice, error: Int) {
            onDisconnected(cameraDevice)
            finish()
        }
    }

    /**
     * カメラ画像生成許可取得ダイアログを表示
     */
    private fun createCameraPreviewSession()
    {
        try
        {
            val texture = previewView.surfaceTexture
            texture.setDefaultBufferSize(previewView.width, previewView.height)

            val surface = Surface(texture)
            previewRequestBuilder = cameraDevice!!.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
            previewRequestBuilder.addTarget(surface)

            cameraDevice?.createCaptureSession(Arrays.asList(surface, imageReader.surface),
                    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
                    object : CameraCaptureSession.StateCallback()
                    {
                        override fun onConfigured(cameraCaptureSession: CameraCaptureSession)
                        {
                            if (cameraDevice == null) return
                            try
                            {
                                captureSession = cameraCaptureSession
                                previewRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE)
                                previewRequest = previewRequestBuilder.build()
                                cameraCaptureSession.setRepeatingRequest(previewRequest, null, Handler(backgroundThread?.looper))
                            } catch (e: CameraAccessException) {

                            }

                        }

                        override fun onConfigureFailed(session: CameraCaptureSession) {
                            //Tools.makeToast(baseContext, "Failed")
                        }
                    }, null)
        } catch (e: CameraAccessException) {

        }
    }

    private fun requestStoragePermission() {
        /**
         * 書き込み権限
         */
        if (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                } else {
                    TODO("VERSION.SDK_INT < M")
                }) {
            AlertDialog.Builder(baseContext)
                    .setMessage("Permission Here")
                    .setPositiveButton(android.R.string.ok) { _, _ ->
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), PERMISION_WRITE_STORAGE)
                        }
                    }
                    .setNegativeButton(android.R.string.cancel) { _, _ ->
                        finish()
                    }
                    .create()
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), PERMISION_WRITE_STORAGE)
            }
        }

        /**
         * 読み込み権限
         */
        if (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)
                } else {
                    TODO("VERSION.SDK_INT < M")
                }) {
            AlertDialog.Builder(baseContext)
                    .setMessage("Permission Here")
                    .setPositiveButton(android.R.string.ok) { _, _ ->
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), PERMISION_READ_STORAGE)
                        }
                    }
                    .setNegativeButton(android.R.string.cancel) { _, _ ->
                        finish()
                    }
                    .create()
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), PERMISION_READ_STORAGE)
            }
        }
    }




    private fun initLoader()
    {
        progressDialog=KProgressHUD.create(this@RegistrationStepTwoActivity)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setCancellable(false)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f)

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
        btnSubmit.isClickable=true
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
        btnSubmit.isClickable=true
        CommonMethod.customSnackBarError(rootLayout,this@RegistrationStepTwoActivity,jsonMessage)
    }

    private fun selectImage() {

        val items = arrayOf<CharSequence>("Camera", "Gallery", "Cancel")
        val dialogView = AlertDialog.Builder(this@RegistrationStepTwoActivity,R.style.MyAlertDialogTheme)
        dialogView.setItems(items) { dialog, item ->

            when {
                items[item] == "Camera" ->
                    cameraClick()
                items[item] == "Gallery" ->
                    galleryClick()
                items[item] == "Cancel" ->
                    dialog.dismiss()
            }
        }

        dialogView.show()

    }

    private fun cameraClick()
    {

        cacheDir.deleteRecursively()
        val captureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val imagePath = File(filesDir, "images")
        photoFile = File(imagePath, "user.jpg")
        if (photoFile.exists()) {
            photoFile.delete()
        } else {
            photoFile.parentFile!!.mkdirs()
        }
        captureFilePath = FileProvider.getUriForFile(this@RegistrationStepTwoActivity, BuildConfig.APPLICATION_ID + ".provider", photoFile!!)

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

      private fun galleryClick()
    {
        val checkSelfPermission = ContextCompat.checkSelfPermission(this@RegistrationStepTwoActivity, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if (checkSelfPermission != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this@RegistrationStepTwoActivity, arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE), requestGallery)
        }
        else{
            openAlbum()
        }
    }

    private fun openAlbum(){
        val galleryIntent = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(galleryIntent, requestGallery)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            requestGallery ->
                if (grantResults.isNotEmpty() && grantResults[0] ==PackageManager.PERMISSION_GRANTED){
                    openAlbum()
                }
                else {
                    Toast.makeText(this@RegistrationStepTwoActivity, "You denied the permission", Toast.LENGTH_SHORT).show()
                }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            requestPhoto ->

                if (resultCode == Activity.RESULT_OK)
                {
                    setImage()
                }
            requestGallery->
                if (resultCode == Activity.RESULT_OK) {
                    handleImage(data)

                }
        }
    }


    private fun setImage() {

       /* try {
            val imgSize = File(captureFilePath.toString())
            val length  = imgSize.length() / 1024
            if(length>3000) // Max Size Under 3MB
            {
                captureImageStatus = false
                Toast.makeText(this@RegistrationStepTwoActivity, "Image size maximum 3Mb", Toast.LENGTH_SHORT).show()
            }
            else {
                val photoPath: Uri = captureFilePath
                imgProfilePhoto.post {
                    val pictureBitmap = BitmapResize.shrinkBitmap(
                            this@RegistrationStepTwoActivity,
                            photoPath,
                            imgProfilePhoto.width,
                            imgProfilePhoto.height
                    )
                    imgProfilePhoto.setImageBitmap(pictureBitmap)
                    imgProfilePhoto.scaleType = ImageView.ScaleType.CENTER_CROP

                }

                captureImageStatus = true

            }
        }
        catch (exp:Exception)
        {

        }*/

    }

    private fun handleImage(data: Intent?) {
        /*var imagePath=""
        try
        {
            val uri = data!!.data
            when {
                DocumentsContract.isDocumentUri(this, uri) -> try {
                    val docId = DocumentsContract.getDocumentId(uri)
                    if ("com.android.providers.media.documents" == uri!!.authority){
                        val id = docId.split(":")[1]
                        val section = MediaStore.Images.Media._ID + "=" + id
                        imagePath = imagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, section)
                    } else if ("com.android.providers.downloads.documents" == uri.authority){
                        val contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), java.lang.Long.valueOf(docId))
                        imagePath = imagePath(contentUri, null)
                    }

                }
                catch (exp: Exception)
                {

                }
                "content".equals(uri!!.scheme, ignoreCase = true) -> imagePath = imagePath(uri, null)
                "file".equals(uri.scheme, ignoreCase = true) -> imagePath = uri.path!!
            }
             displayImage(imagePath)
        }
        catch (exp: Exception)
        {

        }*/

        if (data != null)
        {

            try {
                val contentURI = data.data
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, contentURI)
               // imgProfilePhoto.setImageBitmap(bitmap)
                saveImage(bitmap)
            }
            catch (exp:Exception){
                Toast.makeText(this@RegistrationStepTwoActivity,exp.message,Toast.LENGTH_SHORT).show()
            }

        }
        else
        {
            Toast.makeText(this@RegistrationStepTwoActivity,"Failed",Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveImage(myBitmap: Bitmap):String {

        val bytes = ByteArrayOutputStream()
        myBitmap.compress(Bitmap.CompressFormat.PNG, 90, bytes)
        //val wallpaperDirectory = File ((Environment.getExternalStorageDirectory()).toString())
        val imgDirectory = File ((getExternalFilesDir(Environment.DIRECTORY_PICTURES)).toString())
        if (!imgDirectory.exists())
        {
            imgDirectory.mkdirs()
        }
        try
        {
            captureImageStatus=true
            val file = File(imgDirectory, ((Calendar.getInstance().timeInMillis).toString() + ".png"))
            file.createNewFile()
            val fo = FileOutputStream(file)
            fo.write(bytes.toByteArray())
            MediaScannerConnection.scanFile(this@RegistrationStepTwoActivity, arrayOf(file.path), arrayOf("image/png"), null)
            fo.close()
            photoFile=file
            return file.absolutePath
        }
        catch (e1: IOException){
            e1.printStackTrace()
        }
        return ""
    }

    private fun imagePath(uri: Uri?, selection: String?): String {
        var path: String? = null
        val cursor = contentResolver.query(uri!!, null, selection, null, null )
        if (cursor != null){
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA))
            }
            cursor.close()
        }
        return path!!
    }

    private fun displayImage(imagePath: String?){
        if (imagePath != null) {
            val imgSize = File(imagePath)
            val length  = imgSize.length() / 1024
            if(length>3000) // Max Size Under 3MB
            {
                Toast.makeText(this@RegistrationStepTwoActivity, "Image size maximum 3MB", Toast.LENGTH_SHORT).show()
            }
            else
            {
                captureImageStatus=true
                val bitmap = BitmapFactory.decodeFile(imagePath)
               // imgProfilePhoto?.setImageBitmap(bitmap)
                photoFile=imgSize
                CommonMethod.fileCompress(photoFile)
            }

        }
        else {
            Toast.makeText(this@RegistrationStepTwoActivity, "Failed to get image", Toast.LENGTH_SHORT).show()
        }
    }



    override fun onBackPressed() {


    }


}
