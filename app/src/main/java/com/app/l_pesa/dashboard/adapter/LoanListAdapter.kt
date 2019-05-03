package com.app.l_pesa.dashboard.adapter


/**
 * Created by Intellij Amiya on 20-02-2019.
 * A good programmer is someone who looks both ways before crossing a One-way street.
 * Kindly follow https://source.android.com/setup/code-style
 */
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.app.l_pesa.R
import com.app.l_pesa.common.CommonMethod
import com.app.l_pesa.common.CommonTextRegular
import com.app.l_pesa.dashboard.inter.ICallBackListOnClick
import com.app.l_pesa.dashboard.model.ResDashboard
import com.warkiz.widget.IndicatorSeekBar
import com.warkiz.widget.IndicatorStayLayout
import android.view.MotionEvent
import java.text.DecimalFormat


class LoanListAdapter(private var al_loadOBJ: ArrayList<ResDashboard.Loan>,private var dashBoard: ResDashboard.Data,private val contextOBJ: Context?,val rootLayout:LinearLayout,private val callBackObj: ICallBackListOnClick) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    @SuppressLint("ClickableViewAccessibility", "SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int)
    {

        val viewHolder = holder as SelectViewHolder

        if(al_loadOBJ.size!=0)
        {
            viewHolder.seekBar.setOnTouchListener { _, _ -> true }

            if (al_loadOBJ[position].type =="business_loan")
            {
                viewHolder.imgLoan.setImageResource(R.drawable.ic_business_loan_icon)
                viewHolder.txtLoanType.text = contextOBJ!!.resources.getString(R.string.business_loan)

                if (al_loadOBJ[position].status=="Apply Now")
                {

                    viewHolder.buttonApplyLoan.setBackgroundResource(R.drawable.ic_approve_button)
                    viewHolder.buttonApplyLoan.text                 =al_loadOBJ[position].status
                    viewHolder.txtLoanDetails.text                  =contextOBJ.resources.getString(R.string.you_do_not_have_business_loan)
                    viewHolder.buttonApplyLoan.visibility           =View.VISIBLE

                    viewHolder.txtAmountFirst.visibility            =View.GONE
                    viewHolder.txtAmountSecond.visibility           =View.GONE


                }
                else
                {
                    val format = DecimalFormat()
                    format.isDecimalSeparatorAlwaysShown = false

                    viewHolder.txtLoanDetails.text                  =al_loadOBJ[position].nextRepay!!.leftDaysText
                    viewHolder.txtAmountFirst.text                  =dashBoard.currencyCode+" "+format.format(al_loadOBJ[position].repay!!.amount)

                    if(al_loadOBJ[position].nextRepay!!.leftDays<0)
                    {
                       viewHolder.txtAmountSecond.setTextColor(Color.RED)
                    }
                    if(al_loadOBJ[position].status=="Pending")
                    {
                        viewHolder.buttonApplyLoan.visibility       =View.INVISIBLE
                        viewHolder.txtAmountSecond.visibility       =View.GONE
                    }
                    else
                    {
                        viewHolder.buttonApplyLoan.visibility       =View.VISIBLE
                        viewHolder.txtAmountSecond.visibility       =View.VISIBLE
                        viewHolder.txtAmountSecond.text             =dashBoard.currencyCode+" "+format.format(al_loadOBJ[position].nextRepay!!.amount)

                        viewHolder.buttonApplyLoan.text             = contextOBJ.resources.getString(R.string.pay_now)
                        if(al_loadOBJ[position].status=="Due" )
                        {
                            viewHolder.buttonApplyLoan.setBackgroundResource(R.drawable.ic_red_btn)
                        }
                        else
                        {
                            viewHolder.buttonApplyLoan.setBackgroundResource(R.drawable.ic_approve_button)
                        }
                    }

                    if(al_loadOBJ[position].status=="Active" || al_loadOBJ[position].status=="Due")
                    {
                        viewHolder.txtStart.visibility                    =View.VISIBLE
                        viewHolder.txtEnd.visibility                      =View.VISIBLE
                        viewHolder.txtStart.text = "0"
                        viewHolder.txtEnd.text   = al_loadOBJ[position].repay!!.total.toString()

                        viewHolder.seekBarLayout.visibility=View.VISIBLE
                        viewHolder.seekBar.post{

                            viewHolder.seekBar.max = al_loadOBJ[position].repay!!.total.toFloat()
                            viewHolder.seekBar.setProgress(al_loadOBJ[position].repay!!.total.toFloat()-al_loadOBJ[position].repay!!.done.toFloat())

                        }

                    }
                    else
                    {
                       viewHolder.seekBarLayout.visibility=View.GONE
                    }

                }

                viewHolder.buttonApplyLoan.setOnClickListener {

                    if (al_loadOBJ[position].status=="Apply Now")
                    {
                        if(CommonMethod.isNetworkAvailable(contextOBJ)) {
                            callBackObj.onClickLoanList("BUSINESS")
                        } else {
                            CommonMethod.customSnackBarError(rootLayout,contextOBJ,contextOBJ.resources.getString(R.string.no_internet))
                        }
                    }
                   else if(al_loadOBJ[position].status=="Active" || al_loadOBJ[position].status=="Due")
                    {
                        if(CommonMethod.isNetworkAvailable(contextOBJ)) {
                            callBackObj.onClickPay("business_loan",""+al_loadOBJ[position].repay!!.loan_id.toString())
                        } else {
                            CommonMethod.customSnackBarError(rootLayout,contextOBJ,contextOBJ.resources.getString(R.string.no_internet))
                        }
                    }
                }

            }

            else if (al_loadOBJ[position].type =="current_loan")
            {

                viewHolder.imgLoan.setImageResource(R.drawable.ic_current_loan_icon)
                viewHolder.txtLoanType.text = contextOBJ!!.resources.getString(R.string.personal_loan)

                if (al_loadOBJ[position].status=="Apply Now")
                {

                    viewHolder.buttonApplyLoan.setBackgroundResource(R.drawable.ic_approve_button)
                    viewHolder.buttonApplyLoan.text                    =al_loadOBJ[position].status
                    viewHolder.txtLoanDetails.text                     =contextOBJ.resources.getString(R.string.you_do_not_have_current_loan)
                    viewHolder.buttonApplyLoan.visibility              =View.VISIBLE

                    viewHolder.txtAmountFirst.visibility               =View.GONE
                    viewHolder.txtAmountSecond.visibility              =View.GONE

                }
                else
                {
                    val format = DecimalFormat()
                    format.isDecimalSeparatorAlwaysShown = false

                    viewHolder.txtLoanDetails.text              =al_loadOBJ[position].nextRepay!!.leftDaysText
                    viewHolder.txtAmountFirst.text              =dashBoard.currencyCode+" "+format.format(al_loadOBJ[position].repay!!.amount)

                    if(al_loadOBJ[position].nextRepay!!.leftDays<0)
                    {
                       viewHolder.txtAmountSecond.setTextColor(Color.RED)
                    }

                    if(al_loadOBJ[position].status=="Pending")
                    {
                        viewHolder.buttonApplyLoan.visibility       =View.INVISIBLE
                        viewHolder.txtAmountSecond.visibility       =View.GONE
                    }
                    else
                    {
                        viewHolder.buttonApplyLoan.visibility       =View.VISIBLE
                        viewHolder.txtAmountSecond.visibility       =View.VISIBLE
                        viewHolder.txtAmountSecond.text             =dashBoard.currencyCode+" "+format.format(al_loadOBJ[position].nextRepay!!.amount)

                        viewHolder.buttonApplyLoan.text             = contextOBJ.resources.getString(R.string.pay_now)
                        if(al_loadOBJ[position].status=="Due" )
                        {
                            viewHolder.buttonApplyLoan.setBackgroundResource(R.drawable.ic_red_btn)
                        }
                        else
                        {
                            viewHolder.buttonApplyLoan.setBackgroundResource(R.drawable.ic_approve_button)
                        }
                    }


                    if(al_loadOBJ[position].status=="Active" || al_loadOBJ[position].status=="Due")
                    {
                        viewHolder.txtStart.visibility       =View.VISIBLE
                        viewHolder.txtEnd.visibility         =View.VISIBLE
                        viewHolder.seekBarLayout.visibility  =View.VISIBLE
                        viewHolder.txtStart.text = "0"
                        viewHolder.txtEnd.text = al_loadOBJ[position].repay!!.total.toString()

                        viewHolder.seekBar.post{

                            viewHolder.seekBar.max = al_loadOBJ[position].repay!!.total.toFloat()
                            viewHolder.seekBar.setProgress(al_loadOBJ[position].repay!!.total.toFloat()-al_loadOBJ[position].repay!!.done.toFloat())

                        }

                    }
                    else
                    {
                       viewHolder.seekBarLayout.visibility=View.GONE
                    }

                }

                viewHolder.buttonApplyLoan.setOnClickListener {

                    if (al_loadOBJ[position].status == "Apply Now") {
                        if (CommonMethod.isNetworkAvailable(contextOBJ)) {
                            callBackObj.onClickLoanList("CURRENT")
                        } else {
                            CommonMethod.customSnackBarError(rootLayout, contextOBJ, contextOBJ.resources.getString(R.string.no_internet))
                        }
                    }
                    else if(al_loadOBJ[position].status=="Active" || al_loadOBJ[position].status=="Due")
                    {
                        if(CommonMethod.isNetworkAvailable(contextOBJ)) {
                            callBackObj.onClickPay("current_loan",""+al_loadOBJ[position].repay!!.loan_id.toString())
                        } else {
                            CommonMethod.customSnackBarError(rootLayout,contextOBJ,contextOBJ.resources.getString(R.string.no_internet))
                        }
                    }

                }

            }
           else if (al_loadOBJ[position].type=="lpesa_coin")
            {

                viewHolder.imgLoan.setImageResource(R.drawable.menu_lpk_icon)
                viewHolder.txtLoanType.text                       = contextOBJ!!.resources.getString(R.string.cash_out_lpk)
                viewHolder.txtLoanDetails.text                    ="You have "+al_loadOBJ[position].coins
                viewHolder.buttonApplyLoan.visibility             =View.VISIBLE
                viewHolder.buttonApplyLoan.text                    =contextOBJ.resources.getString(R.string.lets_do)
                viewHolder.buttonApplyLoan.setBackgroundResource(R.drawable.ic_approve_button)
                viewHolder.viewDividerTwo.visibility               =View.GONE

                viewHolder.txtAmountFirst.visibility               =View.GONE
                viewHolder.txtAmountSecond.visibility              =View.GONE

                viewHolder.buttonApplyLoan.setOnClickListener {

                    if(CommonMethod.isNetworkAvailable(contextOBJ)) {
                        callBackObj.onClickLoanList("LPK")
                    } else {
                        CommonMethod.customSnackBarError(rootLayout,contextOBJ,contextOBJ.resources.getString(R.string.no_internet))
                    }
                }

            }
        }

    }

    override fun getItemCount(): Int
            = al_loadOBJ.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder
    {

        val recyclerView: RecyclerView.ViewHolder
        val itemView: View = LayoutInflater.from(parent.context).inflate(R.layout.layout_list_dashboard_loan, parent, false)
        recyclerView = SelectViewHolder(itemView)

        return recyclerView
    }

    companion object
    {
        private class SelectViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
        {

            val imgLoan           : ImageView                       = itemView.findViewById(R.id.imgLoan)
            val txtStart          : CommonTextRegular               = itemView.findViewById(R.id.txtStart)
            val txtEnd            : CommonTextRegular               = itemView.findViewById(R.id.txtEnd)
            val txtLoanType       : CommonTextRegular               = itemView.findViewById(R.id.txt_loan_type)
            val txtLoanDetails    : CommonTextRegular               = itemView.findViewById(R.id.txt_loan_details)
            val txtAmountFirst    : CommonTextRegular               = itemView.findViewById(R.id.txtAmountFirst)
            val txtAmountSecond   : CommonTextRegular               = itemView.findViewById(R.id.txtAmountSecond)
            val buttonApplyLoan   : Button                          = itemView.findViewById(R.id.btn_apply_loan)
            val seekBarLayout     : IndicatorStayLayout             = itemView.findViewById(R.id.seekBarLayout)
            val seekBar           : IndicatorSeekBar                = itemView.findViewById(R.id.seekBar)
            val viewDividerTwo    : View                            = itemView.findViewById(R.id.viewDividerTwo)

        }

    }

}