package com.app.l_pesa.lpk.view

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.app.l_pesa.R
import com.app.l_pesa.common.CommonMethod
import com.app.l_pesa.common.SharedPref
import com.app.l_pesa.lpk.inter.ICallBackTokenWithdrawal
import com.app.l_pesa.lpk.model.ResInfoLPK
import com.app.l_pesa.lpk.presenter.PresenterTokenWithdrawal
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.kaopiz.kprogresshud.KProgressHUD
import kotlinx.android.synthetic.main.fragment_token_withdrawal.*
import java.text.DecimalFormat


class TokenWithdrawalFragment: Fragment(), ICallBackTokenWithdrawal {

    private lateinit  var progressDialog: KProgressHUD
    companion object {
        fun newInstance(): Fragment {
            return TokenWithdrawalFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_token_withdrawal, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initData()
        initLoader()

    }


    @SuppressLint("SetTextI18n")
    private fun initData()
    {
        val sharedPrefOBJ= SharedPref(activity!!)
        val infoLPK = Gson().fromJson<ResInfoLPK.Data>(sharedPrefOBJ.lpkInfo, ResInfoLPK.Data::class.java)
        txt_min_lpk_amount.text = resources.getString(R.string.minimum_withdrawal_amount)+" "+infoLPK.lpkMinWithdrawal.toString()+" LPK"

        val format = DecimalFormat()
        format.isDecimalSeparatorAlwaysShown = false

        txtLPK.text = format.format(infoLPK.lpkValue)+" LPK"
        txtLockedVal.text = format.format(infoLPK.lpkLockedValue)+" LPK is in Deposit"

        buttonSubmit.setOnClickListener {

            if(TextUtils.isEmpty(etToken.text.toString()))
            {
                CommonMethod.hideKeyboardView(activity!! as AppCompatActivity)
                CommonMethod.customSnackBarError(rootLayout,activity!!,resources.getString(R.string.required_token_amount))
            }
            else if(infoLPK.lpkMinWithdrawal.toInt()>etToken.text.toString().toInt())
            {
                CommonMethod.hideKeyboardView(activity!! as AppCompatActivity)
                CommonMethod.customSnackBarError(rootLayout,activity!!,resources.getString(R.string.minimum_withdrawal_amount)+" "+infoLPK.lpkMinWithdrawal.toInt().toString()+" LPK")
            }
            else
            {
                if(CommonMethod.isNetworkAvailable(activity!!))
                {
                    progressDialog.show()
                    buttonSubmit.isClickable=false
                    CommonMethod.hideKeyboardView(activity!! as AppCompatActivity)
                    val jsonObject = JsonObject()
                    jsonObject.addProperty("token_value",etToken.text.toString())

                    val presenterTokenTransfer= PresenterTokenWithdrawal()
                    presenterTokenTransfer.doTokenWithdrawal(activity!!,jsonObject,this)
                }
                else
                {
                    CommonMethod.customSnackBarError(rootLayout,activity!!,resources.getString(R.string.no_internet))
                }
            }

        }

    }

    private fun initLoader()
    {
        progressDialog=KProgressHUD.create(activity)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setCancellable(false)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f)

    }
    private fun dismiss()
    {
        if(progressDialog.isShowing)
        {
            progressDialog.dismiss()
        }
    }

    override fun onSuccessTokenWithdrawal() {
        etToken.text!!.clear()
        dismiss()
        buttonSubmit.isClickable=true
        CommonMethod.customSnackBarSuccess(rootLayout,activity!!,resources.getString(R.string.token_withdrawal_successfully))
    }

    override fun onErrorTokenWithdrawal(message: String, statusCode: Int) {

        dismiss()
        if(statusCode==10097)
        {
            Toast.makeText(activity,resources.getString(R.string.update_erc_address),Toast.LENGTH_SHORT).show()
        }
        else
        {
            CommonMethod.customSnackBarError(rootLayout,activity as AppCompatActivity,message)
        }

        buttonSubmit.isClickable=true

    }
}