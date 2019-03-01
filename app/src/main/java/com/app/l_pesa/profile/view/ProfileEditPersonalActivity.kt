package com.app.l_pesa.profile.view

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Typeface
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import com.app.l_pesa.R
import com.app.l_pesa.common.SharedPref
import com.app.l_pesa.profile.model.ResUserInfo
import com.google.gson.Gson

import kotlinx.android.synthetic.main.activity_profile_edit_personal.*
import kotlinx.android.synthetic.main.content_profile_edit_personal.*

class ProfileEditPersonalActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_edit_personal)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbarFont(this@ProfileEditPersonalActivity)

        initData()

    }

    @SuppressLint("SetTextI18n")
    private fun initData()
    {
        val sharedPrefOBJ= SharedPref(this@ProfileEditPersonalActivity)
        val profileData = Gson().fromJson<ResUserInfo.Data>(sharedPrefOBJ.profileInfo, ResUserInfo.Data::class.java)

       // txtTitle.setText(profileData.userPersonalInfo.title)
        etNameF.setText(profileData.userPersonalInfo.firstName)
        etNameM.setText(profileData.userPersonalInfo.middleName)
        etNameL.setText(profileData.userPersonalInfo.lastName)
        etEmail.setText(profileData.userPersonalInfo.emailAddress)
        etMotherName.setText(profileData.userPersonalInfo.motherMaidenName)

        if(profileData.userPersonalInfo.sex=="M")
        {
            radioMale.isChecked=true
            radioFemale.isChecked=false
        }
        else
        {
            radioMale.isChecked=false
            radioFemale.isChecked=true
        }
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
                // todo: goto back activity from here

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