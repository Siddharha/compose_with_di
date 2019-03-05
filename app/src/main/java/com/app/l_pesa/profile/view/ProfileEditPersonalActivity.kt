package com.app.l_pesa.profile.view

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.graphics.Typeface
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.MenuItem
import android.view.Window
import android.widget.TextView
import com.app.l_pesa.R
import com.app.l_pesa.common.CommonMethod
import com.app.l_pesa.common.SharedPref
import com.app.l_pesa.profile.adapter.MaritalListAdapter
import com.app.l_pesa.profile.adapter.TitleListAdapter
import com.app.l_pesa.profile.inter.ICallBackMarital
import com.app.l_pesa.profile.inter.ICallBackTitle
import com.app.l_pesa.profile.model.ResUserInfo
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.gson.Gson

import kotlinx.android.synthetic.main.activity_profile_edit_personal.*
import kotlinx.android.synthetic.main.content_profile_edit_personal.*
import android.app.DatePickerDialog
import android.content.ContentUris
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.content.FileProvider
import android.support.v7.app.AlertDialog
import android.widget.Toast
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class ProfileEditPersonalActivity : AppCompatActivity(),ICallBackTitle, ICallBackMarital {

    private val PHOTO               = 1
    private val GALLEY              = 2
    private var captureFile: File?  = null
    private var imageFilePath       = ""
    private var imageSelectStatus   : Boolean = false

   override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_edit_personal)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbarFont(this@ProfileEditPersonalActivity)

        val sharedPrefOBJ= SharedPref(this@ProfileEditPersonalActivity)
        val profileData = Gson().fromJson<ResUserInfo.Data>(sharedPrefOBJ.profileInfo, ResUserInfo.Data::class.java)
        initData(profileData)
        loadTitle(profileData)
        loadMarital(profileData)
        loadDate(profileData)
        buttonEvent()

    }

    @SuppressLint("SetTextI18n")
    private fun initData(profileData: ResUserInfo.Data)
    {

        val options = RequestOptions()
        Glide.with(this@ProfileEditPersonalActivity)
                .load(profileData.userInfo.profileImage)
                .apply(options)
                .into(imgProfile)

       // txtTitle.setText(profileData.userPersonalInfo.title)
        etNameF.setText(profileData.userPersonalInfo.firstName)
        etNameM.setText(profileData.userPersonalInfo.middleName)
        etNameL.setText(profileData.userPersonalInfo.lastName)
        etEmail.setText(profileData.userPersonalInfo.emailAddress)
        etMotherName.setText(profileData.userPersonalInfo.motherMaidenName)

        if(profileData.userPersonalInfo.sex=="M")
        {
            radioMale.isChecked=true
            radioFemale.isChecked=false
        }
        else
        {
            radioMale.isChecked=false
            radioFemale.isChecked=true
        }


        onChangeEmail()
        onChangeProfileImage()
    }

    private fun buttonEvent()
    {
        buttonCancel.setOnClickListener {

            onBackPressed()
            overridePendingTransition(R.anim.left_in, R.anim.right_out)

        }

        buttonSubmit.setOnClickListener {

           if(TextUtils.isEmpty(txtTitle.text.toString()))
           {
               CommonMethod.customSnackBarError(rootConstraint,this@ProfileEditPersonalActivity,resources.getString(R.string.required_title))
           }
           else if(TextUtils.isEmpty(etNameF.text.toString()))
           {
               CommonMethod.customSnackBarError(rootConstraint,this@ProfileEditPersonalActivity,resources.getString(R.string.required_name_f))
           }
           else if(TextUtils.isEmpty(etNameL.text.toString()))
           {
               CommonMethod.customSnackBarError(rootConstraint,this@ProfileEditPersonalActivity,resources.getString(R.string.required_name_l))
           }
           else if(TextUtils.isEmpty(etEmail.text.toString()) && !CommonMethod.isValidEmailAddress(etEmail.text.toString()))
           {
               CommonMethod.customSnackBarError(rootConstraint,this@ProfileEditPersonalActivity,resources.getString(R.string.required_email))
           }
           else if(TextUtils.isEmpty(etMotherName.text.toString()))
           {
               CommonMethod.customSnackBarError(rootConstraint,this@ProfileEditPersonalActivity,resources.getString(R.string.required_mother_maiden_name))
           }
           else
           {
               if(CommonMethod.isNetworkAvailable(this@ProfileEditPersonalActivity))
               {
                   swipeRefreshLayout.isRefreshing=true
               }
               else
               {
                   CommonMethod.customSnackBarError(rootConstraint,this@ProfileEditPersonalActivity,resources.getString(R.string.no_internet))
               }

           }

        }
    }

    private fun onChangeProfileImage()
    {

        imgProfile.setOnClickListener {

            val items = arrayOf<CharSequence>("Camera", "Gallery", "Cancel") // array list
            val dialogView = AlertDialog.Builder(this@ProfileEditPersonalActivity)
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
    }

    private fun cameraClick()
    {

        try {
            val imageFile = createImageFile()
            val callCameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

            val authorities = "com.app.l_pesa.fileprovider"
            val imageUri = FileProvider.getUriForFile(this@ProfileEditPersonalActivity, authorities, imageFile)
            callCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
            startActivityForResult(callCameraIntent, PHOTO)

        } catch (e: IOException) {
            Toast.makeText(this@ProfileEditPersonalActivity,"Could not create file!", Toast.LENGTH_SHORT).show()
        }
    }


    @SuppressLint("SimpleDateFormat")
    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName: String = "l_pesa" + timeStamp + "_"
        val storageDir: File = this@ProfileEditPersonalActivity.getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        if (!storageDir.exists()) storageDir.mkdirs()
        captureFile = File.createTempFile(imageFileName, ".jpg", storageDir)
        imageFilePath = captureFile!!.absolutePath
        return captureFile!!
    }

    private fun galleryClick()
    {
        val checkSelfPermission = ContextCompat.checkSelfPermission(this@ProfileEditPersonalActivity, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if (checkSelfPermission != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this@ProfileEditPersonalActivity, arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
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
                    Toast.makeText(this@ProfileEditPersonalActivity, "You denied the permission", Toast.LENGTH_SHORT).show()
                }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            PHOTO ->

                if (resultCode == Activity.RESULT_OK) {
                    imgProfile.setImageBitmap(scaleBitmap())
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
        val imageViewWidth = 500
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
            val bitmap = BitmapFactory.decodeFile(imagePath)
            imgProfile.setImageBitmap(bitmap)
        }
        else {
            Toast.makeText(this@ProfileEditPersonalActivity, "Failed to get image", Toast.LENGTH_SHORT).show()
        }
    }




    override fun onChangeTitle(s: String)
    {
        txtTitle.setText(s)
    }

    private fun loadTitle(profileData: ResUserInfo.Data)
    {
        if(!TextUtils.isEmpty(profileData.userPersonalInfo.title))
        {
            txtTitle.setText(profileData.userPersonalInfo.title)
        }

        txtTitle.isFocusable=false
        txtTitle.setOnClickListener {

            showTitle()

        }
    }

    private fun loadMarital(profileData: ResUserInfo.Data)
    {

        if(!TextUtils.isEmpty(profileData.userPersonalInfo.meritalStatus))
        {
            txtMarital.setText(profileData.userPersonalInfo.meritalStatus)
        }

        txtMarital.isFocusable=false
        txtMarital.setOnClickListener {

            showMarital()

        }
    }

    private fun showMarital()
    {
        val listTitle = arrayListOf("Married", "Unmarried")

        val dialog= Dialog(this@ProfileEditPersonalActivity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_country)
        val recyclerView                = dialog.findViewById(R.id.recycler_country) as RecyclerView?
        val titleAdapter                = MaritalListAdapter(this@ProfileEditPersonalActivity, listTitle,dialog,this)
        recyclerView?.layoutManager     = LinearLayoutManager(this@ProfileEditPersonalActivity, LinearLayoutManager.VERTICAL, false)
        recyclerView?.adapter           = titleAdapter
        dialog.show()
    }

    @SuppressLint("SetTextI18n", "SimpleDateFormat")
    private fun loadDate(profileData: ResUserInfo.Data)
    {

        if(!TextUtils.isEmpty(profileData.userPersonalInfo.dob))
        {
            val inputFormat =  SimpleDateFormat("yyyy-MM-dd")
            val date = inputFormat.parse(profileData.userPersonalInfo.dob)

            val outputFormat = SimpleDateFormat("dd-MM-yyyy")
            txtDOB.setText(outputFormat.format(date))
        }

        txtDOB.isFocusable=false
        txtDOB.setOnClickListener {

            val c       = Calendar.getInstance()
            val year    = c.get(Calendar.YEAR)
            val month   = c.get(Calendar.MONTH)+1
            val day     = c.get(Calendar.DAY_OF_MONTH)

            val dpd = DatePickerDialog(this@ProfileEditPersonalActivity, DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->

                if(dayOfMonth.toString().length==1)
                {
                    if(monthOfYear.toString().length==1)
                    {
                        txtDOB.setText("0$dayOfMonth-0$month-$year")
                    }
                    else
                    {
                        txtDOB.setText("0$dayOfMonth-$month-$year")
                    }

                }
                else
                {
                    if(monthOfYear.toString().length==1)
                    {
                        txtDOB.setText("$dayOfMonth-0$month-$year")
                    }
                    else
                    {
                        txtDOB.setText("$dayOfMonth-$monthOfYear-$year")
                    }

                }

            }, year, month, day)

            dpd.show()
            dpd.datePicker.maxDate = System.currentTimeMillis()

        }
    }


    private fun showTitle()
    {
        val listTitle = arrayListOf("Mr", "Mrs", "Miss","Dr","Prof")
        val listIcon = arrayListOf(R.drawable.ic_mr_icon,R.drawable.ic_mrs_icon,R.drawable.ic_mrs_icon,R.drawable.ic_dr_icon,R.drawable.ic_professor_icon)

        val dialog= Dialog(this@ProfileEditPersonalActivity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_country)
        val recyclerView                = dialog.findViewById(R.id.recycler_country) as RecyclerView?
        val titleAdapter                = TitleListAdapter(this@ProfileEditPersonalActivity, listTitle,listIcon,dialog,this)
        recyclerView?.layoutManager     = LinearLayoutManager(this@ProfileEditPersonalActivity, LinearLayoutManager.VERTICAL, false)
        recyclerView?.adapter           = titleAdapter
        dialog.show()
    }

    override fun onChangeMarital(s: String) {

        txtMarital.setText(s)
    }

    private fun onChangeEmail()
    {
        etEmail.addTextChangedListener(object : TextWatcher {

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

               if(!CommonMethod.isValidEmailAddress(s.toString()))
               {
                   etEmail.setTextColor(ContextCompat.getColor(this@ProfileEditPersonalActivity,R.color.colorRed))
               }
                else
               {
                   etEmail.setTextColor(ContextCompat.getColor(this@ProfileEditPersonalActivity,R.color.textColors))
               }

            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int,after: Int) {

            }

            override fun afterTextChanged(s: Editable) {

            }
        })
    }



    private fun toolbarFont(context: Activity) {

        for (i in 0 until toolbar.childCount) {
            val view = toolbar.getChildAt(i)
            if (view is TextView) {
                val tv = view
                val titleFont = Typeface.createFromAsset(context.assets, "fonts/Montserrat-Regular.ttf")
                if (tv.text == toolbar.title) {
                    tv.typeface = titleFont
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
