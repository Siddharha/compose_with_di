package com.app.l_pesa.wallet.view

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.text.Spannable
import android.text.SpannableString
import android.text.style.RelativeSizeSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.app.l_pesa.R
import com.app.l_pesa.common.CommonMethod
import com.app.l_pesa.common.CustomTypefaceSpan
import com.app.l_pesa.common.SharedPref
import com.app.l_pesa.dashboard.view.DashboardActivity
import com.app.l_pesa.lpk.inter.ICallBackInfoLPK
import com.app.l_pesa.lpk.model.ResInfoLPK
import com.app.l_pesa.lpk.presenter.PresenterInfoLPK
import com.app.l_pesa.main.view.MainActivity
import com.app.l_pesa.wallet.inter.ICallBackWallet
import com.app.l_pesa.wallet.presenter.PresenterWithdrawal
import com.facebook.appevents.AppEventsConstants
import com.facebook.appevents.AppEventsLogger
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.fragment_wallet.*
import java.text.DecimalFormat


class WalletFragment : Fragment(), ICallBackWallet, ICallBackInfoLPK {


    private lateinit  var progressDialog: ProgressDialog

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
        progressDialog = ProgressDialog(activity!!,R.style.MyAlertDialogStyle)
        val message=   SpannableString(resources.getString(R.string.loading))
        val face = Typeface.createFromAsset(activity!!.assets, "fonts/Montserrat-Regular.ttf")
        message.setSpan(RelativeSizeSpan(1.0f), 0, message.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        message.setSpan(CustomTypefaceSpan("", face), 0, message.length, 0)
        progressDialog.isIndeterminate = true
        progressDialog.setMessage(message)
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        progressDialog.setCancelable(false)
        progressDialog.setCanceledOnTouchOutside(false)

    }

    @SuppressLint("SetTextI18n")
    private fun initData()
    {
        Handler().postDelayed({
            (activity as DashboardActivity).visibleFilter(false)
            (activity as DashboardActivity).visibleButton(false)
        }, 200)

        if(CommonMethod.isNetworkAvailable(activity!!))
        {
            val logger = AppEventsLogger.newLogger(activity)
            val params =  Bundle()
            params.putString(AppEventsConstants.EVENT_PARAM_CONTENT_TYPE, "Wallet Information")
            logger.logEvent(AppEventsConstants.EVENT_NAME_VIEWED_CONTENT, params)
            progressDialog.show()

            Thread(Runnable {
                val presenterInfoLPK= PresenterInfoLPK()
                presenterInfoLPK.getInfoLPK(activity!!,this,"")
            }).start()

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
                    val alertDialog = AlertDialog.Builder(activity!!,R.style.MyAlertDialogTheme)
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

        rootLayout.postDelayed({

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
        val dialogBuilder = AlertDialog.Builder(activity!!,R.style.MyAlertDialogTheme)
        dialogBuilder.setMessage(message)
                .setCancelable(false)
                .setPositiveButton("Ok") { dialog, _ ->
                    dialog.dismiss()
                    val sharedPrefOBJ= SharedPref(activity!!)
                    sharedPrefOBJ.removeShared()
                    startActivity(Intent(activity!!, MainActivity::class.java))
                    activity!!.overridePendingTransition(R.anim.right_in, R.anim.left_out)
                    activity!!.finish()
                }

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