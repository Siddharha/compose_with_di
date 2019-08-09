package com.app.l_pesa.lpk.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.*
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import com.app.l_pesa.R
import com.app.l_pesa.common.CommonMethod
import com.app.l_pesa.common.SharedPref
import com.app.l_pesa.dashboard.model.ResDashboard
import com.app.l_pesa.lpk.inter.ICallBackWalletAddress
import com.app.l_pesa.lpk.presenter.PresenterWalletAddress
import com.app.l_pesa.main.view.MainActivity
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.kaopiz.kprogresshud.KProgressHUD
import kotlinx.android.synthetic.main.fragment_wallet_address.*
import java.util.*


class WalletAddressFragment : androidx.fragment.app.Fragment(), ICallBackWalletAddress {

    private  var address=""
    private  val hashMapOLD = HashMap<String, String>()
    private lateinit  var progressDialog: KProgressHUD

    companion object {
        fun newInstance(): androidx.fragment.app.Fragment {
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
        initLoader()

        val sharedPrefOBJ=SharedPref(activity!!)
        val userDashBoard  = Gson().fromJson<ResDashboard.Data>(sharedPrefOBJ.userDashBoard, ResDashboard.Data::class.java)

        hashMapOLD["wallet"]     =userDashBoard.walletAddress
        address=userDashBoard.walletAddress

        if(!TextUtils.isEmpty(userDashBoard.walletAddress))
        {
            etWalletAddress.visibility=View.VISIBLE
            etWalletAddress.setText(userDashBoard.walletAddress)
        }
        else
        {
            txtEtherScan.visibility=View.INVISIBLE
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
                if(CommonMethod.isNetworkAvailable(activity!!))
                {
                    progressDialog.show()
                    buttonWalletAddress.isClickable=false
                    val presenterWalletAddress= PresenterWalletAddress()
                    val jsonObject = JsonObject()

                    jsonObject.addProperty("ether_address",etWalletAddress.text.toString().trim())
                    presenterWalletAddress.doWalletAddress(activity!!,jsonObject,this)
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
        progressDialog= KProgressHUD.create(activity)
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


    override fun onSuccessWalletAddress() {

        dismiss()
        txtEtherScan.visibility=View.VISIBLE
        hashMapOLD["wallet"]  = ""+etWalletAddress.text.toString().trim()
        address=etWalletAddress.text.toString().trim()
        buttonWalletAddress.isClickable=true
        CommonMethod.customSnackBarSuccess(rootLayout,activity!!,resources.getString(R.string.token_update_successfully))
        val sharedPrefOBJ=SharedPref(activity!!)
        val userDashBoard  = Gson().fromJson<ResDashboard.Data>(sharedPrefOBJ.userDashBoard, ResDashboard.Data::class.java)
        userDashBoard.walletAddress=etWalletAddress.text.toString().trim()
        val json = Gson().toJson(userDashBoard)
        sharedPrefOBJ.userDashBoard      = json
    }

    override fun onErrorWalletAddress(message: String) {

        dismiss()
        CommonMethod.customSnackBarError(rootLayout,activity!!,message)
        buttonWalletAddress.isClickable=true

    }

    override fun onSessionTimeOut(message: String) {

        val dialogBuilder = AlertDialog.Builder(activity!!)
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


}