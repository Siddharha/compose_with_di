package com.app.l_pesa.profile.view

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.graphics.Typeface
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.MenuItem
import android.view.Window
import android.widget.TextView
import com.app.l_pesa.R
import com.app.l_pesa.common.CommonMethod
import com.app.l_pesa.common.SharedPref
import com.app.l_pesa.profile.adapter.TitleListAdapter
import com.app.l_pesa.profile.inter.ICallBackTitle
import com.app.l_pesa.profile.model.ResUserInfo
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.gson.Gson

import kotlinx.android.synthetic.main.activity_profile_edit_personal.*
import kotlinx.android.synthetic.main.content_profile_edit_personal.*

class ProfileEditPersonalActivity : AppCompatActivity(),ICallBackTitle {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_edit_personal)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbarFont(this@ProfileEditPersonalActivity)

        initData()
        loadTitle()

    }

    @SuppressLint("SetTextI18n")
    private fun initData()
    {
        val sharedPrefOBJ= SharedPref(this@ProfileEditPersonalActivity)
        val profileData = Gson().fromJson<ResUserInfo.Data>(sharedPrefOBJ.profileInfo, ResUserInfo.Data::class.java)


        val options = RequestOptions()
        Glide.with(this@ProfileEditPersonalActivity)
                .load(profileData!!.userInfo.profileImage)
                .apply(options)
                .into(imgProfile)

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


        onChangeEmail()
    }


    override fun onChangeTitle(s: String)
    {
        txtTitle.setText(s)
    }

    private fun loadTitle()
    {
        val sharedPrefOBJ= SharedPref(this@ProfileEditPersonalActivity)
        val profileData = Gson().fromJson<ResUserInfo.Data>(sharedPrefOBJ.profileInfo, ResUserInfo.Data::class.java)

        if(!TextUtils.isEmpty(profileData.userPersonalInfo.title))
        {
            txtTitle.setText(profileData.userPersonalInfo.title)
        }

        txtTitle.isFocusable=false
        txtTitle.setOnClickListener {

            showTitle()

        }
    }


    private fun showTitle()
    {
        val listTitle = arrayListOf("Mr", "Mrs", "Mis","Dr","Prof")
        val listIcon = arrayListOf(R.drawable.ic_mr_icon,R.drawable.ic_mrs_icon,R.drawable.ic_mrs_icon,R.drawable.ic_dr_icon,R.drawable.ic_professor_icon)

        val dialog= Dialog(this@ProfileEditPersonalActivity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_country)
        val recyclerView                = dialog.findViewById(R.id.recycler_country) as RecyclerView?
        val titleAdapter                = TitleListAdapter(this@ProfileEditPersonalActivity, listTitle,listIcon,dialog,this)
        recyclerView?.layoutManager     = LinearLayoutManager(this@ProfileEditPersonalActivity, LinearLayoutManager.VERTICAL, false)
        recyclerView?.adapter           = titleAdapter
        dialog.show()
    }

    private fun onChangeEmail()
    {
        etEmail.addTextChangedListener(object : TextWatcher {

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

               if(!CommonMethod.isValidEmailAddress(s.toString()))
               {
                   etEmail.setTextColor(ContextCompat.getColor(this@ProfileEditPersonalActivity,R.color.colorRed))
               }
                else
               {
                   etEmail.setTextColor(ContextCompat.getColor(this@ProfileEditPersonalActivity,R.color.textColors))
               }

            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int,after: Int) {

            }

            override fun afterTextChanged(s: Editable) {

            }
        })
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
