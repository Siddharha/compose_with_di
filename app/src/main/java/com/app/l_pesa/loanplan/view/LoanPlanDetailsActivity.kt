package com.app.l_pesa.loanplan.view

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity;
import android.text.Html
import android.text.Spanned
import android.view.MenuItem
import android.widget.TextView
import com.app.l_pesa.R
import com.app.l_pesa.common.SharedPref
import com.app.l_pesa.loanplan.model.GlobalLoanPlanModel

import kotlinx.android.synthetic.main.activity_loan_plan_details.*
import kotlinx.android.synthetic.main.content_loan_plan_details.*

class LoanPlanDetailsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loan_plan_details)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbarFont(this@LoanPlanDetailsActivity)

        initData()

    }

    @SuppressLint("SetTextI18n")
    private fun initData()
    {
        val globalLoanPlanModel= GlobalLoanPlanModel.getInstance().modelData
        val sharedPref= SharedPref(this@LoanPlanDetailsActivity)

        txt_loan_product_price.text = "$"+globalLoanPlanModel!!.loanAmount.toString()
        txt_interest_rate.text = globalLoanPlanModel.loanInterestRate.toString()+"%"
        txt_credit_score_at_time.text = fromHtml(resources.getString(R.string.previous_credit_score)+"<font color='#333333'>"+"</font>")
        txt_credit_score.text = fromHtml(resources.getString(R.string.current_credit_score)+"<font color='#333333'>"+sharedPref.userCreditScore+"</font>")
        txt_loan_amount_value.text = globalLoanPlanModel.currencyCode
        txt_loan_status.text = globalLoanPlanModel.btnText
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
