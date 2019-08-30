package com.app.l_pesa.loanplan.view

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
import android.location.Location
import android.location.LocationManager
import android.os.Build
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
import androidx.core.content.ContextCompat
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
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationListener
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.activity_loan_apply.*
import kotlinx.android.synthetic.main.content_loan_apply.*


class LoanApplyActivity : AppCompatActivity(), ICallBackDescription, ICallBackLoanApply, LocationListener {


    private var loanPurpose=""
    private val listTitle = arrayListOf("For Transport","To Pay Bills","To Clear Debit","To Buy Foodstuff","Emergency Purposes","To Buy Medicine","Build Credit","Others")
    private lateinit var  progressDialog   : ProgressDialog
    private lateinit var  countDownTimer   : CountDownTimer

    private var REQUEST_LOCATION_CODE = 101
    private var mGoogleApiClient: GoogleApiClient? = null
    private var mLocation: Location? = null
    private var mLocationRequest: LocationRequest? = null
    private val UPDATE_INTERVAL = (4000).toLong()  /* 4 secs */
    private val FASTEST_INTERVAL: Long = 2000 /* 2 sec */

    private var loanType=""
    private var productId=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loan_apply)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbarFont(this@LoanApplyActivity)

        buildGoogleApiClient()
        initData()

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
                val alertDialog = AlertDialog.Builder(this@LoanApplyActivity)
                alertDialog.setTitle(resources.getString(R.string.app_name))
                alertDialog.setMessage(resources.getString(R.string.want_to_apply_loan))
                alertDialog.setPositiveButton("Yes") { _, _ -> applyLoan() }
                        .setNegativeButton("No") { dialog, _ -> dialog.dismiss() }
                alertDialog.show()

            }

        }


    }

    private fun startLocationUpdates() {
        // Create the location request
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(UPDATE_INTERVAL)
                .setFastestInterval(FASTEST_INTERVAL)

        if (ActivityCompat.checkSelfPermission(this@LoanApplyActivity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this)
    }

    @SuppressLint("MissingPermission")
    private fun getLocation() {
        mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient)

        if (mLocation == null) {
            startLocationUpdates()
        }
        if (mLocation != null) {
            val sharedPrefObj=SharedPref(this@LoanApplyActivity)
            sharedPrefObj.currentLat = mLocation!!.latitude.toString()
            sharedPrefObj.currentLng = mLocation!!.longitude.toString()
            doApplyLoan()
        } else {
            Toast.makeText(this, "Current Location Not Detected", Toast.LENGTH_SHORT).show();
        }
    }

    private fun applyLoan()
    {
        if (!checkGPSEnabled()) {
            return
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                 getLocation()
            } else {
                 checkLocationPermission()
            }
        } else {
            getLocation()
        }

    }

    private fun doApplyLoan()
    {
        if(CommonMethod.isNetworkAvailable(this@LoanApplyActivity))
        {
            if(isLocationEnabled())
            {
                val shared=SharedPref(this@LoanApplyActivity)
                if(shared.currentLat!="")
                {
                    progressDialog.show()
                    buttonSubmit.isClickable =false
                    loanApply()
                }
                else
                {
                     CommonMethod.customSnackBarError(rootLayout,this@LoanApplyActivity,resources.getString(R.string.please_wait))
                }

            }
            else
            {
                showAlert()
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
        val dialogBuilder = AlertDialog.Builder(this@LoanApplyActivity)
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

    override fun onLocationChanged(location: Location?) {

        val sharedPrefObj=SharedPref(this@LoanApplyActivity)
        sharedPrefObj.currentLat = location!!.latitude.toString()
        sharedPrefObj.currentLng = location.longitude.toString()
    }

    @Synchronized
    private fun buildGoogleApiClient() {
        mGoogleApiClient = GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .build()

        mGoogleApiClient!!.connect()
    }

    private fun checkGPSEnabled(): Boolean {
        if (!isLocationEnabled())
            showAlert()
        return isLocationEnabled()
    }

    private fun showAlert() {
        val dialog = AlertDialog.Builder(this@LoanApplyActivity)
        dialog.setTitle("Enable Location")
                .setMessage("Your Locations Settings is set to OFF.Please Enable Location to use L-Pesa")
                .setPositiveButton("Location Settings") { _, _ ->
                    val myIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    startActivity(myIntent)
                }
                .setNegativeButton("Cancel") { _, _ -> }
        dialog.show()
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                AlertDialog.Builder(this)
                        .setTitle("Location Permission Needed")
                        .setMessage("This app needs the Location permission, please accept to use location functionality")
                        .setPositiveButton("OK", DialogInterface.OnClickListener { dialog, which ->
                            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_LOCATION_CODE)
                        })
                        .create()
                        .show()

            } else ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_LOCATION_CODE)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_LOCATION_CODE -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        doApplyLoan()
                    }
                } else {
                    // permission denied, boo! Disable the functionality that depends on this permission.
                    Toast.makeText(this, "You need to add Location Permission", Toast.LENGTH_LONG).show()
                }
                return
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

    public override fun onResume() {
        super.onResume()

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
        if (mGoogleApiClient!!.isConnected) {
            mGoogleApiClient!!.disconnect()
        }

    }

    override fun onStart() {
        super.onStart()
        mGoogleApiClient?.connect()
    }


}
