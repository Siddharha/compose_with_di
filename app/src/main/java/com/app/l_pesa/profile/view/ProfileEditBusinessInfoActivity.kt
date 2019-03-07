package com.app.l_pesa.profile.view

import android.app.Activity
import android.graphics.Typeface
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.app.l_pesa.R
import com.app.l_pesa.common.CommonTextRegular
import com.app.l_pesa.common.SharedPref
import com.app.l_pesa.profile.model.ResUserInfo
import com.google.gson.Gson

import kotlinx.android.synthetic.main.activity_profile_edit_business_info.*

class ProfileEditBusinessInfoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_edit_business_info)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbarFont(this@ProfileEditBusinessInfoActivity)

        val sharedPrefOBJ= SharedPref(this@ProfileEditBusinessInfoActivity)
        val profileData = Gson().fromJson<ResUserInfo.Data>(sharedPrefOBJ.profileInfo, ResUserInfo.Data::class.java)

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


    private fun customSnackBarError(view: View, message:String) {

        val snackBarOBJ = Snackbar.make(view, "", Snackbar.LENGTH_SHORT)
        snackBarOBJ.view.setBackgroundColor(ContextCompat.getColor(this@ProfileEditBusinessInfoActivity,R.color.colorRed))
        (snackBarOBJ.view as ViewGroup).removeAllViews()
        val customView = LayoutInflater.from(this@ProfileEditBusinessInfoActivity).inflate(R.layout.snackbar_error, null)
        (snackBarOBJ.view as ViewGroup).addView(customView)
        val txtTitle=customView.findViewById(R.id.txtTitle) as CommonTextRegular
        txtTitle.text = message

        snackBarOBJ.show()
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {

               /* if(swipeRefreshLayout.isRefreshing)
                {
                    CommonMethod.customSnackBarError(llRoot,this@ProfileEditEmpInfoActivity,resources.getString(R.string.please_wait))
                }
                else
                {*/
                    onBackPressed()
                    overridePendingTransition(R.anim.left_in, R.anim.right_out)
                //}
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
