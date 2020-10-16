package com.app.l_pesa.profile.view

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.l_pesa.BuildConfig
import com.app.l_pesa.R
import com.app.l_pesa.common.CommonMethod
import com.app.l_pesa.common.SharedPref
import com.app.l_pesa.dashboard.model.ResDashboard
import com.app.l_pesa.dashboard.view.DashboardActivity
import com.app.l_pesa.loanplan.adapter.PersonalIdAdapter
import com.app.l_pesa.profile.inter.ICallBackClickPersonalId
import com.app.l_pesa.profile.model.ResUserInfo
import com.facebook.appevents.AppEventsConstants
import com.facebook.appevents.AppEventsLogger
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_profile_edit_id_info.toolbar
import kotlinx.android.synthetic.main.activity_profile_edit_statement_info.*
import kotlinx.android.synthetic.main.add_statement_bottomsheet_layout.view.*
import java.io.File
import java.net.URI
import java.util.ArrayList

lateinit var captureFilePath: Uri
private lateinit var sharedPref:SharedPref
class ProfileEditStatementInfoActivity : AppCompatActivity(), ICallBackClickPersonalId {
    lateinit var listPersonalIdTEst:ArrayList<ResUserInfo.UserIdsPersonalInfo>
    private lateinit var personalIdAdapter:PersonalIdAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_edit_statement_info)
        toolbar.title = "Statements"
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbarFont(this)
        sharedPref= SharedPref(this)
        initData()
        onActionPerform()
    }

    private fun initData() {
        captureFilePath = Uri.EMPTY
        val logger = AppEventsLogger.newLogger(this)
        val params = Bundle()
        params.putString(AppEventsConstants.EVENT_PARAM_CONTENT_TYPE, "Personal Id Information")
        logger.logEvent(AppEventsConstants.EVENT_NAME_VIEWED_CONTENT, params)

        listPersonalIdTEst = ArrayList()
        val sharedPrefOBJ = SharedPref(this)
        val profileInfo = Gson().fromJson<ResUserInfo.Data>(sharedPrefOBJ.profileInfo, ResUserInfo.Data::class.java)
        listPersonalIdTEst.clear()

        if (profileInfo.userIdsPersonalInfo!!.size > 0) {
            cvNoItm.visibility = View.GONE
            listPersonalIdTEst.addAll(profileInfo.userIdsPersonalInfo!!)
            personalIdAdapter = PersonalIdAdapter(this, listPersonalIdTEst, this)
            rlStatements.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
            rlStatements.adapter = personalIdAdapter
        }else{
            cvNoItm.visibility = View.VISIBLE
        }
    }
    private fun onActionPerform() {
        toolbar.setNavigationOnClickListener {
            onBackPressed()
            overridePendingTransition(R.anim.left_in, R.anim.right_out)
        }

        imgAdd.setOnClickListener {
            val bottomSheetDialog =  AddStatementBottomsheet(this)
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
                val bottomSheetDialog =  AddStatementBottomsheet(this)
                captureFilePath = Uri.EMPTY
                bottomSheetDialog.show(supportFragmentManager, "bottom_sheet_statement")
                return true
            }

            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        if (requestCode == 165) {
            if (resultCode == RESULT_OK) {
                val uri = intent?.data
               // val m_data = data
                Log.d("Hello", captureFilePath.path!!)
         //       val fileImagePath = getRealPathFromURI(uri!!)
             //   val type = intent.type

            //    Log.d("Hello", fileImagePath + "");
//                if (uri != null) {
//                    var path = uri.toString()
//                    if (path.toLowerCase().startsWith("file://")) {
//                        // Selected file/directory path is below
//                        path = ( File(URI.create(path))).absolutePath
//                        Log.d("Hel", path)
//                    }
//
//                }
            } else{
                Log.d("Hello", "Back from pick with cancel status");
        }
    }
    }

    override fun onClickIdList(userIdsPersonalInfo: ResUserInfo.UserIdsPersonalInfo, position: Int, it: View) {
        TODO("Not yet implemented")
    }

    override fun onSelectIdType(id: Int, name: String, type: String) {
        TODO("Not yet implemented")
    }



}

class AddStatementBottomsheet(activity: Activity) : BottomSheetDialogFragment() {

    private val activity = activity

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
         super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.add_statement_bottomsheet_layout, container, false)
        onActionPerform(view)
        loadData(view)
        return view
    }

    private fun loadData(v: View) {
        val typs = ArrayList<String>()
        val userDashBoard  = Gson().fromJson<ResDashboard.Data>(sharedPref.userDashBoard, ResDashboard.Data::class.java)
        if(userDashBoard.personalIdTypes!!.size>0)
        {
            for(itm in userDashBoard.personalIdTypes!!){
                typs.add(itm.name)
            }
        }
        v.spStType.adapter = ArrayAdapter<String>(activity,android.R.layout.simple_spinner_dropdown_item,typs)
    }

    fun onActionPerform(v:View){
        v.btnBrowse.setOnClickListener {
            openPDF(activity)
            v.tvFileName.text = "statement.pdf"
        }
        v.buttonCancel.setOnClickListener {
            dismiss()
        }
        v.buttonSubmit.setOnClickListener {
            if(captureFilePath == Uri.EMPTY) {
                //CommonMethod.customSnackBarError(v.rootView,activity,"Please Upload PDF statement!")
                showErrText("Please Upload PDF statement!")
            } else if (v.ilIdNumber.editText?.text?.isEmpty()!!){
                showErrText("Please enter duration period!")
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
}

private fun openPDF(activity: Activity) {

    val filePath = File(activity.filesDir, "images")
    val pdfFile = File(filePath, "statement.pdf")
    if (pdfFile.exists()) {
        pdfFile.delete()
    } else {
        pdfFile.parentFile!!.mkdirs()
    }
    captureFilePath = FileProvider.getUriForFile(activity.baseContext, BuildConfig.APPLICATION_ID + ".provider", pdfFile)
    val intent =  Intent(Intent.ACTION_GET_CONTENT)
    intent.type = "application/pdf"
   // intent.flags = Intent.FLAG_ACTIVITY_NO_HISTORY
   // intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
   // intent.addCategory(Intent.CATEGORY_OPENABLE)
    intent.data = captureFilePath
    activity.startActivityForResult(intent, 165)



//    val intent = Intent(Intent.ACTION_GET_CONTENT);
//    intent.addCategory(Intent.CATEGORY_OPENABLE);
//
//    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//        intent.setType(mimeTypes.length == 1 ? mimeTypes[0] : "*/*");
//        if (mimeTypes.length > 0) {
//            intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
//        }
//    } else {
//        String mimeTypesStr = "";
//        for (String mimeType : mimeTypes) {
//            mimeTypesStr += mimeType + "|";
//        }
//        intent.setType(mimeTypesStr.substring(0,mimeTypesStr.length() - 1));
//    }
//    startActivityForResult(Intent.createChooser(intent,"ChooseFile"), REQUEST_CODE_DOC);

}

