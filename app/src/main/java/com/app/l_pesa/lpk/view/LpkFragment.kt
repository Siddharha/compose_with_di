package com.app.l_pesa.lpk.view

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.app.l_pesa.R
import com.app.l_pesa.common.CommonMethod
import com.app.l_pesa.common.SharedPref
import com.app.l_pesa.lpk.inter.ICallBackInfoLPK
import com.app.l_pesa.lpk.model.ResInfoLPK
import com.app.l_pesa.lpk.presenter.PresenterInfoLPK
import com.google.gson.Gson
import com.kaopiz.kprogresshud.KProgressHUD
import kotlinx.android.synthetic.main.fragment_lpk.*

class LpkFragment: Fragment(), ICallBackInfoLPK {

    private lateinit  var progressDialog: KProgressHUD

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
        progressDialog=KProgressHUD.create(activity)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f)

    }

    private fun initData()
    {
        constraintWithdrawal.setOnClickListener {

            if(CommonMethod.isNetworkAvailable(activity!!))
            {
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
        val gson = Gson()
        val json = gson.toJson(data)
        sharedPrefOBJ.lpkInfo= json
        dismiss()
        if(type=="WITHDRAWAL")
        {
            startActivity(Intent(activity, LPKWithdrawalActivity::class.java))
            activity?.overridePendingTransition(R.anim.right_in, R.anim.left_out)
        }
        else
        {
            startActivity(Intent(activity, LPKSavingsActivity::class.java))
            activity?.overridePendingTransition(R.anim.right_in, R.anim.left_out)
        }


    }

    override fun onErrorInfoLPK(message: String) {
        dismiss()
        CommonMethod.customSnackBarError(rootLayout,activity!!,message)
    }
}