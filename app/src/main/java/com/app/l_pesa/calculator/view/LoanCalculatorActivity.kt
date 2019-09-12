package com.app.l_pesa.calculator.view

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.app.ProgressDialog
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.TextUtils
import android.view.MenuItem
import android.view.Window
import android.widget.PopupMenu
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.l_pesa.R
import com.app.l_pesa.calculator.adapter.LoanProductAdapter
import com.app.l_pesa.calculator.inter.ICallBackProducts
import com.app.l_pesa.calculator.model.ResProducts
import com.app.l_pesa.calculator.presenter.PresenterCalculator
import com.app.l_pesa.common.CommonMethod
import com.app.l_pesa.common.CustomTypeFaceSpan
import com.app.l_pesa.common.SharedPref
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_loan_calculator.*
import kotlinx.android.synthetic.main.fragment_loan_calculator.*
import java.text.DecimalFormat


class LoanCalculatorActivity : AppCompatActivity(), ICallBackProducts {

    private lateinit var  progressDialog   : ProgressDialog
    private var usdValue=0.0
    private var personalLoanStatus="FALSE"
    private var businessLoanStatus="FALSE"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loan_calculator)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbarFont(this@LoanCalculatorActivity)

        initData()
    }

    @SuppressLint("SetTextI18n")
    private fun initData()
    {
        initLoader()

        val sharedPrefOBJ= SharedPref(this@LoanCalculatorActivity)
        ti_loan_type.typeface=Typeface.createFromAsset(this@LoanCalculatorActivity.assets, "fonts/Montserrat-Regular.ttf")
        ti_loan_type.setOnClickListener {

            popupLoanType()
        }


        buttonCalculateLoan.setOnClickListener {

            calculateLoan()
        }

        ti_product_name.typeface=Typeface.createFromAsset(this@LoanCalculatorActivity.assets, "fonts/Montserrat-Regular.ttf")
        ti_product_name.setOnClickListener {

            when {
                ti_loan_type.text.toString() == resources.getString(com.app.l_pesa.R.string.personal_loan) -> {

                    if(personalLoanStatus==resources.getString(com.app.l_pesa.R.string.status_false))
                    {
                        CommonMethod.customSnackBarError(rootLayout,this@LoanCalculatorActivity,resources.getString(com.app.l_pesa.R.string.service_not_available))
                    }
                    else if(personalLoanStatus==resources.getString(com.app.l_pesa.R.string.status_error))
                    {
                        CommonMethod.customSnackBarError(rootLayout,this@LoanCalculatorActivity,resources.getString(com.app.l_pesa.R.string.something_went_wrong))
                    }
                    else {
                        val currentProduct = Gson().fromJson<ResProducts.Data>(sharedPrefOBJ.currentLoanProduct, ResProducts.Data::class.java)
                        dialogProduct(currentProduct)
                    }


                }
                ti_loan_type.text.toString() == resources.getString(com.app.l_pesa.R.string.business_loan) -> {

                    if(businessLoanStatus==resources.getString(com.app.l_pesa.R.string.status_false))
                    {
                        CommonMethod.customSnackBarError(rootLayout,this@LoanCalculatorActivity,resources.getString(com.app.l_pesa.R.string.service_not_available))
                    }
                    else if(businessLoanStatus==resources.getString(com.app.l_pesa.R.string.status_error))
                    {
                        CommonMethod.customSnackBarError(rootLayout,this@LoanCalculatorActivity,resources.getString(com.app.l_pesa.R.string.something_went_wrong))
                    }
                    else
                    {
                        val businessProduct = Gson().fromJson<ResProducts.Data>(sharedPrefOBJ.businessLoanProduct, ResProducts.Data::class.java)
                        dialogProduct(businessProduct)
                    }


                }

            }
        }


    }

    private fun initLoader()
    {
        progressDialog = ProgressDialog(this@LoanCalculatorActivity,R.style.MyAlertDialogStyle)
        progressDialog.isIndeterminate = true
        progressDialog.setMessage(resources.getString(R.string.loading))
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        progressDialog.setCancelable(false)
        progressDialog.setCanceledOnTouchOutside(false)

    }

    private fun dismiss()
    {
        if(progressDialog.isShowing)
        {
            progressDialog.dismiss()
        }
    }

    private fun popupLoanType()
    {
        val popupMenuOBJ = PopupMenu(this@LoanCalculatorActivity, ti_loan_type)
        popupMenuOBJ.menuInflater.inflate(R.menu.menu_loan_type, popupMenuOBJ.menu)


        popupMenuOBJ.setOnMenuItemClickListener { item: MenuItem? ->

            ti_loan_type.setText(item!!.title)
            ti_loan_type.setBackgroundColor(Color.TRANSPARENT)
            ti_loan_type.backgroundTintList=(ContextCompat.getColorStateList(this@LoanCalculatorActivity, android.R.color.transparent))


            getLoanValue()
            clearTextValues()

            true
        }

        val menu = popupMenuOBJ.menu
        for (i in 0 until menu.size()) {
            val mi = menu.getItem(i)
            applyFontToMenuItem(mi)
        }

        popupMenuOBJ.show()

    }


    private fun getBusinessLoanProduct()
    {

            val sharedPrefOBJ= SharedPref(this@LoanCalculatorActivity)
            if(sharedPrefOBJ.businessLoanProduct=="INIT")
            {
                if(CommonMethod.isNetworkAvailable(this@LoanCalculatorActivity))
                {
                    progressDialog.show()
                    val presenterCalculatorOBJ= PresenterCalculator()
                    presenterCalculatorOBJ.getLoanProducts(this@LoanCalculatorActivity,sharedPrefOBJ.countryCode,"business_loan",this)
                }
                else
                {
                    CommonMethod.customSnackBarError(rootLayout,this@LoanCalculatorActivity,resources.getString(com.app.l_pesa.R.string.no_internet))
                }
            }
            else
            {

                val businessProduct = Gson().fromJson<ResProducts.Data>(sharedPrefOBJ.businessLoanProduct, ResProducts.Data::class.java)
                dialogProduct(businessProduct)

            }

    }

    private fun getLoanValue()
    {
        if(ti_loan_type.text.toString() == resources.getString(com.app.l_pesa.R.string.personal_loan))
        {
               val sharedPrefOBJ= SharedPref(this@LoanCalculatorActivity)
                if(sharedPrefOBJ.currentLoanProduct=="INIT")
                {
                    if(CommonMethod.isNetworkAvailable(this@LoanCalculatorActivity))
                    {
                        progressDialog.show()
                        val presenterCalculatorOBJ=PresenterCalculator()
                        presenterCalculatorOBJ.getLoanProducts(this@LoanCalculatorActivity,sharedPrefOBJ.countryCode,"current_loan",this)
                    }
                    else
                    {
                        CommonMethod.customSnackBarError(rootLayout,this@LoanCalculatorActivity,resources.getString(com.app.l_pesa.R.string.no_internet))
                    }
                }
                else
                {
                    val currentProduct = Gson().fromJson<ResProducts.Data>(sharedPrefOBJ.currentLoanProduct, ResProducts.Data::class.java)
                    dialogProduct(currentProduct)

                }
            }

        else
        {
            getBusinessLoanProduct()
        }
    }

    private fun dialogProduct(product: ResProducts.Data)
    {
        ti_product_name.text!!.clear()
        val dialog= Dialog(this@LoanCalculatorActivity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_id_type)
        val recyclerView                = dialog.findViewById(R.id.recyclerView) as RecyclerView?
        val loanProductAdapter          = LoanProductAdapter(this@LoanCalculatorActivity, product.productList!!,product,dialog,this)
        recyclerView?.layoutManager     = LinearLayoutManager(this@LoanCalculatorActivity, RecyclerView.VERTICAL, false)
        recyclerView?.adapter           = loanProductAdapter
        dialog.show()
    }

    private fun calculateLoan()
    {

        if(TextUtils.isEmpty(ti_loan_type.text.toString()))
        {
            popupLoanType()
        }
        else if(TextUtils.isEmpty(ti_product_name.text.toString()))
        {
            getLoanValue()
        }
        else
        {
            doCalculate()
        }
    }

    private fun clearTextValues()
    {
        txt_credit_score.text  = resources.getText(com.app.l_pesa.R.string.dash_line)
        txt_usd_values.text    = resources.getText(com.app.l_pesa.R.string.dash_line)
        txt_loan_amount.text   = resources.getText(com.app.l_pesa.R.string.dash_line)
        txt_total_payback.text = resources.getText(com.app.l_pesa.R.string.dash_line)
        txt_subscriptionF.text = resources.getText(com.app.l_pesa.R.string.dash_line)
        txt_subscriptionM.text = resources.getText(com.app.l_pesa.R.string.dash_line)
        txt_subscriptionL.text = resources.getText(com.app.l_pesa.R.string.dash_line)
        txt_interest_rate.text = resources.getText(com.app.l_pesa.R.string.dash_line)
        txt_loan_period.text   = resources.getText(com.app.l_pesa.R.string.dash_line)
    }

    @SuppressLint("SetTextI18n")
    private fun doCalculate()
    {
        val sharedPrefOBJ= SharedPref(this@LoanCalculatorActivity)
        val loanProduct = Gson().fromJson<ResProducts.ProductList>(sharedPrefOBJ.loanProduct, ResProducts.ProductList::class.java)

        val format = DecimalFormat()
        format.isDecimalSeparatorAlwaysShown = false

        val loanAmount=Math.round(loanProduct.loanAmount*usdValue)
        txt_credit_score.text=format.format(loanProduct.requiredCreditScore)
        txt_usd_values.text =loanProduct.currencyCode  +" "+usdValue
        txt_loan_amount.text= loanProduct.currencyCode+" "+format.format(loanAmount)

        var loopCounter=1
        if(loanProduct.loanPeriodType=="W")
        {
            loopCounter=7
            txt_loan_period.text= loanProduct.loanPeriod.toString() +" Weeks"
        }
        else
        {
            loopCounter=1
            txt_loan_period.text= loanProduct.loanPeriod.toString() +" Days"
        }


        var totalPayback     = 0.00
        val principalAmount  = Math.round(loanAmount.toDouble()/loanProduct.loanPeriod.toDouble())
        var paymentF         = 0.0
        var paymentM         = 0.0
        var paymentL         = 0.0


        for(i in 1..loanProduct.loanPeriod)
        {
            val curAmount        = loanAmount-(principalAmount* (i-1))
            val insCal           = Math.round((curAmount.toDouble()) * loanProduct.loanInterestRate*i*loopCounter/100)
            totalPayback+=(principalAmount+insCal)

            if(i==1)
            {
                paymentF=Math.round((principalAmount+insCal).toDouble()).toDouble()
            }
            if(i==loanProduct.loanPeriod)
            {
                paymentL=Math.round((principalAmount+insCal).toDouble()).toDouble()
            }

            if(loanProduct.loanPeriod%2==0)
            {
                val checkM = (loanProduct.loanPeriod.toDouble()/2.00)+1
                val midNo=Math.round(checkM)
                if (i == midNo.toInt())
                {
                    paymentM = Math.round((principalAmount + insCal).toDouble()).toDouble()

                }
            }
            else
            {
                val checkLoanPeriodM=(loanProduct.loanPeriod.toDouble()/2.00)
                val midNo=Math.round(checkLoanPeriodM)
                if(i==midNo.toInt())
                {
                    paymentM=Math.round((principalAmount + insCal).toDouble()).toDouble()

                }
            }

        }

        val df = DecimalFormat("#.##")
        txt_total_payback.text= loanProduct.currencyCode+" "+ format.format(Math.round(totalPayback))
        txt_subscriptionF.text= loanProduct.currencyCode+" "+ format.format(Math.round(paymentF))
        txt_subscriptionM.text= loanProduct.currencyCode+" "+ format.format(Math.round(paymentM))
        txt_subscriptionL.text= loanProduct.currencyCode+" "+ format.format(Math.round(paymentL))
        txt_interest_rate.text= df.format(loanProduct.loanInterestRate).toString()+" %"


    }

    private fun applyFontToMenuItem(mi: MenuItem) {

        val font = Typeface.createFromAsset(this@LoanCalculatorActivity.assets, "fonts/Montserrat-Regular.ttf")
        val mNewTitle = SpannableString(mi.title)
        mNewTitle.setSpan(CustomTypeFaceSpan("", font, Color.parseColor("#535559")), 0, mNewTitle.length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
        mi.title = mNewTitle
    }

    override fun onSuccessCurrentLoan(data: ResProducts.Data) {

        personalLoanStatus=resources.getString(com.app.l_pesa.R.string.status_true)
        dismiss()
        val sharedPrefOBJ= SharedPref(this@LoanCalculatorActivity)
        val currentProductList                        = Gson().toJson(data)
        sharedPrefOBJ.currentLoanProduct              = currentProductList
        val currentProduct = Gson().fromJson<ResProducts.Data>(sharedPrefOBJ.currentLoanProduct, ResProducts.Data::class.java)
        dialogProduct(currentProduct)

    }

    override fun onEmptyCurrentLoan() {
        personalLoanStatus=resources.getString(com.app.l_pesa.R.string.status_false)
        ti_product_name.text!!.clear()
        CommonMethod.customSnackBarError(rootLayout,this@LoanCalculatorActivity,resources.getString(com.app.l_pesa.R.string.service_not_available))
        dismiss()
    }

    override fun onErrorCurrentLoan(errorMessageOBJ: String) {
        personalLoanStatus=resources.getString(com.app.l_pesa.R.string.status_error)
        ti_product_name.text!!.clear()
        CommonMethod.customSnackBarError(rootLayout,this@LoanCalculatorActivity,resources.getString(com.app.l_pesa.R.string.service_not_available))
        dismiss()
    }

    override fun onSuccessBusinessLoan(data: ResProducts.Data) {
        businessLoanStatus=resources.getString(com.app.l_pesa.R.string.status_true)
        dismiss()
        val sharedPrefOBJ= SharedPref(this@LoanCalculatorActivity)
        val businessProductList                        = Gson().toJson(data)
        sharedPrefOBJ.businessLoanProduct              = businessProductList

        val businessProduct = Gson().fromJson<ResProducts.Data>(sharedPrefOBJ.businessLoanProduct, ResProducts.Data::class.java)
        dialogProduct(businessProduct)
    }

    override fun onEmptyBusinessLoan() {
        businessLoanStatus=resources.getString(com.app.l_pesa.R.string.status_false)
        ti_product_name.text!!.clear()
        CommonMethod.customSnackBarError(rootLayout,this@LoanCalculatorActivity,resources.getString(com.app.l_pesa.R.string.service_not_available))
        dismiss()
    }

    override fun onErrorBusinessLoan(errorMessageOBJ: String) {
        businessLoanStatus=resources.getString(com.app.l_pesa.R.string.status_error)
        ti_product_name.text!!.clear()
        CommonMethod.customSnackBarError(rootLayout,this@LoanCalculatorActivity,resources.getString(com.app.l_pesa.R.string.service_not_available))
        dismiss()
    }

    override fun onSessionTimeOut(jsonMessage: String) {

        dismiss()
    }

    @SuppressLint("SetTextI18n")
    override fun onClickProduct(productList: ResProducts.ProductList, product: ResProducts.Data)
    {
        usdValue=product.usdValue.toDouble()
        val sharedPrefOBJ= SharedPref(this@LoanCalculatorActivity)
        val productOBJ                        = Gson().toJson(productList)
        sharedPrefOBJ.loanProduct             = productOBJ

        val format = DecimalFormat()
        format.isDecimalSeparatorAlwaysShown = false
        ti_product_name.setText("$ "+format.format(productList.loanAmount))
        ti_product_name.setTextColor(ContextCompat.getColor(this@LoanCalculatorActivity, R.color.textColors))
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
