package com.app.l_pesa.calculator.view

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.graphics.Color
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.TextUtils
import android.view.MenuItem
import android.view.Window
import android.widget.PopupMenu
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.l_pesa.R
import com.app.l_pesa.calculator.adapter.LoanProductAdapter
import com.app.l_pesa.calculator.inter.ICallBackProducts
import com.app.l_pesa.calculator.model.ResProducts
import com.app.l_pesa.calculator.presenter.PresenterCalculator
import com.app.l_pesa.common.*
import com.google.gson.Gson
import com.kaopiz.kprogresshud.KProgressHUD
import kotlinx.android.synthetic.main.activity_loan_calculator.*
import kotlinx.android.synthetic.main.fragment_loan_calculator.*
import java.text.DecimalFormat

class LoanCalculatorActivity : AppCompatActivity(), ICallBackProducts {

    private lateinit  var progressDialog: KProgressHUD
    private var usdValue=""

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
                ti_loan_type.text.toString() == resources.getString(R.string.personal_loan) -> {
                    val currentProduct = Gson().fromJson<ResProducts.Data>(sharedPrefOBJ.currentLoanProduct, ResProducts.Data::class.java)
                    dialogProduct(currentProduct)
                }
                ti_loan_type.text.toString() == resources.getString(R.string.business_loan) -> {
                    val businessProduct = Gson().fromJson<ResProducts.Data>(sharedPrefOBJ.businessLoanProduct, ResProducts.Data::class.java)
                    dialogProduct(businessProduct)
                }
                else -> {
                    popupLoanType()
                    CommonMethod.customSnackBarError(rootLayout,this@LoanCalculatorActivity,resources.getString(R.string.select_loan_type))
                }
            }
        }


    }

    private fun initLoader()
    {
        progressDialog= KProgressHUD.create(this@LoanCalculatorActivity)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setCancellable(false)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f)

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

            val sharedPrefOBJ= SharedPref(this@LoanCalculatorActivity)


            if(ti_loan_type.text.toString() == resources.getString(R.string.personal_loan))
            {
                if(sharedPrefOBJ.currentLoanProduct=="INIT")
                {
                    if(CommonMethod.isNetworkAvailable(this@LoanCalculatorActivity))
                    {
                        progressDialog.show()
                        val presenterCalculatorOBJ= PresenterCalculator()
                        presenterCalculatorOBJ.getLoanProducts(this@LoanCalculatorActivity,sharedPrefOBJ.countryCode,"current_loan",this)
                    }
                    else
                    {
                        CommonMethod.customSnackBarError(rootLayout,this@LoanCalculatorActivity,resources.getString(R.string.no_internet))
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
                CommonMethod.customSnackBarError(rootLayout,this@LoanCalculatorActivity,resources.getString(R.string.no_internet))
            }
        }
        else
        {

            val businessProduct = Gson().fromJson<ResProducts.Data>(sharedPrefOBJ.businessLoanProduct, ResProducts.Data::class.java)
            dialogProduct(businessProduct)

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
            CommonMethod.customSnackBarError(rootLayout,this@LoanCalculatorActivity,resources.getString(com.app.l_pesa.R.string.select_loan_type))
        }
        else
        {
            doCalculate()
        }
    }

    private fun clearTextValues()
    {
        txt_credit_score.text  = resources.getText(R.string.dash_line)
        txt_usd_values.text    = resources.getText(R.string.dash_line)
        txt_loan_amount.text   = resources.getText(R.string.dash_line)
        txt_total_payback.text = resources.getText(R.string.dash_line)
        txt_subscriptionF.text = resources.getText(R.string.dash_line)
        txt_subscriptionM.text = resources.getText(R.string.dash_line)
        txt_subscriptionL.text = resources.getText(R.string.dash_line)
        txt_interest_rate.text = resources.getText(R.string.dash_line)
        txt_loan_period.text   = resources.getText(R.string.dash_line)
    }

    @SuppressLint("SetTextI18n")
    private fun doCalculate()
    {
        val sharedPrefOBJ= SharedPref(this@LoanCalculatorActivity)
        val loanProduct = Gson().fromJson<ResProducts.ProductList>(sharedPrefOBJ.loanProduct, ResProducts.ProductList::class.java)

        val format = DecimalFormat()
        format.isDecimalSeparatorAlwaysShown = false

        txt_credit_score.text=format.format(loanProduct.requiredCreditScore)
        txt_usd_values.text =loanProduct.currencyCode  +" "+usdValue
        txt_loan_amount.text= loanProduct.currencyCode+" "+Math.round(loanProduct.loanAmount*usdValue.toDouble()).toString()

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
        val principalAmount  = loanProduct.loanAmount*usdValue.toDouble()/loanProduct.loanPeriod
        var paymentF         = 0.0
        var paymentM         = 0.0
        var paymentL         = 0.0

        var i = 1
        while (i <= loanProduct.loanPeriod) {

            val curAmount        = loanProduct.loanAmount*usdValue.toDouble()-(principalAmount* (i-1))
            val insCal           = (curAmount * loanProduct.loanInterestRate!!.toDouble() *i*loopCounter)/100
            totalPayback+=(principalAmount+insCal)

            if(i==1)
            {
                paymentF=principalAmount+insCal
            }
            if(i==loanProduct.loanPeriod)
            {
                paymentL=principalAmount+insCal
            }

            if(loanProduct.loanPeriod %2==0) {
                val checkM = (loanProduct.loanPeriod / 2) + 1

                if (i == checkM) {
                    paymentM = principalAmount + insCal

                }
            }
            else
            {
                val checkLoanPeriodM=(loanProduct.loanPeriod/2)
                if(i==checkLoanPeriodM)
                {
                    paymentM=principalAmount+insCal

                }
            }

            i++
        }


        txt_total_payback.text= loanProduct.currencyCode+" "+Math.round(totalPayback)
        txt_subscriptionF.text= loanProduct.currencyCode+" "+Math.round(paymentF).toString()
        txt_subscriptionM.text= loanProduct.currencyCode+" "+Math.round(paymentM).toString()
        txt_subscriptionL.text= loanProduct.currencyCode+" "+Math.round(paymentL).toString()
        txt_interest_rate.text= loanProduct.loanInterestRate.toString()+" %"



    }

    private fun applyFontToMenuItem(mi: MenuItem) {

        val font = Typeface.createFromAsset(this@LoanCalculatorActivity.assets, "fonts/Montserrat-Regular.ttf")
        val mNewTitle = SpannableString(mi.title)
        mNewTitle.setSpan(CustomTypeFaceSpan("", font, Color.parseColor("#535559")), 0, mNewTitle.length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
        mi.title = mNewTitle
    }

    override fun onSuccessCurrentLoan(data: ResProducts.Data) {

        dismiss()
        val sharedPrefOBJ= SharedPref(this@LoanCalculatorActivity)
        val currentProductList                        = Gson().toJson(data)
        sharedPrefOBJ.currentLoanProduct              = currentProductList
        val currentProduct = Gson().fromJson<ResProducts.Data>(sharedPrefOBJ.currentLoanProduct, ResProducts.Data::class.java)
        dialogProduct(currentProduct)

    }

    override fun onEmptyCurrentLoan() {

        dismiss()
    }

    override fun onErrorCurrentLoan(errorMessageOBJ: String) {
        dismiss()
    }

    override fun onSuccessBusinessLoan(data: ResProducts.Data) {

        dismiss()
        val sharedPrefOBJ= SharedPref(this@LoanCalculatorActivity)
        val businessProductList                        = Gson().toJson(data)
        sharedPrefOBJ.businessLoanProduct              = businessProductList

        val businessProduct = Gson().fromJson<ResProducts.Data>(sharedPrefOBJ.businessLoanProduct, ResProducts.Data::class.java)
        dialogProduct(businessProduct)
    }

    override fun onEmptyBusinessLoan() {

        dismiss()
    }

    override fun onErrorBusinessLoan(errorMessageOBJ: String) {
        dismiss()
    }

    override fun onSessionTimeOut(jsonMessage: String) {

        dismiss()
    }

    @SuppressLint("SetTextI18n")
    override fun onClickProduct(productList: ResProducts.ProductList, product: ResProducts.Data)
    {
        usdValue=product.usdValue
        val sharedPrefOBJ= SharedPref(this@LoanCalculatorActivity)
        val productOBJ                        = Gson().toJson(productList)
        sharedPrefOBJ.loanProduct             = productOBJ

        val format = DecimalFormat()
        format.isDecimalSeparatorAlwaysShown = false
        ti_product_name.setText(format.format(productList.loanAmount)+" $")
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
