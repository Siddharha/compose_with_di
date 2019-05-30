package com.app.l_pesa.wallet.view

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.app.l_pesa.R
import com.app.l_pesa.common.CommonMethod
import com.app.l_pesa.lpk.inter.ICallBackInfoLPK
import com.app.l_pesa.lpk.model.ResInfoLPK
import com.app.l_pesa.lpk.presenter.PresenterInfoLPK
import com.app.l_pesa.wallet.inter.ICallBackWallet
import com.app.l_pesa.wallet.presenter.PresenterWithdrawal
import com.google.gson.JsonObject
import com.kaopiz.kprogresshud.KProgressHUD
import kotlinx.android.synthetic.main.fragment_wallet.*
import java.text.DecimalFormat
import android.widget.Toast
import com.app.l_pesa.common.SharedPref
import com.app.l_pesa.main.MainActivity


class WalletFragment :Fragment(), ICallBackWallet, ICallBackInfoLPK {


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
                .setCancellable(false)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f)

    }

    @SuppressLint("SetTextI18n")
    private fun initData()
    {
        if(CommonMethod.isNetworkAvailable(activity!!))
        {
            progressDialog.show()
            val presenterInfoLPK= PresenterInfoLPK()
            presenterInfoLPK.getInfoLPK(activity!!,this,"")
        }
        else
        {
            CommonMethod.customSnackBarError(rootLayout,activity!!,resources.getString(R.string.no_internet))
        }


        buttonWithdraw.setOnClickListener {

            if(etWithdrawalAmount.text.toString().isEmpty())
            {
                CommonMethod.hideKeyboardView(activity as AppCompatActivity)
                CommonMethod.customSnackBarError(rootLayout,activity!!,resources.getString(R.string.required_withdrawal_amount))
            }
            else
            {
                if(CommonMethod.isNetworkAvailable(activity!!))
                {
                    val alertDialog = AlertDialog.Builder(activity!!)
                    alertDialog.setTitle(resources.getString(R.string.app_name))
                    alertDialog.setMessage(resources.getString(R.string.want_to_withdraw))
                    alertDialog.setPositiveButton("Yes") { _, _ -> withdrawAmount() }
                            .setNegativeButton("No") { dialog, _ -> dialog.dismiss() }
                    alertDialog.show()

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

    private fun withdrawAmount()
    {
        progressDialog.show()
        buttonWithdraw.isClickable=false
        CommonMethod.hideKeyboardView(activity as AppCompatActivity)
        val jsonObject = JsonObject()
        jsonObject.addProperty("amount",etWithdrawalAmount.text.toString().trim())

        val presenterWithdrawal= PresenterWithdrawal()
        presenterWithdrawal.doAddWithdrawal(activity!!,jsonObject,this)
    }

    @SuppressLint("SetTextI18n")
    override fun onSuccessInfoLPK(data: ResInfoLPK.Data?, type: String) {

        dismiss()
        val format = DecimalFormat()
        format.isDecimalSeparatorAlwaysShown = false

        rootLayout.postDelayed(Runnable {

            txt_min_val.text = format.format(data!!.lpesa_min_withdrawal_wallet).toString()+" "+data.currency_code
            txt_max_val.text = format.format(data.lpesa_max_withdrawal_wallet).toString()+" "+data.currency_code
            txtWalletBal.text = format.format(data.wallet_balance).toString()+" "+data.currency_code
            txtCommission.text=resources.getString(R.string.commission_for_l_pesa)+" "+format.format(data.commission_eachtime).toString()+"%"

        }, 100)


    }

    override fun onErrorInfoLPK(message: String) {

        dismiss()
        CommonMethod.customSnackBarError(rootLayout,activity!!,message)
    }

    override fun onSessionTimeOut(message: String) {
        dismiss()
        val dialogBuilder = AlertDialog.Builder(activity!!)
        dialogBuilder.setMessage(message)
                // if the dialog is cancelable
                .setCancelable(false)
                .setPositiveButton("Ok", DialogInterface.OnClickListener {
                    dialog, id ->
                    dialog.dismiss()
                    val sharedPrefOBJ= SharedPref(activity!!)
                    sharedPrefOBJ.removeShared()
                    startActivity(Intent(activity!!, MainActivity::class.java))
                    activity!!.overridePendingTransition(R.anim.right_in, R.anim.left_out)
                    activity!!.finish()
                })

        val alert = dialogBuilder.create()
        alert.setTitle(resources.getString(R.string.app_name))
        alert.show()

    }


    override fun onSuccessWalletWithdrawal(message: String) {
       etWithdrawalAmount.text!!.clear()
       buttonWithdraw.isClickable=true
       CommonMethod.customSnackBarSuccess(rootLayout,activity!!,message)
       val presenterInfoLPK= PresenterInfoLPK()
       presenterInfoLPK.getInfoLPK(activity!!,this,"")

    }

    override fun onErrorWalletWithdrawal(message: String) {
        dismiss()
        buttonWithdraw.isClickable=true
        CommonMethod.customSnackBarError(rootLayout,activity!!,message)
    }


    private fun dismiss()
    {
        if(progressDialog.isShowing)
        {
            progressDialog.dismiss()
        }
    }
}