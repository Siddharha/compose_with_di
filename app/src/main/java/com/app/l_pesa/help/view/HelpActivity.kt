package com.app.l_pesa.help.view

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.text.TextUtils
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.app.l_pesa.R
import com.app.l_pesa.common.CommonMethod
import com.app.l_pesa.common.SharedPref
import com.app.l_pesa.help.model.HelpData
import com.app.l_pesa.main.view.MainActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_help.*
import kotlinx.android.synthetic.main.content_help.*


class HelpActivity : AppCompatActivity() {

    private lateinit var countDownTimer          : CountDownTimer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_help)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbarFont(this@HelpActivity)

        initData()
        initTimer()

    }


    fun onSessionTimeOut(message: String) {

        val dialogBuilder = AlertDialog.Builder(this@HelpActivity,R.style.MyAlertDialogTheme)
        dialogBuilder.setMessage(message)
                .setCancelable(false)
                .setPositiveButton("Ok") { dialog, _ ->
                    dialog.dismiss()
                    val sharedPrefOBJ= SharedPref(this@HelpActivity)
                    sharedPrefOBJ.removeShared()
                    startActivity(Intent(this@HelpActivity, MainActivity::class.java))
                    overridePendingTransition(R.anim.right_in, R.anim.left_out)
                    finish()
                }

        val alert = dialogBuilder.create()
        alert.setTitle(resources.getString(R.string.app_name))
        alert.show()

    }

    @SuppressLint("SetTextI18n")
    private fun initData()
    {
        val sharedPrefOBJ= SharedPref(this@HelpActivity)
        txtCountry.text = sharedPrefOBJ.countryName


        val options = RequestOptions()
        Glide.with(this@HelpActivity)
                .load(sharedPrefOBJ.countryFlag)
                .apply(options)
                .into(imgCountry)


        val helpData = Gson().fromJson<HelpData>(sharedPrefOBJ.helpSupport, HelpData::class.java)


        if(!TextUtils.isEmpty(helpData.support_contact_no))
        {
            txtPhone.text = helpData.support_contact_no
        }
        else{
            txtPhone.text = resources.getString(R.string.not_available)
        }

        if(!TextUtils.isEmpty(helpData.support_email))
        {
            txtEmail.text = helpData.support_email
        }
        else{
            txtEmail.text = resources.getString(R.string.not_available)
        }

        if(!TextUtils.isEmpty(helpData.support_telegram_url))
        {
            txtTelegram.text = resources.getString(R.string.telegram)
        }
        else{
            txtTelegram.text = resources.getString(R.string.telegram)+" "+resources.getString(R.string.not_available)
        }

        if(!TextUtils.isEmpty(helpData.support_whatsapp_no))
        {
            txtWhatsApp.text = resources.getString(R.string.whats_app)
        }
        else{
            txtWhatsApp.text = resources.getString(R.string.whats_app)+" "+resources.getString(R.string.not_available)
        }

        txtPhone.setOnClickListener {
            if(!TextUtils.isEmpty(helpData.support_contact_no))
            {
                val intent = Intent(Intent.ACTION_DIAL)
                intent.data = Uri.parse("tel:"+helpData.support_contact_no)
                startActivity(intent)
            }

        }

        txtWhatsApp.setOnClickListener {
            if(!TextUtils.isEmpty(helpData.support_whatsapp_no))
            {
                try {
                    val url = "https://api.whatsapp.com/send?phone=$"+helpData.support_whatsapp_no
                    val pm = packageManager
                    pm.getPackageInfo("com.whatsapp", PackageManager.GET_ACTIVITIES)
                    val i = Intent(Intent.ACTION_VIEW)
                    i.data = Uri.parse(url)
                    startActivity(i)
                } catch (e: PackageManager.NameNotFoundException) {
                    Toast.makeText(this@HelpActivity, "Whatsapp app not installed in your phone", Toast.LENGTH_SHORT).show()
                    e.printStackTrace()
                }

            }

        }

        txtTelegram.setOnClickListener {
            if(!TextUtils.isEmpty(helpData.support_telegram_url))
            {
                try {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(helpData.support_telegram_url))
                    startActivity(intent)
                } catch (e: PackageManager.NameNotFoundException) {
                    Toast.makeText(this@HelpActivity, "Telegram app not installed in your phone", Toast.LENGTH_SHORT).show()
                    e.printStackTrace()
                }

            }

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

    public override fun onDestroy() {
       super.onDestroy()
    }

    private fun initTimer() {

        countDownTimer= object : CountDownTimer(CommonMethod.sessionTime().toLong(), 1000) {
            override fun onTick(millisUntilFinished: Long) {

            }
            override fun onFinish() {
                onSessionTimeOut(resources.getString(R.string.session_time_out))
                countDownTimer.cancel()

            }}
        countDownTimer.start()

    }


    override fun onUserInteraction() {
        super.onUserInteraction()

        countDownTimer.cancel()
        countDownTimer.start()
    }


    public override fun onStop() {
        super.onStop()
        countDownTimer.cancel()

    }

}
