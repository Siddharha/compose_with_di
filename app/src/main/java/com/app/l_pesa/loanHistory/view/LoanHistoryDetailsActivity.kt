package com.app.l_pesa.loanHistory.view

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Typeface
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.widget.TextView
import com.app.l_pesa.R
import com.app.l_pesa.common.SharedPref
import com.app.l_pesa.loanHistory.model.ModelHistoryData

import kotlinx.android.synthetic.main.activity_loan_history_details.*
import kotlinx.android.synthetic.main.content_loan_history_details.*
import android.text.Html
import android.os.Build
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.text.Spanned
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.app.l_pesa.common.CommonMethod
import com.app.l_pesa.common.CommonTextRegular


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

        val loanHistoryData= ModelHistoryData.getInstance().modelData

        txt_loan_product_price.text=" $"+loanHistoryData!!.loan_amount
        txt_loan_no_val.text = loanHistoryData.loan_id.toString()
        txt_interest_rate.text = loanHistoryData.interest_rate
        txt_loan_amount_value.text = loanHistoryData.actual_loan_amount
        txt_request_date.text = loanHistoryData.applied_date
        txt_approval_date.text = loanHistoryData.sanctioned_date
        txt_loan_duration.text = loanHistoryData.duration
        txt_currency_conversion_rate.text = loanHistoryData.currency_code+" "+loanHistoryData.convertion_dollar_value
        txt_currency_conversion.text = loanHistoryData.currency_code+" "+loanHistoryData.convertion_loan_amount
        txt_processing_fee.text = loanHistoryData.processing_fees+" ("+loanHistoryData.currency_code+ loanHistoryData.processing_fees_amount+")"
        txt_purpose.text = loanHistoryData.loan_purpose_message
        txt_conversion_charge.text = loanHistoryData.conversion_charge+" ("+loanHistoryData.currency_code+" "+ loanHistoryData.conversion_charge_amount+")"

        txt_credit_score.text =  fromHtml(resources.getString(R.string.current_credit_score)+"<font color='#333333'>"+shared.userCreditScore+"</font>")
        txt_credit_score_at_time.text = fromHtml(resources.getString(R.string.previous_credit_score)+"<font color='#333333'>"+loanHistoryData.cr_sc_when_requesting_loan+"</font>")

        when {
            loanHistoryData.loan_status=="C" -> {
                txt_status.text = resources.getString(R.string.completed)
                imgStatus.setBackgroundResource(R.drawable.ic_approved_icon)
            }
            loanHistoryData.loan_status=="A" -> {
                txt_status.text = resources.getString(R.string.approved)
                imgStatus.setBackgroundResource(R.drawable.ic_approved_icon)
            }
            loanHistoryData.loan_status=="P" -> {
                txt_status.text = resources.getString(R.string.pending)
                imgStatus.setBackgroundResource(R.drawable.ic_pending_icon)
            }
            loanHistoryData.loan_status=="DA" -> {
                txt_status.text = resources.getString(R.string.disapproved)
                imgStatus.setBackgroundResource(R.drawable.ic_disapproved_icon)
            }
            else -> {
                txt_status.text = resources.getString(R.string.due)
                imgStatus.setBackgroundResource(R.drawable.ic_due_icon)
            }
        }


        ll_payment_scheduled.setOnClickListener {

            if(CommonMethod.isNetworkAvailable(this@LoanHistoryDetailsActivity))
            {

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
