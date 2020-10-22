package com.app.l_pesa.profile.view

import android.app.Activity
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.TextUtils
import android.text.style.RelativeSizeSpan
import android.view.*
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.l_pesa.BuildConfig
import com.app.l_pesa.R
import com.app.l_pesa.common.CommonMethod
import com.app.l_pesa.common.CommonMethod.customSnackBarError
import com.app.l_pesa.common.CustomTypeFaceSpan
import com.app.l_pesa.common.DocumentUtils
import com.app.l_pesa.common.SharedPref
import com.app.l_pesa.dashboard.view.DashboardActivity
import com.app.l_pesa.profile.adapter.StatementListAdapter
import com.app.l_pesa.profile.inter.ICallBackStatement
import com.app.l_pesa.profile.inter.ICallBackStatementDelete
import com.app.l_pesa.profile.inter.ICallBackStatementUpload
import com.app.l_pesa.profile.model.statement.StatementListResponse
import com.app.l_pesa.profile.model.statement.StatementTypeResponse
import com.app.l_pesa.profile.presenter.*
import com.facebook.appevents.AppEventsConstants
import com.facebook.appevents.AppEventsLogger
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.activity_profile_edit_id_info.toolbar
import kotlinx.android.synthetic.main.activity_profile_edit_statement_info.*
import kotlinx.android.synthetic.main.add_statement_bottomsheet_layout.*
import kotlinx.android.synthetic.main.add_statement_bottomsheet_layout.view.*
import java.io.File
import java.util.ArrayList

val FILE_REQUEST_CODE = 1001

//lateinit var captureFilePath: Uri
 var pdfFile:File?=null
lateinit var progressDialog:ProgressDialog
private lateinit var sharedPref:SharedPref
lateinit var statementTyps : ArrayList<String>
lateinit var statementTypIds : ArrayList<Int>
lateinit var  presenterStatement: PresenterStatement
lateinit var statementList:ArrayList<StatementListResponse.Data>
private lateinit var statementListAdapter: StatementListAdapter
lateinit var bottomSheetDialog:AddStatementBottomsheet
class ProfileEditStatementInfoActivity : AppCompatActivity(), ICallBackStatement, ICallBackStatementDelete {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_edit_statement_info)
        toolbar.title = "Statements"
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbarFont(this)
        presenterStatement = PresenterStatement()
        sharedPref= SharedPref(this)
        statementTyps = ArrayList()
        statementTypIds = ArrayList()
        initLoader()
        initData()
        onActionPerform()
    }

    private fun initLoader()
    {
        bottomSheetDialog =  AddStatementBottomsheet(this)

        progressDialog = ProgressDialog(this,R.style.MyAlertDialogStyle)
        val message=   SpannableString(resources.getString(R.string.loading))
        val face = Typeface.createFromAsset(this.assets, "fonts/Montserrat-Regular.ttf")
        message.setSpan(RelativeSizeSpan(1.0f), 0, message.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        message.setSpan(CustomTypeFaceSpan("", face!!, Color.parseColor("#535559")), 0, message.length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
        progressDialog.isIndeterminate = true
        progressDialog.setMessage(message)
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        progressDialog.setCancelable(false)
        progressDialog.setCanceledOnTouchOutside(false)

    }

    private fun initData() {
       // captureFilePath = Uri.EMPTY

        val logger = AppEventsLogger.newLogger(this)
        val params = Bundle()
        params.putString(AppEventsConstants.EVENT_PARAM_CONTENT_TYPE, "Personal Id Information")
        logger.logEvent(AppEventsConstants.EVENT_NAME_VIEWED_CONTENT, params)

        statementList = ArrayList()
        //val sharedPrefOBJ = SharedPref(this)
       // val profileInfo = Gson().fromJson<ResUserInfo.Data>(sharedPrefOBJ.profileInfo, ResUserInfo.Data::class.java)
        statementList.clear()

      //  if (profileInfo.userIdsPersonalInfo!!.size > 0) {
            cvNoItm.visibility = View.GONE
            //statementList.addAll(profileInfo.userIdsPersonalInfo!!)
            statementListAdapter = StatementListAdapter(statementList, this, R.layout.statement_list_cell)
            rlStatements.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
            rlStatements.adapter = statementListAdapter
//        }else{
//            cvNoItm.visibility = View.VISIBLE
//        }

        //load data from API ----//
        if(CommonMethod.isNetworkAvailable(this)) {
            presenterStatement.doGetStatementType(this, this)
            progressDialog.show()
        }else{
            val dialog = AlertDialog.Builder(this,R.style.MyAlertDialogStyle)
            dialog.setMessage(resources.getString(R.string.no_internet))
            dialog.setNegativeButton("Dismiss"){d,_->
                onBackPressed()
            }
            dialog.setCancelable(false)
            dialog.show()
            CommonMethod.customSnackBarError(rootView,this,resources.getString(R.string.no_internet))
        }
        //------End-----------//

    }
    private fun onActionPerform() {
        toolbar.setNavigationOnClickListener {
            onBackPressed()
            overridePendingTransition(R.anim.left_in, R.anim.right_out)
        }

        imgAdd.setOnClickListener {
            bottomSheetDialog.show(supportFragmentManager, "bottom_sheet_statement")
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

    override fun onBackPressed() {


        if(sharedPref.profileUpdate==resources.getString(R.string.status_true))
        {
            sharedPref.navigationTab=resources.getString(R.string.open_tab_profile)
            val intent = Intent(this, DashboardActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.left_in, R.anim.right_out)
        }
        else
        {
            super.onBackPressed()
            overridePendingTransition(R.anim.left_in, R.anim.right_out)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        //return super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.menu_statement_add, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.mnuAdd ->{

                bottomSheetDialog.show(supportFragmentManager, "bottom_sheet_statement")
                return true
            }

            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onSuccessGetStatementType(statementTypes: List<StatementTypeResponse.Data.StatementType>) {
        if(statementTypes.isNotEmpty())
        {
            statementTyps.clear()
            statementTypIds.clear()
            for(itm in statementTypes){
                statementTyps.add(itm.typeName)
                statementTypIds.add(itm.id)
            }

            callStatementListAPI()
        }
    }

    private fun callStatementListAPI() {
        presenterStatement.doGetStatementList(this,this)
    }

    override fun onFailureGetStatementType(message: String) {

    }

    override fun onSuccessGetStatementList(list: List<StatementListResponse.Data>) {

        if(progressDialog.isShowing){
            progressDialog.dismiss()
        }

        statementList.clear()
        statementList.addAll(list)

        if(statementList.isEmpty()){
            cvNoItm.visibility = View.VISIBLE
        }else{
            cvNoItm.visibility = View.GONE
        }
        statementListAdapter.notifyDataSetChanged()

    }

    override fun onFailureGetStatementList(message: String) {
        if(progressDialog.isShowing){
            progressDialog.dismiss()
        }
    }

    override fun onSessionTimeOut(message: String) {
        if(progressDialog.isShowing){
            progressDialog.dismiss()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
//                101 -> {
//                    data?.data?.also { uri ->
//                        Log.i(TAG, "Uri: $uri")
//                        baseAdapter?.add(ImageArray(null, null, uri.toString()))
//                    }
//                }
                111 -> {
                    data?.data?.also { documentUri ->
                        contentResolver?.takePersistableUriPermission(
                                documentUri,
                                Intent.FLAG_GRANT_READ_URI_PERMISSION
                        )
                         pdfFile = DocumentUtils.getFile(this,documentUri)//use pdf as file
                        bottomSheetDialog.tvFileName.text = pdfFile?.name
                    }
                }
            }

        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    fun listPopup(context:Context,view:View,itm:StatementListResponse.Data) {
        val popup =  PopupMenu(context, view)

        popup.menuInflater.inflate(R.menu.statement_popup_menu, popup.menu)

        if(itm.verified==1){
            popup.menu.getItem(0).isVisible = false
        }

        popup.menu.getItem(0).setOnMenuItemClickListener {
            if(CommonMethod.isNetworkAvailable(this))
            {
                val alertDialog = AlertDialog.Builder(this,R.style.MyAlertDialogTheme)
                alertDialog.setTitle(resources.getString(R.string.app_name))
                alertDialog.setMessage("Are you sure to delete this statement?")
                alertDialog.setPositiveButton("Delete") { _, _ ->
                    progressDialog.show()
                    val presenterAWSStatement = PresenterAWSStatement()
                    presenterAWSStatement.deleteStatementAWS(this,this,itm.fileName,itm.id)
                }
                alertDialog.setNegativeButton("Dismiss"){dialod,_->
                    dialod.dismiss()
                }

                alertDialog.show()
            }else{
                CommonMethod.customSnackBarError(rlStatements,this,resources.getString(R.string.no_internet))
            }


           return@setOnMenuItemClickListener true
        }
        popup.menu.getItem(1).setOnMenuItemClickListener {
            showPdf(itm.fileName)
            return@setOnMenuItemClickListener true
        }
        popup.show()
    }

    private fun showPdf(fileName: String) {
        if(!TextUtils.isEmpty(fileName))
        {
            if(CommonMethod.isNetworkAvailable(this))
            {
//                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(BuildConfig.BUSINESS_IMAGE_URL+fileName))
//                startActivity(browserIntent)

                val intent =  Intent(Intent.ACTION_VIEW)

                intent.type = "application/pdf"
                intent.data = Uri.parse(BuildConfig.STATEMENT_PDF_URL+fileName)
                startActivity(intent)
            }


        }
    }

    private fun callDeleteAPI(itemId: Int) {


        val presenterDeleteStatement = PresenterDeleteStatement()
        val jsonObject = JsonObject()
        jsonObject.addProperty("user_statement_id",itemId.toString())
        // Static
        presenterDeleteStatement.doDeleteStatement(this,jsonObject,this)
        //Toast.makeText(this,"Not Implemented yet!",Toast.LENGTH_LONG).show()

    }

    override fun onSuccessStatementDelete() {
        if(progressDialog.isShowing){
            progressDialog.dismiss()
        }
        presenterStatement.doGetStatementType(this,this)
        Toast.makeText(this,"Statement Deleted",Toast.LENGTH_LONG).show()
        sharedPref.profileUpdate=resources.getString(R.string.status_true)
    }

    override fun onFailureStatementDelete(string: String) {
        if(progressDialog.isShowing){
            progressDialog.dismiss()
        }
        Toast.makeText(this,string,Toast.LENGTH_LONG).show()
    }

    override fun isFailureStatementDelete(bool: Boolean) {
        if(progressDialog.isShowing){
            progressDialog.dismiss()
        }
        Toast.makeText(this,"Unable to delete",Toast.LENGTH_LONG).show()
    }

    override fun onDeleteTimeOut(string: String) {
        if(progressDialog.isShowing){
            progressDialog.dismiss()
        }
        Toast.makeText(this,string,Toast.LENGTH_LONG).show()
    }

    override fun onSuccessDeleteAWS(id:Int) {

        runOnUiThread {
            callDeleteAPI(id)
        }

    }

    override fun onFailureDeleteAWS(string: String) {
        if(progressDialog.isShowing){
            progressDialog.dismiss()
        }
       customSnackBarError(window.decorView,this,string)
    }
}

class AddStatementBottomsheet(activity: Activity) : BottomSheetDialogFragment(), ICallBackStatementUpload {

    private val activity = activity
    lateinit var _selectedTypeId:String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
         super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.add_statement_bottomsheet_layout, container, false)

        onActionPerform(view)
        loadData(view)
        return view
    }

    private fun loadData(v:View) {
        v.spStType.adapter = ArrayAdapter<String>(activity,android.R.layout.simple_spinner_dropdown_item,statementTyps)

    }

    fun onActionPerform(v:View){
        v.btnBrowse.setOnClickListener {
            openPDF(activity)
            //v.tvFileName.text = pdfFile.name
        }
        v.buttonCancel.setOnClickListener {
            dismiss()
        }
        v.buttonSubmit.setOnClickListener {

            if(CommonMethod.isNetworkAvailable(activity)){

                try{
                    if(pdfFile !=null && v.tvFileName.text.isNotEmpty()){
                        if(!pdfFile?.isFile!!) {
                            //CommonMethod.customSnackBarError(v.rootView,activity,"Please Upload PDF statement!")
                            showErrText("Please Upload PDF statement!")
                        }else if(v.etDocNo.text?.isEmpty()!!){
                            showErrText("Please enter Document Number period!")
                        }
                        else if (v.ilIdNumber.editText?.text?.isEmpty()!!){
                            showErrText("Please enter duration period!")
                        } else{
                            progressDialog.show()
                            val presenterAWSStatement= PresenterAWSStatement()
                            presenterAWSStatement.uploadStatementFile(activity,this,pdfFile)
                        }
                    }else{
                        showErrText("Please Upload PDF statement!")
                    }

                }catch (e:Exception){
                    e.printStackTrace()
                }
            }else{
                //customSnackBarError(rlStatements,this,resources.getString(R.string.no_internet))
                showErrText(resources.getString(R.string.no_internet))
            }

        }

        v.spStType.onItemSelectedListener = object :AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                _selectedTypeId = statementTypIds[position].toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }
    }

    private fun showErrText(s:String) {
        val t = Toast.makeText(activity, s, Toast.LENGTH_LONG)
        t.view.setBackgroundColor(Color.RED)
        t.view.setPadding(10, 0, 10, 0)
        val ttv = t.view.findViewById<TextView>(android.R.id.message)
        ttv.setTextColor(Color.WHITE)
        ttv.setPadding(5, 5, 5, 5)
        t.show()
    }

    override fun onSuccessUploadAWS(url: String) {
//        val statementAddPayload = StatementAddPayload("1650763",url,"",_selectedTypeId)
//
        val presenterAddStatement = PresenterAddStatement()
val jsonObject = JsonObject()
        jsonObject.addProperty("type_id",_selectedTypeId) // Static
        jsonObject.addProperty("file_name",url)
        jsonObject.addProperty("document_number", bottomSheetDialog.etDocNo.text.toString())
        jsonObject.addProperty("period", bottomSheetDialog.etStatementPeriod.text.toString())
//        if(etPersonalId.text.toString()==resources.getString(R.string.address_prof))
//        {
//            jsonObject.addProperty("id_number","")
//        }
//        else
//        {
//            jsonObject.addProperty("id_number",etIdNumber.text.toString())
//        }
//
//        jsonObject.addProperty("type_name","Personal")
//
//        val presenterAddProof= PresenterAddProof()
//        presenterAddProof.doAddProof(activity!!,jsonObject,this)
        presenterAddStatement.doAddStatement(activity,jsonObject,this)
    }

    override fun onFailureUploadAWS(string: String) {
       if(progressDialog.isShowing){
           progressDialog.dismiss()
       }

    }

    override fun onProgressUploadAWS(progress: Int) {

    }

    override fun onSucessUploadStatement() {
        if(progressDialog.isShowing){
            progressDialog.dismiss()
        }

        val msg = "File added"
        presenterStatement.doGetStatementList(activity,activity as ProfileEditStatementInfoActivity)
        sharedPref.profileUpdate=resources.getString(R.string.status_true)
        dismiss()
    }

    override fun onFailureUploadStatement(string: String) {
        if(progressDialog.isShowing){
            progressDialog.dismiss()
        }

        showErrText(string)
        //customSnackBarError(contextView,activity,string)
    }

    override fun onUploadTimeOut(string: String) {
        if(progressDialog.isShowing){
            progressDialog.dismiss()
        }
        val msg = string
       showErrText(string)
    }

}

private fun openPDF(activity: Activity) {
    val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
        type = "application/pdf"
        addCategory(Intent.CATEGORY_OPENABLE)
        flags = flags or Intent.FLAG_GRANT_READ_URI_PERMISSION
    }
    activity.startActivityForResult(intent, 111)
}



