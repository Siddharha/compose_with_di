package com.app.l_pesa.dashboard.view

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.app.l_pesa.R
import com.app.l_pesa.common.CommonMethod
import com.app.l_pesa.common.SharedPref
import com.app.l_pesa.dashboard.inter.ICallBackDashboard
import com.app.l_pesa.dashboard.model.ResDashboard
import com.app.l_pesa.dashboard.presenter.PresenterDashboard
import com.google.gson.Gson
import kotlinx.android.synthetic.main.dashboard_layout.*
import android.support.v7.widget.LinearLayoutManager
import android.view.animation.TranslateAnimation
import android.widget.LinearLayout
import com.app.l_pesa.dashboard.adapter.LoanListAdapter
import com.app.l_pesa.dashboard.model.SeekBarProgress


class DashboardFragment: Fragment(), ICallBackDashboard {


    private val totalSpan = 99f
    private val redSpan = 33f
    private val blueSpan = 33f
    private val greenSpan = 33f


    private var progressItemList=ArrayList<SeekBarProgress>()
    lateinit var  mProgressItem:SeekBarProgress

    companion object {
        fun newInstance(): Fragment {
            return DashboardFragment()
        }
    }
    override fun onCreateView(inflater: LayoutInflater,container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.dashboard_layout, container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        swipeRefresh()
        initUI()
        initData()
    }

    private fun initUI()
    {
        loan_list.layoutManager = LinearLayoutManager(activity, LinearLayout.VERTICAL, false)
        imgClose.setOnClickListener {
            ll_banner.animate()
                    ?.translationY(ll_banner.height.toFloat())
                    ?.alpha(0.0f)
                    ?.setDuration(500)
                    ?.setListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator) {
                            super.onAnimationEnd(animation)
                            ll_banner.visibility = View.GONE

                            val animate = TranslateAnimation(
                                    0f, // fromXDelta
                                    0f, // toXDelta
                                    loan_list.height.toFloat(), // fromYDelta
                                    0f)                // toYDelta
                            animate.duration = 500
                            animate.fillAfter = true
                            loan_list.startAnimation(animate)
                        }
                    })


        }

        initSeekBar()
    }

    private fun initSeekBar()
    {
        progressItemList = ArrayList()

        mProgressItem = SeekBarProgress()
        mProgressItem.progressItemPercentage = redSpan / totalSpan * 100
        mProgressItem.color = R.color.colorRed
        progressItemList.add(mProgressItem)

        mProgressItem = SeekBarProgress()
        mProgressItem.progressItemPercentage = blueSpan / totalSpan * 100
        mProgressItem.color = R.color.colorBlack
        progressItemList.add(mProgressItem)

        mProgressItem = SeekBarProgress()
        mProgressItem.progressItemPercentage = greenSpan / totalSpan * 100
        mProgressItem.color = R.color.colorApp
        progressItemList.add(mProgressItem)

        seekBar.initData(progressItemList)
        seekBar.invalidate()

    }

    private fun swipeRefresh()
    {

        /*swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent)
        swipeRefreshLayout.setOnRefreshListener {

            initData()
        }*/
    }

    private fun initData()
    {
        val sharedPrefOBJ= SharedPref(activity!!)
        val dashBoard = Gson().fromJson<ResDashboard.Data>(sharedPrefOBJ.userDashBoard, ResDashboard.Data::class.java)

        if(dashBoard!=null)
        {
            setDashBoard(dashBoard)
        }



    }

    private fun loadDashboard()
    {
        if(CommonMethod.isNetworkAvailable(activity!!))
        {
            // swipeRefreshLayout.isRefreshing = true
            val sharedPrefOBJ= SharedPref(activity!!)
            val presenterDashboard= PresenterDashboard()
            presenterDashboard.getDashboard(activity!!,sharedPrefOBJ.accessToken,this)
        }
        else
        {
            CommonMethod.customSnackBarError(rootLayout,activity!!,resources.getString(R.string.no_internet))
        }
    }

    private fun setDashBoard(dashBoard: ResDashboard.Data)
    {

        setData(dashBoard)
    }

    override fun onSuccessDashboard(data: ResDashboard.Data) {

        setData(data)
    }

    override fun onFailureDashboard(jsonMessage: String) {

       // swipeRefreshLayout.isRefreshing = false
    }

    private fun setData(dashBoard: ResDashboard.Data)
    {
        val middleVal       = dashBoard.maxCreditScore /2
        left_header_txt.text    = dashBoard.fixedDepositAmount
        right_header_txt.text   = dashBoard.savingsAmount
        txt_start.text          = dashBoard.minCreditScore.toString()
        txt_middle.text         = middleVal.toString()
        txt_max.text            = dashBoard.maxCreditScore.toString()


        seekBar.post {
            seekBar.max             = dashBoard.maxCreditScore
            seekBar.progress        = dashBoard.creditScore
            seekBar.isEnabled       = false

        }

        if(dashBoard.loans!!.size>0)
        {

            val adapterDashBoard        = LoanListAdapter(dashBoard.loans!!,activity)
            loan_list.adapter           = adapterDashBoard
            adapterDashBoard.notifyDataSetChanged()
        }

    }
}

