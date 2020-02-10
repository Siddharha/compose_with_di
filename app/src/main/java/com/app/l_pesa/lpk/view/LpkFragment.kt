package com.app.l_pesa.lpk.view

import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.text.Spannable
import android.text.SpannableString
import android.text.style.RelativeSizeSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.app.l_pesa.R
import com.app.l_pesa.common.CommonMethod
import com.app.l_pesa.common.CustomTypeFaceSpan
import com.app.l_pesa.common.SharedPref
import com.app.l_pesa.dashboard.view.DashboardActivity
import com.app.l_pesa.lpk.inter.ICallBackInfoLPK
import com.app.l_pesa.lpk.model.ResInfoLPK
import com.app.l_pesa.lpk.presenter.PresenterInfoLPK
import com.app.l_pesa.main.view.MainActivity
import com.facebook.appevents.AppEventsConstants
import com.facebook.appevents.AppEventsLogger
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_lpk.*


class LpkFragment: Fragment(), ICallBackInfoLPK {

    private lateinit  var progressDialog: ProgressDialog

    companion object {
        fun newInstance(): Fragment {
            return LpkFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_lpk, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initLoader()
        initData()

    }

    private fun initLoader()
    {
        progressDialog = ProgressDialog(activity!!, R.style.MyAlertDialogStyle)
        val message=   SpannableString(resources.getString(com.app.l_pesa.R.string.loading))
        val face = Typeface.createFromAsset(activity!!.assets, "fonts/Montserrat-Regular.ttf")
        message.setSpan(RelativeSizeSpan(1.0f), 0, message.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        message.setSpan(CustomTypeFaceSpan("", face!!, Color.parseColor("#535559")), 0, message.length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
        progressDialog.isIndeterminate = true
        progressDialog.setMessage(message)
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        progressDialog.setCancelable(false)
        progressDialog.setCanceledOnTouchOutside(false)

    }

    private fun initData()
    {
        Handler().postDelayed({
            (activity as DashboardActivity).visibleFilter(false)
            (activity as DashboardActivity).visibleButton(false)
        }, 200)

        val logger = AppEventsLogger.newLogger(activity)
        val params =  Bundle()
        params.putString(AppEventsConstants.EVENT_PARAM_CONTENT_TYPE, "LPK Section")
        logger.logEvent(AppEventsConstants.EVENT_NAME_VIEWED_CONTENT, params)

        constraintWithdrawal.setOnClickListener {

            if(CommonMethod.isNetworkAvailable(activity!!))
            {
                constraintWithdrawal.isClickable=false
                progressDialog.show()
                val presenterInfoLPK=PresenterInfoLPK()
                presenterInfoLPK.getInfoLPK(activity!!,this,"WITHDRAWAL")
            }
            else
            {
                CommonMethod.customSnackBarError(rootLayout,activity!!,resources.getString(com.app.l_pesa.R.string.no_internet))
            }


        }

        constraintSavings.setOnClickListener {

            if(CommonMethod.isNetworkAvailable(activity!!))
            {
                constraintSavings.isClickable=false
                progressDialog.show()
                val presenterInfoLPK=PresenterInfoLPK()
                presenterInfoLPK.getInfoLPK(activity!!,this,"SAVINGS")

            }
            else
            {
                CommonMethod.customSnackBarError(rootLayout,activity!!,resources.getString(com.app.l_pesa.R.string.no_internet))
            }


        }

        constraintTreading.setOnClickListener {

            val openBitMart: Intent?
            val manager = activity?.packageManager
            try {
                openBitMart = manager?.getLaunchIntentForPackage("com.bitmart.bitmarket")
                if (openBitMart == null)
                    throw PackageManager.NameNotFoundException()
                openBitMart.addCategory(Intent.CATEGORY_LAUNCHER)
                startActivity(openBitMart)
            } catch (e: PackageManager.NameNotFoundException) {

                val uri = Uri.parse("https://www.bitmart.com/")
                val intent = Intent(Intent.ACTION_VIEW, uri)
                startActivity(intent)
            }

        }
    }

    private fun dismiss()
    {
        if(progressDialog.isShowing)
        {
            progressDialog.dismiss()
        }
    }

    override fun onSuccessInfoLPK(data: ResInfoLPK.Data?, type: String) {

        val sharedPrefOBJ= SharedPref(activity!!)
        val json = Gson().toJson(data)
        sharedPrefOBJ.lpkInfo= json

        if(type=="WITHDRAWAL")
        {
            startActivity(Intent(activity, LPKWithdrawalActivity::class.java))
            activity?.overridePendingTransition(com.app.l_pesa.R.anim.right_in, com.app.l_pesa.R.anim.left_out)
            dismiss()
        }
        else
        {

            startActivity(Intent(activity, LPKSavingsActivity::class.java))
            activity?.overridePendingTransition(com.app.l_pesa.R.anim.right_in, com.app.l_pesa.R.anim.left_out)

            dismiss()

        }

        Handler().postDelayed({
            // Do something after 1s = 1000ms
            constraintSavings.isClickable=true
            constraintWithdrawal.isClickable=true
        }, 1000)


    }

    override fun onSessionTimeOut(message: String) {
        dismiss()
        val dialogBuilder = AlertDialog.Builder(activity!!, com.app.l_pesa.R.style.MyAlertDialogTheme)
        dialogBuilder.setMessage(message)
                .setCancelable(false)
                .setPositiveButton("Ok") { dialog, _ ->
                    dialog.dismiss()
                    val sharedPrefOBJ= SharedPref(activity!!)
                    sharedPrefOBJ.removeShared()
                    startActivity(Intent(activity!!, MainActivity::class.java))
                    activity!!.overridePendingTransition(com.app.l_pesa.R.anim.right_in, com.app.l_pesa.R.anim.left_out)
                    activity!!.finish()
                }

        val alert = dialogBuilder.create()
        alert.setTitle(resources.getString(com.app.l_pesa.R.string.app_name))
        alert.show()

    }

    override fun onErrorInfoLPK(message: String) {
        dismiss()
        constraintWithdrawal.isClickable=true
        constraintSavings.isClickable=true
        CommonMethod.customSnackBarError(rootLayout,activity!!,message)
    }
}