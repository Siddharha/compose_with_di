package com.app.l_pesa.profile.view

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v4.content.FileProvider
import android.support.v7.app.AlertDialog
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.PopupWindow
import android.widget.Toast
import com.app.l_pesa.R
import com.app.l_pesa.common.CommonMethod
import com.app.l_pesa.common.SharedPref
import com.app.l_pesa.dashboard.model.ResDashboard
import com.app.l_pesa.dashboard.view.DashboardActivity
import com.app.l_pesa.loanplan.adapter.PersonalIdAdapter
import com.app.l_pesa.profile.inter.ICallBackClickPersonalId
import com.app.l_pesa.profile.model.ResUserInfo
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_personal_id_layout.*
import com.app.l_pesa.profile.adapter.AdapterPopupWindow
import com.app.l_pesa.profile.adapter.PersonalIdListAdapter
import com.app.l_pesa.profile.inter.ICallBackProof
import com.app.l_pesa.profile.inter.ICallBackRecyclerCallbacks
import com.app.l_pesa.profile.inter.ICallBackUpload
import com.app.l_pesa.profile.model.ModelWindowPopUp
import com.app.l_pesa.profile.presenter.PresenterAWSPersonalId
import com.app.l_pesa.profile.presenter.PresenterAddProof
import com.app.l_pesa.profile.presenter.PresenterDeleteProof
import com.google.gson.JsonObject
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class PersonalIdInfoFragment : Fragment(), ICallBackClickPersonalId, ICallBackProof, ICallBackUpload {


    private var filterPopup : PopupWindow? = null
    private var selectedItem: Int = -1
    var listPersonalId      : ArrayList<ResUserInfo.UserIdsPersonalInfo>? = null
    var personalIdAdapter   : PersonalIdAdapter? = null
    var personalIdType=""
    var personalIdName=""
    var personalId=0

    private val PHOTO               = 1
    private val GALLEY              = 2
    private var captureFile: File?  = null
    private var imageFilePath       = ""
    private var imageSelectStatus   : Boolean = false
    private var idTypeExists        = "FALSE"
    private var imgFileAddress      = ""

   companion object {
        fun newInstance(): Fragment {
            return PersonalIdInfoFragment()
        }
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_personal_id_layout, container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        swipeRefresh()
        initData()

    }

    private fun initData()
    {
        listPersonalId = ArrayList()
        val sharedPrefOBJ= SharedPref(activity!!)
        val profileInfo  = Gson().fromJson<ResUserInfo.Data>(sharedPrefOBJ.profileInfo, ResUserInfo.Data::class.java)
        listPersonalId!!.clear()

        if(profileInfo.userIdsPersonalInfo!!.size>0)
        {
            listPersonalId!!.addAll(profileInfo.userIdsPersonalInfo!!)
            personalIdAdapter                 = PersonalIdAdapter(activity!!,listPersonalId!!,this)
            rvPersonalId.layoutManager        = LinearLayoutManager(activity!!, LinearLayoutManager.VERTICAL, false)
            rvPersonalId.adapter              = personalIdAdapter
        }

        buttonSubmit.setOnClickListener {

            if(!imageSelectStatus)
            {
                CommonMethod.customSnackBarError(llRoot,activity!!,resources.getString(R.string.required_profile_image))
            }
            else if(personalId==0)
            {
                CommonMethod.customSnackBarError(llRoot,activity!!,resources.getString(R.string.required_id_type))
                showDialogIdType(sharedPrefOBJ)
            }
            else if(etPersonalId.text.toString()!=resources.getString(R.string.address_prof) && TextUtils.isEmpty(etIdNumber.text.toString()))
            {
                CommonMethod.customSnackBarError(llRoot,activity!!,resources.getString(R.string.required_id_number))
            }
            else
            {
                if(CommonMethod.isNetworkAvailable(activity!!))
                {

                    swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent)
                    swipeRefreshLayout.isRefreshing=true
                    buttonSubmit.isClickable=false

                    /*if(idTypeExists=="TRUE")
                    {*/
                        val presenterAWSPersonalId= PresenterAWSPersonalId()
                       // presenterAWSPersonalId.deletePersonalAWS(activity!!,imgFileAddress)
                        presenterAWSPersonalId.uploadPersonalId(activity!!,this,captureFile)
                    /*}
                    else
                    {
                        val presenterAWSPersonalId= PresenterAWSPersonalId()
                        presenterAWSPersonalId.uploadPersonalId(activity!!,this,captureFile)
                    }*/


                }
                else
                {
                    CommonMethod.customSnackBarError(llRoot,activity!!,resources.getString(R.string.no_internet))
                }
            }
        }

        buttonCancel.setOnClickListener {

            activity!!.onBackPressed()
            activity!!.overridePendingTransition(R.anim.left_in, R.anim.right_out)
        }

        etPersonalId.isFocusable=false
        etPersonalId.setOnClickListener {
            showDialogIdType(sharedPrefOBJ)

        }

        imgProfile.setOnClickListener {

            val items = arrayOf<CharSequence>("Camera", "Gallery", "Cancel") // array list
            val dialogView = AlertDialog.Builder(activity!!)
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



    override fun onSuccessUploadAWS(url: String) {

        val jsonObject = JsonObject()
        //jsonObject.addProperty("id_image","per_new_138308_7397641a67801aad9fe694c4cfd3c48a.jpg") // Static
        jsonObject.addProperty("id_image",url) // Static
        jsonObject.addProperty("id_type",personalId.toString())
        if(etPersonalId.text.toString()==resources.getString(R.string.address_prof))
        {
            jsonObject.addProperty("id_number","")
        }
        else
        {
            jsonObject.addProperty("id_number",etIdNumber.text.toString())
        }

        jsonObject.addProperty("type_name","Personal")

        println("JSON_REQ"+jsonObject)

        val presenterAddProof= PresenterAddProof()
        presenterAddProof.doAddProof(activity!!,jsonObject,this)

    }

    override fun onFailureUploadAWS(string: String) {

        swipeRefreshLayout.isRefreshing=false
        buttonSubmit.isClickable=true
        CommonMethod.customSnackBarError(llRoot,activity!!,string)
    }

    private fun swipeRefresh()
    {
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent)
        swipeRefreshLayout.setOnRefreshListener {
        swipeRefreshLayout.isRefreshing=false
        }
    }

    private fun showDialogIdType(sharedPrefOBJ: SharedPref)
    {
        val userDashBoard  = Gson().fromJson<ResDashboard.Data>(sharedPrefOBJ.userDashBoard, ResDashboard.Data::class.java)
        if(userDashBoard.personalIdTypes!!.size>0)
        {
            val dialog= Dialog(activity!!)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setContentView(R.layout.dialog_id_type)
            val recyclerView                = dialog.findViewById(R.id.recyclerView) as RecyclerView?
            val personalIdAdapter           = PersonalIdListAdapter(activity!!, userDashBoard.personalIdTypes!!,dialog,this)
            recyclerView?.layoutManager     = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
            recyclerView?.adapter           = personalIdAdapter
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
        val totalSize = 0 until profileInfo.userIdsPersonalInfo!!.size

        if(profileInfo.userIdsPersonalInfo!!.size>0)
        {
            for (i in totalSize)
            {

                if (profileInfo.userIdsPersonalInfo!![i].verified == 1 && profileInfo.userIdsPersonalInfo!![i].idTypeName == name) {
                    Toast.makeText(activity, "Your $name already verified", Toast.LENGTH_SHORT).show()
                    break

                } else
                {

                    if (name == resources.getString(R.string.address_prof)) {
                        ilIdNumber.visibility = View.INVISIBLE
                        personalIdName = name
                        etPersonalId.setText(personalIdName)
                        personalIdType = type
                        personalId = id

                    } else {
                        ilIdNumber.visibility = View.VISIBLE
                        personalIdName = name
                        etPersonalId.setText(personalIdName)
                        personalIdType = type
                        personalId = id
                    }

                }
            }
        }

        else
            {
                ilIdNumber.visibility = View.VISIBLE
                personalIdName = name
                etPersonalId.setText(personalIdName)
                personalIdType = type
                personalId = id
            }

    }


    override fun onClickIdList(userIdsPersonalInfo: ResUserInfo.UserIdsPersonalInfo, position: Int, it: View) {

        dismissPopup()
        filterPopup = showAlertFilter(userIdsPersonalInfo,position)
        filterPopup?.isOutsideTouchable = true
        filterPopup?.isFocusable = true
        filterPopup?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        filterPopup?.showAsDropDown(it)
    }

    private fun showAlertFilter(userIdsPersonalInfo: ResUserInfo.UserIdsPersonalInfo, pos: Int): PopupWindow {

        val filterItemList = mutableListOf<ModelWindowPopUp>()
        if(userIdsPersonalInfo.verified==1)
        {
            filterItemList.add(ModelWindowPopUp(R.drawable.ic_view_file,resources.getString(R.string.view_file)))
        }
        else
        {
            filterItemList.add(ModelWindowPopUp(R.drawable.ic_view_file,resources.getString(R.string.view_file)))
            filterItemList.add(ModelWindowPopUp(R.drawable.ic_delete,resources.getString(R.string.delete)))
        }


        val inflater = activity!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.layout_only_recyclerview, null)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.addItemDecoration(DividerItemDecoration(recyclerView.context, DividerItemDecoration.VERTICAL))

        val adapter = AdapterPopupWindow(activity!!)
        adapter.addAlertFilter(filterItemList)
        recyclerView.adapter = adapter
        adapter.selectedItem(selectedItem)

        adapter.setOnClick(object : ICallBackRecyclerCallbacks<ModelWindowPopUp>{
            override fun onItemClick(view: View, position: Int, item:ModelWindowPopUp) {
                selectedItem = position

                if(position==1)
                {

                    if(CommonMethod.isNetworkAvailable(activity!!))
                    {

                        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent)
                        swipeRefreshLayout.isRefreshing=true
                        deletePersonalIdProof(userIdsPersonalInfo,pos)
                    }
                    else
                    {
                        CommonMethod.customSnackBarError(llRoot,activity!!,resources.getString(R.string.no_internet))
                    }
                }
                else
                {
                    // View File
                    if(!TextUtils.isEmpty(userIdsPersonalInfo.fileName))
                    {
                        if(CommonMethod.isNetworkAvailable(activity!!))
                        {
                            val bundle = Bundle()
                            bundle.putString("FILE_NAME",userIdsPersonalInfo.fileName)
                            val intent = Intent(activity, ActivityViewFile::class.java)
                            intent.putExtras(bundle)
                            startActivity(intent,bundle)
                            activity?.overridePendingTransition(R.anim.right_in, R.anim.left_out)
                        }
                        else
                        {
                            CommonMethod.customSnackBarError(llRoot,activity!!,resources.getString(R.string.no_internet))
                        }

                    }
                }

                dismissPopup()
            }
        })

        return PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    private fun deletePersonalIdProof(userIdsPersonalInfo: ResUserInfo.UserIdsPersonalInfo, pos: Int)
    {
        val jsonObject = JsonObject()
        jsonObject.addProperty("user_type_id",userIdsPersonalInfo.id.toString())

        val presenterDeleteProof= PresenterDeleteProof()
        presenterDeleteProof.doDeleteProof(activity!!,jsonObject,pos,this)
    }

    override fun onSuccessAddProof() {

        val sharedPref=SharedPref(activity!!)
        sharedPref.navigationTab=resources.getString(R.string.open_tab_profile)
        val intent = Intent(activity!!, DashboardActivity::class.java)
        startActivity(intent)
        activity?.overridePendingTransition(R.anim.right_in, R.anim.left_out)
        activity?.finishAffinity()
    }

    override fun onFailureAddProof(message: String) {

        swipeRefreshLayout.isRefreshing=false
        buttonSubmit.isClickable=true
        CommonMethod.customSnackBarError(llRoot,activity!!,message)
    }

    override fun onSuccessDeleteProof(position: Int) {
        swipeRefreshLayout.isRefreshing=false
        listPersonalId!!.removeAt(position)
        personalIdAdapter!!.notifyDataSetChanged()
        val sharedPrefOBJ= SharedPref(activity!!)
        sharedPrefOBJ.profileUpdate=resources.getString(R.string.status_true)
    }

    override fun onFailureDeleteProof(message: String) {
        swipeRefreshLayout.isRefreshing=false
        CommonMethod.customSnackBarError(llRoot,activity!!,message)
    }

    private fun cameraClick()
    {
        try {
            val imageFile = createImageFile()
            val callCameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

            val authorities = "com.app.l_pesa.provider"
            val imageUri = FileProvider.getUriForFile(activity!!, authorities, imageFile)
            callCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
            startActivityForResult(callCameraIntent, PHOTO)

        } catch (e: IOException) {
            Toast.makeText(activity!!,"Could not create file!", Toast.LENGTH_SHORT).show()
        }
    }


    @SuppressLint("SimpleDateFormat")
    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName: String = "personal_id" + timeStamp + "_"
        val storageDir: File = activity!!.getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        if (!storageDir.exists()) storageDir.mkdirs()
        captureFile = File.createTempFile(imageFileName, ".jpg", storageDir)
        imageFilePath = captureFile!!.absolutePath
        return captureFile!!
    }

    private fun galleryClick()
    {
        val checkSelfPermission = ContextCompat.checkSelfPermission(activity!!, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if (checkSelfPermission != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(activity!!, arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
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
                    Toast.makeText(activity!!, "You denied the permission", Toast.LENGTH_SHORT).show()
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
                    imageSelectStatus=true //captureFile
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
        if (DocumentsContract.isDocumentUri(activity!!, uri)){
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
        val cursor = activity!!.contentResolver.query(uri!!, null, selection, null, null )
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
                Toast.makeText(activity, "Image size maximum 3Mb", Toast.LENGTH_SHORT).show()
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
            Toast.makeText(activity!!, "Failed to get image", Toast.LENGTH_SHORT).show()
        }
    }


}