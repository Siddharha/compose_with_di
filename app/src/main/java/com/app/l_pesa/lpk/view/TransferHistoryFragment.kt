package com.app.l_pesa.lpk.view

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.app.l_pesa.R
import com.app.l_pesa.common.CommonMethod
import com.app.l_pesa.lpk.inter.ICallBackTransferHistory
import com.app.l_pesa.lpk.model.ResTransferHistory
import com.app.l_pesa.lpk.presenter.PresenterTransferHistory
import kotlinx.android.synthetic.main.fragment_transfer_history.*
import java.util.ArrayList

class TransferHistoryFragment : Fragment(), ICallBackTransferHistory {


    companion object {
        fun newInstance(): Fragment {
            return TransferHistoryFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_transfer_history, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initData()
        swipeRefresh()

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
        if(CommonMethod.isNetworkAvailable(activity!!))
        {
            swipeRefreshLayout.isRefreshing = true
            val presenterTransferHistory = PresenterTransferHistory()
            presenterTransferHistory.getTokenHistory(activity!!,this)
        }
        else
        {
            CommonMethod.customSnackBarError(rootLayout,activity!!,resources.getString(R.string.no_internet))
        }
    }

    override fun onSuccessTransferHistory(userTransferHistory: ArrayList<ResTransferHistory.UserTransferHistory>) {

        swipeRefreshLayout.isRefreshing = false
    }

    override fun onEmptyTransferHistory() {

        swipeRefreshLayout.isRefreshing = false
    }

    override fun onErrorTransferHistory(message: String) {

        swipeRefreshLayout.isRefreshing = false
    }
}