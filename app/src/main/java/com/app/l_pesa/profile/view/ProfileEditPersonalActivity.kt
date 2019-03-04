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
import com.app.l_pesa.profile.adapter.MaritalListAdapter
import com.app.l_pesa.profile.adapter.TitleListAdapter
import com.app.l_pesa.profile.inter.ICallBackMarital
import com.app.l_pesa.profile.inter.ICallBackTitle
import com.app.l_pesa.profile.model.ResUserInfo
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.gson.Gson

import kotlinx.android.synthetic.main.activity_profile_edit_personal.*
import kotlinx.android.synthetic.main.content_profile_edit_personal.*
import android.app.DatePickerDialog
import android.widget.DatePicker
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*


class ProfileEditPersonalActivity : AppCompatActivity(),ICallBackTitle, ICallBackMarital {


   override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_edit_personal)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbarFont(this@ProfileEditPersonalActivity)

        initData()
        loadTitle()
        loadMarital()
        loadDate()

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

    private fun loadMarital()
    {
        val sharedPrefOBJ= SharedPref(this@ProfileEditPersonalActivity)
        val profileData = Gson().fromJson<ResUserInfo.Data>(sharedPrefOBJ.profileInfo, ResUserInfo.Data::class.java)

        if(!TextUtils.isEmpty(profileData.userPersonalInfo.meritalStatus))
        {
            txtMarital.setText(profileData.userPersonalInfo.meritalStatus)
        }

        txtMarital.isFocusable=false
        txtMarital.setOnClickListener {

            showMarital()

        }
    }

    private fun showMarital()
    {
        val listTitle = arrayListOf("Married", "Unmarried")


        val dialog= Dialog(this@ProfileEditPersonalActivity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_country)
        val recyclerView                = dialog.findViewById(R.id.recycler_country) as RecyclerView?
        val titleAdapter                = MaritalListAdapter(this@ProfileEditPersonalActivity, listTitle,dialog,this)
        recyclerView?.layoutManager     = LinearLayoutManager(this@ProfileEditPersonalActivity, LinearLayoutManager.VERTICAL, false)
        recyclerView?.adapter           = titleAdapter
        dialog.show()
    }

    @SuppressLint("SetTextI18n", "SimpleDateFormat")
    private fun loadDate()
    {
        val sharedPrefOBJ= SharedPref(this@ProfileEditPersonalActivity)
        val profileData = Gson().fromJson<ResUserInfo.Data>(sharedPrefOBJ.profileInfo, ResUserInfo.Data::class.java)

        if(!TextUtils.isEmpty(profileData.userPersonalInfo.dob))
        {
            val inputFormat =  SimpleDateFormat("yyyy-MM-dd")
            val date = inputFormat.parse(profileData.userPersonalInfo.dob)

            val outputFormat = SimpleDateFormat("dd-MM-yyyy")
            txtDOB.setText(outputFormat.format(date))
        }

        txtDOB.isFocusable=false
        txtDOB.setOnClickListener {

            val c       = Calendar.getInstance()
            val year    = c.get(Calendar.YEAR)
            val month   = c.get(Calendar.MONTH)+1
            val day     = c.get(Calendar.DAY_OF_MONTH)

            val dpd = DatePickerDialog(this@ProfileEditPersonalActivity, DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->

                if(dayOfMonth.toString().length==1)
                {
                    if(monthOfYear.toString().length==1)
                    {
                        txtDOB.setText("0$dayOfMonth-0$month-$year")
                    }
                    else
                    {
                        txtDOB.setText("0$dayOfMonth-$month-$year")
                    }

                }
                else
                {
                    if(monthOfYear.toString().length==1)
                    {
                        txtDOB.setText("$dayOfMonth-0$month-$year")
                    }
                    else
                    {
                        txtDOB.setText("$dayOfMonth-$monthOfYear-$year")
                    }

                }

            }, year, month, day)

            dpd.show()
            dpd.datePicker.maxDate = System.currentTimeMillis()

        }
    }


    private fun showTitle()
    {
        val listTitle = arrayListOf("Mr", "Mrs", "Miss","Dr","Prof")
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

    override fun onChangeMarital(s: String) {

        txtMarital.setText(s)
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
