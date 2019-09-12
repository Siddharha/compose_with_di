package com.app.l_pesa.wallet.view

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.os.CountDownTimer
import android.text.TextUtils
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.l_pesa.R
import com.app.l_pesa.common.CommonClass
import com.app.l_pesa.common.CommonMethod
import com.app.l_pesa.common.SharedPref
import com.app.l_pesa.main.view.MainActivity
import com.app.l_pesa.wallet.adapter.WalletHistoryAdapter
import com.app.l_pesa.wallet.inter.ICallBackWalletWithdrawalHistory
import com.app.l_pesa.wallet.model.ResWalletWithdrawalHistory
import com.app.l_pesa.wallet.presenter.PresenterWithdrawalHistory
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.activity_wallet_history.*
import kotlinx.android.synthetic.main.content_wallet_history.*
import kotlinx.android.synthetic.main.layout_filter_by_date.*
import java.util.*

class WalletHistoryActivity : AppCompatActivity(), ICallBackWalletWithdrawalHistory {

    private lateinit var listWithdrawalHistory   : ArrayList<ResWalletWithdrawalHistory.WithdrawalHistory>
    private lateinit var adapterWalletHistory    : WalletHistoryAdapter
    private lateinit var bottomSheetBehavior     : BottomSheetBehavior<*>
    private lateinit var countDownTimer          : CountDownTimer

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
        initTimer()

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

        initData("","","DEFAULT")

    }

    private fun initData(from_date: String, to_date: String,type:String)
    {
        if(CommonMethod.isNetworkAvailable(this@WalletHistoryActivity))
        {
            swipeRefreshLayout.isRefreshing=true
            val presenterWithdrawalHistory=PresenterWithdrawalHistory()
            presenterWithdrawalHistory.getWithdrawalHistory(this@WalletHistoryActivity,from_date,to_date,type,this)
        }
        else
        {
            swipeRefreshLayout.isRefreshing=false
            CommonMethod.customSnackBarError(rootLayout,this@WalletHistoryActivity,resources.getString(R.string.no_internet))
        }

    }

    private fun doFilter()
    {

        when {
            bottomSheetBehavior.state == BottomSheetBehavior.STATE_HIDDEN -> {
                bottomSheetBehavior.state =(BottomSheetBehavior.STATE_EXPANDED)
                resetFilter()

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
                CommonMethod.customSnackBarError(rootLayout, this@WalletHistoryActivity, resources.getString(R.string.you_have_select_from_date_to_date))
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
            llmOBJ.orientation          = RecyclerView.VERTICAL
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

   /* private fun loadMore(from_date: String, to_date: String)
    {
        if(CommonMethod.isNetworkAvailable(this@WalletHistoryActivity))
        {
            val loadModel  = ResWalletWithdrawalHistory.WithdrawalHistory(0, 0, 0.0,0.0, 0.0,"", "", "", "")

            listWithdrawalHistory.add(loadModel)
            adapterWalletHistory.notifyItemInserted(listWithdrawalHistory.size-1)

            val presenterWithdrawalHistory=PresenterWithdrawalHistory()
            presenterWithdrawalHistory.getWithdrawalHistoryPaginate(this@WalletHistoryActivity,after,from_date,to_date,this)

        }
        else{
            swipeRefreshLayout.isRefreshing = false
            CommonMethod.customSnackBarError(rootLayout,this@WalletHistoryActivity,resources.getString(R.string.no_internet))
        }
    }*/

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

    override fun onEmptyWalletWithdrawalHistory(type: String) {

        swipeRefreshLayout.isRefreshing = false
        rlList.visibility=View.INVISIBLE
        if(type=="FILTER")
        {
            txt_message.text = resources.getString(R.string.no_result_found)
        }
        else
        {
            txt_message.text = resources.getString(R.string.empty_withdrawal_history_message)
        }
        cardView.visibility=View.VISIBLE
    }

    override fun onSessionTimeOut(message: String) {

        swipeRefreshLayout.isRefreshing = false
        val dialogBuilder = AlertDialog.Builder(this@WalletHistoryActivity,R.style.MyAlertDialogTheme)
        dialogBuilder.setMessage(message)
                .setCancelable(false)
                .setPositiveButton("Ok") { dialog, _ ->
                    dialog.dismiss()
                    val sharedPrefOBJ= SharedPref(this@WalletHistoryActivity)
                    sharedPrefOBJ.removeShared()
                    startActivity(Intent(this@WalletHistoryActivity, MainActivity::class.java))
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
                if(swipeRefreshLayout.isRefreshing && CommonMethod.isNetworkAvailable(this@WalletHistoryActivity))
                {
                    CommonMethod.customSnackBarError(rootLayout,this@WalletHistoryActivity,resources.getString(R.string.please_wait))
                }
                else
                {
                    onBackPressed()
                }
                true

            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.left_in, R.anim.right_out)
    }

    private fun initTimer() {

        countDownTimer= object : CountDownTimer(CommonMethod.sessionTime().toLong(), 1000) {
            override fun onTick(millisUntilFinished: Long) {

            }
            override fun onFinish() {
                onSessionTimeOut(resources.getString(R.string.session_time_out))
                countDownTimer.cancel()

            }}
        countDownTimer.start()

    }


    override fun onUserInteraction() {
        super.onUserInteraction()

        countDownTimer.cancel()
        countDownTimer.start()
    }


    public override fun onStop() {
        super.onStop()
        countDownTimer.cancel()

    }

}
