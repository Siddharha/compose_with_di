package com.app.l_pesa.loanHistory.view

import android.app.Activity
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import com.app.l_pesa.R
import com.app.l_pesa.common.CommonMethod
import com.app.l_pesa.common.SharedPref
import com.app.l_pesa.loanHistory.adapter.LoanPaymentHistoryAdapter
import com.app.l_pesa.loanHistory.inter.ICallBackPaymentHistory
import com.app.l_pesa.loanHistory.model.ResPaymentHistory
import com.app.l_pesa.loanHistory.presenter.PresenterPaymentHistory
import com.app.l_pesa.main.view.MainActivity
import kotlinx.android.synthetic.main.activity_loan_payment_history.*
import kotlinx.android.synthetic.main.content_loan_payment_history.*
import java.util.*

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
        rlPaybackHistory.layoutManager   = androidx.recyclerview.widget.LinearLayoutManager(this@LoanPaymentHistory, androidx.recyclerview.widget.LinearLayoutManager.VERTICAL, false)
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

    override fun onSessionTimeOut(message: String) {

        swipeRefreshLayout.isRefreshing=false
        val dialogBuilder = AlertDialog.Builder(this@LoanPaymentHistory)
        dialogBuilder.setMessage(message)
                .setCancelable(false)
                .setPositiveButton("Ok") { dialog, _ ->
                    dialog.dismiss()
                    val sharedPrefOBJ= SharedPref(this@LoanPaymentHistory)
                    sharedPrefOBJ.removeShared()
                    startActivity(Intent(this@LoanPaymentHistory, MainActivity::class.java))
                    overridePendingTransition(R.anim.right_in, R.anim.left_out)
                    finish()
                }

        val alert = dialogBuilder.create()
        alert.setTitle(resources.getString(R.string.app_name))
        alert.show()

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

}
