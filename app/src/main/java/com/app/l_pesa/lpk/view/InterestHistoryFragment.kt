package com.app.l_pesa.lpk.view

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.os.Bundle
import android.support.design.widget.BottomSheetBehavior
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.app.l_pesa.R
import com.app.l_pesa.common.CommonMethod
import com.app.l_pesa.lpk.adapter.AdapterInterestHistory
import com.app.l_pesa.lpk.inter.ICallBackInterestHistory
import com.app.l_pesa.lpk.model.ResInterestHistory
import com.app.l_pesa.lpk.presenter.PresenterInterestHistory
import kotlinx.android.synthetic.main.fragment_interest_history.*
import kotlinx.android.synthetic.main.layout_filter_by_date.*
import java.util.*

class InterestHistoryFragment : Fragment(), ICallBackInterestHistory {


    private lateinit var listInterestHistory           : ArrayList<ResInterestHistory.UserInterestHistory>
    private lateinit var adapterInterestHistory        : AdapterInterestHistory
    private lateinit var bottomSheetBehavior           : BottomSheetBehavior<*>

    private var hasNext=false
    private var after=""



    companion object {
        fun newInstance(): Fragment {
            return InterestHistoryFragment()
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
            initData()
        }
    }

    private fun initData()
    {
        listInterestHistory= ArrayList()
        adapterInterestHistory= AdapterInterestHistory(activity!!,listInterestHistory)
        if(CommonMethod.isNetworkAvailable(activity!!))
        {
            swipeRefreshLayout.isRefreshing = true
            val presenterInterestHistory = PresenterInterestHistory()
            presenterInterestHistory.getInterestHistory(activity!!,this)
        }
        else
        {
            swipeRefreshLayout.isRefreshing = false
            CommonMethod.customSnackBarError(rootLayout,activity!!,resources.getString(R.string.no_internet))
        }
    }

    fun doFilter()
    {
        if(bottomSheetBehavior.state == BottomSheetBehavior.STATE_HIDDEN)
        {
            bottomSheetBehavior.state =(BottomSheetBehavior.STATE_HALF_EXPANDED)

        }
        else if(bottomSheetBehavior.state == BottomSheetBehavior.STATE_HALF_EXPANDED)
        {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        }

        etFromDate.setOnClickListener {

            showDatePickerFrom()

        }

        etToDate.setOnClickListener {

            showDatePickerTo()

        }

        imgCancel.setOnClickListener {

            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN)

        }
    }

    @SuppressLint("SetTextI18n")
    private fun showDatePickerFrom()
    {
        val c       = Calendar.getInstance()
        val year    = c.get(Calendar.YEAR)
        val month   = c.get(Calendar.MONTH)+1
        val day     = c.get(Calendar.DAY_OF_MONTH)

        val dpd = DatePickerDialog(activity!!, DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->

            if(dayOfMonth.toString().length==1)
            {
                if(monthOfYear.toString().length==1)
                {
                    etFromDate.setText("0$dayOfMonth-0$month-$year")
                }
                else
                {
                    etFromDate.setText("0$dayOfMonth-$month-$year")
                }

            }
            else
            {
                if(monthOfYear.toString().length==1)
                {
                    etFromDate.setText("$dayOfMonth-0$month-$year")
                }
                else
                {
                    etFromDate.setText("$dayOfMonth-$monthOfYear-$year")
                }

            }

        }, year, month, day)

        dpd.show()
        dpd.datePicker.maxDate = System.currentTimeMillis()
    }

    @SuppressLint("SetTextI18n")
    private fun showDatePickerTo()
    {
        val c       = Calendar.getInstance()
        val year    = c.get(Calendar.YEAR)
        val month   = c.get(Calendar.MONTH)+1
        val day     = c.get(Calendar.DAY_OF_MONTH)

        val dpd = DatePickerDialog(activity!!, DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->

            if(dayOfMonth.toString().length==1)
            {
                if(monthOfYear.toString().length==1)
                {
                    etToDate.setText("0$dayOfMonth-0$month-$year")
                }
                else
                {
                    etToDate.setText("0$dayOfMonth-$month-$year")
                }

            }
            else
            {
                if(monthOfYear.toString().length==1)
                {
                    etToDate.setText("$dayOfMonth-0$month-$year")
                }
                else
                {
                    etToDate.setText("$dayOfMonth-$monthOfYear-$year")
                }

            }

        }, year, month, day)

        dpd.show()
        dpd.datePicker.maxDate = System.currentTimeMillis()
    }

    override fun onSuccessInterestHistory(userInterestHistory: ArrayList<ResInterestHistory.UserInterestHistory>?, cursors: ResInterestHistory.Cursors?) {

        cardView.visibility=View.INVISIBLE
        rlList.visibility=View.VISIBLE

        activity!!.runOnUiThread {

            swipeRefreshLayout.isRefreshing = false

            listInterestHistory.clear()
            listInterestHistory.addAll(userInterestHistory!!)
            adapterInterestHistory      = AdapterInterestHistory(activity!!, listInterestHistory)
            val llmOBJ                  = LinearLayoutManager(activity)
            llmOBJ.orientation          = LinearLayoutManager.VERTICAL
            rlList.layoutManager        = llmOBJ
            rlList.adapter              = adapterInterestHistory

            hasNext =cursors!!.hasNext
            after   =cursors.after

            adapterInterestHistory.setLoadMoreListener(object : AdapterInterestHistory.OnLoadMoreListener {
                override fun onLoadMore() {

                    rlList.post {

                        if(hasNext)
                        {
                            loadMore()
                        }

                    }

                }
            })


        }
    }

    override fun onSuccessInterestHistoryPaginate(userInterestHistory: ArrayList<ResInterestHistory.UserInterestHistory>?, cursors: ResInterestHistory.Cursors?) {

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

    private fun loadMore()
    {
        if(CommonMethod.isNetworkAvailable(activity!!))
        {
            val loadModel  = ResInterestHistory.UserInterestHistory(0,0,0,"",
                            "","","",
                            "","","","","","")

            listInterestHistory.add(loadModel)
            adapterInterestHistory.notifyItemInserted(listInterestHistory.size-1)


            val presenterInterestHistory = PresenterInterestHistory()
            presenterInterestHistory.getInterestHistoryPaginate(activity!!,after,this)

        }
        else{
            swipeRefreshLayout.isRefreshing = false
            CommonMethod.customSnackBarError(rootLayout,activity!!,resources.getString(R.string.no_internet))
        }
    }

    override fun onEmptyInterestHistory() {

        swipeRefreshLayout.isRefreshing = false
        rlList.visibility=View.INVISIBLE
        cardView.visibility=View.VISIBLE
    }

    override fun onErrorInterestHistory(message: String) {

        rlList.visibility=View.INVISIBLE
        cardView.visibility=View.INVISIBLE
        swipeRefreshLayout.isRefreshing = false
        CommonMethod.customSnackBarError(rootLayout,activity!!,message)
    }
}