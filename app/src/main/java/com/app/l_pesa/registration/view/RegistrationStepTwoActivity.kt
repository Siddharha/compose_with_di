package com.app.l_pesa.registration.view

import android.app.Activity
import android.content.ContentUris
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.content.FileProvider
import android.support.v7.app.AlertDialog
import android.widget.Toast
import com.app.l_pesa.R
import kotlinx.android.synthetic.main.activity_registration_step_two.*
import java.io.File

class RegistrationStepTwoActivity : AppCompatActivity() {

    private val FINAL_TAKE_PHOTO    = 1
    private val FINAL_CHOOSE_PHOTO  = 2
    private var imageUri: Uri?      = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration_step_two)


        imgEditPhoto.setOnClickListener {

            selectImage()
        }
    }

    private fun selectImage() {

        val items = arrayOf<CharSequence>("Camera", "Gallery", "Cancel") // array list
        val dialogView = AlertDialog.Builder(this@RegistrationStepTwoActivity)
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
        dialogView.show() // show dialog
    }

    private fun cameraClick()
    {
        val outputImage = File(externalCacheDir, "output_image.jpg")
        if(outputImage.exists()) {
            outputImage.delete()
        }
        outputImage.createNewFile()
        imageUri = if(Build.VERSION.SDK_INT >= 24){
            FileProvider.getUriForFile(this, "com.app.l_pesa.fileprovider", outputImage)
        } else {
            Uri.fromFile(outputImage)
        }

        val intent = Intent("android.media.action.IMAGE_CAPTURE")
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri!!)
        startActivityForResult(intent, FINAL_TAKE_PHOTO)
    }

    private fun galleryClick()
    {
        val checkSelfPermission = ContextCompat.checkSelfPermission(this@RegistrationStepTwoActivity, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if (checkSelfPermission != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this@RegistrationStepTwoActivity, arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
        }
        else{
            openAlbum()
        }
    }

    private fun openAlbum(){
        val intent = Intent("android.intent.action.GET_CONTENT")
        intent.type = "image/*"
        startActivityForResult(intent, FINAL_CHOOSE_PHOTO)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            1 ->
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
            FINAL_TAKE_PHOTO ->
                if (resultCode == Activity.RESULT_OK) {
                    val bitmap = BitmapFactory.decodeStream(contentResolver.openInputStream(imageUri))
                    imgProfilePhoto!!.setImageBitmap(bitmap)
                }
            FINAL_CHOOSE_PHOTO ->
                if (resultCode == Activity.RESULT_OK) {
                    handleImage(data)

                }
        }
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
            val bitmap = BitmapFactory.decodeFile(imagePath)
            imgProfilePhoto?.setImageBitmap(bitmap)
        }
        else {
            Toast.makeText(this@RegistrationStepTwoActivity, "Failed to get image", Toast.LENGTH_SHORT).show()
        }
    }



    override fun onBackPressed() {

        val intent = Intent(this@RegistrationStepTwoActivity, RegistrationStepOneActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        overridePendingTransition(R.anim.left_in, R.anim.right_out)
    }


}
