package com.app.l_pesa.login.view

import android.app.Activity
import android.graphics.Typeface
import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity;
import com.app.l_pesa.R
import com.app.l_pesa.common.CommonMethod

import kotlinx.android.synthetic.main.activity_email_required.*

class EmailRequiredActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_email_required)
        setSupportActionBar(toolbar)
        toolbarFont(this@EmailRequiredActivity)


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
        /*super.onBackPressed()
        overridePendingTransition(R.anim.left_in, R.anim.right_out)*/
    }
}
