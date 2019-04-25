package com.app.l_pesa.wallet.view

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.graphics.Typeface
import android.os.Bundle
import android.support.design.widget.BottomSheetBehavior
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import com.app.l_pesa.R
import com.app.l_pesa.wallet.inter.ICallBackWalletWithdrawalHistory
import com.app.l_pesa.wallet.model.ResWalletWithdrawalHistory
import com.app.l_pesa.wallet.presenter.PresenterWithdrawalHistory
import kotlinx.android.synthetic.main.activity_wallet_history.*
import kotlinx.android.synthetic.main.layout_filter_by_date.*
import java.util.*

class WalletHistoryActivity : AppCompatActivity(), ICallBackWalletWithdrawalHistory {

    private lateinit var bottomSheetBehavior          : BottomSheetBehavior<*>

    private var hasNext=false
    private var after=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wallet_history)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbarFont(this@WalletHistoryActivity)

        initLoad()
        swipeRefresh()

    }

    private fun swipeRefresh()
    {

    }

    private fun initLoad()
    {
        bottomSheetBehavior = BottomSheetBehavior.from<View>(bottom_sheet)
        bottomSheetBehavior.isHideable=true
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        imgFilter.setOnClickListener {

            doFilter()
        }

        val presenterWithdrawalHistory=PresenterWithdrawalHistory()
        presenterWithdrawalHistory.getWithdrawalHistory(this@WalletHistoryActivity,this)

    }

    private fun doFilter()
    {

        if(bottomSheetBehavior.state == BottomSheetBehavior.STATE_HIDDEN)
        {
            bottomSheetBehavior.state =(BottomSheetBehavior.STATE_HALF_EXPANDED)
            resetFilter()
            filterDate()

        }
        else if(bottomSheetBehavior.state == BottomSheetBehavior.STATE_HALF_EXPANDED)
        {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        }

        etFromDate.setOnClickListener {

            showDatePickerFrom()

        }

        etToDate.setOnClickListener {

            showDatePickerTo()

        }

        imgCancel.setOnClickListener {

            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN)

        }
    }

    @SuppressLint("SetTextI18n")
    private fun showDatePickerFrom()
    {
        val c       = Calendar.getInstance()
        val year    = c.get(Calendar.YEAR)
        val month   = c.get(Calendar.MONTH)+1
        val day     = c.get(Calendar.DAY_OF_MONTH)

        val dpd = DatePickerDialog(this@WalletHistoryActivity, DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->

            if(dayOfMonth.toString().length==1)
            {
                if(monthOfYear.toString().length==1)
                {
                    etFromDate.setText("0$dayOfMonth-0$month-$year")
                }
                else
                {
                    etFromDate.setText("0$dayOfMonth-$month-$year")
                }

            }
            else
            {
                if(monthOfYear.toString().length==1)
                {
                    etFromDate.setText("$dayOfMonth-0$month-$year")
                }
                else
                {
                    etFromDate.setText("$dayOfMonth-$monthOfYear-$year")
                }

            }

        }, year, month, day)

        dpd.show()
        dpd.datePicker.maxDate = System.currentTimeMillis()
    }

    @SuppressLint("SetTextI18n")
    private fun showDatePickerTo()
    {
        val c       = Calendar.getInstance()
        val year    = c.get(Calendar.YEAR)
        val month   = c.get(Calendar.MONTH)+1
        val day     = c.get(Calendar.DAY_OF_MONTH)

        val dpd = DatePickerDialog(this@WalletHistoryActivity, DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->

            if(dayOfMonth.toString().length==1)
            {
                if(monthOfYear.toString().length==1)
                {
                    etToDate.setText("0$dayOfMonth-0$month-$year")
                }
                else
                {
                    etToDate.setText("0$dayOfMonth-$month-$year")
                }

            }
            else
            {
                if(monthOfYear.toString().length==1)
                {
                    etToDate.setText("$dayOfMonth-0$month-$year")
                }
                else
                {
                    etToDate.setText("$dayOfMonth-$monthOfYear-$year")
                }

            }

        }, year, month, day)

        dpd.show()
        dpd.datePicker.maxDate = System.currentTimeMillis()
    }


    private fun resetFilter()
    {
        buttonReset.setOnClickListener {

            etFromDate.text!!.clear()
            etToDate.text!!.clear()

        }
    }

    private fun filterDate()
    {
        buttonFilterSubmit.setOnClickListener {

            /*if(TextUtils.isEmpty(etFromDate.text.toString()) && TextUtils.isEmpty(etToDate.text.toString()))
            {
                CommonMethod.customSnackBarError(rootLayout,activity!!,resources.getString(R.string.required_filter_date))
            }
            else
            {

            }*/
        }

    }

    override fun onSuccessWalletWithdrawalHistory(withdrawal_history: ArrayList<ResWalletWithdrawalHistory.WithdrawalHistory>, cursors: ResWalletWithdrawalHistory.Cursors) {

    }

    override fun onSuccessWalletWithdrawalHistoryPaginate(withdrawal_history: ArrayList<ResWalletWithdrawalHistory.WithdrawalHistory>, cursors: ResWalletWithdrawalHistory.Cursors) {

    }

    override fun onErrorWalletWithdrawalHistory(message: String) {

    }

    override fun onEmptyWalletWithdrawalHistory() {

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
