package com.app.l_pesa.loanplan.view

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Html
import android.text.Spanned
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.app.l_pesa.R
import com.app.l_pesa.application.MyApplication
import com.app.l_pesa.common.CommonMethod
import com.app.l_pesa.common.SharedPref
import com.app.l_pesa.loanplan.model.GlobalLoanPlanModel
import com.app.l_pesa.main.view.MainActivity
import com.facebook.appevents.AppEventsConstants
import com.facebook.appevents.AppEventsLogger
import kotlinx.android.synthetic.main.activity_loan_plan_details.*
import kotlinx.android.synthetic.main.content_loan_plan_details.*
import java.text.DecimalFormat

class LoanPlanDetailsActivity : AppCompatActivity() {

    private lateinit var countDownTimer: CountDownTimer
    private val MY_PERMISSIONS_REQUEST_LOCATION = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loan_plan_details)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbarFont(this@LoanPlanDetailsActivity)

        initData()
        initTimer()

    }

    @SuppressLint("SetTextI18n")
    private fun initData()
    {

        val format = DecimalFormat()
        format.isDecimalSeparatorAlwaysShown = false

        val globalLoanPlanModel= GlobalLoanPlanModel.getInstance().modelData
        val sharedPref= SharedPref(this@LoanPlanDetailsActivity)

        val logger = AppEventsLogger.newLogger(this@LoanPlanDetailsActivity)
        val params =  Bundle()
        params.putString(AppEventsConstants.EVENT_PARAM_CONTENT_ID, globalLoanPlanModel?.productId.toString())
        params.putString(AppEventsConstants.EVENT_PARAM_CONTENT_TYPE, "Loan Plan Details")
        logger.logEvent(AppEventsConstants.EVENT_NAME_VIEWED_CONTENT, params)

        txt_loan_product_price.text = fromHtml(resources.getString(R.string.loan_product)+"<font color='#333333'>"+" $"+format.format(globalLoanPlanModel!!.loanAmount).toString()+"</font>")
        txt_interest_rate.text = fromHtml(resources.getString(R.string.interest_rate)+"<font color='#333333'>"+" "+globalLoanPlanModel.loanInterestRate.toString()+"%"+"</font>")
        txt_required_credit_score.text = fromHtml(resources.getString(R.string.required_credit_score)+"<font color='#333333'>"+ globalLoanPlanModel.requiredCreditScore.toString()+"</font>")
        txt_currency_conversion_rate.text = fromHtml(resources.getString(R.string.currency_conversion_rate)+"<font color='#333333'>"+" "+ globalLoanPlanModel.currencyCode+" "+format.format(globalLoanPlanModel.convertionDollarValue)+"</font>")
        txt_loan_after_currency_conversion.text = fromHtml(resources.getString(R.string.loan_after_currency_conversion)+"<font color='#333333'>"+" "+ globalLoanPlanModel.currencyCode+" "+format.format(globalLoanPlanModel.convertionLoanAmount).toString()+"</font>")
        txt_credit_score.text = fromHtml(resources.getString(R.string.current_credit_score)+"<font color='#333333'>"+sharedPref.userCreditScore+"</font>")
        txt_loan_amount_value.text = globalLoanPlanModel.currencyCode+" "+format.format(globalLoanPlanModel.actualLoanAmount)
        txt_loan_status.text = globalLoanPlanModel.btnText
        txt_conversion_charge.text =fromHtml(resources.getString(R.string.conversion_charge)+"<font color='#333333'>"+" "+ globalLoanPlanModel.conversionCharge.toString()+"% ("+globalLoanPlanModel.currencyCode+" "+format.format(globalLoanPlanModel.conversionChargeAmount)+")"+"</font>")
        txt_processing_fee.text =fromHtml(resources.getString(R.string.processing_fee)+"<font color='#333333'>"+" "+ globalLoanPlanModel.processingFees.toString()+"% ("+globalLoanPlanModel.currencyCode+" "+format.format(globalLoanPlanModel.processingFeesAmount)+")"+"</font>")

        Button_apply_loan.setOnClickListener {

            //
            if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                        MY_PERMISSIONS_REQUEST_LOCATION)
            }else{
                val bundleOBJ  = intent.extras
                val bundle     = Bundle()
                bundle.putString("PRODUCT_ID",globalLoanPlanModel.productId.toString())
                bundle.putString("LOAN_TYPE",bundleOBJ!!.getString("LOAN_TYPE"))
                val intent = Intent(this@LoanPlanDetailsActivity, LoanApplyActivity::class.java)
                intent.putExtras(bundle)
                startActivity(intent,bundle)
                overridePendingTransition(R.anim.right_in, R.anim.left_out)
            }


        }
    }

    private fun fromHtml(source: String): Spanned {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml(source, Html.FROM_HTML_MODE_LEGACY)
        } else {
            Html.fromHtml(source)
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

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.left_in, R.anim.right_out)
    }

    fun onSessionTimeOut(jsonMessage: String) {

        val dialogBuilder = AlertDialog.Builder(this@LoanPlanDetailsActivity,R.style.MyAlertDialogTheme)
        dialogBuilder.setMessage(jsonMessage)
                .setCancelable(false)
                .setPositiveButton("Ok") { dialog, _ ->
                    dialog.dismiss()
                    val sharedPrefOBJ= SharedPref(this@LoanPlanDetailsActivity)
                    sharedPrefOBJ.removeShared()
                    startActivity(Intent(this@LoanPlanDetailsActivity, MainActivity::class.java))
                    overridePendingTransition(R.anim.right_in, R.anim.left_out)
                    finish()
                }

        val alert = dialogBuilder.create()
        alert.setTitle(resources.getString(R.string.app_name))
        alert.show()
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
        MyApplication.getInstance().trackScreenView(this@LoanPlanDetailsActivity::class.java.simpleName)

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
             MY_PERMISSIONS_REQUEST_LOCATION -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty()
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                                    Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        //Request location updates:
                        Button_apply_loan.performClick()
                        //locationManager.requestLocationUpdates(provider, 400, 1, this);
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.

                }
                return
            }

        }
    }


}
