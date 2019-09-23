package com.app.l_pesa.loanplan.view

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Typeface
import android.location.Criteria
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.provider.Settings
import android.text.TextUtils
import android.view.MenuItem
import android.view.View
import android.view.Window
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.l_pesa.R
import com.app.l_pesa.common.CommonMethod
import com.app.l_pesa.common.SharedPref
import com.app.l_pesa.dashboard.view.DashboardActivity
import com.app.l_pesa.loanHistory.inter.ICallBackLoanApply
import com.app.l_pesa.loanplan.adapter.DescriptionAdapter
import com.app.l_pesa.loanplan.inter.ICallBackDescription
import com.app.l_pesa.loanplan.presenter.PresenterLoanApply
import com.app.l_pesa.main.view.MainActivity
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.activity_loan_apply.*
import kotlinx.android.synthetic.main.content_loan_apply.*


class LoanApplyActivity : AppCompatActivity(), ICallBackDescription, ICallBackLoanApply, LocationListener {


    private var loanPurpose=""
    private val listTitle = arrayListOf("For Transport","To Pay Bills","To Clear Debit","To Buy Foodstuff","Emergency Purposes","To Buy Medicine","Build Credit","Others")
    private lateinit var  progressDialog   : ProgressDialog
    private lateinit var  countDownTimer   : CountDownTimer

    lateinit var locationManager: LocationManager
    internal var provider: String? = null

    private var loanType=""
    private var productId=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loan_apply)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbarFont(this@LoanApplyActivity)

        locationWork()
        initData()

    }

    private fun locationWork()
    {
        if (ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Check Permissions Now
            ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    0)
        }
        // Getting LocationManager object

        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        // Creating an empty criteria object
        val criteria = Criteria()

        // Getting the name of the provider that meets the criteria
        provider = locationManager.getBestProvider(criteria, false)

        if (provider != null && provider != "") {
            if (!provider!!.contains("gps")) { // if gps is disabled
                val poke = Intent()
                poke.setClassName("com.android.settings",
                        "com.android.settings.widget.SettingsAppWidgetProvider")
                poke.addCategory(Intent.CATEGORY_ALTERNATIVE)
                poke.data = Uri.parse("3")
                sendBroadcast(poke)
            }
            // Get the location from the given provider
            var location = locationManager
                    .getLastKnownLocation(LocationManager.NETWORK_PROVIDER)

            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 500, 0f, this)

            if (location != null)
                onLocationChanged(location)
            else
                location = locationManager.getLastKnownLocation(provider!!)
            if (location != null)
                onLocationChanged(location)


        } else {
            Toast.makeText(baseContext, "No Provider Found",Toast.LENGTH_SHORT).show()
        }
    }

    private fun initData()
    {
         val bundle  = intent.extras
         productId   = bundle!!.getString("PRODUCT_ID")!!
         loanType    = bundle.getString("LOAN_TYPE")!!


        initTimer()
        initLoader()
        loadDescription()

        buttonCancel.setOnClickListener {

            onBackPressed()
            overridePendingTransition(R.anim.left_in, R.anim.right_out)
        }

        buttonSubmit.setOnClickListener {

            if(TextUtils.isEmpty(loanPurpose))
            {
                showDescription()
                CommonMethod.customSnackBarError(rootLayout,this@LoanApplyActivity,resources.getString(R.string.required_loan_purpose))
            }
            else if(loanPurpose=="Others" && TextUtils.isEmpty(etDescription.text.toString()))
            {
                etDescription.requestFocus()
                CommonMethod.customSnackBarError(rootLayout,this@LoanApplyActivity,resources.getString(R.string.required_loan_purpose_description))
            }
            else
            {
                val alertDialog = AlertDialog.Builder(this@LoanApplyActivity,R.style.MyAlertDialogTheme)
                alertDialog.setTitle(resources.getString(R.string.app_name))
                alertDialog.setMessage(resources.getString(R.string.want_to_apply_loan))
                alertDialog.setPositiveButton("Yes") { _, _ -> applyLoan() }
                        .setNegativeButton("No") { dialog, _ -> dialog.dismiss() }
                alertDialog.show()

            }

        }


    }



    private fun applyLoan()
    {
        if(CommonMethod.isNetworkAvailable(this@LoanApplyActivity))
        {
            val manager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

            if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

                alertMessageNoGps()
            }
            else {

                val shared = SharedPref(this@LoanApplyActivity)
                if (shared.currentLat != "") {
                    progressDialog.show()
                    buttonSubmit.isClickable = false
                    loanApply()
                } else {
                    CommonMethod.customSnackBarError(rootLayout, this@LoanApplyActivity, resources.getString(R.string.please_wait))
                }

            }


        }
        else
        {
            CommonMethod.customSnackBarError(rootLayout,this@LoanApplyActivity,resources.getString(R.string.no_internet))
        }
    }

    private fun loanApply()
    {
        val shared=SharedPref(this@LoanApplyActivity)
        CommonMethod.hideKeyboardView(this@LoanApplyActivity)
        val jsonObject = JsonObject()
        jsonObject.addProperty("loan_type",loanType)
        jsonObject.addProperty("product_id",productId)
        if(loanPurpose=="Others")
        {
            jsonObject.addProperty("loan_purpose",etDescription.text.toString())
        }
        else
        {
            jsonObject.addProperty("loan_purpose",loanPurpose)

        }

        jsonObject.addProperty("latitude",shared.currentLat)
        jsonObject.addProperty("longitude",shared.currentLng)


        val presenterLoanApply= PresenterLoanApply()
        presenterLoanApply.doLoanApply(this@LoanApplyActivity,jsonObject,this)

    }

    private fun loadDescription()
    {
        showDescription()
        etChooseDescription.isFocusable =false
        etChooseDescription.setOnClickListener {

            showDescription()
        }

    }

    private fun showDescription()
    {

        val dialog= Dialog(this@LoanApplyActivity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.layout_list_single)
        val recyclerView                = dialog.findViewById(R.id.recyclerView) as RecyclerView?
        val titleAdapter                = DescriptionAdapter(this@LoanApplyActivity, listTitle,dialog,this)
        recyclerView?.layoutManager     = LinearLayoutManager(this@LoanApplyActivity, RecyclerView.VERTICAL, false)
        recyclerView?.adapter           = titleAdapter
        dialog.show()
    }

    override fun onSelectDescription(s: String) {

        etChooseDescription.setText(s)
        loanPurpose=s
        if(loanPurpose=="Others")
        {
            txt_loan_description.visibility     = View.VISIBLE
            tilDescription.visibility           = View.VISIBLE
            txt_max_words.visibility            = View.VISIBLE
            txt_loan_description.visibility     = View.VISIBLE
            etDescription.requestFocus()

        }
        else
        {
            txt_loan_description.visibility     = View.GONE
            tilDescription.visibility           = View.GONE
            txt_max_words.visibility            = View.GONE
            txt_loan_description.visibility     = View.GONE
        }
    }

    override fun onSuccessLoanApply() {

        Toast.makeText(this@LoanApplyActivity,resources.getString(R.string.loan_applied_successfully),Toast.LENGTH_SHORT).show()
        dismiss()
        val sharedPref=SharedPref(this@LoanApplyActivity)
        sharedPref.navigationTab=resources.getString(R.string.open_tab_loan)
        val intent = Intent(this@LoanApplyActivity, DashboardActivity::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.right_in, R.anim.left_out)

    }

    override fun onErrorLoanApply(message: String) {
        dismiss()
        buttonSubmit.isClickable =true
        CommonMethod.customSnackBarError(rootLayout,this@LoanApplyActivity,message)
    }

    override fun onSessionTimeOut(jsonMessage: String) {

        dismiss()
        val dialogBuilder = AlertDialog.Builder(this@LoanApplyActivity,R.style.MyAlertDialogTheme)
        dialogBuilder.setMessage(jsonMessage)
                .setCancelable(false)
                .setPositiveButton("Ok") { dialog, _ ->
                    dialog.dismiss()
                    val sharedPrefOBJ= SharedPref(this@LoanApplyActivity)
                    sharedPrefOBJ.removeShared()
                    startActivity(Intent(this@LoanApplyActivity, MainActivity::class.java))
                    overridePendingTransition(R.anim.right_in, R.anim.left_out)
                    finish()
                }

        val alert = dialogBuilder.create()
        alert.setTitle(resources.getString(R.string.app_name))
        alert.show()
    }



    private fun initLoader()
    {
        progressDialog = ProgressDialog(this@LoanApplyActivity,R.style.MyAlertDialogStyle)
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

    private fun alertMessageNoGps() {
        val builder = AlertDialog.Builder(this@LoanApplyActivity,R.style.MyAlertDialogTheme)
        builder.setMessage(
                "Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false).setPositiveButton("Yes"
                ) { dialog, id ->
                    startActivity(Intent(
                            Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                }
                .setNegativeButton("No") { dialog, id -> dialog.cancel() }
        val alert = builder.create()
        alert.setCancelable(false)
        alert.show()
    }

    override fun onLocationChanged(location: Location) {

        val sharedPref=SharedPref(this@LoanApplyActivity)
        sharedPref.currentLat=location.latitude.toString()
        sharedPref.currentLng=location.longitude.toString()

    }

    override fun onProviderDisabled(provider: String) {

        if (ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Check Permissions Now
            ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    0)
        }
    }

    override fun onProviderEnabled(provider: String) {

    }

    override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {

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


    public override fun onDestroy() {

        super.onDestroy()
        countDownTimer.cancel()
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

    public override fun onResume() {
        super.onResume()
        locationWork()

    }


}
