package com.app.l_pesa.registration.view

import android.app.Activity
import android.app.Dialog
import android.content.ClipData
import android.content.ContentUris
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.DocumentsContract
import android.provider.MediaStore
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.Window
import android.widget.ImageView
import android.widget.Toast
import com.app.l_pesa.BuildConfig
import com.app.l_pesa.R
import com.app.l_pesa.common.BitmapResize
import com.app.l_pesa.common.CommonMethod
import com.app.l_pesa.login.view.LoginActivity
import com.app.l_pesa.profile.inter.ICallBackId
import com.app.l_pesa.profile.inter.ICallBackUpload
import com.app.l_pesa.profile.presenter.PresenterAWSProfile
import com.app.l_pesa.registration.adapter.PersonalIdListAdapter
import com.app.l_pesa.registration.inter.ICallBackRegisterThree
import com.app.l_pesa.registration.presenter.PresenterRegistrationThree
import com.google.gson.JsonObject
import com.kaopiz.kprogresshud.KProgressHUD
import kotlinx.android.synthetic.main.content_registration_step_three.*
import java.io.File


class RegistrationStepThreeActivity : AppCompatActivity(), ICallBackId, ICallBackUpload, ICallBackRegisterThree {

    private lateinit     var progressDialog: KProgressHUD
    private val idList   = arrayListOf("1","2","3","4")
    private val nameList = arrayListOf("Passport", "Driving License", "National ID","Voter ID")
    private var typeId="0"
    private val requestPhoto        = 16
    private val requestGallery      = 17
    private var captureImageStatus : Boolean    = false
    private var photoFile          : File?      = null
    private var captureFilePath    : Uri?       = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration_step_three)

        initLoader()
        initUI()

    }

    private fun initUI()
    {
        etIdType.setOnClickListener {

            showIdType()
        }

        imgPersonalType.setOnClickListener {

            val items = arrayOf<CharSequence>("Camera", "Gallery", "Cancel")
            val dialogView = AlertDialog.Builder(this@RegistrationStepThreeActivity,R.style.MyAlertDialogTheme)
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

        buttonSubmit.setOnClickListener {

            onSubmitData()
        }
    }

    private fun initLoader()
    {
        progressDialog=KProgressHUD.create(this@RegistrationStepThreeActivity)
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


    private fun onSubmitData()
    {
      CommonMethod.hideKeyboardView(this@RegistrationStepThreeActivity)
      if(typeId=="0")
      {
          showIdType()
          CommonMethod.customSnackBarError(rootLayout,this@RegistrationStepThreeActivity,resources.getString(R.string.required_id_type))
      }
      else if(!captureImageStatus)
      {
          CommonMethod.customSnackBarError(rootLayout,this@RegistrationStepThreeActivity,resources.getString(R.string.required_id_image))
      }
      else if(etNo.text.toString().length<3)
      {
          CommonMethod.customSnackBarError(rootLayout,this@RegistrationStepThreeActivity,resources.getString(R.string.required_id_number))
      }
      else
      {
          if(CommonMethod.isNetworkAvailable(this@RegistrationStepThreeActivity))
          {
              progressDialog.show()
              buttonSubmit.isClickable=false
              val presenterAWSProfile= PresenterAWSProfile()
              presenterAWSProfile.uploadPersonalID(this@RegistrationStepThreeActivity,this,photoFile)
          }
          else
          {
              CommonMethod.customSnackBarError(rootLayout,this@RegistrationStepThreeActivity,resources.getString(R.string.no_internet))
          }
      }
    }

    override fun onSuccessUploadAWS(url: String) {

        uploadData(url)
    }

    override fun onFailureUploadAWS(string: String) {

        dismiss()
        buttonSubmit.isClickable=true

    }

    private fun uploadData(imageURL: String)
    {

        val jsonObject = JsonObject()
        jsonObject.addProperty("id_image",imageURL)
        jsonObject.addProperty("type_id",typeId)
        jsonObject.addProperty("id_number",etNo.text.toString())

        val presenterRegistrationThree= PresenterRegistrationThree()
        presenterRegistrationThree.doRegistrationStepThree(this@RegistrationStepThreeActivity,jsonObject,this)


    }

    override fun onSuccessRegistrationThree() {
        Toast.makeText(this@RegistrationStepThreeActivity,resources.getString(R.string.sent_pin_via_sms),Toast.LENGTH_LONG).show()
        startActivity(Intent(this@RegistrationStepThreeActivity, LoginActivity::class.java))
        overridePendingTransition(R.anim.right_in, R.anim.left_out)

    }

    override fun onErrorRegistrationThree(jsonMessage: String) {

        dismiss()
        buttonSubmit.isClickable=true
        CommonMethod.customSnackBarError(rootLayout,this@RegistrationStepThreeActivity,jsonMessage)
    }

    private fun cameraClick()
    {
        cacheDir.deleteRecursively()
        val captureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val imagePath = File(filesDir, "images")
        photoFile = File(imagePath, "user.jpg")
        if (photoFile!!.exists()) {
            photoFile!!.delete()
        } else {
            photoFile!!.parentFile.mkdirs()
        }
        captureFilePath = FileProvider.getUriForFile(this@RegistrationStepThreeActivity, BuildConfig.APPLICATION_ID + ".provider", photoFile!!)

        captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, captureFilePath)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            captureIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        } else {
            val clip = ClipData.newUri(this@RegistrationStepThreeActivity.contentResolver, "id photo", captureFilePath)
            captureIntent.clipData = clip
            captureIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        }

        startActivityForResult(captureIntent, requestPhoto)
    }

    private fun galleryClick()
    {
        val checkSelfPermission = ContextCompat.checkSelfPermission(this@RegistrationStepThreeActivity, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if (checkSelfPermission != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this@RegistrationStepThreeActivity, arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
        }
        else{
            openAlbum()
        }
    }

    private fun openAlbum(){
        val intent = Intent("android.intent.action.GET_CONTENT")
        intent.type = "image/*"
        startActivityForResult(intent, requestGallery)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            1 ->
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    openAlbum()
                }
                else {
                    Toast.makeText(this@RegistrationStepThreeActivity, "You denied the permission", Toast.LENGTH_SHORT).show()
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
            requestGallery ->
                if (resultCode == Activity.RESULT_OK) {
                    handleImage(data)

                }
        }
    }

    private fun setImage() {

        try {
            val imgSize = File(captureFilePath.toString())
            val length  = imgSize.length() / 1024
            if(length>3000) // Max Size Under 3MB
            {
                captureImageStatus = false
                Toast.makeText(this@RegistrationStepThreeActivity, "Image size maximum 3Mb", Toast.LENGTH_SHORT).show()
            }
            else {
                val photoPath: Uri = captureFilePath ?: return
                imgPersonalType.post {
                    val pictureBitmap = BitmapResize.shrinkBitmap(
                            this@RegistrationStepThreeActivity,
                            photoPath,
                            imgPersonalType.width,
                            imgPersonalType.height
                    )
                    imgPersonalType.setImageBitmap(pictureBitmap)
                    imgPersonalType.scaleType = ImageView.ScaleType.CENTER_CROP
                    CommonMethod.fileCompress(photoFile!!)
                }

                captureImageStatus = true

            }
        }
        catch (exp:Exception)
        {

        }

    }

    private fun handleImage(data: Intent?) {
        var imagePath=""
        try
        {
            val uri = data!!.data
            when {
                DocumentsContract.isDocumentUri(this@RegistrationStepThreeActivity, uri) -> try {
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

        }
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
                captureImageStatus=false
                Toast.makeText(this@RegistrationStepThreeActivity, "Image size maximum 3Mb", Toast.LENGTH_SHORT).show()
            }
            else
            {
                captureImageStatus=true
                val bitmap = BitmapFactory.decodeFile(imagePath)
                imgPersonalType.setImageBitmap(bitmap)
                photoFile=imgSize
                CommonMethod.fileCompress(photoFile!!)
            }

        }
        else {
            captureImageStatus=false
            Toast.makeText(this@RegistrationStepThreeActivity, "Failed to get image", Toast.LENGTH_SHORT).show()
        }
    }


    private fun showIdType()
    {
        val dialog= Dialog(this@RegistrationStepThreeActivity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.layout_list_single)
        val recyclerView                = dialog.findViewById(R.id.recyclerView) as RecyclerView?
        val titleAdapter                = PersonalIdListAdapter(this@RegistrationStepThreeActivity, idList,nameList,dialog,this)
        recyclerView?.layoutManager     = LinearLayoutManager(this@RegistrationStepThreeActivity, RecyclerView.VERTICAL, false)
        recyclerView?.adapter           = titleAdapter
        dialog.show()
    }


    override fun onClickIdType(position: Int, type: String) {

        etIdType.setText(nameList[position])
        typeId=idList[position]
    }

    override fun onBackPressed() {

    }


}
