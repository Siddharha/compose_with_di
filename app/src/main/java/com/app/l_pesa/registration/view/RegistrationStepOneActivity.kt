package com.app.l_pesa.registration.view

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.telephony.TelephonyManager
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.MenuItem
import android.view.View
import android.view.Window
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.l_pesa.BuildConfig
import com.app.l_pesa.R
import com.app.l_pesa.common.CommonEditTextRegular
import com.app.l_pesa.common.CommonMethod
import com.app.l_pesa.common.SharedPref
import com.app.l_pesa.login.adapter.CountryListAdapter
import com.app.l_pesa.login.inter.ICallBackCountryList
import com.app.l_pesa.login.view.LoginActivity
import com.app.l_pesa.registration.inter.ICallBackRegisterOne
import com.app.l_pesa.registration.model.RegistrationData
import com.app.l_pesa.registration.presenter.PresenterRegistrationOne
import com.app.l_pesa.splash.model.ResModelCountryList
import com.app.l_pesa.splash.model.ResModelData
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.iid.FirebaseInstanceId
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.kaopiz.kprogresshud.KProgressHUD
import kotlinx.android.synthetic.main.activity_registration_step_one.*
import kotlinx.android.synthetic.main.layout_registration_step_one.*


class RegistrationStepOneActivity : AppCompatActivity(), ICallBackCountryList,ICallBackRegisterOne {


    private lateinit var  progressDialog  : ProgressDialog
    private var countryCode     ="+255"
    private var countryFound    = false

    private lateinit var alCountry        : ArrayList<ResModelCountryList>
    private lateinit var adapterCountry   : CountryListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_registration_step_one)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbarFont(this@RegistrationStepOneActivity)

        initLoader()
        initData()
        loadCountry()
        checkQualify()
    }

    private fun initData()
    {
        imgCountry.setOnClickListener {

            loadCountry()
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


    private fun initLoader()
    {
        progressDialog = ProgressDialog(this@RegistrationStepOneActivity,R.style.MyAlertDialogStyle)
        progressDialog.isIndeterminate = true
        progressDialog.setMessage(resources.getString(R.string.loading))
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        progressDialog.setCancelable(false)
        progressDialog.setCanceledOnTouchOutside(false)

    }

    private fun dismiss()
    {
        if(progressDialog.isShowing)
        {
            progressDialog.dismiss()
        }
    }

    private fun checkQualify()
    {
        etEmail.setOnEditorActionListener { _, actionId, _ ->
            var handled = false
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                verifyField()
                handled = true
            }
            handled
        }

        btnSubmit.setOnClickListener {

            verifyField()
        }
    }

    @SuppressLint("MissingPermission")
    private fun verifyField()
    {
        val telephonyManager    = getSystemService(Context.TELEPHONY_SERVICE) as? TelephonyManager

       /* var getIMEI=""
        getIMEI = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            telephonyManager!!.imei
        } else {
            telephonyManager!!.deviceId
        }*/

        val deviceId= Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)

        CommonMethod.hideKeyboardView(this@RegistrationStepOneActivity)
        if((etPhone.text.toString().length<9))
        {
            CommonMethod.customSnackBarError(rootLayout,this@RegistrationStepOneActivity,resources.getString(R.string.required_phone))
        }
        else if(TextUtils.isEmpty(etEmail.text.toString()) || !CommonMethod.isValidEmailAddress(etEmail.text.toString()))
        {
            CommonMethod.customSnackBarError(rootLayout,this@RegistrationStepOneActivity,resources.getString(R.string.required_email))
        }
        /*else if(TextUtils.isEmpty(telephonyManager.simSerialNumber))
        {
            CommonMethod.customSnackBarError(ll_root,this@RegistrationStepOneActivity,resources.getString(R.string.required_sim))
        }*/
        else
        {
            //CommonMethod.hideKeyboardView(this@RegistrationStepOneActivity)
            if(CommonMethod.isNetworkAvailable(this@RegistrationStepOneActivity))
            {
                progressDialog.show()
                //btnSubmit.isClickable=false

                val sharedPref = SharedPref(this@RegistrationStepOneActivity)
                sharedPref.accessToken="121212"

                val bundle = Bundle()
                bundle.putString("OTP","123456")
                dismiss()
                val intent = Intent(this@RegistrationStepOneActivity, RegistrationStepTwoActivity::class.java)
                intent.putExtras(bundle)
                startActivity(intent,bundle)
                overridePendingTransition(R.anim.right_in, R.anim.left_out)
                /*progressDialog.show()
                btnSubmit.isClickable=false

                val displayMetrics = resources.displayMetrics
                val width = displayMetrics.widthPixels
                val height = displayMetrics.heightPixels

                val jsonObject = JsonObject()
                jsonObject.addProperty("phone_no",etPhone.text.toString())
                jsonObject.addProperty("email_address",etEmail.text.toString())
                jsonObject.addProperty("country_code",countryCode)
                jsonObject.addProperty("platform_type","A")
                jsonObject.addProperty("device_token", FirebaseInstanceId.getInstance().token.toString())

                val jsonObjectRequestChild = JsonObject()
                jsonObjectRequestChild.addProperty("device_id", deviceId)
                jsonObjectRequestChild.addProperty("sdk",""+Build.VERSION.SDK_INT)
                jsonObjectRequestChild.addProperty("imei","311477629513071")
                jsonObjectRequestChild.addProperty("imsi","311477629513071")
                jsonObjectRequestChild.addProperty("simSerial_no","311477629513071")
                jsonObjectRequestChild.addProperty("sim_operator_Name","")
                jsonObjectRequestChild.addProperty("screen_height",""+height)
                jsonObjectRequestChild.addProperty("screen_width",""+width)
                jsonObjectRequestChild.addProperty("device", Build.DEVICE)
                jsonObjectRequestChild.addProperty("model", Build.MODEL)
                jsonObjectRequestChild.addProperty("product", Build.PRODUCT)
                jsonObjectRequestChild.addProperty("manufacturer", Build.MANUFACTURER)
                jsonObjectRequestChild.addProperty("app_version", BuildConfig.VERSION_NAME)
                jsonObjectRequestChild.addProperty("app_version_code", BuildConfig.VERSION_CODE.toString())

                jsonObject.add("device_data",jsonObjectRequestChild)

                val presenterRegistrationOneObj= PresenterRegistrationOne()
                presenterRegistrationOneObj.doRegistration(this@RegistrationStepOneActivity,jsonObject,this)
*/
                /* jsonObjectRequestChild.addProperty("imei",getIMEI)
                jsonObjectRequestChild.addProperty("imsi",""+telephonyManager.subscriberId)
                jsonObjectRequestChild.addProperty("simSerial_no",""+telephonyManager.simSerialNumber)
                jsonObjectRequestChild.addProperty("sim_operator_Name",telephonyManager.simOperatorName)*/
            }
            else
            {
                CommonMethod.customSnackBarError(rootLayout,this@RegistrationStepOneActivity,resources.getString(R.string.no_internet))
            }
        }
    }

    private fun loadCountry()
    {
        val sharedPrefOBJ= SharedPref(this@RegistrationStepOneActivity)
        val countryData = Gson().fromJson<ResModelData>(sharedPrefOBJ.countryList, ResModelData::class.java)

        val dialog= Dialog(this@RegistrationStepOneActivity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_country)
        alCountry= ArrayList()

        val recyclerView    = dialog.findViewById(R.id.recyclerView) as RecyclerView?
        val etCountry       = dialog.findViewById(R.id.etCountry) as CommonEditTextRegular?
        alCountry.addAll(countryData.countries_list)

        adapterCountry                  = CountryListAdapter(this@RegistrationStepOneActivity, alCountry,dialog,this)
        recyclerView?.layoutManager     = LinearLayoutManager(this@RegistrationStepOneActivity, RecyclerView.VERTICAL, false)
        recyclerView?.adapter           = adapterCountry
        dialog.show()
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)
        etCountry!!.addTextChangedListener(object : TextWatcher {

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

                filterCountry(s.toString())

            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int,after: Int) {

            }

            override fun afterTextChanged(s: Editable) {

            }
        })

    }

    override fun onClickCountry(resModelCountryList: ResModelCountryList) {

         countryCode=resModelCountryList.country_code
         if(TextUtils.isEmpty(etPhone.text.toString()))
         {
             etPhone.requestFocus()
         }
         else if(TextUtils.isEmpty(etEmail.text.toString()))
         {
             etEmail.requestFocus()
         }

    }

    /*private fun countrySpinner(countryList: ResModelData)
    {
        val dialog= Dialog(this@RegistrationStepOneActivity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_country)
        listCountry= ArrayList()
        val recyclerView    = dialog.findViewById(R.id.recyclerView) as RecyclerView?
        val etCountry       = dialog.findViewById(R.id.etCountry) as CommonEditTextRegular?
        listCountry!!.addAll(countryList.countries_list)
        adapterCountry                  = CountryListAdapter(this@RegistrationStepOneActivity, listCountry!!,dialog,this)
        recyclerView?.layoutManager     = LinearLayoutManager(this@RegistrationStepOneActivity, RecyclerView.VERTICAL, false)
        recyclerView?.adapter           = adapterCountry
        dialog.show()

        etCountry!!.addTextChangedListener(object : TextWatcher {

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

                filterCountry(s.toString())

            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int,after: Int) {

            }

            override fun afterTextChanged(s: Editable) {

            }
        })


    }*/

    private fun filterCountry(countryName: String) {

        val filteredCountry: ArrayList<ResModelCountryList> = ArrayList()

        for (country in alCountry) {
            if (country.country_name.toLowerCase().startsWith(countryName.toLowerCase())) {
                filteredCountry.add(country)
            }
            else
            {

            }
        }

        if(filteredCountry.size==0)
        {
            filteredCountry.clear()
            val emptyList=ResModelCountryList(0,resources.getString(R.string.search_result_not_found),"","","","","","","","","","","")
            filteredCountry.add(emptyList)
        }

        adapterCountry.filterList(filteredCountry)

    }


    override fun onBackPressed() {

        super.onBackPressed()
        overridePendingTransition(R.anim.left_in, R.anim.right_out)
    }

    override fun onSuccessRegistrationOne(data: RegistrationData) {

        dismiss()
        Toast.makeText(this@RegistrationStepOneActivity,resources.getString(R.string.refer_to_otp), Toast.LENGTH_LONG).show()
        btnSubmit.isClickable =true
        val sharedPref = SharedPref(this@RegistrationStepOneActivity)
        sharedPref.accessToken=data.access_token

        val bundle = Bundle()
        bundle.putString("OTP",data.otp)
        val intent = Intent(this@RegistrationStepOneActivity, RegistrationStepTwoActivity::class.java)
        intent.putExtras(bundle)
        startActivity(intent,bundle)
        overridePendingTransition(R.anim.right_in, R.anim.left_out)
    }

    override fun onErrorRegistrationOne(jsonMessage: String) {

        dismiss()
        btnSubmit.isClickable=true
        CommonMethod.customSnackBarError(rootLayout,this@RegistrationStepOneActivity,jsonMessage)
    }
}
