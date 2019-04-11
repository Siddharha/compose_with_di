package com.app.l_pesa.wallet.view

import android.app.Activity
import android.graphics.Typeface
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.TextView
import com.app.l_pesa.R
import com.app.l_pesa.common.CommonMethod
import com.app.l_pesa.wallet.adapter.TransactionAllAdapter
import com.app.l_pesa.wallet.inter.ICallBackTransaction
import com.app.l_pesa.wallet.model.ResWalletHistory
import com.app.l_pesa.wallet.presenter.PresenterTransactionAll

import kotlinx.android.synthetic.main.activity_transaction_history.*
import kotlinx.android.synthetic.main.layout_recycler.*
import java.util.ArrayList

class TransactionHistoryActivity : AppCompatActivity(), ICallBackTransaction {


    private lateinit var listSavingsHistory           : ArrayList<ResWalletHistory.SavingsHistory>
    private lateinit var adapterTransactionHistory    : TransactionAllAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transaction_history)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbarFont(this@TransactionHistoryActivity)

        swipeRefresh()
        initData()

    }

    private fun swipeRefresh()
    {
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent)
        swipeRefreshLayout.setOnRefreshListener {
            initData()
        }
    }

    private fun initData()
    {
        listSavingsHistory           = ArrayList()
        adapterTransactionHistory    = TransactionAllAdapter(this@TransactionHistoryActivity, listSavingsHistory)

        swipeRefreshLayout.isRefreshing=true
        val presenterTransactionAll= PresenterTransactionAll()
        presenterTransactionAll.getTransactionAll(this@TransactionHistoryActivity,this)

    }

    override fun onSuccessTransaction(savingsHistory: ArrayList<ResWalletHistory.SavingsHistory>) {


        runOnUiThread {

           // hasNext = cursors!!.hasNext
            //after = cursors.after
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

                        /*if (hasNext)
                        {
                            loadMore()
                        }*/

                    }

                }
            })
        }
    }

    private fun loadMore()
    {
        if(CommonMethod.isNetworkAvailable(this@TransactionHistoryActivity))
        {
            val loadModel  = ResWalletHistory.SavingsHistory(0, 0, 0.0,
                            "", "", "",
                            "", "", "", "", "", "", "","")

            listSavingsHistory.add(loadModel)
            adapterTransactionHistory.notifyItemInserted(listSavingsHistory.size-1)

            //val presenterWithdrawalHistory= PresenterWithdrawalHistory()
            //presenterWithdrawalHistory.getWithdrawalHistoryPaginate(activity!!,after,this)

        }
        else{
            swipeRefreshLayout.isRefreshing = false
            CommonMethod.customSnackBarError(rootLayout,this@TransactionHistoryActivity,resources.getString(R.string.no_internet))
        }
    }

    override fun onEmptyTransaction() {

        swipeRefreshLayout.isRefreshing=false
    }

    override fun onErrorTransaction(message: String) {

        swipeRefreshLayout.isRefreshing=true
        CommonMethod.customSnackBarError(rootLayout,this@TransactionHistoryActivity,message)
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
