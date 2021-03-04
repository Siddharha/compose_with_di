package com.app.l_pesa.settings.view

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import com.app.l_pesa.R
import com.app.l_pesa.common.CommonMethod
import com.app.l_pesa.common.SharedPref
import kotlinx.android.synthetic.main.activity_appearance_account.toolbar
import kotlinx.android.synthetic.main.content_appreance_account.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class AppearanceAccountActivity : AppCompatActivity() {
    lateinit var pref: SharedPref
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_appearance_account)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbarFont(this@AppearanceAccountActivity)
        initialize()
        onActionPerform()
    }

    private fun initialize(){
        pref = SharedPref(this)

        swDark.isChecked = pref.isDarkTheme
        swDark.setSwitchTypeface(Typeface.createFromAsset(this.assets, "fonts/Montserrat-Regular.ttf"))
    }
    private fun onActionPerform() {
        swDark.setOnCheckedChangeListener { buttonView, isChecked ->

            doAsync {
                if (isChecked){
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                }else{
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                }
                pref.isDarkTheme = isChecked
                uiThread {
                    showThemeChangeDialog()
                    //onBackPressed()
                }
            }

            //AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            //finish()
            ///startActivity(Intent(this,AppearanceAccountActivity::class.java))
        }
    }

    private fun showThemeChangeDialog() {
        val dialog = AlertDialog.Builder(this)
        dialog.setMessage("To Apply Theme correctly, you need to go to the dashboard")
        dialog.setOnCancelListener {
            it.dismiss()
        }
        dialog.setPositiveButton("Yes"){d,_->
            onBackPressed()
            d.dismiss()
        }
                .create().show()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.left_in, R.anim.right_out)
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

    private fun hideKeyboard()
    {

        try {
            CommonMethod.hideKeyboardView(this@AppearanceAccountActivity)
        } catch (exp: Exception) {

        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                hideKeyboard()
                onBackPressed()
                overridePendingTransition(R.anim.left_in, R.anim.right_out)
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

}

