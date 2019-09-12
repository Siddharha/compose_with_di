package com.app.l_pesa.lpk.view

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import com.app.l_pesa.R
import com.app.l_pesa.common.CommonMethod
import com.app.l_pesa.common.SharedPref
import com.app.l_pesa.dashboard.view.DashboardActivity
import com.app.l_pesa.lpk.inter.ICallBackInfoLPK
import com.app.l_pesa.lpk.model.ResInfoLPK
import com.app.l_pesa.lpk.presenter.PresenterInfoLPK
import com.app.l_pesa.main.view.MainActivity
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_lpk.*


class LpkFragment: androidx.fragment.app.Fragment(), ICallBackInfoLPK {

    private lateinit  var progressDialog: ProgressDialog

    companion object {
        fun newInstance(): androidx.fragment.app.Fragment {
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
        progressDialog = ProgressDialog(activity!!,R.style.MyAlertDialogStyle)
        progressDialog.isIndeterminate = true
        progressDialog.setMessage(resources.getString(R.string.loading))
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        progressDialog.setCancelable(false)
        progressDialog.setCanceledOnTouchOutside(false)

    }

    private fun initData()
    {
        (activity as DashboardActivity).visibleFilter(false)
        (activity as DashboardActivity).visibleButton(false)

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
                CommonMethod.customSnackBarError(rootLayout,activity!!,resources.getString(R.string.no_internet))
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
                CommonMethod.customSnackBarError(rootLayout,activity!!,resources.getString(R.string.no_internet))
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
            activity?.overridePendingTransition(R.anim.right_in, R.anim.left_out)
            dismiss()
        }
        else
        {

            startActivity(Intent(activity, LPKSavingsActivity::class.java))
            activity?.overridePendingTransition(R.anim.right_in, R.anim.left_out)

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

    override fun onErrorInfoLPK(message: String) {
        dismiss()
        constraintWithdrawal.isClickable=true
        constraintSavings.isClickable=true
        CommonMethod.customSnackBarError(rootLayout,activity!!,message)
    }
}