package com.app.l_pesa.lpk.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.AppCompatTextView
import android.text.*
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import com.app.l_pesa.R
import com.app.l_pesa.common.CommonMethod
import com.app.l_pesa.common.SharedPref
import com.app.l_pesa.dashboard.model.ResDashboard
import com.app.l_pesa.lpk.inter.ICallBackWalletAddress
import com.app.l_pesa.lpk.presenter.PresenterWalletAddress
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.fragment_wallet_address.*
import java.util.HashMap
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.widget.TextView
import java.lang.Exception


class WalletAddressFragment : Fragment(), ICallBackWalletAddress {

    private  var address=""
    private  val hashMapOLD = HashMap<String, String>()

    companion object {
        fun newInstance(): Fragment {
            return WalletAddressFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_wallet_address, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initData()

    }



    private fun initData()
    {
        val sharedPrefOBJ=SharedPref(activity!!)
        val userDashBoard  = Gson().fromJson<ResDashboard.Data>(sharedPrefOBJ.userDashBoard, ResDashboard.Data::class.java)

        hashMapOLD["wallet"]     = ""+userDashBoard.walletAddress
        address=""+userDashBoard.walletAddress

        if(!TextUtils.isEmpty(userDashBoard.walletAddress))
        {
            etWalletAddress.setText(userDashBoard.walletAddress)
        }

        buttonWalletAddress.setOnClickListener {

            loadWalletAddress()
        }

        etWalletAddress.setOnEditorActionListener { _, actionId, _ ->
            var handled = false
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                loadWalletAddress()
                handled = true
            }
            handled
        }


       txtEtherScan.makeLinks(Pair("Etherscan", View.OnClickListener {

            try {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse("https://etherscan.io/address/$address")
                context!!.startActivity(intent)
            }
            catch (exp: Exception)
            {}

        }))
    }

    private fun AppCompatTextView.makeLinks(vararg links: Pair<String, View.OnClickListener>) {
        val spannableString = SpannableString(resources.getString(R.string.view_etherscan))
        for (link in links) {
            val clickableSpan = object : ClickableSpan() {
                override fun onClick(view: View) {
                    Selection.setSelection((view as AppCompatTextView).text as Spannable, 0)
                    view.invalidate()
                    link.second.onClick(view)
                }
            }
            val startIndexOfLink = resources.getString(R.string.view_etherscan).indexOf(link.first)
            spannableString.setSpan(clickableSpan, startIndexOfLink, startIndexOfLink + link.first.length,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        this.movementMethod = LinkMovementMethod.getInstance() // without LinkMovementMethod, link can not click
        this.setText(spannableString, TextView.BufferType.SPANNABLE)
    }

    private fun loadWalletAddress()
    {
        CommonMethod.hideKeyboardView(activity as AppCompatActivity)

        val hashMapNew = HashMap<String, String>()
        hashMapNew["wallet"]     = etWalletAddress.text.toString().trim()

        when {
            hashMapOLD==hashMapNew -> CommonMethod.customSnackBarError(rootLayout,activity!!,resources.getString(R.string.change_one_info))
            TextUtils.isEmpty(etWalletAddress.text.toString()) -> CommonMethod.customSnackBarError(rootLayout,activity!!,resources.getString(R.string.required_wallet_address))
            else -> {
                buttonWalletAddress.isClickable=false
                val presenterWalletAddress= PresenterWalletAddress()
                val jsonObject = JsonObject()

                jsonObject.addProperty("ether_address",etWalletAddress.text.toString().trim())
                presenterWalletAddress.doWalletAddress(activity!!,jsonObject,this)

            }
        }
    }


    override fun onSuccessWalletAddress() {
        hashMapOLD["wallet"]  = ""+etWalletAddress.text.toString().trim()
        address=""+etWalletAddress.text.toString().trim()
        buttonWalletAddress.isClickable=true
        CommonMethod.customSnackBarSuccess(rootLayout,activity!!,resources.getString(R.string.token_update_successfully))
    }

    override fun onErrorWalletAddress(message: String) {

        CommonMethod.customSnackBarError(rootLayout,activity!!,message)
        buttonWalletAddress.isClickable=true

    }


}