package com.app.l_pesa.lpk.view

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.os.Bundle
import android.support.design.widget.BottomSheetBehavior
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
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
import java.text.SimpleDateFormat
import java.util.*

class InterestHistoryFragment : Fragment(), ICallBackInterestHistory {


    private lateinit var listInterestHistory           : ArrayList<ResInterestHistory.UserInterestHistory>
    private lateinit var adapterInterestHistory        : AdapterInterestHistory
    private lateinit var bottomSheetBehavior           : BottomSheetBehavior<*>

    private var hasNext=false
    private var after=""

    private var calFrom = Calendar.getInstance()
    private var calTo   = Calendar.getInstance()

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
            loadInterestHistory("","")
        }
        else
        {
            swipeRefreshLayout.isRefreshing = false
            CommonMethod.customSnackBarError(rootLayout,activity!!,resources.getString(R.string.no_internet))
        }
    }

    private fun loadInterestHistory(from_date:String,to_date:String)
    {
        swipeRefreshLayout.isRefreshing = true
        val presenterInterestHistory = PresenterInterestHistory()
        presenterInterestHistory.getInterestHistory(activity!!,from_date,to_date,this)
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
                    loadInterestHistory(fromDate!!,toDate!!)
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
    }

    @SuppressLint("SetTextI18n")
    private fun showDatePickerFrom()
    {
        val dateSetListener = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            calFrom.set(Calendar.YEAR, year)
            calFrom.set(Calendar.MONTH, monthOfYear)
            calFrom.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            val myFormat = "dd-MM-yyyy" // mention the format you need
            val sdf = SimpleDateFormat(myFormat, Locale.US)
            etFromDate!!.setText(sdf.format(calFrom.time))
        }

        val dpd =DatePickerDialog(activity!!,
                dateSetListener,
                // set DatePickerDialog to point to today's date when it loads up
                calFrom.get(Calendar.YEAR),
                calFrom.get(Calendar.MONTH),
                calFrom.get(Calendar.DAY_OF_MONTH))

        dpd.show()
        dpd.datePicker.maxDate = System.currentTimeMillis()
    }

    @SuppressLint("SetTextI18n")
    private fun showDatePickerTo()
    {
        val dateSetListener = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            calTo.set(Calendar.YEAR, year)
            calTo.set(Calendar.MONTH, monthOfYear)
            calTo.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            val myFormat = "dd-MM-yyyy" // mention the format you need
            val sdf = SimpleDateFormat(myFormat, Locale.US)
            etToDate!!.setText(sdf.format(calTo.time))
        }

        val dpd =DatePickerDialog(activity!!,
                dateSetListener,
                // set DatePickerDialog to point to today's date when it loads up
                calTo.get(Calendar.YEAR),
                calTo.get(Calendar.MONTH),
                calTo.get(Calendar.DAY_OF_MONTH))

        dpd.show()
        dpd.datePicker.maxDate = System.currentTimeMillis()
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