package com.app.l_pesa.lpk.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.l_pesa.R
import com.app.l_pesa.common.CommonClass
import com.app.l_pesa.common.CommonMethod
import com.app.l_pesa.lpk.adapter.AdapterInterestHistory
import com.app.l_pesa.lpk.inter.ICallBackInterestHistory
import com.app.l_pesa.lpk.model.ResInterestHistory
import com.app.l_pesa.lpk.presenter.PresenterInterestHistory
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.fragment_interest_history.*
import kotlinx.android.synthetic.main.layout_filter_by_date.*
import java.util.*

class EarnedInterestFragment : Fragment(), ICallBackInterestHistory {

    private lateinit var listInterestHistory           : ArrayList<ResInterestHistory.UserInterestHistory>
    private lateinit var adapterInterestHistory        : AdapterInterestHistory
    private lateinit var bottomSheetBehavior           : BottomSheetBehavior<*>

    private var hasNext=false
    private var after=""

    companion object {
        fun newInstance(): Fragment {
            return EarnedInterestFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_interest_history, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initData()
        swipeRefresh()

        bottomSheetBehavior = BottomSheetBehavior.from<View>(bottom_sheet)
        bottomSheetBehavior.isHideable=true
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

    }

    private fun swipeRefresh()
    {
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent)
        swipeRefreshLayout.setOnRefreshListener {
            etFromDate.text!!.clear()
            etToDate.text!!.clear()
            initData()
        }
    }

    private fun initData()
    {
        listInterestHistory= ArrayList()
        adapterInterestHistory= AdapterInterestHistory(activity!!,listInterestHistory)
        if(CommonMethod.isNetworkAvailable(activity!!))
        {
            loadInterestHistory("","","DEFAULT")
        }
        else
        {
            swipeRefreshLayout.isRefreshing = false
            CommonMethod.customSnackBarError(rootLayout,activity!!,resources.getString(R.string.no_internet))
        }
    }

    private fun loadInterestHistory(from_date:String,to_date:String,type:String)
    {
        swipeRefreshLayout.isRefreshing = true
        val presenterInterestHistory = PresenterInterestHistory()
        presenterInterestHistory.getInterestHistory(activity!!,from_date,to_date,type,this)
    }

    fun doFilter()
    {
        when {
            bottomSheetBehavior.state == BottomSheetBehavior.STATE_HIDDEN -> {
                bottomSheetBehavior.state =(BottomSheetBehavior.STATE_EXPANDED)

            }
            bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED -> bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        }

        etFromDate.setOnClickListener {

            showDatePickerFrom()

        }

        etToDate.setOnClickListener {

            showDatePickerTo()

        }

        buttonFilterSubmit.setOnClickListener {

            if(TextUtils.isEmpty(etFromDate.text.toString()) && TextUtils.isEmpty(etToDate.text.toString()))
            {
                CommonMethod.customSnackBarError(rootLayout,activity!!,resources.getString(R.string.you_have_select_from_date_to_date))
            }
            else
            {
                if(CommonMethod.isNetworkAvailable(activity!!))
                {
                    val fromDate=CommonMethod.dateConvertYMD(etFromDate.text.toString())
                    val toDate  =CommonMethod.dateConvertYMD(etToDate.text.toString())
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                    loadInterestHistory(fromDate!!,toDate!!,"FILTER")
                }
                else
                {
                    swipeRefreshLayout.isRefreshing = false
                    CommonMethod.customSnackBarError(rootLayout,activity!!,resources.getString(R.string.no_internet))
                }

            }

        }

        imgCancel.setOnClickListener {

            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN)

        }

        buttonReset.setOnClickListener {

            etFromDate.text!!.clear()
            etToDate.text!!.clear()

        }

        imgCleanFrom_date.setOnClickListener {

            etFromDate.text!!.clear()

        }

        imgCleanTo_date.setOnClickListener {

            etToDate.text!!.clear()

        }
    }

    @SuppressLint("SetTextI18n")
    private fun showDatePickerFrom()
    {
        val commonClass= CommonClass()
        commonClass.datePicker(activity,etFromDate)
    }

    @SuppressLint("SetTextI18n")
    private fun showDatePickerTo()
    {
        val commonClass= CommonClass()
        commonClass.datePicker(activity,etToDate)
    }

    override fun onSuccessInterestHistory(userInterestHistory: ArrayList<ResInterestHistory.UserInterestHistory>?, cursors: ResInterestHistory.Cursors?, from_date: String, to_date: String) {

        cardView.visibility=View.INVISIBLE
        rlList.visibility=View.VISIBLE

        activity!!.runOnUiThread {

            swipeRefreshLayout.isRefreshing = false

            listInterestHistory.clear()
            listInterestHistory.addAll(userInterestHistory!!)
            adapterInterestHistory      = AdapterInterestHistory(activity!!, listInterestHistory)
            val llmOBJ                  = LinearLayoutManager(activity)
            rlList.layoutManager        = llmOBJ
            rlList.adapter              = adapterInterestHistory

            hasNext =cursors!!.hasNext
            after   =cursors.after

            adapterInterestHistory.setLoadMoreListener(object : AdapterInterestHistory.OnLoadMoreListener {
                override fun onLoadMore() {

                    rlList.post {

                        if(hasNext)
                        {
                            loadMore(from_date,to_date)
                        }

                    }

                }
            })


        }
    }

    override fun onSuccessInterestHistoryPaginate(userInterestHistory: ArrayList<ResInterestHistory.UserInterestHistory>?, cursors: ResInterestHistory.Cursors?, from_date: String, to_date: String) {

        hasNext =cursors!!.hasNext
        after   =cursors.after
        if(listInterestHistory.size!=0)
        {
            try {

                listInterestHistory.removeAt(listInterestHistory.size - 1)
                adapterInterestHistory.notifyDataChanged()
                listInterestHistory.addAll(userInterestHistory!!)
                adapterInterestHistory.notifyItemRangeInserted(0, listInterestHistory.size)

            }
            catch (e:Exception)
            {}
        }
    }

    private fun loadMore(from_date: String, to_date: String)
    {
        if(CommonMethod.isNetworkAvailable(activity!!))
        {
            val loadModel  = ResInterestHistory.UserInterestHistory(0,0,0,"",
                            "","","",
                            "","","","","","")

            listInterestHistory.add(loadModel)
            adapterInterestHistory.notifyItemInserted(listInterestHistory.size-1)


            val presenterInterestHistory = PresenterInterestHistory()
            presenterInterestHistory.getInterestHistoryPaginate(activity!!,after,from_date,to_date,this)

        }
        else{
            swipeRefreshLayout.isRefreshing = false
            CommonMethod.customSnackBarError(rootLayout,activity!!,resources.getString(R.string.no_internet))
        }
    }

    override fun onEmptyInterestHistory(type: String) {

        swipeRefreshLayout.isRefreshing = false
        rlList.visibility=View.INVISIBLE
        if(type=="FILTER")
        {
            txt_message.text = resources.getString(R.string.no_result_found)
        }
        else
        {
            txt_message.text = resources.getString(R.string.empty_loan_history_message)
        }
        cardView.visibility=View.VISIBLE
    }

    override fun onErrorInterestHistory(message: String) {

        rlList.visibility=View.INVISIBLE
        cardView.visibility=View.INVISIBLE
        swipeRefreshLayout.isRefreshing = false
        CommonMethod.customSnackBarError(rootLayout,activity!!,message)
    }
}