package com.app.l_pesa.pin.view

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Typeface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.telephony.TelephonyManager
import android.text.*
import android.text.method.SingleLineTransformationMethod
import android.text.style.RelativeSizeSpan
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.l_pesa.BuildConfig
import com.app.l_pesa.R
import com.app.l_pesa.common.*
import com.app.l_pesa.login.adapter.CountryListAdapter
import com.app.l_pesa.login.inter.ICallBackCountryList
import com.app.l_pesa.login.view.LoginActivity
import com.app.l_pesa.otpview.view.OTPActivity
import com.app.l_pesa.pin.inter.ICallBackChangePin
import com.app.l_pesa.pin.model.PinData
import com.app.l_pesa.pin.presenter.PresenterPassword
import com.app.l_pesa.pinview.view.PinSetActivity
import com.app.l_pesa.splash.model.ResModelCountryList
import com.app.l_pesa.splash.model.ResModelData
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.iid.FirebaseInstanceId
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.activity_forgot_pin.*
import java.util.HashMap
import kotlin.collections.ArrayList
import kotlin.collections.set

class ForgotPinActivity : AppCompatActivity(),  ICallBackCountryList, ICallBackChangePin {

    private lateinit var  progressDialog   : ProgressDialog
    private lateinit var  alCountry        : ArrayList<ResModelCountryList>
    private lateinit var  adapterCountry   : CountryListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_pin)
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbarFont(this@ForgotPinActivity)

        initLoader()
        loadCountry()
        forgetPin()
    }

    private fun initLoader()
    {
        progressDialog = ProgressDialog(this@ForgotPinActivity,R.style.MyAlertDialogStyle)
        val message=   SpannableString(resources.getString(R.string.loading))
        val face = Typeface.createFromAsset(assets, "fonts/Montserrat-Regular.ttf")
        message.setSpan(RelativeSizeSpan(1.0f), 0, message.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        message.setSpan(CustomTypefaceSpan("", face), 0, message.length, 0)
        progressDialog.isIndeterminate = true
        progressDialog.setMessage(message)
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

    private fun forgetPin()
    {

        etPhone.transformationMethod = SingleLineTransformationMethod.getInstance()
        etPhone.setOnEditorActionListener { _, actionId, _ ->
            var handled = false
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                verifyField()
                handled = true
            }
            handled
        }

        buttonRecoverPin.setOnClickListener {
            verifyField()

        }

    }

    @SuppressLint("MissingPermission")
    private fun verifyField()
    {
        hideKeyBoard()
        if(etPhone.text.toString().length<9 )
        {
            customSnackBarError(rootLayout,resources.getString(R.string.required_phone_email))
        }
        else
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                checkAndRequestPermissions()
            }
            else
            {
                doForgotPinProcess()
            }

        }


    }

    @SuppressLint("MissingPermission", "HardwareIds")
    private fun doForgotPinProcess()
    {
        val telephonyManager = getSystemService(Context.TELEPHONY_SERVICE) as? TelephonyManager

        var getIMEI = ""
        getIMEI = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            telephonyManager!!.imei
        } else {
            telephonyManager!!.deviceId
        }

        val deviceId = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)

        if (TextUtils.isEmpty(telephonyManager.simSerialNumber))
        {
            CommonMethod.customSnackBarError(rootLayout, this@ForgotPinActivity, resources.getString(R.string.required_sim))
        }
        else
        {
            if(CommonMethod.isNetworkAvailable(this@ForgotPinActivity))
            {
                progressDialog.show()
                val sharedPrefOBJ= SharedPref(this@ForgotPinActivity)
                buttonRecoverPin.isClickable   = false

                val displayMetrics = resources.displayMetrics
                val width = displayMetrics.widthPixels
                val height = displayMetrics.heightPixels

                val jsonObject = JsonObject()
                jsonObject.addProperty("phone_no",etPhone.text.toString())
                jsonObject.addProperty("country_code",sharedPrefOBJ.countryIsdCode)
                jsonObject.addProperty("platform_type","A")
                jsonObject.addProperty("device_token", FirebaseInstanceId.getInstance().token.toString())

                val jsonObjectRequestChild = JsonObject()
                jsonObjectRequestChild.addProperty("device_id", deviceId)
                jsonObjectRequestChild.addProperty("sdk",""+Build.VERSION.SDK_INT)
                jsonObjectRequestChild.addProperty("imei",getIMEI)
                jsonObjectRequestChild.addProperty("imsi","" + telephonyManager.subscriberId)
                jsonObjectRequestChild.addProperty("simSerial_no","" + telephonyManager.simSerialNumber)
                jsonObjectRequestChild.addProperty("sim_operator_Name","" + telephonyManager.simOperatorName)
                jsonObjectRequestChild.addProperty("screen_height",""+height)
                jsonObjectRequestChild.addProperty("screen_width",""+width)
                jsonObjectRequestChild.addProperty("device", Build.DEVICE)
                jsonObjectRequestChild.addProperty("model", Build.MODEL)
                jsonObjectRequestChild.addProperty("product", Build.PRODUCT)
                jsonObjectRequestChild.addProperty("manufacturer", Build.MANUFACTURER)
                jsonObjectRequestChild.addProperty("app_version", BuildConfig.VERSION_NAME)
                jsonObjectRequestChild.addProperty("app_version_code", BuildConfig.VERSION_CODE.toString())

                jsonObject.add("device_data",jsonObjectRequestChild)

                val presenterForgetPassword= PresenterPassword()
                presenterForgetPassword.doForgetPassword(this@ForgotPinActivity,jsonObject,this)


            }
            else
            {
                CommonMethod.customSnackBarError(rootLayout,this@ForgotPinActivity,resources.getString(R.string.no_internet))
            }
        }
    }

    private fun hideKeyBoard()
    {
        try {
            CommonMethod.hideKeyboardView(this@ForgotPinActivity)
        }
        catch (exp:Exception)
        {}

    }

    private fun customSnackBarError(view: View,message:String) {

        val snackBarOBJ = Snackbar.make(view, "", Snackbar.LENGTH_SHORT)
        snackBarOBJ.view.setBackgroundColor(ContextCompat.getColor(this@ForgotPinActivity,R.color.colorRed))
        (snackBarOBJ.view as ViewGroup).removeAllViews()
        val customView = LayoutInflater.from(this).inflate(R.layout.snackbar_error, null)
        (snackBarOBJ.view as ViewGroup).addView(customView)
        val txtTitle=customView.findViewById(R.id.txtTitle) as CommonTextRegular
        txtTitle.text = message
        snackBarOBJ.show()
    }

    override fun onSuccessResetPin(data: PinData) {
        dismiss()
        buttonRecoverPin.isClickable=true
        if(data.next_step=="next_otp")
        {
            Toast.makeText(this@ForgotPinActivity,resources.getString(R.string.sent_otp_via_sms),Toast.LENGTH_LONG).show()
            val sharedPrefOBJ=SharedPref(this@ForgotPinActivity)
            val json = Gson().toJson(data)
            sharedPrefOBJ.deviceInfo      = json
            val intent = Intent(this@ForgotPinActivity, OTPActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.right_in, R.anim.left_out)
        }
        else
        {
            Toast.makeText(this@ForgotPinActivity,resources.getString(R.string.sent_pin_via_sms),Toast.LENGTH_LONG).show()
            val sharedPrefOBJ=SharedPref(this@ForgotPinActivity)
            val json = Gson().toJson(data)
            sharedPrefOBJ.deviceInfo      = json
            val intent = Intent(this@ForgotPinActivity, PinSetActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.right_in, R.anim.left_out)
        }
    }

    override fun onErrorResetPin(message: String) {

        dismiss()
        buttonRecoverPin.isClickable=true
        customSnackBarError(rootLayout,message)
    }




    private fun loadCountry()
    {
        val sharedPrefOBJ= SharedPref(this@ForgotPinActivity)
        if(TextUtils.isEmpty(sharedPrefOBJ.countryIsdCode))
        {
            etPhone.tag="+000   "
            showCountry()
        }
        else
        {
            txtCountry.visibility=View.VISIBLE
            txtCountry.text = sharedPrefOBJ.countryName
            etPhone.tag=sharedPrefOBJ.countryIsdCode+"   "
        }

        txtCountry.setOnClickListener {

            showCountry()

        }
    }

    private fun showCountry()
    {
        val sharedPrefOBJ= SharedPref(this@ForgotPinActivity)
        val countryData = Gson().fromJson<ResModelData>(sharedPrefOBJ.countryList, ResModelData::class.java)

        val dialog= Dialog(this@ForgotPinActivity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_country)
        alCountry= ArrayList()

        val recyclerView    = dialog.findViewById(R.id.recyclerView) as RecyclerView?
        val etCountry       = dialog.findViewById(R.id.etCountry) as CommonEditTextRegular?
        alCountry.addAll(countryData.countries_list)

        adapterCountry                  = CountryListAdapter(this@ForgotPinActivity, alCountry,dialog,this)
        recyclerView?.layoutManager     = LinearLayoutManager(this@ForgotPinActivity, RecyclerView.VERTICAL, false)
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


    private fun checkAndRequestPermissions(): Boolean {

        val permissionPhoneState    = ContextCompat.checkSelfPermission(this@ForgotPinActivity, Manifest.permission.READ_PHONE_STATE)

        val listPermissionsNeeded = ArrayList<String>()

        if (permissionPhoneState != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_PHONE_STATE)
        }
        if (listPermissionsNeeded.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toTypedArray(), REQUEST_ID_PERMISSIONS)
            return false
        }
        else
        {
            doForgotPinProcess()
        }
        return true
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {

        when (requestCode) {
            REQUEST_ID_PERMISSIONS -> {

                val perms = HashMap<String, Int>()
                // Initialize the map with both permissions
                perms[Manifest.permission.READ_PHONE_STATE]         = PackageManager.PERMISSION_GRANTED
                // Fill with actual results from user
                if (grantResults.isNotEmpty()) {
                    for (i in permissions.indices)
                        perms[permissions[i]] = grantResults[i]
                    // Check for both permissions
                    if (perms[Manifest.permission.READ_PHONE_STATE]  == PackageManager.PERMISSION_GRANTED) {

                        doForgotPinProcess()
                        //else any one or both the permissions are not granted
                    } else {

                        if (ActivityCompat.shouldShowRequestPermissionRationale(this@ForgotPinActivity, Manifest.permission.READ_PHONE_STATE)) {
                            showDialogOK("Permissions are required for this app",
                                    DialogInterface.OnClickListener { _, which ->
                                        when (which) {
                                            DialogInterface.BUTTON_POSITIVE -> checkAndRequestPermissions()
                                            DialogInterface.BUTTON_NEGATIVE ->

                                                finish()
                                        }
                                    })
                        } else {
                            permissionDialog("You need to give some mandatory permissions to continue. Do you want to go to app settings?")

                        }
                    }
                }
            }
        }

    }

    private fun showDialogOK(message: String, okListener: DialogInterface.OnClickListener) {
        AlertDialog.Builder(this@ForgotPinActivity,R.style.MyAlertDialogTheme)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", okListener)
                .create()
                .show()
    }

    private fun permissionDialog(msg: String) {
        val dialog = AlertDialog.Builder(this@ForgotPinActivity,R.style.MyAlertDialogTheme)
        dialog.setMessage(msg)
                .setPositiveButton("Yes") { _, _ ->
                    startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:com.app.l_pesa")))
                }
                .setNegativeButton("Cancel") { _, _ -> finish() }
        dialog.show()
    }

    companion object {

        private const  val REQUEST_ID_PERMISSIONS = 1

    }
    override fun onClickCountry(resModelCountryList: ResModelCountryList) {

        txtCountry.visibility=View.VISIBLE
        txtCountry.text = resModelCountryList.country_name
        etPhone.requestFocus()
        etPhone.tag = resModelCountryList.country_code+"   "
        val sharedPrefOBJ= SharedPref(this@ForgotPinActivity)
        sharedPrefOBJ.countryCode=resModelCountryList.code
        sharedPrefOBJ.countryName=resModelCountryList.country_name
        sharedPrefOBJ.countryIsdCode=resModelCountryList.country_code
        sharedPrefOBJ.countryFlag=resModelCountryList.image

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
                CommonMethod.hideKeyboardView(this@ForgotPinActivity)
                onBackPressed()
                overridePendingTransition(R.anim.left_in, R.anim.right_out)
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }



    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this@ForgotPinActivity, LoginActivity::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.left_in, R.anim.right_out)

    }
}
