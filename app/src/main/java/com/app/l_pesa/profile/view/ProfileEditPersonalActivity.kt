package com.app.l_pesa.profile.view

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.ProgressDialog
import android.content.ClipData
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.*
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.provider.Settings
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.MenuItem
import android.view.Window
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.exifinterface.media.ExifInterface
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.l_pesa.BuildConfig
import com.app.l_pesa.R
import com.app.l_pesa.common.CommonMethod
import com.app.l_pesa.common.SharedPref
import com.app.l_pesa.main.view.MainActivity
import com.app.l_pesa.pinview.model.LoginData
import com.app.l_pesa.profile.adapter.MaritalListAdapter
import com.app.l_pesa.profile.adapter.TitleListAdapter
import com.app.l_pesa.profile.inter.ICallBackMarital
import com.app.l_pesa.profile.inter.ICallBackPersonalInfo
import com.app.l_pesa.profile.inter.ICallBackTitle
import com.app.l_pesa.profile.inter.ICallBackUpload
import com.app.l_pesa.profile.model.ResUserInfo
import com.app.l_pesa.profile.presenter.PresenterAWSProfile
import com.app.l_pesa.profile.presenter.PresenterPersonalInfo
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.gson.Gson
import com.google.gson.JsonObject
import id.zelory.compressor.Compressor
import kotlinx.android.synthetic.main.activity_profile_edit_personal.*
import kotlinx.android.synthetic.main.content_profile_edit_personal.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class ProfileEditPersonalActivity : AppCompatActivity(),ICallBackTitle, ICallBackMarital, ICallBackPersonalInfo, ICallBackUpload {

    private val requestPhoto             = 10
    private val requestGalley            = 11
    private var captureImageStatus          : Boolean    = false
    private lateinit var photoFile          : File
    private lateinit var captureFilePath    : Uri
    private lateinit var  progressDialog    : ProgressDialog

   override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_edit_personal)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbarFont(this@ProfileEditPersonalActivity)

        val sharedPrefOBJ= SharedPref(this@ProfileEditPersonalActivity)
        val profileData = Gson().fromJson<ResUserInfo.Data>(sharedPrefOBJ.profileInfo, ResUserInfo.Data::class.java)
        initLoader()
        initData(profileData)
        loadTitle(profileData)
        loadMarital(profileData)
        loadDate(profileData)
        buttonEvent(profileData)

    }


    private fun initLoader()
    {
        progressDialog = ProgressDialog(this@ProfileEditPersonalActivity,R.style.MyAlertDialogStyle)
        progressDialog.isIndeterminate = true
        progressDialog.setMessage(resources.getString(R.string.loading))
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        progressDialog.setCancelable(false)
        progressDialog.setCanceledOnTouchOutside(false)

    }

    @SuppressLint("SetTextI18n")
    private fun initData(profileData: ResUserInfo.Data)
    {

        val options = RequestOptions()
        options.placeholder(R.drawable.ic_user)
        Glide.with(this@ProfileEditPersonalActivity)
                .load(BuildConfig.PROFILE_IMAGE_URL+profileData.userInfo!!.profileImage)
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

           /* var dateRequest=""
            dateRequest = if(!TextUtils.isEmpty(txtDOB.text.toString())) {
                val inputFormat  = SimpleDateFormat("dd-MM-yyyy")
                val date         = inputFormat.parse(txtDOB.text.toString())

                val outputFormat = SimpleDateFormat("yyyy-MM-dd")
                outputFormat.format(date!!)
            } else {
                ""
            }*/


            val hashMapNew = HashMap<String, String>()
            hashMapNew["title"]     = txtTitle.text.toString()
            hashMapNew["nameF"]     = etNameF.text.toString()
            hashMapNew["nameM"]     = etNameM.text.toString()
            hashMapNew["nameL"]     = etNameL.text.toString()
            hashMapNew["email"]     = etEmail.text.toString()
            hashMapNew["dob"]       = txtDOB.text.toString()
            hashMapNew["status"]    = txtMarital.text.toString()
            hashMapNew["motherM"]   = etMotherName.text.toString()
            hashMapNew["sex"]       = gender
            hashMapNew["imgChange"] = captureImageStatus.toString()

            CommonMethod.hideKeyboardView(this@ProfileEditPersonalActivity)
            if(hashMapOLD == hashMapNew)
            {
                CommonMethod.customSnackBarError(rootConstraint,this@ProfileEditPersonalActivity,resources.getString(R.string.change_one_info))
            }
            else
            {
                if(TextUtils.isEmpty(txtTitle.text.toString().trim()))
                {
                    CommonMethod.customSnackBarError(rootConstraint,this@ProfileEditPersonalActivity,resources.getString(R.string.required_title))
                }
                else if(TextUtils.isEmpty(etNameF.text.toString().trim()))
                {
                    CommonMethod.customSnackBarError(rootConstraint,this@ProfileEditPersonalActivity,resources.getString(R.string.required_name_f))
                }
                else if(TextUtils.isEmpty(etNameL.text.toString().trim()))
                {
                    CommonMethod.customSnackBarError(rootConstraint,this@ProfileEditPersonalActivity,resources.getString(R.string.required_name_l))
                }
                else if(TextUtils.isEmpty(etEmail.text.toString().trim()))
                {
                    CommonMethod.customSnackBarError(rootConstraint,this@ProfileEditPersonalActivity,resources.getString(R.string.required_email))
                }
                else if(TextUtils.isEmpty(txtDOB.text.toString()))
                {
                    showDatePicker()
                    CommonMethod.customSnackBarError(rootConstraint,this@ProfileEditPersonalActivity,resources.getString(R.string.required_date_of_birth))
                }
                else if(TextUtils.isEmpty(etMotherName.text.toString().trim()))
                {
                    CommonMethod.customSnackBarError(rootConstraint,this@ProfileEditPersonalActivity,resources.getString(R.string.required_mother_maiden_name))
                }
                else if(!radioMale.isChecked && !radioFemale.isChecked)
                {
                    CommonMethod.customSnackBarError(rootConstraint,this@ProfileEditPersonalActivity,resources.getString(R.string.required_gender))
                }
                else
                {
                    if(CommonMethod.isNetworkAvailable(this@ProfileEditPersonalActivity))
                    {
                        progressDialog.show()
                        buttonSubmit.isClickable=false
                        if(captureImageStatus)
                        {
                            val presenterAWSProfile= PresenterAWSProfile()
                            presenterAWSProfile.uploadProfileImage(this@ProfileEditPersonalActivity,this,photoFile)
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

    private fun dismiss()
    {
        if(progressDialog.isShowing)
        {
            progressDialog.dismiss()
        }
    }

    override fun onSuccessUploadAWS(url: String) {

        val sharedPrefOBJ= SharedPref(this@ProfileEditPersonalActivity)
        val userData = Gson().fromJson<LoginData>(sharedPrefOBJ.userInfo, LoginData::class.java)
        userData.user_personal_info.profile_image=url
        val json = Gson().toJson(userData)
        sharedPrefOBJ.userInfo      = json
        uploadData(url)

    }

    override fun onFailureUploadAWS(string: String) {
        buttonSubmit.isClickable=true
        dismiss()
        CommonMethod.customSnackBarError(rootConstraint,this@ProfileEditPersonalActivity,string)
    }

    @SuppressLint("SimpleDateFormat")
    private fun uploadData(imageURL: String)
    {
        var gender="M"
        gender = if(radioMale.isChecked) {
            "M"
        } else {
            "F"
        }


        val format      = SimpleDateFormat("yyyy-MM-dd")
        val date        = format.parse(txtDOB.text.toString())

        val outputFormat = SimpleDateFormat("yyyy-MM-dd")
        val dateRequest  = outputFormat.format(date!!)

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
        jsonObject.addProperty("profile_image",imageURL)

        val presenterPersonalInfo= PresenterPersonalInfo()
        presenterPersonalInfo.doChangePersonalInfo(this@ProfileEditPersonalActivity,jsonObject,this)

    }

    override fun onSuccessPersonalInfo() {

        dismiss()
        buttonSubmit.isClickable=true
        val sharedPrefOBJ = SharedPref(this@ProfileEditPersonalActivity)
        sharedPrefOBJ.profileUpdate=resources.getString(R.string.status_true)

        onBackPressed()
        overridePendingTransition(R.anim.left_in, R.anim.right_out)

    }

    override fun onFailurePersonalInfo(message: String) {

        buttonSubmit.isClickable=true
        dismiss()
        CommonMethod.customSnackBarError(rootConstraint,this@ProfileEditPersonalActivity,message)
    }

    override fun onSessionTimeOut(message: String) {

        dismiss()
        val dialogBuilder = AlertDialog.Builder(this@ProfileEditPersonalActivity, R.style.MyAlertDialogTheme)
        dialogBuilder.setMessage(message)
                .setCancelable(false)
                .setPositiveButton("Ok") { dialog, _ ->
                    dialog.dismiss()
                    val sharedPrefOBJ= SharedPref(this@ProfileEditPersonalActivity)
                    sharedPrefOBJ.removeShared()
                    startActivity(Intent(this@ProfileEditPersonalActivity, MainActivity::class.java))
                    overridePendingTransition(R.anim.right_in, R.anim.left_out)
                    finish()
                }

        val alert = dialogBuilder.create()
        alert.setTitle(resources.getString(R.string.app_name))
        alert.show()

    }

    private fun onChangeProfileImage()
    {

        imgProfile.setOnClickListener {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                if(checkAndRequestPermissions())
                {
                    cameraClick()
                }
                else
                {
                    checkAndRequestPermissions()
                }
            }
            else{
                cameraClick()
            }

        }
    }

    private fun cameraClick()
    {
        val captureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val imagePath = File(filesDir, "images")
        photoFile = File(imagePath, "user.jpg")
        if (photoFile.exists()) {
            photoFile.delete()
        } else {
            photoFile.parentFile!!.mkdirs()
        }
        captureFilePath = FileProvider.getUriForFile(this@ProfileEditPersonalActivity, BuildConfig.APPLICATION_ID + ".provider", photoFile)

        captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, captureFilePath)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            captureIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        } else {
            val clip = ClipData.newUri(contentResolver, "user photo", captureFilePath)
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

       val photoPath: Uri = captureFilePath
       try {
           if(photoPath!=Uri.EMPTY)
           {
               progressDialog.show()
               handleRotation(photoFile.absolutePath)
               Handler().postDelayed({
                   dismiss()
                   imgProfile.setImageURI(null)
                   imgProfile.setImageURI(photoPath)

                   captureImageStatus       = true
                   photoFile   = Compressor(this@ProfileEditPersonalActivity).compressToFile(photoFile)
               }, 1000)

           }
           else
           {
               Toast.makeText(this@ProfileEditPersonalActivity,"Retake Photo", Toast.LENGTH_SHORT).show()
           }

       }
       catch (exp:Exception)
       {
           Toast.makeText(this@ProfileEditPersonalActivity,"Retake Photo", Toast.LENGTH_SHORT).show()
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
                            else ->origin       //origin.rotate(270f)
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
        val recyclerView                = dialog.findViewById(R.id.recyclerView) as RecyclerView?
        val titleAdapter                = MaritalListAdapter(this@ProfileEditPersonalActivity, listTitle,dialog,this)
        recyclerView?.layoutManager     = LinearLayoutManager(this@ProfileEditPersonalActivity, RecyclerView.VERTICAL, false)
        recyclerView?.adapter           = titleAdapter
        dialog.show()
    }

    @SuppressLint("SetTextI18n", "SimpleDateFormat")
    private fun loadDate(profileData: ResUserInfo.Data)
    {

        if(!TextUtils.isEmpty(profileData.userPersonalInfo!!.dob))
        {
            /*val inputFormat =  SimpleDateFormat("yyyy-MM-dd")
            val date = inputFormat.parse(profileData.userPersonalInfo!!.dob)

            val outputFormat = SimpleDateFormat("dd-MM-yyyy")*/
            //txtDOB.setText(outputFormat.format(date!!))
            txtDOB.setText(profileData.userPersonalInfo!!.dob)
        }

        txtDOB.isFocusable=false
        txtDOB.setOnClickListener {

            showDatePicker()

        }
    }

    @SuppressLint("SetTextI18n")
    private fun showDatePicker()
    {
        val c          = Calendar.getInstance()
        val yearOBJ    = c.get(Calendar.YEAR)
        val monthOBJ   = c.get(Calendar.MONTH)+1
        val dayOBJ     = c.get(Calendar.DAY_OF_MONTH)

        val dpd = DatePickerDialog(this@ProfileEditPersonalActivity, DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->

            val month=monthOfYear+1
            txtDOB.setText("$year-$month-$dayOfMonth")
        }, yearOBJ, monthOBJ, dayOBJ)
            /*if(dayOfMonth.toString().length==1)
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

            }*/

        dpd.show()
        dpd.datePicker.maxDate = System.currentTimeMillis()
    }

    private fun checkAndRequestPermissions(): Boolean {

        val permissionCamera        = ContextCompat.checkSelfPermission(this@ProfileEditPersonalActivity, Manifest.permission.CAMERA)
        val permissionStorage       = ContextCompat.checkSelfPermission(this@ProfileEditPersonalActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE)

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
                    if (perms[Manifest.permission.CAMERA]                           == PackageManager.PERMISSION_GRANTED
                            && perms[Manifest.permission.WRITE_EXTERNAL_STORAGE]    == PackageManager.PERMISSION_GRANTED) {

                        cameraClick()
                    } else {

                        if (ActivityCompat.shouldShowRequestPermissionRationale(this@ProfileEditPersonalActivity, Manifest.permission.CAMERA)
                                && ActivityCompat.shouldShowRequestPermissionRationale(this@ProfileEditPersonalActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
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
        AlertDialog.Builder(this@ProfileEditPersonalActivity, R.style.MyAlertDialogTheme)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", okListener)
                .create()
                .show()
    }

    private fun permissionDialog(msg: String) {
        val dialog = AlertDialog.Builder(this@ProfileEditPersonalActivity, R.style.MyAlertDialogTheme)
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



    private fun showTitle()
    {
        val listTitle = arrayListOf("Mr", "Mrs", "Miss","Dr","Prof")
        val listIcon = arrayListOf(R.drawable.ic_mr_icon,R.drawable.ic_mrs_icon,R.drawable.ic_mrs_icon,R.drawable.ic_dr_icon,R.drawable.ic_professor_icon)

        val dialog= Dialog(this@ProfileEditPersonalActivity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.layout_list_single)
        val recyclerView                = dialog.findViewById(R.id.recyclerView) as RecyclerView?
        val titleAdapter                = TitleListAdapter(this@ProfileEditPersonalActivity, listTitle,listIcon,dialog,this)
        recyclerView?.layoutManager     = LinearLayoutManager(this@ProfileEditPersonalActivity, RecyclerView.VERTICAL, false)
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
                    CommonMethod.hideKeyboardView(this@ProfileEditPersonalActivity)
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
