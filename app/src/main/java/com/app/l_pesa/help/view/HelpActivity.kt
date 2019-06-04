package com.app.l_pesa.help.view

import android.annotation.SuppressLint
import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import com.app.l_pesa.R
import com.app.l_pesa.common.SharedPref
import com.app.l_pesa.help.inter.ICallBackHelp
import com.app.l_pesa.help.model.HelpData
import com.app.l_pesa.help.presenter.PresenterHelp
import com.app.l_pesa.main.MainActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.kaopiz.kprogresshud.KProgressHUD

import kotlinx.android.synthetic.main.activity_help.*
import kotlinx.android.synthetic.main.content_help.*

class HelpActivity : AppCompatActivity(), ICallBackHelp {

    private lateinit var progressDialog                : KProgressHUD

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_help)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbarFont(this@HelpActivity)

        initLoader()
        initData()

    }

    @SuppressLint("SetTextI18n")
    private fun initData()
    {
        val sharedPrefOBJ= SharedPref(this@HelpActivity)
        txtCountry.text = ""+sharedPrefOBJ.countryName

        val options = RequestOptions()
        Glide.with(this@HelpActivity)
                .load(sharedPrefOBJ.countryFlag)
                .apply(options)
                .into(imgCountry)

        progressDialog.show()
        val presenterHelp= PresenterHelp()
        presenterHelp.getHelp(this@HelpActivity,this)
    }

    private fun initLoader()
    {
        progressDialog= KProgressHUD.create(this@HelpActivity)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setCancellable(false)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f)

    }

    private fun dismiss()
    {
        if(progressDialog.isShowing)
        {
            progressDialog.dismiss()
        }
    }


    override fun onSuccessHelp(data: HelpData) {

        txtPhone.text = data.support_contact_no
        txtEmail.text = data.support_email

        dismiss()

        txtPhone.setOnClickListener {
            if(!TextUtils.isEmpty(data.support_contact_no))
            {
                val intent = Intent(Intent.ACTION_DIAL)
                intent.data = Uri.parse("tel:"+data.support_contact_no)
                startActivity(intent)
            }

        }
    }

    override fun onErrorHelp(message: String) {

        dismiss()
        Toast.makeText(this@HelpActivity,message,Toast.LENGTH_LONG).show()
    }

    override fun onSessionTimeOut(message: String) {

        dismiss()
        val dialogBuilder = AlertDialog.Builder(this@HelpActivity)
        dialogBuilder.setMessage(message)
                .setCancelable(false)
                .setPositiveButton("Ok", DialogInterface.OnClickListener {
                    dialog, _ ->
                    dialog.dismiss()
                    val sharedPrefOBJ= SharedPref(this@HelpActivity)
                    sharedPrefOBJ.removeShared()
                    startActivity(Intent(this@HelpActivity, MainActivity::class.java))
                    overridePendingTransition(R.anim.right_in, R.anim.left_out)
                    finish()
                })

        val alert = dialogBuilder.create()
        alert.setTitle(resources.getString(R.string.app_name))
        alert.show()

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
