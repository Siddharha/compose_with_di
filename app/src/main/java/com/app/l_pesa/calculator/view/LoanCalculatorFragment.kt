package com.app.l_pesa.calculator.view

import android.annotation.SuppressLint
import android.app.Dialog
import android.app.ProgressDialog
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Spannable
import android.text.SpannableString
import android.text.TextUtils
import android.text.style.RelativeSizeSpan
import android.util.Log
import android.view.*
import android.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.l_pesa.R
import com.app.l_pesa.calculator.adapter.LoanProductAdapter
import com.app.l_pesa.calculator.inter.ICallBackCalculateLoan
import com.app.l_pesa.calculator.inter.ICallBackProducts
import com.app.l_pesa.calculator.model.ResCalculation
import com.app.l_pesa.calculator.model.ResProducts
import com.app.l_pesa.calculator.presenter.PresenterCalculator
import com.app.l_pesa.common.CommonMethod
import com.app.l_pesa.common.CustomTypeFaceSpan
import com.app.l_pesa.common.SharedPref
import com.app.l_pesa.dashboard.view.DashboardActivity
import com.facebook.appevents.AppEventsConstants
import com.facebook.appevents.AppEventsLogger
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_loan_calculator.*
import java.text.DecimalFormat


class LoanCalculatorFragment:Fragment(), ICallBackProducts, ICallBackCalculateLoan {

    private lateinit  var progressDialog: ProgressDialog
    private var usdValue=0.0
    private var personalLoanStatus="FALSE"
    private var businessLoanStatus="FALSE"
    private var selectedLoanType = ""
    private val sharedPrefOBJ:SharedPref by lazy {SharedPref(requireActivity())}

    private val currentProduct:ResProducts.Data by lazy{ Gson().fromJson(sharedPrefOBJ.currentLoanProduct, ResProducts.Data::class.java)}
    companion object {
        fun newInstance(): Fragment {
            return LoanCalculatorFragment()
        }
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_loan_calculator, container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Handler(Looper.getMainLooper()).postDelayed({
            (activity as DashboardActivity).visibleFilter(false)
            (activity as DashboardActivity).visibleButton(false)
        }, 200)


        initData()
    }

    @SuppressLint("SetTextI18n")
    private fun initData()
    {
        initLoader()

        val sharedPrefOBJ= SharedPref(requireActivity())
        /*sharedPrefOBJ.currentLoanProduct=resources.getString(R.string.init)
        sharedPrefOBJ.businessLoanProduct=resources.getString(R.string.init)*/
        ti_loan_type.typeface=Typeface.createFromAsset(requireActivity().assets, "fonts/Montserrat-Regular.ttf")
        ti_loan_type.setOnClickListener {

            popupLoanType()
        }


        buttonCalculateLoan.setOnClickListener {

            calculateLoan()
        }

        ti_product_name.typeface=Typeface.createFromAsset(requireActivity().assets, "fonts/Montserrat-Regular.ttf")
        ti_product_name.setOnClickListener {

            when {
                ti_loan_type.text.toString() == resources.getString(R.string.personal_loan) -> {

                    if(personalLoanStatus==resources.getString(R.string.status_false) && sharedPrefOBJ.currentLoanProduct==resources.getString(R.string.init))
                    {
                        CommonMethod.customSnackBarError(rootLayout,requireActivity(),resources.getString(R.string.service_not_available))
                    }
                    else if(personalLoanStatus==resources.getString(R.string.status_error) && sharedPrefOBJ.currentLoanProduct==resources.getString(R.string.init))
                    {
                        CommonMethod.customSnackBarError(rootLayout,requireActivity(),resources.getString(R.string.something_went_wrong))
                    }
                    else {
                        val currentProduct = Gson().fromJson<ResProducts.Data>(sharedPrefOBJ.currentLoanProduct, ResProducts.Data::class.java)
                        dialogProduct(currentProduct)
                    }


                }
                ti_loan_type.text.toString() == resources.getString(R.string.business_loan) -> {

                    if(businessLoanStatus==resources.getString(R.string.status_false) && sharedPrefOBJ.businessLoanProduct==resources.getString(R.string.init))
                    {
                        CommonMethod.customSnackBarError(rootLayout,requireActivity(),resources.getString(R.string.service_not_available))
                    }
                    else if(businessLoanStatus==resources.getString(R.string.status_error) && sharedPrefOBJ.businessLoanProduct==resources.getString(R.string.init))
                    {
                        CommonMethod.customSnackBarError(rootLayout,requireActivity(),resources.getString(R.string.something_went_wrong))
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
        progressDialog = ProgressDialog(requireActivity(),R.style.MyAlertDialogStyle)
        val message=   SpannableString(resources.getString(R.string.loading))
        val face = Typeface.createFromAsset(requireActivity().assets, "fonts/Montserrat-Regular.ttf")
        message.setSpan(RelativeSizeSpan(1.0f), 0, message.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
       // message.setSpan(CustomTypefaceSpan("", face), 0, message.length, 0)
        progressDialog.isIndeterminate = true
        progressDialog.setMessage(message)
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
        val popupMenuOBJ = PopupMenu(requireActivity(), ti_loan_type)
        popupMenuOBJ.menuInflater.inflate(R.menu.menu_loan_type, popupMenuOBJ.menu)


        popupMenuOBJ.setOnMenuItemClickListener { item: MenuItem? ->

            ti_loan_type.setText(item!!.title)
            ti_loan_type.setBackgroundColor(Color.TRANSPARENT)
            ti_loan_type.backgroundTintList=(ContextCompat.getColorStateList(requireActivity(), android.R.color.transparent))


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

        val sharedPrefOBJ= SharedPref(requireActivity())
        if(sharedPrefOBJ.businessLoanProduct=="INIT")
        {
            if(CommonMethod.isNetworkAvailable(requireActivity()))
            {
                progressDialog.show()
                val presenterCalculatorOBJ= PresenterCalculator()
                presenterCalculatorOBJ.getLoanProducts(requireActivity(),sharedPrefOBJ.countryCode,"business_loan",this)
                selectedLoanType = "business_loan"
            }
            else
            {
                CommonMethod.customSnackBarError(rootLayout,requireActivity(),resources.getString(R.string.no_internet))
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
        if(ti_loan_type.text.toString() == resources.getString(R.string.personal_loan))
        {
            val sharedPrefOBJ= SharedPref(requireActivity())
            if(sharedPrefOBJ.currentLoanProduct=="INIT")
            {
                if(CommonMethod.isNetworkAvailable(requireActivity()))
                {
                    progressDialog.show()
                    val presenterCalculatorOBJ=PresenterCalculator()
                    presenterCalculatorOBJ.getLoanProducts(requireActivity(),sharedPrefOBJ.countryCode,"current_loan",this)
                    selectedLoanType = "current_loan"
                }
                else
                {
                    CommonMethod.customSnackBarError(rootLayout,requireActivity(),resources.getString(R.string.no_internet))
                }
            }
            else
            {

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
        val dialog= Dialog(requireActivity())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_id_type)
        val recyclerView                = dialog.findViewById(R.id.recyclerView) as RecyclerView?
        val loanProductAdapter          = LoanProductAdapter(requireActivity(), product.productList!!,product,dialog,this)
        recyclerView?.layoutManager     = LinearLayoutManager(requireActivity(), RecyclerView.VERTICAL, false)
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
            //doCalculate()
            doCalculateFromAPI()
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

    private fun doCalculateFromAPI(){
        //calculate loan online.
        val loanProduct:ResProducts.ProductList by lazy {Gson().fromJson(sharedPrefOBJ.loanProduct, ResProducts.ProductList::class.java)}
        val sharedPrefOBJ= SharedPref(requireActivity())

            if(CommonMethod.isNetworkAvailable(requireActivity()))
            {
                if(selectedLoanType.isNotEmpty()){
                    progressDialog.show()
                    val presenterCalculatorOBJ= PresenterCalculator()
                    presenterCalculatorOBJ.getCalculateLoan(requireActivity(),
                        loanProduct.id.toString(),
                        sharedPrefOBJ.countryCode,
                        loanProduct.currencyCode,
                        selectedLoanType,//"business_loan", //current_loan
                        this)
                }else{
                    CommonMethod.customSnackBarError(rootLayout,requireActivity(),"Select Loan Type before calculating!")

                }

            }
            else
            {
                CommonMethod.customSnackBarError(rootLayout,requireActivity(),resources.getString(R.string.no_internet))
            }


    }
    @SuppressLint("SetTextI18n")
    private fun doCalculate()
    {
        val logger = AppEventsLogger.newLogger(activity)
        val params =  Bundle()
        params.putString(AppEventsConstants.EVENT_PARAM_CONTENT_TYPE, "Loan Calculate")
        logger.logEvent(AppEventsConstants.EVENT_NAME_VIEWED_CONTENT, params)



        val format = DecimalFormat()
        format.isDecimalSeparatorAlwaysShown = false
         val loanProduct:ResProducts.ProductList by lazy {Gson().fromJson(sharedPrefOBJ.loanProduct, ResProducts.ProductList::class.java)}
        val loanAmount= if(currentProduct.isDollar==1) {
            Math.round(loanProduct.loanAmount*usdValue)
        } else {
            Math.round(loanProduct.loanAmount)
        }
        Log.e("loanAmount", "doCalculate: $loanAmount")
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
            val curAmount        = loanAmount - (principalAmount* (i-1))
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

        val font = Typeface.createFromAsset(requireActivity().assets, "fonts/Montserrat-Regular.ttf")
        val mNewTitle = SpannableString(mi.title)
        mNewTitle.setSpan(CustomTypeFaceSpan("", font, Color.parseColor("#535559")), 0, mNewTitle.length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
        mi.title = mNewTitle
    }

    override fun onSuccessCurrentLoan(data: ResProducts.Data) {

        personalLoanStatus=resources.getString(R.string.status_true)
        dismiss()
        val sharedPrefOBJ= SharedPref(requireActivity())
        val currentProductList                        = Gson().toJson(data)
        sharedPrefOBJ.currentLoanProduct              = currentProductList
        val currentProduct = Gson().fromJson<ResProducts.Data>(sharedPrefOBJ.currentLoanProduct, ResProducts.Data::class.java)
        dialogProduct(currentProduct)

    }

    override fun onEmptyCurrentLoan() {
        personalLoanStatus=resources.getString(R.string.status_false)
        ti_product_name.text!!.clear()
        CommonMethod.customSnackBarError(rootLayout,requireActivity(),resources.getString(R.string.service_not_available))
        dismiss()
    }

    override fun onErrorCurrentLoan(errorMessageOBJ: String) {
        personalLoanStatus=resources.getString(R.string.status_error)
        ti_product_name.text!!.clear()
        CommonMethod.customSnackBarError(rootLayout,requireActivity(),resources.getString(R.string.service_not_available))
        dismiss()
    }

    override fun onSuccessBusinessLoan(data: ResProducts.Data) {
        businessLoanStatus=resources.getString(R.string.status_true)
        dismiss()
        val sharedPrefOBJ= SharedPref(requireActivity())
        val businessProductList                        = Gson().toJson(data)
        sharedPrefOBJ.businessLoanProduct              = businessProductList

        val businessProduct = Gson().fromJson<ResProducts.Data>(sharedPrefOBJ.businessLoanProduct, ResProducts.Data::class.java)
        dialogProduct(businessProduct)
    }

    override fun onEmptyBusinessLoan() {
        businessLoanStatus=resources.getString(R.string.status_false)
        ti_product_name.text!!.clear()
        CommonMethod.customSnackBarError(rootLayout,requireActivity(),resources.getString(R.string.service_not_available))
        dismiss()
    }

    override fun onErrorBusinessLoan(errorMessageOBJ: String) {
        businessLoanStatus=resources.getString(R.string.status_error)
        ti_product_name.text!!.clear()
        CommonMethod.customSnackBarError(rootLayout,requireActivity(),resources.getString(R.string.service_not_available))
        dismiss()
    }

    override fun onSuccessCalculateLoan(data: ResCalculation.Data) {
        val loanProduct:ResProducts.ProductList by lazy {Gson().fromJson(sharedPrefOBJ.loanProduct, ResProducts.ProductList::class.java)}

        data.apply {
            txt_credit_score.text = requiredCreditScore.toString()
            txt_usd_values.text = currentRate
            txt_loan_amount.text = loanProduct.currencyCode+" "+loanAmount
            txt_loan_period.text = loanPeriod.toString()
            txt_interest_rate.text = loanInterestRate + " %"
            txt_total_payback.text = loanProduct.currencyCode+" "+totalPayBack
            txt_subscriptionF.text = loanProduct.currencyCode+" "+firstWeeklyPayment
            txt_subscriptionM.text = loanProduct.currencyCode+" "+midWeeklyPayment
            txt_subscriptionL.text = loanProduct.currencyCode+" "+lastWeeklyPayment
        }
        dismiss()

    }

    override fun onErrorCalculateLoan(errorMessageOBJ: String) {
        CommonMethod.customSnackBarError(rootLayout,requireActivity(),resources.getString(R.string.service_not_available))
        dismiss()
    }

    override fun onSessionTimeOut(jsonMessage: String) {

        dismiss()
    }

    @SuppressLint("SetTextI18n")
    override fun onClickProduct(productList: ResProducts.ProductList, product: ResProducts.Data)
    {
        usdValue=product.usdValue.toDouble()
        val sharedPrefOBJ= SharedPref(requireActivity())
        val productOBJ                        = Gson().toJson(productList)
        sharedPrefOBJ.loanProduct             = productOBJ

        val format = DecimalFormat()
        format.isDecimalSeparatorAlwaysShown = false
        ti_product_name.setText(productList.loanAmountTxt)
        ti_product_name.setTextColor(ContextCompat.getColor(requireActivity(), R.color.textColors))
    }


    



}