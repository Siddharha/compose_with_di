package com.app.l_pesa.calculator.view

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_loan_calculator.*
import android.text.Spannable
import com.app.l_pesa.common.CustomTypeFaceSpan
import android.text.SpannableString
import android.graphics.Typeface
import androidx.core.content.ContextCompat
import android.text.TextUtils
import com.app.l_pesa.R
import com.app.l_pesa.common.CommonMethod
import com.app.l_pesa.common.SharedPref
import com.app.l_pesa.dashboard.model.ResDashboard
import com.app.l_pesa.pinview.model.LoginData
import com.google.gson.Gson


class LoanCalculatorFragment:Fragment() {

    private var isLoanTypeEnable    = false

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


        val sharedPrefOBJ= SharedPref(activity!!)
        val dashBoard = Gson().fromJson<ResDashboard.Data>(sharedPrefOBJ.userDashBoard, ResDashboard.Data::class.java)
        val userData = Gson().fromJson<LoginData>(sharedPrefOBJ.userInfo, LoginData::class.java)

        txt_credit_score.text = resources.getString(R.string.credit_score_calculator) +"\n"+ userData.user_info.credit_score


        ti_loan_type.typeface=Typeface.createFromAsset(activity!!.assets, "fonts/Montserrat-Regular.ttf")
        ti_loan_type.setOnClickListener {

           popupLoanType()
        }


        ti_product_name.typeface=Typeface.createFromAsset(activity!!.assets, "fonts/Montserrat-Regular.ttf")
        ti_product_name.setOnClickListener {

            if(!isLoanTypeEnable)
            {
                popupLoanType()
                CommonMethod.customSnackBarError(rootLayout,activity!!,resources.getString(R.string.select_loan_type))
            }
        }


        seekBar.maxProgress=dashBoard.maxCreditScore
        seekBar.progress=userData.user_info.credit_score

        seekBar.setOnTouchListener { _, _ -> true }

        buttonCalculateLoan.setOnClickListener {

            calculateLoan()
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

            true
        }

        val menu = popupMenuOBJ.menu
        for (i in 0 until menu.size()) {
            val mi = menu.getItem(i)
            applyFontToMenuItem(mi)
        }

        popupMenuOBJ.show()
    }

    private fun calculateLoan()
    {


        if(TextUtils.isEmpty(ti_loan_type.text.toString()))
        {
            popupLoanType()
        }
        else
        {
            if(CommonMethod.isNetworkAvailable(activity!!))
            {

            }
            else
            {
                CommonMethod.customSnackBarError(rootLayout,activity!!,resources.getString(R.string.no_internet))
            }
        }
    }

    private fun applyFontToMenuItem(mi: MenuItem) {

        val font = Typeface.createFromAsset(activity!!.assets, "fonts/Montserrat-Regular.ttf")
        val mNewTitle = SpannableString(mi.title)
        mNewTitle.setSpan(CustomTypeFaceSpan("", font, Color.parseColor("#535559")), 0, mNewTitle.length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
        mi.title = mNewTitle
    }

}