package com.app.l_pesa.loanplan.view

import android.app.Activity
import android.app.Dialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Typeface
import android.location.LocationManager
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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.l_pesa.R
import com.app.l_pesa.common.*
import com.app.l_pesa.dashboard.view.DashboardActivity
import com.app.l_pesa.loanHistory.inter.ICallBackLoanApply
import com.app.l_pesa.loanplan.adapter.DescriptionAdapter
import com.app.l_pesa.loanplan.inter.ICallBackDescription
import com.app.l_pesa.loanplan.presenter.PresenterLoanApply
import com.app.l_pesa.main.view.MainActivity
import com.google.gson.JsonObject
import com.kaopiz.kprogresshud.KProgressHUD
import kotlinx.android.synthetic.main.activity_loan_apply.*
import kotlinx.android.synthetic.main.content_loan_apply.*


class LoanApplyActivity : AppCompatActivity(), ICallBackDescription, ICallBackLoanApply {

    private var loanPurpose=""
    private val listTitle = arrayListOf("For Transport","To Pay Bills","To Clear Debit","To Buy Foodstuff","Emergency Purposes","To Buy Medicine","Build Credit","Others")
    private lateinit  var progressDialog: KProgressHUD
    private lateinit var countDownTimer: CountDownTimer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loan_apply)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbarFont(this@LoanApplyActivity)

        initData()

    }

    private fun initData()
    {
        val bundle       = intent.extras
        val productID    = bundle!!.getString("PRODUCT_ID")
        val loanType     = bundle.getString("LOAN_TYPE")

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
                CommonMethod.customSnackBarError(llRoot,this@LoanApplyActivity,resources.getString(R.string.required_loan_purpose))
            }
            else if(loanPurpose=="Others" && TextUtils.isEmpty(etDescription.text.toString()))
            {
                etDescription.requestFocus()
                CommonMethod.customSnackBarError(llRoot,this@LoanApplyActivity,resources.getString(R.string.required_loan_purpose_description))
            }
            else
            {
                val alertDialog = AlertDialog.Builder(this@LoanApplyActivity)
                alertDialog.setTitle(resources.getString(R.string.app_name))
                alertDialog.setMessage(resources.getString(R.string.want_to_apply_loan))
                alertDialog.setPositiveButton("Yes") { _, _ -> applyLoan(loanType,productID) }
                        .setNegativeButton("No") { dialog, _ -> dialog.dismiss() }
                alertDialog.show()

            }

        }

        fetchLocation()

    }



    private fun applyLoan(loan_type: String?, product_id: String?)
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
                    loanApply(loan_type,product_id)
                }
                else
                {
                    CommonMethod.customSnackBarError(llRoot,this@LoanApplyActivity,resources.getString(R.string.please_wait))
                }

            }
            else
            {
                showAlert()
            }

        }
        else
        {
           CommonMethod.customSnackBarError(llRoot,this@LoanApplyActivity,resources.getString(R.string.no_internet))
        }
    }

    private fun loanApply(loan_type: String?, product_id: String?)
    {
        val shared=SharedPref(this@LoanApplyActivity)
        CommonMethod.hideKeyboardView(this@LoanApplyActivity)
        val jsonObject = JsonObject()
        jsonObject.addProperty("loan_type",loan_type)
        jsonObject.addProperty("product_id",product_id)
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

        //println("REQUEST"+jsonObject.toString())

        val presenterLoanApply= PresenterLoanApply()
        presenterLoanApply.doLoanApply(this@LoanApplyActivity,jsonObject,this)

    }

    private fun loadDescription()
    {
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
        CommonMethod.customSnackBarError(llRoot,this@LoanApplyActivity,message)
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


    private fun showAlert() {
        val dialog = AlertDialog.Builder(this@LoanApplyActivity)
        dialog.setTitle("Enable Location")
                .setMessage("Your Locations Settings is set to 'Off'.\nPlease Enable Location to use this app")
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

    private fun initLoader()
    {
        progressDialog= KProgressHUD.create(this@LoanApplyActivity)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setCancellable(false)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f)

    }

    private fun dismiss()
    {
        if(progressDialog.isShowing)
        {
            progressDialog.dismiss()
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
        fetchLocation()
    }

    private fun fetchLocation()
    {
        val intent = Intent(this, LocationBackgroundService::class.java)
        startService(intent)

        val sharedPref=SharedPref(this@LoanApplyActivity)

        androidx.localbroadcastmanager.content.LocalBroadcastManager.getInstance(this).registerReceiver(
                object : BroadcastReceiver() {
                    override fun onReceive(context: Context, intent: Intent) {

                        sharedPref.currentLat  = intent.getStringExtra(EXTRA_LATITUDE)!!
                        sharedPref.currentLng = intent.getStringExtra(EXTRA_LONGITUDE)!!

                    }
                }, IntentFilter(ACTION_LOCATION_BROADCAST)
        )

    }

    public override fun onDestroy() {

        stopService(Intent(this, LocationBackgroundService::class.java))
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


}
