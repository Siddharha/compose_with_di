package com.app.l_pesa.wallet.view

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.app.l_pesa.R
import com.app.l_pesa.wallet.inter.ICallBackTransaction
import com.app.l_pesa.wallet.model.ResWalletHistory
import com.app.l_pesa.wallet.presenter.PresenterTransactionAll
import kotlinx.android.synthetic.main.layout_recycler.*
import java.util.ArrayList

class TransactionAllFragment:Fragment(), ICallBackTransaction {



    companion object {
        fun newInstance(): Fragment {
            return TransactionAllFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.layout_recycler, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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

        val presenterTransactionAll= PresenterTransactionAll()
        presenterTransactionAll.getTransactionAll(activity!!,this)

    }

    override fun onSuccessTransaction(savingsHistory: ArrayList<ResWalletHistory.SavingsHistory>) {

        Toast.makeText(activity,""+savingsHistory.size,Toast.LENGTH_SHORT).show()
    }

    override fun onEmptyTransaction() {

    }

    override fun onErrorTransaction(message: String) {

    }
}