package com.app.l_pesa.loanHistory.view

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.text.Spanned
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.app.l_pesa.R
import com.app.l_pesa.application.MyApplication
import com.app.l_pesa.common.CommonMethod
import com.app.l_pesa.common.CommonTextRegular
import com.app.l_pesa.common.SharedPref
import com.app.l_pesa.loanHistory.model.GlobalLoanHistoryModel
import com.facebook.appevents.AppEventsConstants
import com.facebook.appevents.AppEventsLogger
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_loan_history_details.*
import kotlinx.android.synthetic.main.content_loan_history_details.*
import java.text.DecimalFormat


class LoanHistoryDetailsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loan_history_details)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbarFont(this@LoanHistoryDetailsActivity)

        initData()
    }

    @SuppressLint("SetTextI18n")
    private fun initData()
    {
        val shared= SharedPref(this@LoanHistoryDetailsActivity)

        val format = DecimalFormat()
        format.isDecimalSeparatorAlwaysShown = false

        val loanHistoryData= GlobalLoanHistoryModel.getInstance().modelData

        val logger = AppEventsLogger.newLogger(this@LoanHistoryDetailsActivity)
        val params =  Bundle()
        params.putString(AppEventsConstants.EVENT_PARAM_CONTENT_ID, loanHistoryData?.loan_id.toString())
        params.putString(AppEventsConstants.EVENT_PARAM_CONTENT_TYPE, "Loan History Details")
        params.putString(AppEventsConstants.EVENT_PARAM_CURRENCY, loanHistoryData?.currency_code)
        logger.logEvent(AppEventsConstants.EVENT_NAME_VIEWED_CONTENT, params)

        txt_loan_product_price.text=loanHistoryData!!.loan_amount_txt
        txt_loan_no_val.text = loanHistoryData.identity_number
        txt_interest_rate.text = loanHistoryData.interest_rate
        txt_loan_amount_value.text = loanHistoryData.currency_code+" "+format.format(loanHistoryData.actual_loan_amount.toDouble()).toString()
        txt_request_date.text = loanHistoryData.applied_date

        txt_loan_duration.text = loanHistoryData.duration

        if(loanHistoryData.convertion_dollar_value.isEmpty()){
            txt_currency_conversion_rate.text = loanHistoryData.currency_code+" "+"0"
        }else if(!loanHistoryData.currency_flag){
            txt_currency_conversion_rate.text = "n/a"
        } else{
            txt_currency_conversion_rate.text = loanHistoryData.currency_code+" "+format.format(loanHistoryData.convertion_dollar_value.toDouble()).toString()
        }

        if(!loanHistoryData.currency_flag){
            txt_currency_conversion.text = "n/a"
            txt_conversion_charge.text = "n/a"
        }else{
            txt_currency_conversion.text = loanHistoryData.currency_code+" "+format.format(loanHistoryData.convertion_loan_amount.toDouble()).toString()
            txt_conversion_charge.text = loanHistoryData.conversion_charge+" ("+loanHistoryData.currency_code+" "+ format.format(loanHistoryData.conversion_charge_amount.toDouble()).toString()+")"
        }

        txt_processing_fee.text = loanHistoryData.processing_fees+" ("+loanHistoryData.currency_code+" "+format.format(loanHistoryData.processing_fees_amount.toDouble()).toString()+")"
        txt_purpose.text = loanHistoryData.loan_purpose_message


        txt_credit_score.text =  fromHtml(resources.getString(R.string.current_credit_score)+"<font color='#333333'>"+shared.userCreditScore+"</font>")
        txt_credit_score_at_time.text = fromHtml(resources.getString(R.string.previous_credit_score)+"<font color='#333333'>"+loanHistoryData.cr_sc_when_requesting_loan+"</font>")

        if(loanHistoryData.loan_status=="DA")
        {
            txt_date.text = this@LoanHistoryDetailsActivity.resources.getString(R.string.disapproved_on)
            txt_approval_date.text = loanHistoryData.disapprove_date
        }
        else
        {
            txt_date.text = this@LoanHistoryDetailsActivity.resources.getString(R.string.approved_on)
            if(loanHistoryData.loan_status=="P")
            {
                txt_approval_date.text = "---"
            }
            else
            {
                txt_approval_date.text = loanHistoryData.sanctioned_date
            }


        }

        txt_credit_score_at_time.text = fromHtml(resources.getString(R.string.previous_credit_score)+"<font color='#333333'>"+loanHistoryData.cr_sc_when_requesting_loan+"</font>")

        when {
            loanHistoryData.loan_status=="C" -> {
                rlDisApproved.visibility=View.GONE
                txt_status.text = resources.getString(R.string.completed)
                imgStatus.setBackgroundResource(R.drawable.ic_approved_icon)
            }
            loanHistoryData.loan_status=="A" -> {
                rlDisApproved.visibility=View.GONE
                txt_status.text = resources.getString(R.string.approved)
                imgStatus.setBackgroundResource(R.drawable.ic_approved_icon)
            }
            loanHistoryData.loan_status=="P" -> {
                rlDisApproved.visibility=View.GONE
                txt_status.text = resources.getString(R.string.pending)
                imgStatus.setBackgroundResource(R.drawable.ic_loan_pending)
                rlPayment.visibility=View.GONE
            }
            loanHistoryData.loan_status=="DA" -> {
                txt_status.text = resources.getString(R.string.disapproved)
                txt_status.setTextColor(Color.RED)
                imgStatus.setBackgroundResource(R.drawable.ic_loan_disapproved)
                rlDisApproved.visibility=View.VISIBLE
                rlPayment.visibility=View.GONE
                txt_disapproved.text = loanHistoryData.disapprove_reason
            }
            else -> {
                rlDisApproved.visibility=View.GONE
                txt_status.text = resources.getString(R.string.due)
                txt_status.setTextColor(Color.RED)
                imgStatus.setBackgroundResource(R.drawable.ic_due_icon)
            }
        }


        ll_payment_scheduled.setOnClickListener {

            if(CommonMethod.isNetworkAvailable(this@LoanHistoryDetailsActivity))
            {
                shared.payFullAmount=loanHistoryData.loan_status
                val bundle     = intent.extras
                val loanType   = bundle!!.getString("LOAN_TYPE")
                bundle.putString("LOAN_TYPE",loanType)
                bundle.putString("LOAN_ID",loanHistoryData.loan_id.toString())
                val intent = Intent(this@LoanHistoryDetailsActivity, LoanPaybackScheduledActivity::class.java)
                intent.putExtras(bundle)
                startActivity(intent,bundle)
                overridePendingTransition(R.anim.right_in, R.anim.left_out)

            }
            else
            {
                customSnackBarError(rlRoot,resources.getString(R.string.no_internet))
            }
        }

        ll_payment_history.setOnClickListener {

            if(CommonMethod.isNetworkAvailable(this@LoanHistoryDetailsActivity))
            {
                shared.payFullAmount=loanHistoryData.loan_status
                val bundle     = intent.extras
                val loanType   = bundle!!.getString("LOAN_TYPE")
                bundle.putString("LOAN_TYPE",loanType)
                bundle.putString("LOAN_ID",loanHistoryData.loan_id.toString())
                val intent = Intent(this@LoanHistoryDetailsActivity, LoanPaymentHistory::class.java)
                intent.putExtras(bundle)
                startActivity(intent,bundle)
                overridePendingTransition(R.anim.right_in, R.anim.left_out)

            }
            else
            {
                customSnackBarError(rlRoot,resources.getString(R.string.no_internet))
            }
        }


    }

    private fun customSnackBarError(view: View, message:String) {

        val snackBarOBJ = Snackbar.make(view, "", Snackbar.LENGTH_SHORT)
        snackBarOBJ.view.setBackgroundColor(ContextCompat.getColor(this@LoanHistoryDetailsActivity,R.color.colorRed))
        (snackBarOBJ.view as ViewGroup).removeAllViews()
        val customView = LayoutInflater.from(this).inflate(R.layout.snackbar_error, null)
        (snackBarOBJ.view as ViewGroup).addView(customView)

        val txtTitle=customView.findViewById(R.id.txtTitle) as CommonTextRegular

        txtTitle.text = message

        snackBarOBJ.show()
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

    public override fun onResume() {
        super.onResume()
        MyApplication.getInstance().trackScreenView(this@LoanHistoryDetailsActivity::class.java.simpleName)

    }

}
