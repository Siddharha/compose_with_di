package com.app.l_pesa.loanplan.view

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Typeface
import android.location.*
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.provider.Settings
import android.text.Spannable
import android.text.SpannableString
import android.text.TextUtils
import android.text.style.RelativeSizeSpan
import android.util.Log
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
import com.app.l_pesa.application.MyApplication
import com.app.l_pesa.common.CommonMethod
import com.app.l_pesa.common.SharedPref
import com.app.l_pesa.dashboard.view.DashboardActivity
import com.app.l_pesa.loanHistory.inter.ICallBackLoanApply
import com.app.l_pesa.loanplan.adapter.DescriptionAdapter
import com.app.l_pesa.loanplan.adapter.LoanTermsListAdapter
import com.app.l_pesa.loanplan.inter.ICallBackLoanTenure
import com.app.l_pesa.loanplan.inter.ICallBackTermsDescription
import com.app.l_pesa.loanplan.model.GlobalLoanPlanModel
import com.app.l_pesa.loanplan.model.ResLoanTenure
import com.app.l_pesa.loanplan.presenter.PresenterLoanApply
import com.app.l_pesa.loanplan.presenter.PresenterLoanApplyTenure
import com.app.l_pesa.main.view.MainActivity
import com.facebook.appevents.AppEventsConstants
import com.facebook.appevents.AppEventsLogger
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.activity_loan_apply.*
import kotlinx.android.synthetic.main.content_loan_apply.*
import java.util.*
import kotlin.collections.ArrayList


class LoanApplyActivity : AppCompatActivity(), ICallBackTermsDescription, ICallBackLoanApply, LocationListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {


    private var loanPurpose = ""
    private var loanTanure = "" //Need to send weeks value as String
    private val listTitle = arrayListOf("For Transport", "To Pay Bills", "To Clear Debit", "To Buy Foodstuff", "Emergency Purposes", "To Buy Medicine", "Build Credit", "Others")
    private  val listTerms:ArrayList<ResLoanTenure.Data.Option> by lazy { ArrayList() }
    /*= arrayListOf(
        LoanTermItem(
            id = 0,
            title = "28 Days",
            paymentDuration = "1 monthly payment",
            totalAmountDue = "1,175"
        ),
        LoanTermItem(
            id = 0,
            title = "28 Days",
            paymentDuration = "1 monthly payment",
            totalAmountDue = "1,175"
        ),
        LoanTermItem(
            id = 0,
            title = "28 Days",
            paymentDuration = "1 monthly payment",
            totalAmountDue = "1,175"
        ),
        LoanTermItem(
            id = 0,
            title = "28 Days",
            paymentDuration = "1 monthly payment",
            totalAmountDue = "1,175"
        )
    )*/
    private lateinit var progressDialog: ProgressDialog
    private lateinit var countDownTimer: CountDownTimer

    private lateinit var locationManager: LocationManager
    private lateinit var provider: String

    private var loanType = ""
    private var productId = ""

    private var fusedLocationClient: FusedLocationProviderClient? = null
    private var locationRequest: LocationRequest? = null
    private val TAG = "Loan Apply Activity"

    private var location: Location? = null
    private var googleApiClient: GoogleApiClient? = null


    companion object {
        val PLAY_SERVICES_REQUEST: Int = 100
        val UPDATE_INTERVAL: Long = 5000 //5 sec
        val FASTEST_INTERVAL: Long = 5000 //5 sec
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loan_apply)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbarFont(this@LoanApplyActivity)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        initialize()
        locationWork()
        initData()
        //getLastLocation()




    }

    private fun initialize(){
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val criteria = Criteria()
        provider = locationManager.getBestProvider(criteria, false)!!
    }

    private fun getLastLocation() {
        Log.d(TAG, "getLastLocation:  getlastlocation")
        //last location update:
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        fusedLocationClient?.lastLocation?.addOnSuccessListener(this) { location ->
                    Log.d(TAG, "onSuccess: location " + location!!.latitude + location.longitude)
                    if (location != null) {
                        val lat = location.latitude
                        val lng = location.longitude
                        Log.d(TAG, "onSuccess: $lat and $lng")
                        Log.d(TAG, "onLatLngChange: $lat/$lng")
                    }
                }
    }

    override fun onStart() {
        super.onStart()
       /* if (googleApiClient != null) {
            googleApiClient?.connect()
        }*/
    }

    private fun startLocationUpdates() {
        locationRequest = LocationRequest()
        locationRequest!!.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest!!.interval = UPDATE_INTERVAL
        locationRequest!!.fastestInterval = FASTEST_INTERVAL
        if (ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "You need to enable permissions to display location !", Toast.LENGTH_SHORT).show()
        }
        //LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this)
    }


    private fun locationWork() {
        if (ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Check Permissions Now
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 0)
        }




        // Creating an empty criteria object

        if (provider != "") {
            if (!provider.contains("gps")) {
                val poke = Intent()
                poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider")
                poke.addCategory(Intent.CATEGORY_ALTERNATIVE)
                poke.data = Uri.parse("3")
                sendBroadcast(poke)
            }

            var location = locationManager.getLastKnownLocation(provider)

            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 500, 0f, this)

            if (location != null)
                onLocationChanged(location)
            else
                location = locationManager.getLastKnownLocation(provider!!)
            if (location != null)
                onLocationChanged(location)

        } else {
            Toast.makeText(baseContext, "No Provider Found", Toast.LENGTH_SHORT).show()
        }
    }

    private fun initData() {
        val bundle = intent.extras
        productId = bundle!!.getString("PRODUCT_ID")!!
        loanType = bundle.getString("LOAN_TYPE")!!

        initTimer()
        initLoader()
        loadDescription()
        loadTerms(productId)
        etChooseTerm.setText("9 Weeks")
        buttonCancel.setOnClickListener {

            onBackPressed()
            overridePendingTransition(R.anim.left_in, R.anim.right_out)
        }

        buttonSubmit.setOnClickListener {

            if (TextUtils.isEmpty(loanPurpose)) {
                showDescription()
                CommonMethod.customSnackBarError(rootLayout, this@LoanApplyActivity, resources.getString(R.string.required_loan_purpose))
            } else if (loanPurpose == "Others" && TextUtils.isEmpty(etDescription.text.toString())) {
                etDescription.requestFocus()
                hideKeyboard()
                CommonMethod.customSnackBarError(rootLayout, this@LoanApplyActivity, resources.getString(R.string.required_loan_purpose_description))
            } else {
                val alertDialog = AlertDialog.Builder(this@LoanApplyActivity, R.style.MyAlertDialogTheme)
                alertDialog.setTitle(resources.getString(R.string.app_name))
                alertDialog.setMessage(resources.getString(R.string.want_to_apply_loan))
                alertDialog.setPositiveButton("Yes") { _, _ -> applyLoan() }
                        .setNegativeButton("No") { dialog, _ -> dialog.dismiss() }
                alertDialog.show()

            }

        }

    }


    private fun applyLoan() {
        if (CommonMethod.isNetworkAvailable(this@LoanApplyActivity)) {
            if (!checkIfLocationOpened()) {

                alertMessageNoGps()
            } else {

                val shared = SharedPref(this@LoanApplyActivity)
                if (shared.currentLat != "") {
                    progressDialog.show()
                    buttonSubmit.isClickable = false
                    buttonSubmit.isEnabled = false
                    loanApply()
                } else {
                    CommonMethod.customSnackBarError(rootLayout, this@LoanApplyActivity, resources.getString(R.string.location_not_available))
                }

            }

        } else {
            hideKeyboard()
            CommonMethod.customSnackBarError(rootLayout, this@LoanApplyActivity, resources.getString(R.string.no_internet))
        }
    }

    private fun hideKeyboard() {
        try {
            CommonMethod.hideKeyboardView(this@LoanApplyActivity)
        } catch (exp: Exception) {

        }
    }

    private fun checkIfLocationOpened(): Boolean {
        val provider = Settings.Secure.getString(contentResolver, Settings.Secure.LOCATION_PROVIDERS_ALLOWED)
        if (provider.contains("gps") || provider.contains("network")) {
            return true
        }
        return false
    }

    private fun loanApply() {
        val shared = SharedPref(this@LoanApplyActivity)
        CommonMethod.hideKeyboardView(this@LoanApplyActivity)
        val jsonObject = JsonObject()
        jsonObject.addProperty("loan_type", loanType)
        jsonObject.addProperty("product_id", productId)
        if (loanPurpose == "Others") {
            jsonObject.addProperty("loan_purpose", etDescription.text.toString())
        } else {
            jsonObject.addProperty("loan_purpose", loanPurpose)

        }
        if(loanTanure.isNotBlank()){
        jsonObject.addProperty("tenure",loanTanure)
        }
        jsonObject.addProperty("latitude", shared.currentLat)
        jsonObject.addProperty("longitude", shared.currentLng)

        /*jsonObject.addProperty("address",shared.address)
        jsonObject.addProperty("locality",shared.locality)
        jsonObject.addProperty("pincode",shared.pincode)*/

        val globalLoanPlanModel = GlobalLoanPlanModel.getInstance().modelData
        val logger = AppEventsLogger.newLogger(this@LoanApplyActivity)
        println("Loan Amount is ${globalLoanPlanModel?.loanAmount}")

        val params = Bundle()
        params.putString(AppEventsConstants.EVENT_PARAM_DESCRIPTION, (globalLoanPlanModel!!.loanAmount).toString())
        logger.logEvent(AppEventsConstants.EVENT_NAME_SUBMIT_APPLICATION, params)

        // CommonMethod.customSnackBarError(rootLayout, this@LoanApplyActivity,jsonObject.toString())

        println("data is $jsonObject")
        dismiss()
        val presenterLoanApply = PresenterLoanApply()
        presenterLoanApply.doLoanApply(this@LoanApplyActivity, jsonObject, this)

    }

    private fun loadDescription() {

        etChooseDescription.isFocusable = false
        etChooseDescription.setOnClickListener {

            showDescription()
        }

    }

    private fun loadTerms(productId:String){

        if(!progressDialog.isShowing){
            progressDialog.show()
        }
        val jsonObject = JsonObject()
        jsonObject.addProperty("product_id",productId)
        val presenterLoanPlans = PresenterLoanApplyTenure()
        presenterLoanPlans.getLoanTenureList(this,jsonObject,object :ICallBackLoanTenure{
            override fun onSuccessLoanTenureList(item: List<ResLoanTenure.Data.Option>) {
                if(progressDialog.isShowing){
                    progressDialog.dismiss()
                }
                if(listTerms.isNotEmpty()){
                    listTerms.clear()
                }
                listTerms.addAll(item)
                showTerms()
                tilChooseTerm.isFocusable = false
                etChooseTerm.setOnClickListener {
                    showTerms()
                }
            }

            override fun onEmptyLoanTenureList() {
                if(progressDialog.isShowing){
                    progressDialog.dismiss()
                }

                val dialogBuilder = AlertDialog.Builder(this@LoanApplyActivity, R.style.MyAlertDialogTheme)
                dialogBuilder.setMessage("Unable to find any Tenure for the loan. Default will be applied")
                    .setCancelable(false)
                    .setPositiveButton("Ok") { dialog, _ ->
                        dialog.dismiss()
                        val sharedPrefOBJ = SharedPref(this@LoanApplyActivity)
                        sharedPrefOBJ.removeShared()
                        startActivity(Intent(this@LoanApplyActivity, MainActivity::class.java))
                        overridePendingTransition(R.anim.right_in, R.anim.left_out)
                        finish()
                    }

                val alert = dialogBuilder.create()
                alert.setTitle(resources.getString(R.string.app_name))
                alert.show()

            }

            override fun onFailureLoanTenureList(jsonMessage: String) {
                if(progressDialog.isShowing){
                    progressDialog.dismiss()
                }

                val dialogBuilder = AlertDialog.Builder(this@LoanApplyActivity, R.style.MyAlertDialogTheme)
                dialogBuilder.setMessage(jsonMessage)
                    .setCancelable(false)
                    .setPositiveButton("Ok") { dialog, _ ->
                        dialog.dismiss()
                        val sharedPrefOBJ = SharedPref(this@LoanApplyActivity)
                        sharedPrefOBJ.removeShared()
                        startActivity(Intent(this@LoanApplyActivity, MainActivity::class.java))
                        overridePendingTransition(R.anim.right_in, R.anim.left_out)
                        finish()
                    }

                val alert = dialogBuilder.create()
                alert.setTitle(resources.getString(R.string.app_name))
                alert.show()

            }

            override fun onSessionTimeOut(message: String) {
                if(progressDialog.isShowing){
                    progressDialog.dismiss()
                }
                val dialogBuilder = AlertDialog.Builder(this@LoanApplyActivity, R.style.MyAlertDialogTheme)
                dialogBuilder.setMessage(message)
                    .setCancelable(false)
                    .setPositiveButton("Ok") { dialog, _ ->
                        dialog.dismiss()
                        val sharedPrefOBJ = SharedPref(this@LoanApplyActivity)
                        sharedPrefOBJ.removeShared()
                        startActivity(Intent(this@LoanApplyActivity, MainActivity::class.java))
                        overridePendingTransition(R.anim.right_in, R.anim.left_out)
                        finish()
                    }

                val alert = dialogBuilder.create()
                alert.setTitle(resources.getString(R.string.app_name))
                alert.show()


            }

        })

    }

    private fun showDescription() {

        val dialog = Dialog(this@LoanApplyActivity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.layout_list_single)
        val recyclerView = dialog.findViewById(R.id.recyclerView) as RecyclerView?
        val titleAdapter = DescriptionAdapter(this@LoanApplyActivity, listTitle, dialog, this)
        recyclerView?.layoutManager = LinearLayoutManager(this@LoanApplyActivity, RecyclerView.VERTICAL, false)
        recyclerView?.adapter = titleAdapter
        dialog.show()
    }

    private fun showTerms() {

        val dialog = Dialog(this@LoanApplyActivity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.layout_list_loan_terms)
        val recyclerView = dialog.findViewById(R.id.rvLoanTerms) as RecyclerView?
        val titleAdapter = LoanTermsListAdapter(this@LoanApplyActivity, listTerms, dialog, this)
        recyclerView?.layoutManager = LinearLayoutManager(this@LoanApplyActivity, RecyclerView.VERTICAL, false)
        recyclerView?.adapter = titleAdapter
        dialog.show()
    }

    override fun onSelectDescription(s: String) {

        etChooseDescription.setText(s)
        loanPurpose = s
        if (loanPurpose == "Others") {
            txt_loan_description.visibility = View.VISIBLE
            tilDescription.visibility = View.VISIBLE
            txt_max_words.visibility = View.VISIBLE
            txt_loan_description.visibility = View.VISIBLE
            etDescription.requestFocus()

        } else {
            txt_loan_description.visibility = View.GONE
            tilDescription.visibility = View.GONE
            txt_max_words.visibility = View.GONE
            txt_loan_description.visibility = View.GONE
        }
    }

    override fun onSelectTerms(t: ResLoanTenure.Data.Option) {
        // txt_loan_description.visibility = View.GONE
        //tilDescription.visibility = View.GONE
        //txt_max_words.visibility = View.GONE
        //txt_loan_description.visibility = View.GONE
        tilChooseTerm.visibility = View.VISIBLE
        etChooseTerm.setText("${t.weeks} Weeks")
        if(t.default){
        loanTanure = t.weeks.toString()}
        showDescription()

    }

    override fun onSuccessLoanApply() {
        buttonSubmit.isEnabled = true
        Toast.makeText(this@LoanApplyActivity, resources.getString(R.string.loan_applied_successfully), Toast.LENGTH_SHORT).show()
        dismiss()
        val sharedPref = SharedPref(this@LoanApplyActivity)
        sharedPref.navigationTab = resources.getString(R.string.open_tab_loan)
        val intent = Intent(this@LoanApplyActivity, DashboardActivity::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.right_in, R.anim.left_out)

    }

    override fun onErrorLoanApply(message: String) {
        dismiss()
        buttonSubmit.isClickable = true
        buttonSubmit.isEnabled = true
        CommonMethod.customSnackBarError(rootLayout, this@LoanApplyActivity, message)
    }

    override fun onSessionTimeOut(jsonMessage: String) {

        dismiss()
        val dialogBuilder = AlertDialog.Builder(this@LoanApplyActivity, R.style.MyAlertDialogTheme)
        dialogBuilder.setMessage(jsonMessage)
                .setCancelable(false)
                .setPositiveButton("Ok") { dialog, _ ->
                    dialog.dismiss()
                    val sharedPrefOBJ = SharedPref(this@LoanApplyActivity)
                    sharedPrefOBJ.removeShared()
                    startActivity(Intent(this@LoanApplyActivity, MainActivity::class.java))
                    overridePendingTransition(R.anim.right_in, R.anim.left_out)
                    finish()
                }

        val alert = dialogBuilder.create()
        alert.setTitle(resources.getString(R.string.app_name))
        alert.show()
    }


    private fun initLoader() {
        progressDialog = ProgressDialog(this@LoanApplyActivity, R.style.MyAlertDialogStyle)
        val message = SpannableString(resources.getString(R.string.loading))
        val face = Typeface.createFromAsset(assets, "fonts/Montserrat-Regular.ttf")
        message.setSpan(RelativeSizeSpan(1.0f), 0, message.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        //message.setSpan(CustomTypefaceSpan("", face), 0, message.length, 0)
        progressDialog.isIndeterminate = true
        progressDialog.setMessage(message)
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        progressDialog.setCancelable(false)
        progressDialog.setCanceledOnTouchOutside(false)

    }

    private fun dismiss() {
        if (progressDialog.isShowing) {
            progressDialog.dismiss()
        }
    }

    private fun alertMessageNoGps() {
        val builder = AlertDialog.Builder(this@LoanApplyActivity, R.style.MyAlertDialogTheme)
        builder.setMessage(
                "Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false).setPositiveButton("Yes"
                ) { _, _ ->
                    startActivity(Intent(
                            Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                }
                .setNegativeButton("No") { dialog, _ -> dialog.cancel() }
        val alert = builder.create()
        alert.setCancelable(false)
        alert.show()
    }

    override fun onLocationChanged(location: Location) {

        val sharedPref = SharedPref(this@LoanApplyActivity)
        sharedPref.currentLat = location.latitude.toString()
        sharedPref.currentLng = location.longitude.toString()


        /*val geocoder = Geocoder(this, Locale.ENGLISH)
        //val address = geocoder.getFromLocation(location.latitude, location.longitude, 1)
        if (Geocoder.isPresent()){
            try {
                Log.e("latitude", "inside latitude--${location.latitude}")
                val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                if (addresses != null && addresses.size > 0) {
                    sharedPref.address = addresses[0].getAddressLine(0)
                    sharedPref.locality = addresses[0].locality
                    val state: String = addresses[0].adminArea
                    val country: String = addresses[0].countryName
                    sharedPref.pincode = addresses[0].postalCode
                    val knownName: String = addresses[0].featureName
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }else{
            Toast.makeText(this@LoanApplyActivity,"Geocoder is not present in your country",Toast.LENGTH_LONG).show()
        }*/


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

        countDownTimer = object : CountDownTimer(CommonMethod.sessionTime().toLong(), 1000) {
            override fun onTick(millisUntilFinished: Long) {

            }

            override fun onFinish() {
                onSessionTimeOut(resources.getString(R.string.session_time_out))
                countDownTimer.cancel()

            }
        }
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
        MyApplication.getInstance().trackScreenView(this@LoanApplyActivity::class.java.simpleName)

    }

    override fun onConnected(p0: Bundle?) {
        //startLocationUpdates()
    }

    override fun onConnectionSuspended(p0: Int) {

    }

    override fun onConnectionFailed(p0: ConnectionResult) {

    }


}
