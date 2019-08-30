package com.app.l_pesa.profile.view

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.app.ProgressDialog
import android.content.ClipData
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.exifinterface.media.ExifInterface
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.l_pesa.BuildConfig
import com.app.l_pesa.R
import com.app.l_pesa.common.BitmapResize
import com.app.l_pesa.common.CommonMethod
import com.app.l_pesa.common.SharedPref
import com.app.l_pesa.dashboard.model.ResDashboard
import com.app.l_pesa.dashboard.view.DashboardActivity
import com.app.l_pesa.main.view.MainActivity
import com.app.l_pesa.profile.adapter.AdapterPopupWindow
import com.app.l_pesa.profile.adapter.BusinessIdAdapter
import com.app.l_pesa.profile.adapter.BusinessIdListAdapter
import com.app.l_pesa.profile.inter.ICallBackClickBusinessId
import com.app.l_pesa.profile.inter.ICallBackProof
import com.app.l_pesa.profile.inter.ICallBackRecyclerCallbacks
import com.app.l_pesa.profile.inter.ICallBackUpload
import com.app.l_pesa.profile.model.ModelWindowPopUp
import com.app.l_pesa.profile.model.ResUserInfo
import com.app.l_pesa.profile.presenter.PresenterAWSBusinesslId
import com.app.l_pesa.profile.presenter.PresenterAddProof
import com.app.l_pesa.profile.presenter.PresenterDeleteProof
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.fragment_business_id_layout.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*

class BusinessIdInfoFragment : Fragment(), ICallBackClickBusinessId, ICallBackProof, ICallBackUpload {

    private var filterPopup : PopupWindow? = null
    private var selectedItem: Int = -1
    private var listBusinessId      : ArrayList<ResUserInfo.UserIdsBusinessInfo>? = null
    private var businessIdAdapter   : BusinessIdAdapter? = null
    private var businessIdType=""
    private var businessIdName=""
    private var businessId=0

    private val requestPhoto      = 14
    private val requestGallery    = 15
    private var captureImageStatus : Boolean    = false
    private lateinit var  photoFile          : File
    private lateinit  var captureFilePath    : Uri

    private lateinit  var progressDialog: ProgressDialog
    private  val REQUEST_ID_PERMISSIONS = 1

    companion object {
        fun newInstance(): Fragment {
            return BusinessIdInfoFragment()
        }
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_business_id_layout, container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initLoader()
        initData()

    }

    private fun initData()
    {
        listBusinessId = ArrayList()
        val sharedPrefOBJ= SharedPref(activity!!)
        val profileInfo  = Gson().fromJson<ResUserInfo.Data>(sharedPrefOBJ.profileInfo, ResUserInfo.Data::class.java)
        listBusinessId!!.clear()

        if(profileInfo.userIdsBusinessInfo!!.size>0)
        {
            listBusinessId!!.addAll(profileInfo.userIdsBusinessInfo!!)
            businessIdAdapter                 = BusinessIdAdapter(activity!!,listBusinessId!!,this)
            rvBusinessId.layoutManager        = LinearLayoutManager(activity!!, RecyclerView.VERTICAL, false)
            rvBusinessId.adapter              = businessIdAdapter
        }

        buttonSubmit.setOnClickListener {

            if(!captureImageStatus)
            {
                CommonMethod.customSnackBarError(rootLayout,activity!!,resources.getString(R.string.required_id_image))
            }
            else if(businessId==0)
            {
                CommonMethod.customSnackBarError(rootLayout,activity!!,resources.getString(R.string.required_id_type))
                showDialogIdType(sharedPrefOBJ)
            }

            else
            {
                if(CommonMethod.isNetworkAvailable(activity!!))
                {

                    progressDialog.show()
                    buttonSubmit.isClickable=false
                    val imgFile=CommonMethod.fileCompress(photoFile)

                    /*if(idTypeExists=="TRUE")
                    {*/
                    val presenterAWSBusinessId= PresenterAWSBusinesslId()
                    // presenterAWSPersonalId.deletePersonalAWS(activity!!,imgFileAddress)
                    presenterAWSBusinessId.uploadBusinessId(activity!!,this,imgFile)
                    /*}
                    else
                    {
                        val presenterAWSPersonalId= PresenterAWSPersonalId()
                        presenterAWSPersonalId.uploadPersonalId(activity!!,this,captureFile)
                    }*/


                }
                else
                {
                    CommonMethod.customSnackBarError(rootLayout,activity!!,resources.getString(R.string.no_internet))
                }
            }
        }

        buttonCancel.setOnClickListener {

            activity!!.onBackPressed()
            activity!!.overridePendingTransition(R.anim.left_in, R.anim.right_out)
        }

        etBusinessId.isFocusable=false
        etBusinessId.setOnClickListener {
            showDialogIdType(sharedPrefOBJ)

        }

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

    private fun checkAndRequestPermissions(): Boolean {

        val permissionCamera        = ContextCompat.checkSelfPermission(activity!!, Manifest.permission.CAMERA)
        val permissionStorage       = ContextCompat.checkSelfPermission(activity!!, Manifest.permission.WRITE_EXTERNAL_STORAGE)

        val listPermissionsNeeded = ArrayList<String>()

        if (permissionCamera != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA)
        }
        if (permissionStorage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
        if (listPermissionsNeeded.isNotEmpty()) {
            ActivityCompat.requestPermissions(activity!!, listPermissionsNeeded.toTypedArray(), REQUEST_ID_PERMISSIONS)
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

                        if (ActivityCompat.shouldShowRequestPermissionRationale(activity!!, Manifest.permission.CAMERA)
                                || ActivityCompat.shouldShowRequestPermissionRationale(activity!!, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                            showDialogOK("Permissions are required for this app",
                                    DialogInterface.OnClickListener { _, which ->
                                        when (which) {
                                            DialogInterface.BUTTON_POSITIVE -> checkAndRequestPermissions()
                                            DialogInterface.BUTTON_NEGATIVE ->

                                                activity!!.finish()
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
        AlertDialog.Builder(activity!!)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", okListener)
                .create()
                .show()
    }

    private fun permissionDialog(msg: String) {
        val dialog = AlertDialog.Builder(activity!!)
        dialog.setMessage(msg)
                .setPositiveButton("Yes") { _, _ ->
                    startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:com.app.l_pesa")))
                }
                .setNegativeButton("Cancel") { _, _ -> activity!!.finish() }
        dialog.show()
    }


    private fun dismiss()
    {
        if(progressDialog.isShowing)
        {
            progressDialog.dismiss()
        }
    }

    private fun initLoader()
    {
        progressDialog = ProgressDialog(activity!!,R.style.MyAlertDialogStyle)
        progressDialog.isIndeterminate = true
        progressDialog.setMessage(resources.getString(R.string.loading))
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        progressDialog.setCancelable(false)
        progressDialog.setCanceledOnTouchOutside(false)

    }


    override fun onSuccessUploadAWS(url: String) {

        val jsonObject = JsonObject()
        //jsonObject.addProperty("id_image","per_new_138308_7397641a67801aad9fe694c4cfd3c48a.jpg") // Static
        jsonObject.addProperty("id_image",url) // Static
        jsonObject.addProperty("id_type",businessId.toString())
        jsonObject.addProperty("id_number","")
        jsonObject.addProperty("type_name","Business")

        val presenterAddProof= PresenterAddProof()
        presenterAddProof.doAddProof(activity!!,jsonObject,this)

    }

    override fun onFailureUploadAWS(string: String) {

        dismiss()
        buttonSubmit.isClickable=true
        CommonMethod.customSnackBarError(rootLayout,activity!!,string)
    }

    override fun onSessionTimeOut(message: String) {

        dismiss()
        val dialogBuilder = AlertDialog.Builder(activity!!)
        dialogBuilder.setMessage(message)
                .setCancelable(false)
                .setPositiveButton("Ok") { dialog, _ ->
                    dialog.dismiss()
                    val sharedPrefOBJ= SharedPref(activity!!)
                    sharedPrefOBJ.removeShared()
                    startActivity(Intent(activity!!, MainActivity::class.java))
                    activity!!.overridePendingTransition(R.anim.right_in, R.anim.left_out)
                    activity!!.finish()
                }

        val alert = dialogBuilder.create()
        alert.setTitle(resources.getString(R.string.app_name))
        alert.show()

    }


    private fun showDialogIdType(sharedPrefOBJ: SharedPref)
    {
        val userDashBoard  = Gson().fromJson<ResDashboard.Data>(sharedPrefOBJ.userDashBoard, ResDashboard.Data::class.java)
        if(userDashBoard.businessIdTypes!!.size>0)
        {
            val dialog= Dialog(activity!!)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setContentView(R.layout.dialog_id_type)
            val recyclerView                = dialog.findViewById(R.id.recyclerView) as RecyclerView?
            val businessIdAdapter           = BusinessIdListAdapter(activity!!, userDashBoard.businessIdTypes!!,dialog,this)
            recyclerView?.layoutManager     = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
            recyclerView?.adapter           = businessIdAdapter
            dialog.show()
        }
    }

    private fun dismissPopup() {
        filterPopup?.let {
            if(it.isShowing){
                it.dismiss()
            }
            filterPopup = null
        }

    }

    override fun onSelectIdType(id: Int, name: String, type: String) {

        val sharedPrefOBJ= SharedPref(activity!!)
        val profileInfo  = Gson().fromJson<ResUserInfo.Data>(sharedPrefOBJ.profileInfo, ResUserInfo.Data::class.java)
        val totalSize = 0 until profileInfo.userIdsBusinessInfo!!.size

        if(profileInfo.userIdsBusinessInfo!!.size>0)
        {
            for(i in totalSize)
            {
                if(profileInfo.userIdsBusinessInfo!![i].verified==1 && profileInfo.userIdsBusinessInfo!![i].idTypeName==name)
                {
                    Toast.makeText(activity, "Your $name already verified", Toast.LENGTH_SHORT).show()
                    break
                }
                else
                {
                    businessIdName=name
                    etBusinessId.setText(businessIdName)
                    businessIdType=type
                    businessId=id

                }
            }
        }
        else
        {
            businessIdName=name
            etBusinessId.setText(businessIdName)
            businessIdType=type
            businessId=id
        }

    }


    override fun onClickIdList(userIdsBusinessInfo: ResUserInfo.UserIdsBusinessInfo, position: Int, it: View) {

        dismissPopup()
        filterPopup = showAlertFilter(userIdsBusinessInfo,position)
        filterPopup?.isOutsideTouchable = true
        filterPopup?.isFocusable = true
        filterPopup?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        filterPopup?.showAsDropDown(rootLayout,165,-400)
    }

    private fun showAlertFilter(userIdsBusinessInfo:ResUserInfo.UserIdsBusinessInfo, pos: Int): PopupWindow {

        val filterItemList = mutableListOf<ModelWindowPopUp>()
        if(userIdsBusinessInfo.verified==1)
        {
            filterItemList.add(ModelWindowPopUp(R.drawable.ic_view_file,resources.getString(R.string.view_file)))
        }
        else
        {
            filterItemList.add(ModelWindowPopUp(R.drawable.ic_view_file,resources.getString(R.string.view_file)))
            filterItemList.add(ModelWindowPopUp(R.drawable.ic_delete,resources.getString(R.string.delete)))
        }


        val inflater = activity!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.layout_recyclerview, null)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.addItemDecoration(DividerItemDecoration(recyclerView.context, DividerItemDecoration.VERTICAL))

        val adapter = AdapterPopupWindow(activity!!)
        adapter.addAlertFilter(filterItemList)
        recyclerView.adapter = adapter
        adapter.selectedItem(selectedItem)

        adapter.setOnClick(object : ICallBackRecyclerCallbacks<ModelWindowPopUp> {
            override fun onItemClick(view: View, position: Int, item: ModelWindowPopUp) {
                selectedItem = position

                if(position==1)
                {

                    if(CommonMethod.isNetworkAvailable(activity!!))
                    {
                        val alertDialog = AlertDialog.Builder(activity!!)
                        alertDialog.setTitle(resources.getString(R.string.app_name))
                        alertDialog.setMessage(resources.getString(R.string.delete_this_item))
                        alertDialog.setPositiveButton("Yes") { _, _ -> deletePersonalIdProof(userIdsBusinessInfo,pos) }
                                .setNegativeButton("No") { dialog, _ -> dialog.dismiss() }
                        alertDialog.show()

                    }
                    else
                    {
                        CommonMethod.customSnackBarError(rootLayout,activity!!,resources.getString(R.string.no_internet))
                    }
                }
                else
                {
                    // View File
                    if(!TextUtils.isEmpty(userIdsBusinessInfo.fileName))
                    {
                        if(CommonMethod.isNetworkAvailable(activity!!))
                        {
                            val bundle = Bundle()
                            bundle.putString("FILE_NAME",BuildConfig.BUSINESS_IMAGE_URL+userIdsBusinessInfo.fileName)
                            val intent = Intent(activity, ActivityViewFile::class.java)
                            intent.putExtras(bundle)
                            startActivity(intent,bundle)
                            activity?.overridePendingTransition(R.anim.right_in, R.anim.left_out)
                        }
                        else
                        {
                            CommonMethod.customSnackBarError(rootLayout,activity!!,resources.getString(R.string.no_internet))
                        }

                    }
                }

                dismissPopup()
            }
        })

        return PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    private fun deletePersonalIdProof(userIdsBusinessInfo: ResUserInfo.UserIdsBusinessInfo, pos: Int)
    {
        dismiss()
        val jsonObject = JsonObject()
        jsonObject.addProperty("user_type_id",userIdsBusinessInfo.id.toString())

        val presenterDeleteProof= PresenterDeleteProof()
        presenterDeleteProof.doDeleteProof(activity!!,jsonObject,pos,this)
    }

    override fun onSuccessAddProof() {
        dismiss()
        val sharedPref= SharedPref(activity!!)
        sharedPref.navigationTab=resources.getString(R.string.open_tab_profile)
        val intent = Intent(activity!!, DashboardActivity::class.java)
        startActivity(intent)
        activity?.overridePendingTransition(R.anim.right_in, R.anim.left_out)

    }

    override fun onFailureAddProof(message: String) {
        dismiss()
        buttonSubmit.isClickable=true
        CommonMethod.customSnackBarError(rootLayout,activity!!,message)
    }

    override fun onSuccessDeleteProof(position: Int) {
        dismiss()
        listBusinessId!!.removeAt(position)
        businessIdAdapter!!.notifyDataSetChanged()
        val sharedPrefOBJ= SharedPref(activity!!)
        sharedPrefOBJ.profileUpdate=resources.getString(R.string.status_true)
    }

    override fun onFailureDeleteProof(message: String) {
        dismiss()
        CommonMethod.customSnackBarError(rootLayout,activity!!,message)
    }

    private fun cameraClick()
    {
        activity!!.cacheDir.deleteRecursively()
        val captureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val imagePath = File(activity!!.filesDir, "images")
        photoFile = File(imagePath, "user.jpg")
        if (photoFile.exists()) {
            photoFile.delete()
        } else {
            photoFile.parentFile!!.mkdirs()
        }
        captureFilePath = FileProvider.getUriForFile(activity!!, BuildConfig.APPLICATION_ID + ".provider", photoFile)

        captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, captureFilePath)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            captureIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        } else {
            val clip = ClipData.newUri(activity!!.contentResolver, "business id photo", captureFilePath)
            captureIntent.clipData = clip
            captureIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        }

        startActivityForResult(captureIntent, requestPhoto)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode) {
            requestPhoto ->

                if (resultCode == Activity.RESULT_OK) {
                    setImage()
                }

        }
    }

    private fun setImage() {

        try {
           val photoPath: Uri = captureFilePath
            handleRotation(photoFile.absolutePath)
            imgProfile.post {
                val pictureBitmap = BitmapResize.shrinkBitmap(
                        activity!!,
                        photoPath,
                        imgProfile.width,
                        imgProfile.height
                )
                imgProfile.setImageBitmap(pictureBitmap)
                imgProfile.scaleType = ImageView.ScaleType.CENTER_CROP

            }

        captureImageStatus = true

        }
        catch (exp:Exception)
        {

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
                            else ->origin     //origin.rotate(270f)
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



}