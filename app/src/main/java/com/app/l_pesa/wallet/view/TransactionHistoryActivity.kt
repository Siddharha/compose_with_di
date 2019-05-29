package com.app.l_pesa.wallet.view

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.graphics.Typeface
import android.os.Bundle
import android.support.design.widget.BottomSheetBehavior
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import com.app.l_pesa.R
import com.app.l_pesa.common.CommonClass
import com.app.l_pesa.common.CommonMethod
import com.app.l_pesa.wallet.adapter.TransactionAllAdapter
import com.app.l_pesa.wallet.inter.ICallBackTransaction
import com.app.l_pesa.wallet.model.ResWalletHistory
import com.app.l_pesa.wallet.presenter.PresenterTransactionAll
import kotlinx.android.synthetic.main.activity_transaction_history.*
import kotlinx.android.synthetic.main.content_transaction_history.*
import kotlinx.android.synthetic.main.layout_filter_by_date.*
import java.util.*
import android.support.annotation.NonNull


class TransactionHistoryActivity : AppCompatActivity(), ICallBackTransaction {

    private lateinit var listSavingsHistory           : ArrayList<ResWalletHistory.SavingsHistory>
    private lateinit var adapterTransactionHistory    : TransactionAllAdapter
    private lateinit var bottomSheetBehavior          : BottomSheetBehavior<*>

    private var hasNext=false
    private var after=""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transaction_history)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbarFont(this@TransactionHistoryActivity)

        swipeRefresh()
        initData("","","DEFAULT")

    }

    private fun swipeRefresh()
    {
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent)
        swipeRefreshLayout.setOnRefreshListener {
            etFromDate.text!!.clear()
            etToDate.text!!.clear()
            initData("","","DEFAULT")
        }
    }

    private fun initData(from_date: String, to_date: String,type:String)
    {
        listSavingsHistory           = ArrayList()
        adapterTransactionHistory    = TransactionAllAdapter(this@TransactionHistoryActivity, listSavingsHistory)

        bottomSheetBehavior          = BottomSheetBehavior.from<View>(bottom_sheet)
        bottomSheetBehavior.isHideable=true
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN


        if(CommonMethod.isNetworkAvailable(this@TransactionHistoryActivity))
        {
            swipeRefreshLayout.isRefreshing=true
            val presenterTransactionAll= PresenterTransactionAll()
            presenterTransactionAll.getTransactionAll(this@TransactionHistoryActivity,from_date,to_date,type,this)
        }

        else
        {
            swipeRefreshLayout.isRefreshing=false
            CommonMethod.customSnackBarError(rootLayout,this@TransactionHistoryActivity,resources.getString(R.string.no_internet))
        }


        imgFilter.setOnClickListener {

            doFilter()
        }

    }

    override fun onSuccessTransaction(savingsHistory: ArrayList<ResWalletHistory.SavingsHistory>, cursors: ResWalletHistory.Cursors, from_date: String, to_date: String) {

        cardView.visibility=View.INVISIBLE
        rlList.visibility=View.VISIBLE

        runOnUiThread {

            hasNext = cursors.hasNext
            after = cursors.after
            swipeRefreshLayout.isRefreshing = false
            listSavingsHistory.clear()
            listSavingsHistory.addAll(savingsHistory)
            adapterTransactionHistory   = TransactionAllAdapter(this@TransactionHistoryActivity, listSavingsHistory)
            val llmOBJ                  = LinearLayoutManager(this@TransactionHistoryActivity)
            llmOBJ.orientation          = LinearLayoutManager.VERTICAL
            rlList.layoutManager        = llmOBJ
            rlList.adapter              = adapterTransactionHistory
            adapterTransactionHistory.notifyDataSetChanged()
            adapterTransactionHistory.setLoadMoreListener(object : TransactionAllAdapter.OnLoadMoreListener {
                override fun onLoadMore() {

                    rlList.post {

                        if (hasNext)
                        {
                           loadMore(from_date,to_date)
                        }

                    }

                }
            })
        }
    }

    override fun onSuccessTransactionPaginate(savingsHistory: ArrayList<ResWalletHistory.SavingsHistory>, cursors: ResWalletHistory.Cursors,from_date: String, to_date: String) {

        cardView.visibility=View.INVISIBLE
        rlList.visibility=View.VISIBLE

        runOnUiThread {

            hasNext = cursors.hasNext
            after = cursors.after

            if (listSavingsHistory.size != 0) {
                try {

                    listSavingsHistory.removeAt(listSavingsHistory.size - 1)
                    adapterTransactionHistory.notifyDataChanged()
                    listSavingsHistory.addAll(savingsHistory)
                    adapterTransactionHistory.notifyItemRangeInserted(0, listSavingsHistory.size)

                } catch (e: Exception) {
                }
            }
        }
    }

    private fun doFilter()
    {

        when {
            bottomSheetBehavior.state == BottomSheetBehavior.STATE_HIDDEN -> {
                bottomSheetBehavior.state =(BottomSheetBehavior.STATE_EXPANDED)

            }
            bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED -> bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        }
        etFromDate.setOnClickListener {

            showDatePickerFrom()

        }

        etToDate.setOnClickListener {

            showDatePickerTo()

        }

        buttonFilterSubmit.setOnClickListener {

            if (TextUtils.isEmpty(etFromDate.text.toString()) && TextUtils.isEmpty(etToDate.text.toString())) {
                CommonMethod.customSnackBarError(rootLayout, this@TransactionHistoryActivity, resources.getString(R.string.you_have_select_from_date_to_date))
            } else {

                val fromDate = CommonMethod.dateConvertYMD(etFromDate.text.toString())
                val toDate = CommonMethod.dateConvertYMD(etToDate.text.toString())
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                initData(fromDate!!,toDate!!,"FILTER")

            }
        }

        imgCancel.setOnClickListener {

            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN)

        }

        buttonReset.setOnClickListener {

            etFromDate.text!!.clear()
            etToDate.text!!.clear()

        }

        imgCleanFrom_date.setOnClickListener {

            etFromDate.text!!.clear()

        }

        imgCleanTo_date.setOnClickListener {

            etToDate.text!!.clear()

        }
    }

    private fun loadMore(from_date: String, to_date: String)
    {
        if(CommonMethod.isNetworkAvailable(this@TransactionHistoryActivity))
        {
            val loadModel  = ResWalletHistory.SavingsHistory(0, 0, 0.0,
                            0.0, 0.0, 0.0,
                            "", "", "", "", "", "", "","")

            listSavingsHistory.add(loadModel)
            adapterTransactionHistory.notifyItemInserted(listSavingsHistory.size-1)

            val presenterTransactionAll= PresenterTransactionAll()
            presenterTransactionAll.getTransactionPaginate(this@TransactionHistoryActivity,after,from_date,to_date,this)

        }
        else{
            swipeRefreshLayout.isRefreshing = false
            CommonMethod.customSnackBarError(rootLayout,this@TransactionHistoryActivity,resources.getString(R.string.no_internet))
        }
    }

    override fun onEmptyTransaction(type:String) {

        swipeRefreshLayout.isRefreshing=false
        rlList.visibility=View.INVISIBLE
        if(type=="FILTER")
        {
            txt_message.text = resources.getString(R.string.no_result_found)
        }
        else
        {
            txt_message.text = resources.getString(R.string.empty_transactional_history_message)
        }
        cardView.visibility=View.VISIBLE
    }

    override fun onErrorTransaction(message: String) {

        rlList.visibility=View.INVISIBLE
        cardView.visibility=View.INVISIBLE
        swipeRefreshLayout.isRefreshing=false
        CommonMethod.customSnackBarError(rootLayout,this@TransactionHistoryActivity,message)
    }

    @SuppressLint("SetTextI18n")
    private fun showDatePickerFrom()
    {
        val commonClass= CommonClass()
        commonClass.datePicker(this@TransactionHistoryActivity,etFromDate)
    }

    @SuppressLint("SetTextI18n")
    private fun showDatePickerTo()
    {
        val commonClass= CommonClass()
        commonClass.datePicker(this@TransactionHistoryActivity,etToDate)
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
