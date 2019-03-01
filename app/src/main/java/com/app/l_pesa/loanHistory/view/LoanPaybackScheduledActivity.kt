package com.app.l_pesa.loanHistory.view

import android.app.Activity
import android.graphics.Typeface
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.MenuItem
import android.widget.TextView
import com.app.l_pesa.R
import com.app.l_pesa.loanHistory.adapter.PaymentScheduleAdapter
import com.app.l_pesa.loanHistory.inter.ICallBackPaybackSchedule
import com.app.l_pesa.loanHistory.model.ResPaybackSchedule
import com.app.l_pesa.loanHistory.presenter.PresenterPaybackSchedule
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.activity_loan_payback_scheduled.*
import kotlinx.android.synthetic.main.content_loan_payback_scheduled.*


class LoanPaybackScheduledActivity : AppCompatActivity(), ICallBackPaybackSchedule {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loan_payback_scheduled)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbarFont(this@LoanPaybackScheduledActivity)

        initLoad()


    }

    private fun initLoad()
    {
        val bundle     = intent.extras
        val loanType   = bundle!!.getString("LOAN_TYPE")
        val loanId     = bundle.getString("LOAN_ID")

        val jsonObject = JsonObject()
        jsonObject.addProperty("loan_type",loanType)
        jsonObject.addProperty("loan_id",loanId)


        val presenterPaybackSchedule= PresenterPaybackSchedule()
        presenterPaybackSchedule.doPaybackSchedule(this@LoanPaybackScheduledActivity,jsonObject,this)
    }

    override fun onSuccessPaybackSchedule(data: ResPaybackSchedule.Data) {


        txt_total_payback.text = data.loanInfo.totalPayback

        if(data.paybackSchedule.size>0)
        {

            val adapterPaymentSchedule          = PaymentScheduleAdapter(this@LoanPaybackScheduledActivity,data.paybackSchedule)
            rlPayback.layoutManager            = LinearLayoutManager(this@LoanPaybackScheduledActivity, LinearLayoutManager.VERTICAL, false)
            rlPayback.adapter                  = adapterPaymentSchedule
        }
    }

    override fun onErrorPaybackSchedule(jsonMessage: String) {


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
