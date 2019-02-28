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
        txt_credit_score.text =  shared.userCreditScore
        txt_loan_amount_value.text = loanHistoryData.actual_loan_amount
        txt_request_date.text = loanHistoryData.applied_date
        txt_approval_date.text = loanHistoryData.sanctioned_date
        //txt_loan_duration.text = loanHistoryData.sanctioned_date


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
