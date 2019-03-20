package com.app.l_pesa.loanHistory.view

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Typeface
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.Window
import android.widget.TextView
import com.app.l_pesa.R
import com.app.l_pesa.common.CommonMethod
import com.app.l_pesa.common.CommonTextRegular
import com.app.l_pesa.common.SharedPref
import com.app.l_pesa.loanHistory.adapter.PaymentScheduleAdapter
import com.app.l_pesa.loanHistory.inter.ICallBackPaybackSchedule
import com.app.l_pesa.loanHistory.model.ResPaybackSchedule
import com.app.l_pesa.loanHistory.presenter.PresenterPaybackSchedule
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.activity_loan_payback_scheduled.*
import kotlinx.android.synthetic.main.content_loan_payback_scheduled.*


class LoanPaybackScheduledActivity : AppCompatActivity(), ICallBackPaybackSchedule {

    private var dataOBJ :ResPaybackSchedule.Data ?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loan_payback_scheduled)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbarFont(this@LoanPaybackScheduledActivity)

        swipeRefresh()
        initLoad()


    }

    private fun swipeRefresh()
    {

        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent)
        swipeRefreshLayout.setOnRefreshListener {

            if(CommonMethod.isNetworkAvailable(this@LoanPaybackScheduledActivity))
            {
                initLoad()
            }
            else
            {

               CommonMethod.customSnackBarError(llRoot,this@LoanPaybackScheduledActivity,resources.getString(R.string.no_internet))

            }

        }


        val sharedPref= SharedPref(this@LoanPaybackScheduledActivity)
        if(sharedPref.payFullAmount=="C" )
        {
            button_pay_all.visibility= View.INVISIBLE
        }
        else
        {
            button_pay_all.visibility= View.VISIBLE
            button_pay_all.setOnClickListener {

                if(swipeRefreshLayout.isRefreshing)
                {
                    CommonMethod.customSnackBarError(llRoot,this@LoanPaybackScheduledActivity,resources.getString(R.string.please_wait))
                }
                else
                {

                    if(dataOBJ!!.loanInfo!!.loanId!=0)
                    {
                        payAll(dataOBJ!!)
                    }
                }

            }
        }


    }

    @SuppressLint("SetTextI18n")
    private fun payAll(dataOBJ: ResPaybackSchedule.Data)
    {
        val alertDialog         = AlertDialog.Builder(this@LoanPaybackScheduledActivity).create()
        val inflater            = LayoutInflater.from(this@LoanPaybackScheduledActivity)
        val dialogView          = inflater.inflate(R.layout.dialog_payment_schedule, null)
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        alertDialog!!.setCancelable(true)
        alertDialog.setCanceledOnTouchOutside(true)
        alertDialog.setView(dialogView)
        alertDialog.show()

        val txtTitle   = dialogView.findViewById<CommonTextRegular>(R.id.txtTitle)
        val txtContent = dialogView.findViewById<TextView>(R.id.txtContent)
        val txtData    = dialogView.findViewById<TextView>(R.id.txtData)

        txtTitle.text   =   dataOBJ.loanInfo!!.payment_message!!.header
        txtContent.text =   dataOBJ.loanInfo!!.payment_message!!.header2
        txtData.text    =   "Amount to pay is: "+dataOBJ.loanInfo!!.currencyCode+" "+dataOBJ.loanInfo!!.payfullamount!!.paidAmount.toString()+"\n"+
                "Reference number is: "+dataOBJ.loanInfo!!.identityNumber+"\n"+
                "L-Pesa Short code is: "+dataOBJ.loanInfo!!.merchantCode.toString()

    }

    private fun initLoad()
    {
        swipeRefreshLayout.isRefreshing = true
        val bundle     = intent.extras
        val loanType   = bundle!!.getString("LOAN_TYPE")
        val loanId     = bundle.getString("LOAN_ID")

        val jsonObject = JsonObject()
        jsonObject.addProperty("loan_type",loanType)
        jsonObject.addProperty("loan_id",loanId)

        val presenterPaybackSchedule= PresenterPaybackSchedule()
        presenterPaybackSchedule.doPaybackSchedule(this@LoanPaybackScheduledActivity,jsonObject,this)
    }

    @SuppressLint("SetTextI18n")
    override fun onSuccessPaybackSchedule(data: ResPaybackSchedule.Data) {

        dataOBJ=data
        swipeRefreshLayout.isRefreshing = false
        txt_total_payback.text          = data.loanInfo!!.currencyCode+" "+data.loanInfo!!.totalPayback.toString()

        if(data.schedule!!.size>0)
        {

            val adapterPaymentSchedule         = PaymentScheduleAdapter(this@LoanPaybackScheduledActivity,data.schedule!!,data.loanInfo!!)
            rlPayback.layoutManager            = LinearLayoutManager(this@LoanPaybackScheduledActivity, LinearLayoutManager.VERTICAL, false)
            rlPayback.adapter                  = adapterPaymentSchedule
        }
    }

    override fun onErrorPaybackSchedule(jsonMessage: String) {

        swipeRefreshLayout.isRefreshing = false
        CommonMethod.customSnackBarError(llRoot,this@LoanPaybackScheduledActivity,jsonMessage)

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
                if(swipeRefreshLayout.isRefreshing)
                {
                    CommonMethod.customSnackBarError(llRoot,this@LoanPaybackScheduledActivity,resources.getString(R.string.please_wait))
                }
                else
                {
                    onBackPressed()
                    overridePendingTransition(R.anim.left_in, R.anim.right_out)

                }
                true

            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {

        if(swipeRefreshLayout.isRefreshing)
        {
            CommonMethod.customSnackBarError(llRoot,this@LoanPaybackScheduledActivity,resources.getString(R.string.please_wait))
        }
        else
        {
            super.onBackPressed()
            overridePendingTransition(R.anim.left_in, R.anim.right_out)
        }

    }

}
