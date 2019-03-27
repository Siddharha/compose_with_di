package com.app.l_pesa.lpk.view

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.app.l_pesa.R
import com.app.l_pesa.common.CommonMethod
import kotlinx.android.synthetic.main.fragment_token_transfer.*

class TokenTransferFragment : Fragment() {

    companion object {
        fun newInstance(): Fragment {
            return TokenTransferFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_token_transfer, container, false)
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
            swipeRefreshLayout.isRefreshing=false
        }
    }

    private fun initData()
    {
        buttonTransfer.setOnClickListener {

            if(TextUtils.isEmpty(etToken.text.toString()))
            {
                CommonMethod.hideKeyboardView(activity!! as AppCompatActivity)
                CommonMethod.customSnackBarError(rootLayout,activity!!,resources.getString(R.string.required_token_amount))
            }
            else
            {
                if(CommonMethod.isNetworkAvailable(activity!!))
                {
                    buttonTransfer.isClickable=false
                    swipeRefreshLayout.isRefreshing=true
                }
                else
                {
                    CommonMethod.customSnackBarError(rootLayout,activity!!,resources.getString(R.string.no_internet))
                }
            }

        }
    }
}