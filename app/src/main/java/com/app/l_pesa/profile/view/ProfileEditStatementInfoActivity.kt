package com.app.l_pesa.profile.view

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.*
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.FileProvider
import com.app.l_pesa.BuildConfig
import com.app.l_pesa.R
import com.app.l_pesa.common.SharedPref
import com.app.l_pesa.dashboard.view.DashboardActivity
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.activity_profile_edit_id_info.toolbar
import kotlinx.android.synthetic.main.activity_profile_edit_statement_info.*
import kotlinx.android.synthetic.main.add_statement_bottomsheet_layout.view.*
import java.io.File

class ProfileEditStatementInfoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_edit_statement_info)
        toolbar.title = "Upload Statements"
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbarFont(this)

        onActionPerform()
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

        val sharedPref= SharedPref(this)
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
                bottomSheetDialog.show(supportFragmentManager, "bottom_sheet_statement")
                return true
            }

            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)


    }
}

class AddStatementBottomsheet(activity: Activity) : BottomSheetDialogFragment() {

    private val activity = activity

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
         super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.add_statement_bottomsheet_layout, container, false)
        onActionPerform(view)
        return view
    }

    fun onActionPerform(v:View){
        v.btnBrowse.setOnClickListener {
            openPDF(activity)
        }
        v.buttonCancel.setOnClickListener {
            dismiss()
        }
        v.buttonSubmit.setOnClickListener {

        }
    }
}

private fun openPDF(activity: Activity) {
     lateinit var captureFilePath: Uri
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
    intent.data = captureFilePath
    activity.startActivityForResult(intent, 100)
}