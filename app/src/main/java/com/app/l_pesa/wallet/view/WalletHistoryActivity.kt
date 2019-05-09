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
import com.app.l_pesa.wallet.adapter.WalletHistoryAdapter
import com.app.l_pesa.wallet.inter.ICallBackWalletWithdrawalHistory
import com.app.l_pesa.wallet.model.ResWalletWithdrawalHistory
import com.app.l_pesa.wallet.presenter.PresenterWithdrawalHistory
import kotlinx.android.synthetic.main.activity_wallet_history.*
import kotlinx.android.synthetic.main.content_wallet_history.*
import kotlinx.android.synthetic.main.layout_filter_by_date.*
import java.text.SimpleDateFormat
import java.util.*

class WalletHistoryActivity : AppCompatActivity(), ICallBackWalletWithdrawalHistory {

    private lateinit var listWithdrawalHistory   : ArrayList<ResWalletWithdrawalHistory.WithdrawalHistory>
    private lateinit var adapterWalletHistory    : WalletHistoryAdapter
    private lateinit var bottomSheetBehavior     : BottomSheetBehavior<*>

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
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent)
        swipeRefreshLayout.setOnRefreshListener {
            etFromDate.text!!.clear()
            etToDate.text!!.clear()
            initData("","")
        }
    }

    private fun initLoad()
    {
        listWithdrawalHistory           = ArrayList()
        adapterWalletHistory            = WalletHistoryAdapter(this@WalletHistoryActivity, listWithdrawalHistory)

        bottomSheetBehavior = BottomSheetBehavior.from<View>(bottom_sheet)
        bottomSheetBehavior.isHideable=true
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        imgFilter.setOnClickListener {

            doFilter()
        }

        initData("","")

    }

    private fun initData(from_date: String, to_date: String)
    {
        if(CommonMethod.isNetworkAvailable(this@WalletHistoryActivity))
        {
            swipeRefreshLayout.isRefreshing=true
            val presenterWithdrawalHistory=PresenterWithdrawalHistory()
            presenterWithdrawalHistory.getWithdrawalHistory(this@WalletHistoryActivity,from_date,to_date,this)
        }
        else
        {
            CommonMethod.customSnackBarError(rootLayout,this@WalletHistoryActivity,resources.getString(R.string.no_internet))
        }

    }

    private fun doFilter()
    {

        if(bottomSheetBehavior.state == BottomSheetBehavior.STATE_HIDDEN)
        {
            bottomSheetBehavior.state =(BottomSheetBehavior.STATE_HALF_EXPANDED)
            resetFilter()

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

        buttonFilterSubmit.setOnClickListener {

            if (TextUtils.isEmpty(etFromDate.text.toString()) && TextUtils.isEmpty(etToDate.text.toString())) {
                CommonMethod.customSnackBarError(rootLayout, this@WalletHistoryActivity, resources.getString(R.string.you_have_select_from_date_to_date))
            } else {

                    val fromDate = CommonMethod.dateConvertYMD(etFromDate.text.toString())
                    val toDate = CommonMethod.dateConvertYMD(etToDate.text.toString())
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                    initData(fromDate!!,toDate!!)

            }
        }

        imgCancel.setOnClickListener {

            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN)

        }

        buttonReset.setOnClickListener {

            etFromDate.text!!.clear()
            etToDate.text!!.clear()

        }
    }

    @SuppressLint("SetTextI18n")
    private fun showDatePickerFrom()
    {

       val commonClass= CommonClass()
       commonClass.datePicker(this@WalletHistoryActivity,etFromDate)
    }

    @SuppressLint("SetTextI18n")
    private fun showDatePickerTo()
    {
        val commonClass= CommonClass()
        commonClass.datePicker(this@WalletHistoryActivity,etToDate)
    }

    private fun resetFilter()
    {
        buttonReset.setOnClickListener {

            etFromDate.text!!.clear()
            etToDate.text!!.clear()

        }
    }


    override fun onSuccessWalletWithdrawalHistory(withdrawal_history: ArrayList<ResWalletWithdrawalHistory.WithdrawalHistory>, cursors: ResWalletWithdrawalHistory.Cursors, from_date: String, to_date: String) {

        runOnUiThread {
            cardView.visibility=View.INVISIBLE
            rlList.visibility=View.VISIBLE
            hasNext = cursors.hasNext
            after   = cursors.after
            swipeRefreshLayout.isRefreshing = false
            listWithdrawalHistory.clear()
            listWithdrawalHistory.addAll(withdrawal_history)
            adapterWalletHistory        = WalletHistoryAdapter(this@WalletHistoryActivity, listWithdrawalHistory)
            val llmOBJ                  = LinearLayoutManager(this@WalletHistoryActivity)
            llmOBJ.orientation          = LinearLayoutManager.VERTICAL
            rlList.layoutManager        = llmOBJ
            rlList.adapter              = adapterWalletHistory
            adapterWalletHistory.notifyDataSetChanged()
            adapterWalletHistory.setLoadMoreListener(object : WalletHistoryAdapter.OnLoadMoreListener {
                override fun onLoadMore() {

                    rlList.post {

                        if (hasNext)
                        {
                          //  loadMore()
                        }

                    }

                }
            })
        }

    }

    private fun loadMore(from_date: String, to_date: String)
    {
        if(CommonMethod.isNetworkAvailable(this@WalletHistoryActivity))
        {
            val loadModel  = ResWalletWithdrawalHistory.WithdrawalHistory(0, 0, 0.0,0.0, "", "", "", "")

            listWithdrawalHistory.add(loadModel)
            adapterWalletHistory.notifyItemInserted(listWithdrawalHistory.size-1)

            val presenterWithdrawalHistory=PresenterWithdrawalHistory()
            presenterWithdrawalHistory.getWithdrawalHistoryPaginate(this@WalletHistoryActivity,after,from_date,to_date,this)

        }
        else{
            swipeRefreshLayout.isRefreshing = false
            CommonMethod.customSnackBarError(rootLayout,this@WalletHistoryActivity,resources.getString(R.string.no_internet))
        }
    }

    override fun onSuccessWalletWithdrawalHistoryPaginate(withdrawal_history: ArrayList<ResWalletWithdrawalHistory.WithdrawalHistory>, cursors: ResWalletWithdrawalHistory.Cursors,from_date: String, to_date: String) {

            hasNext = cursors.hasNext
            after = cursors.after

            if (listWithdrawalHistory.size != 0) {
                try {

                    listWithdrawalHistory.removeAt(listWithdrawalHistory.size - 1)
                    adapterWalletHistory.notifyDataChanged()
                    listWithdrawalHistory.addAll(withdrawal_history)
                    adapterWalletHistory.notifyItemRangeInserted(0, listWithdrawalHistory.size)

                } catch (e: Exception) {
                }
            }

    }

    override fun onErrorWalletWithdrawalHistory(message: String) {

        swipeRefreshLayout.isRefreshing = false
        cardView.visibility=View.INVISIBLE
        rlList.visibility=View.INVISIBLE
        CommonMethod.customSnackBarError(rootLayout,this@WalletHistoryActivity,message)
    }

    override fun onEmptyWalletWithdrawalHistory() {

        swipeRefreshLayout.isRefreshing = false
        rlList.visibility=View.INVISIBLE
        cardView.visibility=View.VISIBLE
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
