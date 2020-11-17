package com.app.l_pesa.profile.view

import android.Manifest
import android.app.Activity
import android.app.Activity.RESULT_CANCELED
import android.app.Dialog
import android.app.ProgressDialog
import android.content.ClipData
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.*
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.provider.Settings
import android.text.Spannable
import android.text.SpannableString
import android.text.TextUtils
import android.text.style.RelativeSizeSpan
import android.util.Base64.DEFAULT
import android.util.Base64.decode
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
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
import com.app.l_pesa.common.*
import com.app.l_pesa.dashboard.model.ResDashboard
import com.app.l_pesa.dashboard.view.DashboardActivity
import com.app.l_pesa.loanplan.adapter.PersonalIdAdapter
import com.app.l_pesa.main.view.MainActivity
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
import com.app.l_pesa.zoop.ICallBackZoop
import com.app.l_pesa.zoop.PresenterZoop
import com.app.l_pesa.zoop.ZoopInitFailureResponse
import com.app.l_pesa.zoop.ZoopInitResponse
import com.facebook.appevents.AppEventsConstants
import com.facebook.appevents.AppEventsLogger
import com.google.gson.Gson
import com.google.gson.JsonObject
import id.zelory.compressor.Compressor
import kotlinx.android.synthetic.main.confirm_aadhaar_layout.*
import kotlinx.android.synthetic.main.fragment_personal_id_layout.*
import org.jetbrains.anko.runOnUiThread
import org.json.JSONException
import org.json.JSONObject
import sdk.zoop.one.offline_aadhaar.zoopActivity.ZoopConsentActivity
import sdk.zoop.one.offline_aadhaar.zoopUtils.ZoopConstantUtils.*
import android.util.Base64
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*


class PersonalIdInfoFragment : Fragment(), ICallBackClickPersonalId, ICallBackProof, ICallBackUpload, ICallBackZoop {


    private var filterPopup : PopupWindow? = null
    private var selectedItem: Int = -1
    private var listPersonalId      : ArrayList<ResUserInfo.UserIdsPersonalInfo>? = null
    private var personalIdAdapter   : PersonalIdAdapter? = null
    private var personalIdType=""
    private var personalIdName=""
    private var personalId=0
    lateinit var sharedPrefOBJ:SharedPref
    private val requestPhoto      = 12
    private var captureImageStatus : Boolean    = false
    private lateinit var photoFile          : File
    private lateinit var captureFilePath    : Uri
    private var idTypeExists        = "FALSE"
    private var imgFileAddress      = ""

    private lateinit  var progressDialog: ProgressDialog
    private  val requestPermission = 1

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

        val logger = AppEventsLogger.newLogger(activity)
        val params =  Bundle()
        params.putString(AppEventsConstants.EVENT_PARAM_CONTENT_TYPE, "Personal Id Information")
        logger.logEvent(AppEventsConstants.EVENT_NAME_VIEWED_CONTENT, params)

        listPersonalId = ArrayList()
        sharedPrefOBJ= SharedPref(activity!!)
        val profileInfo  = Gson().fromJson<ResUserInfo.Data>(sharedPrefOBJ.profileInfo, ResUserInfo.Data::class.java)
        listPersonalId!!.clear()

        if(profileInfo.userIdsPersonalInfo!!.size>0)
        {
            listPersonalId!!.addAll(profileInfo.userIdsPersonalInfo!!)
            personalIdAdapter                 = PersonalIdAdapter(activity!!,listPersonalId!!,this)
            rvPersonalId.layoutManager        = LinearLayoutManager(activity!!, RecyclerView.VERTICAL, false)
            rvPersonalId.adapter              = personalIdAdapter
        }

        buttonSubmit.setOnClickListener {

            if(sharedPrefOBJ.countryCode == "in") {
                if (etPersonalId.text.toString().contentEquals("Aadhaar Card")) {
                    if (!captureImageStatus) {
                        CommonMethod.customSnackBarError(rootLayout, activity!!, resources.getString(R.string.required_id_image))
                    } else if (personalId == 0) {
                        CommonMethod.customSnackBarError(rootLayout, activity!!, resources.getString(R.string.required_id_type))
                        showDialogIdType(sharedPrefOBJ)
                    } else if (etPersonalId.text.toString() != resources.getString(R.string.address_prof) && TextUtils.isEmpty(etIdNumber.text.toString().trim())) {
                        CommonMethod.customSnackBarError(rootLayout, activity!!, resources.getString(R.string.required_id_number))
                    } else {
                        if (CommonMethod.isNetworkAvailable(activity!!)) {
                            progressDialog.show()
                            val presenterZoop = PresenterZoop()
                            presenterZoop.doOfflineAadharInit(activity!!, this)
                        }
                    }

                }else{
                    updateIdData(sharedPrefOBJ)
                }
            }else{
                updateIdData(sharedPrefOBJ)
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

    private fun updateIdData(sharedPrefOBJ: SharedPref) {
        if (!captureImageStatus) {
            CommonMethod.customSnackBarError(rootLayout, activity!!, resources.getString(R.string.required_id_image))
        } else if (personalId == 0) {
            CommonMethod.customSnackBarError(rootLayout, activity!!, resources.getString(R.string.required_id_type))
            showDialogIdType(sharedPrefOBJ)
        } else if (etPersonalId.text.toString() != resources.getString(R.string.address_prof) && TextUtils.isEmpty(etIdNumber.text.toString().trim())) {
            CommonMethod.customSnackBarError(rootLayout, activity!!, resources.getString(R.string.required_id_number))
        } else {
            if (CommonMethod.isNetworkAvailable(activity!!)) {
                progressDialog.show()
                buttonSubmit.isClickable = false

                /*if(idTypeExists=="TRUE")
                    {*/
                val presenterAWSPersonalId = PresenterAWSPersonalId()
                // presenterAWSPersonalId.deletePersonalAWS(activity!!,imgFileAddress)
                presenterAWSPersonalId.uploadPersonalId(activity!!, this, photoFile)
                /*}
                    else
                    {
                        val presenterAWSPersonalId= PresenterAWSPersonalId()
                        presenterAWSPersonalId.uploadPersonalId(activity!!,this,captureFile)
                    }*/


            } else {
                CommonMethod.customSnackBarError(rootLayout, activity!!, resources.getString(R.string.no_internet))
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
            ActivityCompat.requestPermissions(activity!!, listPermissionsNeeded.toTypedArray(), requestPermission)
            return false
        }

        return true
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {

        when (requestCode) {
            requestPermission -> {

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

                        if (ActivityCompat.shouldShowRequestPermissionRationale( activity!!, Manifest.permission.CAMERA)
                                || ActivityCompat.shouldShowRequestPermissionRationale( activity!!, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                            showDialogOK("Permissions are required for L-Pesa",
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
        AlertDialog.Builder(activity!!,R.style.MyAlertDialogTheme)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", okListener)
                .create()
                .show()
    }

    private fun permissionDialog(msg: String) {
        val dialog = AlertDialog.Builder(activity!!,R.style.MyAlertDialogTheme)
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
        val message=   SpannableString(resources.getString(R.string.loading))
        val face = Typeface.createFromAsset(activity!!.assets, "fonts/Montserrat-Regular.ttf")
        message.setSpan(RelativeSizeSpan(1.0f), 0, message.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        message.setSpan(CustomTypeFaceSpan("", face!!, Color.parseColor("#535559")), 0, message.length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
        progressDialog.isIndeterminate = true
        progressDialog.setMessage(message)
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        progressDialog.setCancelable(false)
        progressDialog.setCanceledOnTouchOutside(false)

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

    override fun onSucessDeleteUploadAWS(userIdsPersonalInfo:ResUserInfo.UserIdsPersonalInfo,pos: Int) {

        context?.runOnUiThread {
            deletePersonalIdProof(userIdsPersonalInfo,pos)
        }

    }

    override fun onFailureDeleteAWS(message: String) {
        CommonMethod.customSnackBarError(rootLayout,activity!!,message)
    }

    override fun onSucessProfileImgDeleteAWS() {
        //
    }

    override fun onSessionTimeOut(message: String) {

        dismiss()
        val dialogBuilder = AlertDialog.Builder(activity!!,R.style.MyAlertDialogTheme)
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
            recyclerView?.layoutManager     = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
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
                        //imgProfile.visibility = View.VISIBLE
                        //textView9.visibility = View.VISIBLE
                        personalIdName = name
                        etPersonalId.setText(personalIdName)
                        personalIdType = type
                        personalId = id

                    }else if (name == resources.getString(R.string.aadhaar_card)) {

                    personalIdName = name
                    etPersonalId.setText(personalIdName)
                        //ilIdNumber.visibility = View.INVISIBLE
                        //imgProfile.visibility = View.GONE
                        //textView9.visibility = View.GONE
                    personalIdType = type
                    personalId = id


                }else {
                        ilIdNumber.visibility = View.VISIBLE
                        //imgProfile.visibility = View.VISIBLE
                        //textView9.visibility = View.VISIBLE
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
                        val alertDialog = AlertDialog.Builder(activity!!,R.style.MyAlertDialogTheme)
                        alertDialog.setTitle(resources.getString(R.string.app_name))
                        alertDialog.setMessage(resources.getString(R.string.delete_this_item))
                        alertDialog.setPositiveButton("Yes") { _, _ ->
                            progressDialog.show()
                            val presenterAWSPersonalId = PresenterAWSPersonalId()
                            presenterAWSPersonalId.deletePersonalAWS(context!!,
                                    this@PersonalIdInfoFragment,
                                    userIdsPersonalInfo.fileName,
                                    userIdsPersonalInfo,pos)
                         }
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

        val jsonObject = JsonObject()
        jsonObject.addProperty("user_type_id",userIdsPersonalInfo.id.toString())

        val presenterDeleteProof= PresenterDeleteProof()
        presenterDeleteProof.doDeleteProof(activity!!,jsonObject,pos,this)
    }

    override fun onSuccessAddProof() {

        dismiss()
        val sharedPref=SharedPref(activity!!)
        sharedPref.navigationTab=resources.getString(R.string.open_tab_profile)
        sharedPref.profileUpdate=resources.getString(R.string.status_true)
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
        //activity!!.cacheDir.deleteRecursively()
        val captureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val imagePath = File(activity!!.filesDir, "images")
        photoFile = File(imagePath, "personal.jpg")
        if (photoFile.exists()) {
            photoFile.delete()
        } else {
            photoFile.parentFile!!.mkdirs()
        }
        captureFilePath = FileProvider.getUriForFile(activity!! as ProfileEditIdInfoActivity, BuildConfig.APPLICATION_ID + ".provider", photoFile)

        captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, captureFilePath)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            captureIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        } else {
            val clip = ClipData.newUri(activity!!.contentResolver, "id photo", captureFilePath)
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
            REQUEST_AADHAARAPI ->
                {
                    var requestType = "null";
                    if (data?.hasExtra(ZOOP_REQUEST_TYPE)!!) {
                        requestType = data.getStringExtra(ZOOP_REQUEST_TYPE)!!
                        //Log.d(ZOOP_TAG, " res 1" + requestType);
                    } else {
                        //Log.d(ZOOP_TAG, " res 1" + requestType);
                    }

                    if (resultCode == RESULT_CANCELED) {
                        val responseString = data . getStringExtra (ZOOP_RESULT)
                        CommonMethod.customSnackBarError(rootLayout, activity!!, "Aadhaar Not Verified!")
                        Log.e("SDK test error ", requestType + " err " + responseString + resultCode);
                    }

                    if (requestType.equals(OFFLINE_AADHAAR,true)) {
                        //String responseString1 = data.getStringExtra(ZOOP_RESULT);
                        if (resultCode == OFFLINE_AADHAAR_SUCCESS) {
                            val responseString = data. getStringExtra (ZOOP_RESULT)
//                            tvResult.setVisibility(View.VISIBLE)
//                            resultDisplayLayout.setVisibility(View.VISIBLE);
//                            llResultBg.setVisibility(View.VISIBLE);

                            try {
                                val jsonObject =  JSONObject(responseString!!)
                                //parseResultJson(jsonObject)
                                showConfirmAadhaarPopup(jsonObject)
                                CommonMethod.customSnackBarSuccess(rootLayout, activity!!, "Aadhaar verified with ${jsonObject.getString("id")}")
                            } catch ( e: JSONException) {
                                e.printStackTrace();
                            }

                           // tvResult.setText(String.format("complete Response: %s", responseString));
                           // Log.d("SDK test result ", requestType + " res " + responseString);
                        }

                        if (resultCode == OFFLINE_AADHAAR_ERROR) {
                            val errorString = data . getStringExtra (ZOOP_RESULT)

                            try {
                                val jResp = JSONObject(errorString!!)
                                //sdk_response
                                CommonMethod.customSnackBarError(rootLayout, activity!!, "${jResp.getString("sdk_response")}")
                                // tvResult.setText(errorString);
//                            tvResult.setVisibility(View.VISIBLE);
//                            resultDisplayLayout.setVisibility(View.GONE);
//                            llResultBg.setVisibility(View.GONE);
//                            tvResult.setText(errorString);
                                Log.d("SDK test error ", requestType + " err " + errorString);
                            }catch (e:java.lang.Exception){
                                e.printStackTrace()
                            }
                        }
                    } else {
                       // Log.d(ZOOP_TAG, " res 1" + requestType);
                    }
                }
        }



    }

    private fun showConfirmAadhaarPopup(jsonObject: JSONObject) {
        val jsonObjectTrans = jsonObject.optJSONObject("transaction_data")
        val imgUser = jsonObjectTrans?.optString("Image")
        val AadharNo = jsonObjectTrans?.optString("AadhaarInfo")
        val AddressEnglish = jsonObjectTrans?.optString("AddressEnglish")
        val EmailInfo = jsonObjectTrans?.optString("EmailInfo")
        val PhoneInfo = jsonObjectTrans?.optString("PhoneInfo")
        val UserSelfie = jsonObjectTrans?.optString("UserSelfie")

        val basicInfo = jsonObjectTrans?.optJSONObject("BasicInfo")
        val UserName = basicInfo?.optString("Name")
        val DOB = basicInfo?.optString("DOB")
        val Gender = basicInfo?.optString("Gender")

        val dialog = Dialog(activity!!)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.confirm_aadhaar_layout)

        with(dialog){

            val imageBytes = Base64.decode(imgUser, Base64.DEFAULT)
            val decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes?.size!!)
            imgAadhaar.setImageBitmap(decodedImage)

            val imageBytesSelfy = Base64.decode(imgUser, Base64.DEFAULT)
            val decodedImageSelfy = BitmapFactory.decodeByteArray(imageBytesSelfy, 0, imageBytesSelfy?.size!!)
            imgUserSelfy.setImageBitmap(decodedImageSelfy)
            tvAddress.text = AddressEnglish
            tvEmail.text = if(EmailInfo?.isNotBlank()!!) EmailInfo else "---"
            tvPhone.text =  if(PhoneInfo?.isNotBlank()!!) PhoneInfo else "---"
            tvAadhaar.text = AadharNo
            tvName.text = UserName
            tvDob.text = DOB
            tvGender.text = Gender

            yesBtn.setOnClickListener {
                updateIdData(sharedPrefOBJ)
            dialog.dismiss()
        }

        }
//        val body = dialog.findViewById(R.id.body) as TextView
//        body.text = title
//        val yesBtn = dialog.findViewById(R.id.yesBtn) as Button
//        val noBtn = dialog.findViewById(R.id.noBtn) as TextView
//        noBtn.setOnClickListener { dialog.dismiss() }
            dialog.show()
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
                    photoFile   = Compressor(activity).compressToFile(photoFile)
                }, 1000)


            }
            else
            {
                Toast.makeText(activity!!,"Retake Photo", Toast.LENGTH_SHORT).show()
            }

        }
        catch (exp:Exception)
        {
            Toast.makeText(activity!!,"Retake Photo", Toast.LENGTH_SHORT).show()
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
                            else ->origin         //origin.rotate(270f)
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

    override fun onSucessInit(response: ZoopInitResponse) {
        if (progressDialog.isShowing){
            progressDialog.dismiss()
        }
        sharedPrefOBJ.zoopGatewayId = response.id   //setting zoop trans id to pref.
        openOfflineAadhaarActivity()
       // updateIdData(sharedPrefOBJ)
    }

    private fun openOfflineAadhaarActivity() {
        val gatewayIntent =  Intent(activity!!, ZoopConsentActivity::class.java)
        gatewayIntent.putExtra(ZOOP_TRANSACTION_ID, sharedPrefOBJ.zoopGatewayId)
        gatewayIntent.putExtra(ZOOP_BASE_URL, "preprod.aadhaarapi.com")
       // gatewayIntent.putExtra(ZOOP_EMAIL, Email) //not mandatory
       // gatewayIntent.putExtra(ZOOP_UID, 238114456672); //not mandatory
     //   gatewayIntent.putExtra(ZOOP_PHONE, phone); //not mandatory
        gatewayIntent.putExtra(ZOOP_IS_ASSIST_MODE_ONLY, true) //not mandatory
        gatewayIntent.putExtra(ZOOP_REQUEST_TYPE, OFFLINE_AADHAAR)
        startActivityForResult(gatewayIntent, REQUEST_AADHAARAPI)
    }

    override fun onFailureInit(response: ZoopInitFailureResponse) {
        if (progressDialog.isShowing){
            progressDialog.dismiss()
        }
        Toast.makeText(activity!!,response.message,Toast.LENGTH_LONG).show()
    }

    override fun onUnknownErr(mgs: String) {
        if (progressDialog.isShowing){
            progressDialog.dismiss()
        }
        Toast.makeText(activity!!,mgs,Toast.LENGTH_LONG).show()
    }

}