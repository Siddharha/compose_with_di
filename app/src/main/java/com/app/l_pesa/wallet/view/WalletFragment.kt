package com.app.l_pesa.wallet.view

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.app.l_pesa.R
import com.app.l_pesa.common.CommonMethod
import com.app.l_pesa.common.SharedPref
import com.app.l_pesa.dashboard.inter.ICallBackDashboard
import com.app.l_pesa.dashboard.model.ResDashboard
import com.app.l_pesa.dashboard.presenter.PresenterDashboard
import com.app.l_pesa.wallet.inter.ICallBackWallet
import com.app.l_pesa.wallet.presenter.PresenterWithdrawal
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.kaopiz.kprogresshud.KProgressHUD
import kotlinx.android.synthetic.main.fragment_wallet.*
import java.text.DecimalFormat



class WalletFragment :Fragment(), ICallBackWallet, ICallBackDashboard {



    private lateinit  var progressDialog: KProgressHUD

    companion object {
        fun newInstance(): Fragment {
            return WalletFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_wallet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        CommonMethod.hideKeyboardView(activity as AppCompatActivity)
        initLoader()
        initData()


    }

    private fun initLoader()
    {
        progressDialog=KProgressHUD.create(activity)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f)

    }

    @SuppressLint("SetTextI18n")
    private fun initData()
    {
        val sharedPrefOBJ= SharedPref(activity!!)
        val userDashBoard  = Gson().fromJson<ResDashboard.Data>(sharedPrefOBJ.userDashBoard, ResDashboard.Data::class.java)


        val format = DecimalFormat()
        format.isDecimalSeparatorAlwaysShown = false
        if(userDashBoard!=null)
        {
            txtWalletBal.text=format.format(userDashBoard.wallet_balance).toString()+" "+userDashBoard.currencyCode
            txtCommission.text=resources.getString(R.string.commission_for_l_pesa)+" "+format.format(userDashBoard.commission_eachtime).toString()+"%"
        }

        buttonWithdraw.setOnClickListener {

            if(etWithdrawalAmount.text.toString().isEmpty())
            {
                CommonMethod.customSnackBarError(rootLayout,activity!!,resources.getString(R.string.required_withdrawal_amount))
            }
            else
            {
                if(CommonMethod.isNetworkAvailable(activity!!))
                {
                    progressDialog.show()
                    buttonWithdraw.isClickable=false
                    CommonMethod.hideKeyboardView(activity as AppCompatActivity)
                    val jsonObject = JsonObject()
                    jsonObject.addProperty("amount",etWithdrawalAmount.text.toString().trim())

                    val presenterWithdrawal= PresenterWithdrawal()
                    presenterWithdrawal.doAddWithdrawal(activity!!,jsonObject,this)
                }
                else
                {
                    CommonMethod.customSnackBarError(rootLayout,activity!!,resources.getString(R.string.no_internet))
                }
            }

        }

        imgTransactionHistory.setOnClickListener {

            if(CommonMethod.isNetworkAvailable(activity!!))
            {
                val intent = Intent(activity, TransactionHistoryActivity::class.java)
                startActivity(intent)
                activity?.overridePendingTransition(R.anim.right_in, R.anim.left_out)
            }
            else
            {
                CommonMethod.customSnackBarError(rootLayout,activity!!,resources.getString(R.string.no_internet))
            }


        }

        imgWithdrawalHistory.setOnClickListener {

            if(CommonMethod.isNetworkAvailable(activity!!))
            {
                val intent = Intent(activity, WalletHistoryActivity::class.java)
                startActivity(intent)
                activity?.overridePendingTransition(R.anim.right_in, R.anim.left_out)
            }
            else
            {
                CommonMethod.customSnackBarError(rootLayout,activity!!,resources.getString(R.string.no_internet))
            }

        }
    }


    override fun onSuccessWalletWithdrawal(message: String) {

        CommonMethod.customSnackBarSuccess(rootLayout,activity!!,message)
        val sharedPrefOBJ = SharedPref(activity!!)
        val presenterDashboard= PresenterDashboard()
        presenterDashboard.getDashboard(activity!!,sharedPrefOBJ.accessToken,this)
    }

    override fun onErrorWalletWithdrawal(message: String) {
        dismiss()
        buttonWithdraw.isClickable=true
        CommonMethod.customSnackBarError(rootLayout,activity!!,message)
    }

    @SuppressLint("SetTextI18n")
    override fun onSuccessDashboard(data: ResDashboard.Data) {

        val sharedPrefOBJ = SharedPref(activity!!)
        val gson = Gson()
        val dashBoardData = gson.toJson(data)
        sharedPrefOBJ.userDashBoard = dashBoardData

        val format = DecimalFormat()
        format.isDecimalSeparatorAlwaysShown = false

        activity!!.runOnUiThread {

            txtWalletBal.text=format.format(data.wallet_balance).toString()+" "+data.currencyCode
            txtCommission.text=resources.getString(R.string.commission_for_l_pesa)+" "+format.format(data.commission_eachtime).toString()+"%"


        }


    }

    override fun onFailureDashboard(jsonMessage: String) {
        dismiss()
        buttonWithdraw.isClickable=true
        CommonMethod.customSnackBarError(rootLayout,activity!!,jsonMessage)
    }

    private fun dismiss()
    {
        if(progressDialog.isShowing)
        {
            progressDialog.dismiss()
        }
    }
}