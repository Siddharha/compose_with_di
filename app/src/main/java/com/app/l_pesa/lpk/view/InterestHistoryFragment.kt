package com.app.l_pesa.lpk.view

import android.os.Bundle
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
import kotlinx.android.synthetic.main.layout_recycler.*
import java.util.ArrayList

class InterestHistoryFragment : Fragment(), ICallBackInterestHistory {


    private var listInterestHistory           : ArrayList<ResInterestHistory.UserInterestHistory>? = null
    private var adapterInterestHistory        : AdapterInterestHistory?                   = null

    private var hasNext=false
    private var after=""

    companion object {
        fun newInstance(): Fragment {
            return InterestHistoryFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.layout_recycler, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initData()
        swipeRefresh()

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

    }

    override fun onSuccessInterestHistory(userInterestHistory: ArrayList<ResInterestHistory.UserInterestHistory>?, cursors: ResInterestHistory.Cursors?) {


        activity!!.runOnUiThread {

            swipeRefreshLayout.isRefreshing = false

            listInterestHistory!!.clear()
            listInterestHistory!!.addAll(userInterestHistory!!)
            adapterInterestHistory      = AdapterInterestHistory(activity!!, listInterestHistory)
            val llmOBJ                  = LinearLayoutManager(activity)
            llmOBJ.orientation          = LinearLayoutManager.VERTICAL
            rlList.layoutManager        = llmOBJ
            rlList.adapter              = adapterInterestHistory

            hasNext =cursors!!.hasNext
            after   =cursors.after

            adapterInterestHistory!!.setLoadMoreListener(object : AdapterInterestHistory.OnLoadMoreListener {
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
        if(listInterestHistory!!.size!=0)
        {
            try {

                listInterestHistory!!.removeAt(listInterestHistory!!.size - 1)
                adapterInterestHistory!!.notifyDataChanged()
                listInterestHistory!!.addAll(userInterestHistory!!)
                adapterInterestHistory!!.notifyItemRangeInserted(0, listInterestHistory!!.size)

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
                            "","","","","")

            listInterestHistory!!.add(loadModel)
            adapterInterestHistory!!.notifyItemInserted(listInterestHistory!!.size-1)


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
    }

    override fun onErrorInterestHistory(message: String) {

        swipeRefreshLayout.isRefreshing = false
    }
}