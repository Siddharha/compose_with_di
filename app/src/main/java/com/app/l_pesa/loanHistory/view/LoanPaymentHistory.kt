package com.app.l_pesa.loanHistory.view

import android.app.Activity
import android.graphics.Typeface
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.app.l_pesa.R
import com.app.l_pesa.common.CommonMethod
import com.app.l_pesa.loanHistory.adapter.LoanPaymentHistoryAdapter
import com.app.l_pesa.loanHistory.inter.ICallBackPaymentHistory
import com.app.l_pesa.loanHistory.model.ResPaymentHistory
import com.app.l_pesa.loanHistory.presenter.PresenterPaymentHistory

import kotlinx.android.synthetic.main.activity_loan_payment_history.*
import kotlinx.android.synthetic.main.content_loan_payment_history.*
import java.util.ArrayList

class LoanPaymentHistory : AppCompatActivity(),ICallBackPaymentHistory {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loan_payment_history)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbarFont(this@LoanPaymentHistory)

        initData()
        swipeRefresh()

    }

    private fun initData()
    {
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent)

        val bundle     = intent.extras
        val loanType   = bundle!!.getString("LOAN_TYPE")
        val loanId     = bundle.getString("LOAN_ID")

        println("JSON"+loanType+"ID"+loanId)

        if(CommonMethod.isNetworkAvailable(this@LoanPaymentHistory))
        {
            swipeRefreshLayout.isRefreshing=true
            val presenterPaymentHistory= PresenterPaymentHistory()
            presenterPaymentHistory.getPaymentHistory(this@LoanPaymentHistory,loanType!!,loanId!!,this)
        }
        else
        {
            CommonMethod.customSnackBarError(rootLayout,this@LoanPaymentHistory,resources.getString(R.string.no_internet))
        }
    }

    private fun swipeRefresh()
    {

        swipeRefreshLayout.setOnRefreshListener {
        initData()
        }
    }

    override fun onSuccessPaymentHistory(paymentHistory: ArrayList<ResPaymentHistory.PaymentHistory>) {

        swipeRefreshLayout.isRefreshing =false
        cardView.visibility=View.INVISIBLE
        rlPaybackHistory.visibility=View.VISIBLE

        val adapterPaymentSchedule       = LoanPaymentHistoryAdapter(this@LoanPaymentHistory,paymentHistory)
        rlPaybackHistory.layoutManager   = LinearLayoutManager(this@LoanPaymentHistory, LinearLayoutManager.VERTICAL, false)
        rlPaybackHistory.adapter         = adapterPaymentSchedule

    }

    override fun onEmptyPaymentHistory() {

        swipeRefreshLayout.isRefreshing=false
        rlPaybackHistory.visibility=View.INVISIBLE
        cardView.visibility=View.VISIBLE
    }

    override fun onErrorPaymentHistory(message: String) {

        cardView.visibility=View.INVISIBLE
        rlPaybackHistory.visibility=View.INVISIBLE
        swipeRefreshLayout.isRefreshing=false
        CommonMethod.customSnackBarError(rootLayout,this@LoanPaymentHistory,message)
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
