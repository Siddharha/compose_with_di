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
import android.media.ExifInterface
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.content.FileProvider
import android.support.v7.app.AlertDialog
import android.widget.Toast
import com.app.l_pesa.login.inter.ICallBackLogin
import com.app.l_pesa.login.model.LoginData
import com.app.l_pesa.login.presenter.PresenterLogin
import com.app.l_pesa.profile.inter.ICallBackPersonalInfo
import com.app.l_pesa.profile.inter.ICallBackUpload
import com.app.l_pesa.profile.model.ResUserInfo
import com.app.l_pesa.profile.presenter.PresenterAWSProfile
import com.app.l_pesa.profile.presenter.PresenterPersonalInfo
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*


class ProfileEditPersonalActivity : AppCompatActivity(),ICallBackTitle, ICallBackMarital, ICallBackPersonalInfo, ICallBackLogin , ICallBackUpload {

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
        swipeRefresh()
        initData(profileData)
        loadTitle(profileData)
        loadMarital(profileData)
        loadDate(profileData)
        buttonEvent(profileData)

    }



    private fun swipeRefresh()
    {
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent)
        swipeRefreshLayout.setOnRefreshListener {
            swipeRefreshLayout.isRefreshing=false
        }
    }

    @SuppressLint("SetTextI18n")
    private fun initData(profileData: ResUserInfo.Data)
    {

        val options = RequestOptions()
        options.error(R.drawable.ic_profile)
        options.placeholder(R.drawable.ic_profile)
        Glide.with(this@ProfileEditPersonalActivity)
                .load(resources.getString(R.string.profile_image_url)+profileData.userInfo!!.profileImage)
                .apply(options)
                .into(imgProfile)

        if(!TextUtils.isEmpty(profileData.userPersonalInfo!!.firstName))
        {
            etNameF.setText(profileData.userPersonalInfo!!.firstName)
        }
        if(!TextUtils.isEmpty(profileData.userPersonalInfo!!.middleName))
        {
            etNameM.setText(profileData.userPersonalInfo!!.middleName)
        }
        if(!TextUtils.isEmpty(profileData.userPersonalInfo!!.lastName))
        {
            etNameL.setText(profileData.userPersonalInfo!!.lastName)
        }
        if(!TextUtils.isEmpty(profileData.userPersonalInfo!!.emailAddress))
        {
            etEmail.setText(profileData.userPersonalInfo!!.emailAddress)
        }
        if(!TextUtils.isEmpty(profileData.userPersonalInfo!!.motherMaidenName))
        {
            etMotherName.setText(profileData.userPersonalInfo!!.motherMaidenName)
        }

        if(profileData.userPersonalInfo!!.sex=="M")
        {
            radioMale.isChecked=true
            radioFemale.isChecked=false
        }
        else if(profileData.userPersonalInfo!!.sex=="F")
        {
            radioMale.isChecked=false
            radioFemale.isChecked=true
        }


        onChangeEmail(profileData)
        onChangeProfileImage()
    }

    @SuppressLint("SimpleDateFormat")
    private fun buttonEvent(profileData: ResUserInfo.Data)
    {
        buttonCancel.setOnClickListener {

            onBackPressed()
            overridePendingTransition(R.anim.left_in, R.anim.right_out)

        }

        buttonSubmit.setOnClickListener {

            var gender="M"
            gender = if(radioMale.isChecked) {
                "M"
            } else {
                "F"
            }


            val hashMapOLD = HashMap<String, String>()
            hashMapOLD["title"]     = ""+profileData.userPersonalInfo!!.title
            hashMapOLD["nameF"]     = ""+profileData.userPersonalInfo!!.firstName
            hashMapOLD["nameM"]     = ""+profileData.userPersonalInfo!!.middleName
            hashMapOLD["nameL"]     = ""+profileData.userPersonalInfo!!.lastName
            hashMapOLD["email"]     = ""+profileData.userPersonalInfo!!.emailAddress
            hashMapOLD["dob"]       = ""+profileData.userPersonalInfo!!.dob
            hashMapOLD["status"]    = ""+profileData.userPersonalInfo!!.meritalStatus
            hashMapOLD["motherM"]   = ""+profileData.userPersonalInfo!!.motherMaidenName
            hashMapOLD["sex"]       = ""+profileData.userPersonalInfo!!.sex
            hashMapOLD["imgChange"] = "false"

            var dateRequest=""
            dateRequest = if(!TextUtils.isEmpty(txtDOB.text.toString())) {
                val inputFormat  = SimpleDateFormat("dd-MM-yyyy")
                val date         = inputFormat.parse(txtDOB.text.toString())

                val outputFormat = SimpleDateFormat("yyyy-MM-dd")
                outputFormat.format(date)
            } else {
                ""
            }


            val hashMapNew = HashMap<String, String>()
            hashMapNew["title"]     = txtTitle.text.toString()
            hashMapNew["nameF"]     = etNameF.text.toString()
            hashMapNew["nameM"]     = etNameM.text.toString()
            hashMapNew["nameL"]     = etNameL.text.toString()
            hashMapNew["email"]     = etEmail.text.toString()
            hashMapNew["dob"]       = dateRequest
            hashMapNew["status"]    = txtMarital.text.toString()
            hashMapNew["motherM"]   = etMotherName.text.toString()
            hashMapNew["sex"]       = gender
            hashMapNew["imgChange"] = imageSelectStatus.toString()


            if(hashMapOLD == hashMapNew)
            {
                CommonMethod.customSnackBarError(rootConstraint,this@ProfileEditPersonalActivity,resources.getString(R.string.change_one_info))
            }
            else
            {
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
                else if(TextUtils.isEmpty(etEmail.text.toString()))
                {
                    CommonMethod.customSnackBarError(rootConstraint,this@ProfileEditPersonalActivity,resources.getString(R.string.required_email))
                }
                else if(TextUtils.isEmpty(txtDOB.text.toString()))
                {
                    showDatePicker()
                    CommonMethod.customSnackBarError(rootConstraint,this@ProfileEditPersonalActivity,resources.getString(R.string.required_date_of_birth))
                }
                else if(TextUtils.isEmpty(etMotherName.text.toString()))
                {
                    CommonMethod.customSnackBarError(rootConstraint,this@ProfileEditPersonalActivity,resources.getString(R.string.required_mother_maiden_name))
                }
                else
                {
                    if(CommonMethod.isNetworkAvailable(this@ProfileEditPersonalActivity))
                    {
                        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent)
                        swipeRefreshLayout.isRefreshing=true
                        buttonSubmit.isClickable=false
                        if(imageSelectStatus)
                        {
                            val presenterAWSProfile= PresenterAWSProfile()
                            presenterAWSProfile.uploadProfileImage(this@ProfileEditPersonalActivity,this,captureFile)
                        }
                        else
                        {
                            uploadData(profileData.userPersonalInfo!!.profileImage)
                        }
                    }
                    else
                    {
                        CommonMethod.customSnackBarError(rootConstraint,this@ProfileEditPersonalActivity,resources.getString(R.string.no_internet))
                    }

                }

            }

        }
    }

    override fun onSuccessUploadAWS(url: String) {

        uploadData(url)
    }

    override fun onFailureUploadAWS(string: String) {
        swipeRefreshLayout.isRefreshing=false
        CommonMethod.customSnackBarError(rootConstraint,this@ProfileEditPersonalActivity,string)
    }

    private fun uploadData(imageURL: String)
    {
        var gender="M"
        gender = if(radioMale.isChecked) {
            "M"
        } else {
            "F"
        }

        val inputFormat  = SimpleDateFormat("dd-MM-yyyy")
        val date         = inputFormat.parse(txtDOB.text.toString())

        val outputFormat = SimpleDateFormat("yyyy-MM-dd")
        val dateRequest  = outputFormat.format(date)

        val jsonObject = JsonObject()
        jsonObject.addProperty("title",txtTitle.text.toString())
        jsonObject.addProperty("first_name",etNameF.text.toString())
        jsonObject.addProperty("middle_name",etNameM.text.toString())
        jsonObject.addProperty("last_name",etNameL.text.toString())
        jsonObject.addProperty("email_address",etEmail.text.toString())
        jsonObject.addProperty("dob",dateRequest)
        jsonObject.addProperty("sex",gender)
        jsonObject.addProperty("merital_status",txtMarital.text.toString())
        jsonObject.addProperty("mother_maiden_name",etMotherName.text.toString())
        jsonObject.addProperty("profile_image",""+imageURL)

        println("JSON"+jsonObject)


        val presenterPersonalInfo= PresenterPersonalInfo()
        presenterPersonalInfo.doChangePersonalInfo(this@ProfileEditPersonalActivity,jsonObject,this)

    }

    override fun onSuccessPersonalInfo() {

        val sharedPrefOBJ = SharedPref(this@ProfileEditPersonalActivity)
        val jsonObject = JsonParser().parse(sharedPrefOBJ.loginRequest).asJsonObject
        val presenterLoginObj = PresenterLogin()
        presenterLoginObj.doLogin(this@ProfileEditPersonalActivity, jsonObject, this)

    }

    override fun onFailurePersonalInfo(message: String) {

        buttonSubmit.isClickable=true
        swipeRefreshLayout.isRefreshing=false
        CommonMethod.customSnackBarError(rootConstraint,this@ProfileEditPersonalActivity,message)
    }

    override fun onSuccessLogin(data: LoginData) {

        val sharedPrefOBJ=SharedPref(this@ProfileEditPersonalActivity)
        sharedPrefOBJ.profileUpdate=resources.getString(R.string.status_true)
        sharedPrefOBJ.accessToken   = data.access_token
        val gson = Gson()
        val json = gson.toJson(data)
        sharedPrefOBJ.userInfo      = json
        swipeRefreshLayout.isRefreshing=false
        onBackPressed()
        overridePendingTransition(R.anim.left_in, R.anim.right_out)

    }

    override fun onIncompleteLogin(message: String) {

        swipeRefreshLayout.isRefreshing=false
        buttonSubmit.isClickable=true

    }

    override fun onErrorLogin(jsonMessage: String) {

     swipeRefreshLayout.isRefreshing=false
     Toast.makeText(this@ProfileEditPersonalActivity,jsonMessage,Toast.LENGTH_SHORT).show()

    }

    override fun onFailureLogin(jsonMessage: String) {

        buttonSubmit.isClickable=true
        swipeRefreshLayout.isRefreshing=false
        CommonMethod.customSnackBarError(rootConstraint,this@ProfileEditPersonalActivity,jsonMessage)
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

            val authorities = "com.app.l_pesa.provider"
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
        val imageFileName: String = "profile" + timeStamp + "_"
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

                if (resultCode == Activity.RESULT_OK)
                {

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
                imageSelectStatus=false
                Toast.makeText(this@ProfileEditPersonalActivity, "Image size maximum 3Mb", Toast.LENGTH_SHORT).show()
            }
            else
            {
                imageSelectStatus=true
                val bitmap = BitmapFactory.decodeFile(imagePath)
                imgProfile.setImageBitmap(bitmap)
                captureFile=imgSize
                CommonMethod.fileCompress(captureFile!!)
            }

        }
        else {
            imageSelectStatus=false
            Toast.makeText(this@ProfileEditPersonalActivity, "Failed to get image", Toast.LENGTH_SHORT).show()
        }
    }

    /*fun saveImage(myBitmap: Bitmap):String {

        val bytes = ByteArrayOutputStream()
        myBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes)
        val wallpaperDirectory = File(
                (Environment.getExternalStorageDirectory()).toString() +"/lpesa_app")
        // have the object build the directory structure, if needed.

        if (!wallpaperDirectory.exists())
        {

            wallpaperDirectory.mkdirs()
        }

        try
        {
            val f = File(wallpaperDirectory, ((Calendar.getInstance()
                    .timeInMillis).toString() + ".jpg"))
            f.createNewFile()
            captureFile=f
            val fo = FileOutputStream(f)
            fo.write(bytes.toByteArray())
            MediaScannerConnection.scanFile(this@ProfileEditPersonalActivity,
                    arrayOf(f.path),
                    arrayOf("image/jpeg"), null)
            fo.close()


            return f.getAbsolutePath()
        }
        catch (e1: IOException) {
            e1.printStackTrace()
        }

        return ""
    }*/


    override fun onChangeTitle(s: String)
    {
        txtTitle.setText(s)
    }

    private fun loadTitle(profileData: ResUserInfo.Data)
    {
        if(!TextUtils.isEmpty(profileData.userPersonalInfo!!.title))
        {
            txtTitle.setText(profileData.userPersonalInfo!!.title)
        }

        txtTitle.isFocusable=false
        txtTitle.setOnClickListener {

            showTitle()

        }
    }

    private fun loadMarital(profileData: ResUserInfo.Data)
    {

        if(!TextUtils.isEmpty(profileData.userPersonalInfo!!.meritalStatus))
        {
            txtMarital.setText(profileData.userPersonalInfo!!.meritalStatus)
        }

        txtMarital.isFocusable=false
        txtMarital.setOnClickListener {

            showMarital()

        }
    }

    private fun showMarital()
    {
        val listTitle = arrayListOf("Married", "Divorced","Single")

        val dialog= Dialog(this@ProfileEditPersonalActivity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.layout_list_single)
        val recyclerView                = dialog.findViewById(R.id.recycler_country) as RecyclerView?
        val titleAdapter                = MaritalListAdapter(this@ProfileEditPersonalActivity, listTitle,dialog,this)
        recyclerView?.layoutManager     = LinearLayoutManager(this@ProfileEditPersonalActivity, LinearLayoutManager.VERTICAL, false)
        recyclerView?.adapter           = titleAdapter
        dialog.show()
    }

    @SuppressLint("SetTextI18n", "SimpleDateFormat")
    private fun loadDate(profileData: ResUserInfo.Data)
    {

        if(!TextUtils.isEmpty(profileData.userPersonalInfo!!.dob))
        {
            val inputFormat =  SimpleDateFormat("yyyy-MM-dd")
            val date = inputFormat.parse(profileData.userPersonalInfo!!.dob)

            val outputFormat = SimpleDateFormat("dd-MM-yyyy")
            txtDOB.setText(outputFormat.format(date))
        }

        txtDOB.isFocusable=false
        txtDOB.setOnClickListener {

            showDatePicker()

        }
    }

    @SuppressLint("SetTextI18n")
    private fun showDatePicker()
    {
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


    private fun showTitle()
    {
        val listTitle = arrayListOf("Mr", "Mrs", "Miss","Dr","Prof")
        val listIcon = arrayListOf(R.drawable.ic_mr_icon,R.drawable.ic_mrs_icon,R.drawable.ic_mrs_icon,R.drawable.ic_dr_icon,R.drawable.ic_professor_icon)

        val dialog= Dialog(this@ProfileEditPersonalActivity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.layout_list_single)
        val recyclerView                = dialog.findViewById(R.id.recycler_country) as RecyclerView?
        val titleAdapter                = TitleListAdapter(this@ProfileEditPersonalActivity, listTitle,listIcon,dialog,this)
        recyclerView?.layoutManager     = LinearLayoutManager(this@ProfileEditPersonalActivity, LinearLayoutManager.VERTICAL, false)
        recyclerView?.adapter           = titleAdapter
        dialog.show()
    }

    override fun onChangeMarital(s: String) {

        txtMarital.setText(s)
    }

    private fun onChangeEmail(profileData: ResUserInfo.Data)
    {
        if(!TextUtils.isEmpty(profileData.userPersonalInfo!!.emailAddress))
        {
            etEmail.isEnabled=false
        }
        else
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
                if(swipeRefreshLayout.isRefreshing)
                {
                    CommonMethod.customSnackBarError(rootConstraint,this@ProfileEditPersonalActivity,resources.getString(R.string.please_wait))
                }
                else
                {
                    CommonMethod.hideKeyboardView(this@ProfileEditPersonalActivity)
                    onBackPressed()
                    overridePendingTransition(R.anim.left_in, R.anim.right_out)
                }

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
