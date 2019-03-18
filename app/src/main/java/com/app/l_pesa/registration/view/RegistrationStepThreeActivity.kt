package com.app.l_pesa.registration.view

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.ContentUris
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.content.FileProvider
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Window
import android.widget.Toast
import com.app.l_pesa.R
import com.app.l_pesa.common.CommonMethod
import com.app.l_pesa.profile.inter.ICallBackId
import com.app.l_pesa.registration.adapter.PersonalIdListAdapter
import kotlinx.android.synthetic.main.content_registration_step_three.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class RegistrationStepThreeActivity : AppCompatActivity(), ICallBackId {


    private val idList   = arrayListOf("1","2","3","4")
    private val nameList = arrayListOf("Passport", "Driving License", "National ID","Voter ID")
    private val PHOTO               = 1
    private val GALLEY              = 2
    private var captureFile: File?  = null
    private var imageFilePath       = ""
    private var imageSelectStatus   : Boolean = false

    var typeId=0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration_step_three)

        initUI()

    }

    private fun initUI()
    {
        etIdType.setOnClickListener {

            showIdType()
        }

        imgPersonalType.setOnClickListener {

            val items = arrayOf<CharSequence>("Camera", "Gallery", "Cancel") // array list
            val dialogView = AlertDialog.Builder(this@RegistrationStepThreeActivity)
            dialogView.setTitle("Choose Options")

            dialogView.setItems(items) { dialog, item ->

                when {
                    items[item] == "Camera" -> // open camera
                        cameraClick() // open default camera
                    items[item] == "Gallery" -> // open gallery
                        galleryClick() // open default gallery
                    items[item] == "Cancel" -> // close dialog
                        dialog.dismiss()
                }
            }
            dialogView.show()
        }

        buttonSubmit.setOnClickListener {

            onSubmitData()
        }
    }

    private fun onSubmitData()
    {
      if(typeId==0)
      {
          showIdType()
          CommonMethod.customSnackBarError(rootLayout,this@RegistrationStepThreeActivity,resources.getString(R.string.required_id_type))
      }
      else if(!imageSelectStatus)
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
              buttonSubmit.isClickable=false
              swipeRefreshLayout.isRefreshing=true
              swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent)
          }
          else
          {
              CommonMethod.customSnackBarError(rootLayout,this@RegistrationStepThreeActivity,resources.getString(R.string.no_internet))
          }
      }
    }

    private fun cameraClick()
    {
        try {
            val imageFile = createImageFile()
            val callCameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

            val authorities = "com.app.l_pesa.provider"
            val imageUri = FileProvider.getUriForFile(this@RegistrationStepThreeActivity, authorities, imageFile)
            callCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
            startActivityForResult(callCameraIntent, PHOTO)

        } catch (e: IOException) {
            Toast.makeText(this@RegistrationStepThreeActivity,"Could not create file!", Toast.LENGTH_SHORT).show()
        }
    }

    @SuppressLint("SimpleDateFormat")
    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName: String = "a_personal" + timeStamp + "_"
        val storageDir: File = this@RegistrationStepThreeActivity.getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        if (!storageDir.exists()) storageDir.mkdirs()
        captureFile = File.createTempFile(imageFileName, ".jpg", storageDir)
        imageFilePath = captureFile!!.absolutePath
        return captureFile!!
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
        startActivityForResult(intent, GALLEY)
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
            PHOTO ->

                if (resultCode == Activity.RESULT_OK) {
                    imgPersonalType.setImageBitmap(scaleBitmap())
                    CommonMethod.fileCompress(captureFile!!)
                    imageSelectStatus=true
                } else

                {
                    if(imageSelectStatus)
                    {
                    }
                    else
                    {
                        imageSelectStatus=false
                    }

                }
            GALLEY ->
                if (resultCode == Activity.RESULT_OK) {
                    handleImage(data)

                }
        }
    }

    private fun scaleBitmap(): Bitmap
    {
        val imageViewWidth  = 500
        val imageViewHeight = 500

        val bmOptions = BitmapFactory.Options()
        bmOptions.inJustDecodeBounds = true
        BitmapFactory.decodeFile(imageFilePath, bmOptions)
        val bitmapWidth = bmOptions.outWidth
        val bitmapHeight = bmOptions.outHeight
        val scaleFactor = Math.min(bitmapWidth / imageViewWidth, bitmapHeight / imageViewHeight)

        bmOptions.inJustDecodeBounds = false
        bmOptions.inSampleSize = scaleFactor
        return BitmapFactory.decodeFile(imageFilePath, bmOptions)

    }

    private fun handleImage(data: Intent?) {
        var imagePath=""
        val uri = data!!.data
        if (DocumentsContract.isDocumentUri(this, uri)){
            val docId = DocumentsContract.getDocumentId(uri)
            if ("com.android.providers.media.documents" == uri!!.authority){
                val id = docId.split(":")[1]
                val section = MediaStore.Images.Media._ID + "=" + id
                imagePath = imagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, section)
            }
            else if ("com.android.providers.downloads.documents" == uri.authority){
                val contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), java.lang.Long.valueOf(docId))
                imagePath = imagePath(contentUri, null)
            }
        }
        else if ("content".equals(uri!!.scheme, ignoreCase = true)){
            imagePath = imagePath(uri, null)
        }
        else if ("file".equals(uri.scheme, ignoreCase = true)){
            imagePath = uri.path!!
        }
        displayImage(imagePath)
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
                imageSelectStatus=false
                Toast.makeText(this@RegistrationStepThreeActivity, "Image size maximum 3Mb", Toast.LENGTH_SHORT).show()
            }
            else
            {
                imageSelectStatus=true
                val bitmap = BitmapFactory.decodeFile(imagePath)
                imgPersonalType.setImageBitmap(bitmap)
                captureFile=imgSize
                CommonMethod.fileCompress(captureFile!!)
            }

        }
        else {
            imageSelectStatus=false
            Toast.makeText(this@RegistrationStepThreeActivity, "Failed to get image", Toast.LENGTH_SHORT).show()
        }
    }


    private fun showIdType()
    {
        val dialog= Dialog(this@RegistrationStepThreeActivity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_country)
        val recyclerView                = dialog.findViewById(R.id.recycler_country) as RecyclerView?
        val titleAdapter                = PersonalIdListAdapter(this@RegistrationStepThreeActivity, idList,nameList,dialog,this)
        recyclerView?.layoutManager     = LinearLayoutManager(this@RegistrationStepThreeActivity, LinearLayoutManager.VERTICAL, false)
        recyclerView?.adapter           = titleAdapter
        dialog.show()
    }


    override fun onClickIdType(position: Int, type: String) {

        etIdType.setText(nameList[position])
        typeId=idList[position].toInt()
    }


}
