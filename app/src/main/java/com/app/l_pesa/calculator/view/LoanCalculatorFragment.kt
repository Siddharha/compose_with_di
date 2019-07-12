package com.app.l_pesa.calculator.view

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_loan_calculator.*
import android.text.Spannable
import com.app.l_pesa.common.CustomTypeFaceSpan
import android.text.SpannableString
import android.graphics.Typeface
import androidx.core.content.ContextCompat
import android.text.TextUtils
import android.view.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.l_pesa.R
import com.app.l_pesa.calculator.adapter.LoanProductAdapter
import com.app.l_pesa.calculator.inter.ICallBackProducts
import com.app.l_pesa.calculator.model.ResProducts
import com.app.l_pesa.calculator.presenter.PresenterCalculator
import com.app.l_pesa.common.CommonMethod
import com.app.l_pesa.common.SharedPref
import com.google.gson.Gson
import com.kaopiz.kprogresshud.KProgressHUD
import java.text.DecimalFormat


class LoanCalculatorFragment:Fragment(), ICallBackProducts{

    private lateinit  var progressDialog: KProgressHUD
    private var usdValue=""

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


        initData()
    }

    @SuppressLint("SetTextI18n")
    private fun initData()
    {
        initLoader()

        val sharedPrefOBJ= SharedPref(activity!!)
        ti_loan_type.typeface=Typeface.createFromAsset(activity!!.assets, "fonts/Montserrat-Regular.ttf")
        ti_loan_type.setOnClickListener {

           popupLoanType()
        }


        buttonCalculateLoan.setOnClickListener {

            calculateLoan()
        }

        ti_product_name.typeface=Typeface.createFromAsset(activity!!.assets, "fonts/Montserrat-Regular.ttf")
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
                    CommonMethod.customSnackBarError(rootLayout,activity!!,resources.getString(R.string.select_loan_type))
                }
            }
        }


    }

    private fun initLoader()
    {
        progressDialog= KProgressHUD.create(activity)
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
        val popupMenuOBJ = PopupMenu(activity!!, ti_loan_type)
        popupMenuOBJ.menuInflater.inflate(R.menu.menu_loan_type, popupMenuOBJ.menu)


        popupMenuOBJ.setOnMenuItemClickListener { item: MenuItem? ->

            ti_loan_type.setText(item!!.title)
            ti_loan_type.setBackgroundColor(Color.TRANSPARENT)
            ti_loan_type.backgroundTintList=(ContextCompat.getColorStateList(activity!!, android.R.color.transparent))

            val sharedPrefOBJ= SharedPref(activity!!)


            if(ti_loan_type.text.toString() == resources.getString(R.string.personal_loan))
            {
                if(sharedPrefOBJ.currentLoanProduct=="INIT")
                {
                    if(CommonMethod.isNetworkAvailable(activity!!))
                    {
                        progressDialog.show()
                        val presenterCalculatorOBJ=PresenterCalculator()
                        presenterCalculatorOBJ.getLoanProducts(activity!!,sharedPrefOBJ.countryCode,"current_loan",this)
                    }
                    else
                    {
                        CommonMethod.customSnackBarError(rootLayout,activity!!,resources.getString(R.string.no_internet))
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
        val sharedPrefOBJ= SharedPref(activity!!)
        if(sharedPrefOBJ.businessLoanProduct=="INIT")
        {
            if(CommonMethod.isNetworkAvailable(activity!!))
            {
                progressDialog.show()
                val presenterCalculatorOBJ=PresenterCalculator()
                presenterCalculatorOBJ.getLoanProducts(activity!!,sharedPrefOBJ.countryCode,"business_loan",this)
            }
            else
            {
                CommonMethod.customSnackBarError(rootLayout,activity!!,resources.getString(R.string.no_internet))
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
        val dialog= Dialog(activity!!)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_id_type)
        val recyclerView                = dialog.findViewById(R.id.recyclerView) as RecyclerView?
        val loanProductAdapter          = LoanProductAdapter(activity!!, product.productList!!,product,dialog,this)
        recyclerView?.layoutManager     = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
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
            CommonMethod.customSnackBarError(rootLayout,activity!!,resources.getString(R.string.select_loan_type))
        }
        else
        {
            doCalculate()
        }
    }

    private fun clearTextValues()
    {
        txt_credit_score.text = resources.getText(R.string.dash_line)
        txt_usd_values.text = resources.getText(R.string.dash_line)
        txt_loan_amount.text = resources.getText(R.string.dash_line)
    }

    @SuppressLint("SetTextI18n")
    private fun doCalculate()
    {
        val sharedPrefOBJ= SharedPref(activity!!)
        val loanProduct = Gson().fromJson<ResProducts.ProductList>(sharedPrefOBJ.loanProduct, ResProducts.ProductList::class.java)

        val format = DecimalFormat()
        format.isDecimalSeparatorAlwaysShown = false

        txt_credit_score.text=format.format(loanProduct.requiredCreditScore)
        txt_usd_values.text=usdValue +" "+loanProduct.currencyCode
        txt_loan_amount.text= (loanProduct.loanAmount*usdValue.toDouble()).toString()+" "+loanProduct.currencyCode


      /*  val totalSize = 1 until loanProduct.loanPeriod.toInt()
        for (i in totalSize) {

            //   $curAmount = $loan_amount - ($eachPrinAmount * ($d - 1));
            //Each Principal Amount = round of 2 decimal place ‘Loan Amount’ / ‘loan_period’
            //      $total_pay_back += $eachPrinAmount + $InsCal;
            val principalAmount=loanProduct.loanAmount/loanProduct.loanPeriod
            val curAmount= loanProduct.loanAmount-principalAmount
            val totalPayback= principalAmount+loanProduct.insuranceCoverage

            println("TAKA"+totalPayback)
        }*/



    }

    private fun applyFontToMenuItem(mi: MenuItem) {

        val font = Typeface.createFromAsset(activity!!.assets, "fonts/Montserrat-Regular.ttf")
        val mNewTitle = SpannableString(mi.title)
        mNewTitle.setSpan(CustomTypeFaceSpan("", font, Color.parseColor("#535559")), 0, mNewTitle.length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
        mi.title = mNewTitle
    }

    override fun onSuccessCurrentLoan(data: ResProducts.Data) {

        dismiss()
        val sharedPrefOBJ=SharedPref(activity!!)
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
        val sharedPrefOBJ=SharedPref(activity!!)
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
        val sharedPrefOBJ=SharedPref(activity!!)
        val productOBJ                        = Gson().toJson(productList)
        sharedPrefOBJ.loanProduct             = productOBJ

        val format = DecimalFormat()
        format.isDecimalSeparatorAlwaysShown = false
        ti_product_name.setText(format.format(productList.loanAmount)+" $")
        ti_product_name.setTextColor(ContextCompat.getColor(activity!!, R.color.textColors))
    }

}