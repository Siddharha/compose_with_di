package com.app.l_pesa.profile.view

import android.app.Activity
import android.app.Dialog
import android.content.ClipData
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
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
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.Toast
import com.app.l_pesa.BuildConfig
import com.app.l_pesa.R
import com.app.l_pesa.common.BitmapResize
import com.app.l_pesa.common.CommonMethod
import com.app.l_pesa.common.SharedPref
import com.app.l_pesa.dashboard.model.ResDashboard
import com.app.l_pesa.dashboard.view.DashboardActivity
import com.app.l_pesa.loanplan.adapter.PersonalIdAdapter
import com.app.l_pesa.main.MainActivity
import com.app.l_pesa.profile.adapter.AdapterPopupWindow
import com.app.l_pesa.profile.adapter.PersonalIdListAdapter
import com.app.l_pesa.profile.inter.ICallBackClickPersonalId
import com.app.l_pesa.profile.inter.ICallBackProof
import com.app.l_pesa.profile.inter.ICallBackRecyclerCallbacks
import com.app.l_pesa.profile.inter.ICallBackUpload
import com.app.l_pesa.profile.model.ModelWindowPopUp
import com.app.l_pesa.profile.model.ResUserInfo
import com.app.l_pesa.profile.presenter.PresenterAWSPersonalId
import com.app.l_pesa.profile.presenter.PresenterAddProof
import com.app.l_pesa.profile.presenter.PresenterDeleteProof
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.kaopiz.kprogresshud.KProgressHUD
import kotlinx.android.synthetic.main.fragment_personal_id_layout.*
import java.io.File
import java.util.*


class PersonalIdInfoFragment : Fragment(), ICallBackClickPersonalId, ICallBackProof, ICallBackUpload {


    private var filterPopup : PopupWindow? = null
    private var selectedItem: Int = -1
    private var listPersonalId      : ArrayList<ResUserInfo.UserIdsPersonalInfo>? = null
    private var personalIdAdapter   : PersonalIdAdapter? = null
    private var personalIdType=""
    private var personalIdName=""
    private var personalId=0

    private val requestPhoto      = 12
    private val requestGallery    = 13
    private var captureImageStatus : Boolean    = false
    private var photoFile          : File?      = null
    private var captureFilePath    : Uri?       = null
    private var idTypeExists        = "FALSE"
    private var imgFileAddress      = ""

    private lateinit  var progressDialog: KProgressHUD

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

        initLoader()
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

            if(!captureImageStatus)
            {
                CommonMethod.customSnackBarError(rootLayout,activity!!,resources.getString(R.string.required_id_image))
            }
            else if(personalId==0)
            {
                CommonMethod.customSnackBarError(rootLayout,activity!!,resources.getString(R.string.required_id_type))
                showDialogIdType(sharedPrefOBJ)
            }
            else if(etPersonalId.text.toString()!=resources.getString(R.string.address_prof) && TextUtils.isEmpty(etIdNumber.text.toString()))
            {
                CommonMethod.customSnackBarError(rootLayout,activity!!,resources.getString(R.string.required_id_number))
            }
            else
            {
                if(CommonMethod.isNetworkAvailable(activity!!))
                {
                    progressDialog.show()
                    buttonSubmit.isClickable=false

                    /*if(idTypeExists=="TRUE")
                    {*/
                        val presenterAWSPersonalId= PresenterAWSPersonalId()
                       // presenterAWSPersonalId.deletePersonalAWS(activity!!,imgFileAddress)
                        presenterAWSPersonalId.uploadPersonalId(activity!!,this,photoFile)
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

        etPersonalId.isFocusable=false
        etPersonalId.setOnClickListener {
            showDialogIdType(sharedPrefOBJ)

        }

        imgProfile.setOnClickListener {

            val items = arrayOf<CharSequence>("Camera", "Gallery", "Cancel")
            val dialogView = AlertDialog.Builder(activity!!,R.style.MyAlertDialogTheme)
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
        progressDialog= KProgressHUD.create(activity)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setCancellable(false)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f)

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
                if (name == resources.getString(R.string.address_prof))
                {
                    ilIdNumber.visibility = View.INVISIBLE
                    personalIdName = name
                    etPersonalId.setText(personalIdName)
                    personalIdType = type
                    personalId = id

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

    }


    override fun onClickIdList(userIdsPersonalInfo: ResUserInfo.UserIdsPersonalInfo, position: Int, it: View) {

        dismissPopup()
        filterPopup = showAlertFilter(userIdsPersonalInfo,position)
        filterPopup?.isOutsideTouchable = true
        filterPopup?.isFocusable = true
        filterPopup?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        filterPopup?.showAsDropDown(rootLayout,165,-400)

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
        val view = inflater.inflate(R.layout.layout_recyclerview, null)
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
                        val alertDialog = AlertDialog.Builder(activity!!)
                        alertDialog.setTitle(resources.getString(R.string.app_name))
                        alertDialog.setMessage(resources.getString(R.string.delete_this_item))
                        alertDialog.setPositiveButton("Yes") { _, _ -> deletePersonalIdProof(userIdsPersonalInfo,pos) }
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
                    if(!TextUtils.isEmpty(userIdsPersonalInfo.fileName))
                    {
                        if(CommonMethod.isNetworkAvailable(activity!!))
                        {
                            val bundle = Bundle()
                            bundle.putString("FILE_NAME",BuildConfig.BUSINESS_IMAGE_URL+userIdsPersonalInfo.fileName)
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

    private fun deletePersonalIdProof(userIdsPersonalInfo: ResUserInfo.UserIdsPersonalInfo, pos: Int)
    {
        progressDialog.show()
        val jsonObject = JsonObject()
        jsonObject.addProperty("user_type_id",userIdsPersonalInfo.id.toString())

        val presenterDeleteProof= PresenterDeleteProof()
        presenterDeleteProof.doDeleteProof(activity!!,jsonObject,pos,this)
    }

    override fun onSuccessAddProof() {

        dismiss()
        val sharedPref=SharedPref(activity!!)
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
        listPersonalId!!.removeAt(position)
        personalIdAdapter!!.notifyDataSetChanged()
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
        if (photoFile!!.exists()) {
            photoFile!!.delete()
        } else {
            photoFile!!.parentFile.mkdirs()
        }
        captureFilePath = FileProvider.getUriForFile(activity!!, BuildConfig.APPLICATION_ID + ".provider", photoFile!!)

        captureIntent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, captureFilePath)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            captureIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        } else {
            val clip = ClipData.newUri(activity!!.contentResolver, "personal id photo", captureFilePath)
            captureIntent.clipData = clip
            captureIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        }

        startActivityForResult(captureIntent, requestPhoto)
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
                    Toast.makeText(activity!!, "You denied the permission", Toast.LENGTH_SHORT).show()
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
                Toast.makeText(activity, "Image size maximum 3Mb", Toast.LENGTH_SHORT).show()
            }
            else {
                val photoPath: Uri = captureFilePath ?: return
                imgProfile.post {
                    val pictureBitmap = BitmapResize.shrinkBitmap(
                            activity!!,
                            photoPath,
                            imgProfile.width,
                            imgProfile.height
                    )
                    imgProfile.setImageBitmap(pictureBitmap)
                    imgProfile.scaleType = ImageView.ScaleType.CENTER_CROP
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
                DocumentsContract.isDocumentUri(activity, uri) -> try {
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
                captureImageStatus=false
                Toast.makeText(activity, "Image size maximum 3Mb", Toast.LENGTH_SHORT).show()
            }
            else
            {
                captureImageStatus=true
                val bitmap = BitmapFactory.decodeFile(imagePath)
                imgProfile.setImageBitmap(bitmap)
                photoFile=imgSize
                CommonMethod.fileCompress(photoFile!!)
            }

        }
        else {
            captureImageStatus=false
            Toast.makeText(activity!!, "Failed to get image", Toast.LENGTH_SHORT).show()
        }
    }


}