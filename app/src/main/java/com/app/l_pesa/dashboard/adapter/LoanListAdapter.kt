package com.app.l_pesa.dashboard.adapter


/**
 * Created by Intellij Amiya on 20-02-2019.
 * A good programmer is someone who looks both ways before crossing a One-way street.
 * Kindly follow https://source.android.com/setup/code-style
 */
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


class LoanListAdapter(private var al_loadOBJ: ArrayList<ResDashboard.Loan>, private val contextOBJ: Context?,val rootLayout:LinearLayout,private val callBackObj: ICallBackListOnClick) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int)
    {

        val viewHolder = holder as SelectViewHolder

        if(al_loadOBJ.size!=0)
        {

            if (al_loadOBJ[position].name == "Business Loan")
            {
                viewHolder.imgLoan.setImageResource(R.drawable.ic_business_loan_icon)
                viewHolder.loanName.text = contextOBJ!!.resources.getString(R.string.business_loan)

                if (al_loadOBJ[position].status=="Apply Now")
                {

                    viewHolder.btn_apply_loan.setBackgroundResource(R.drawable.ic_approve_button)
                    viewHolder.btn_apply_loan.text                    =al_loadOBJ[position].status
                    viewHolder.txtLoanDetails.text               =contextOBJ.resources.getString(R.string.you_do_not_have_business_loan)
                    viewHolder.btn_apply_loan.visibility              =View.VISIBLE
                    viewHolder.txt_amount_first.visibility            =View.GONE
                    viewHolder.txt_amount_second.visibility           =View.GONE
                    viewHolder.ll_center.visibility                   =View.GONE

                    viewHolder.btn_apply_loan.setOnClickListener {

                        if(CommonMethod.isNetworkAvailable(contextOBJ))
                        {
                            callBackObj.onClickLoanList()
                        }
                        else
                        {
                            CommonMethod.customSnackBarError(rootLayout,contextOBJ,contextOBJ.resources.getString(R.string.no_internet))
                        }


                    }

                }
                else
                {
                    val totalRange = al_loadOBJ[position].repay!!.done + al_loadOBJ[position].repay!!.left

                    viewHolder.txtLoanDetails.text                     =al_loadOBJ[position].nextRepay!!.leftDaysText
                    viewHolder.loanBtn.text                             =al_loadOBJ[position].status
                    viewHolder.txt_amount_first.text                    =al_loadOBJ[position].repay!!.amount
                    viewHolder.txt_amount_second.text                   =al_loadOBJ[position].nextRepay!!.amount
                    viewHolder.verticalDivider.visibility         =View.INVISIBLE

                    when {
                        al_loadOBJ[position].status=="Pending" //YELLOW
                        -> viewHolder.loanBtn.setBackgroundResource(R.drawable.ic_yellow_btn)
                        al_loadOBJ[position].status=="Due" //RED
                        -> viewHolder.loanBtn.setBackgroundResource(R.drawable.ic_red_btn) //GREEN
                        else -> viewHolder.loanBtn.setBackgroundResource(R.drawable.ic_approve_button)
                    }


                }

            }

            else if (al_loadOBJ[position].name == ("Current Loan"))
            {

                viewHolder.imgLoan.setImageResource(R.drawable.ic_current_loan_icon)
                viewHolder.loanName.text = contextOBJ!!.resources.getString(R.string.current_loan)

                if (al_loadOBJ[position].status=="Apply Now")
                {

                    viewHolder.btn_apply_loan.setBackgroundResource(R.drawable.ic_approve_button)
                    viewHolder.btn_apply_loan.text                    =al_loadOBJ[position].status
                    viewHolder.txtLoanDetails.text               =contextOBJ.resources.getString(R.string.you_do_not_have_current_loan)
                    viewHolder.btn_apply_loan.visibility              =View.VISIBLE
                    viewHolder.txt_amount_first.visibility            =View.GONE
                    viewHolder.txt_amount_second.visibility           =View.GONE
                    viewHolder.ll_center.visibility                   =View.GONE

                    viewHolder.btn_apply_loan.setOnClickListener {

                        viewHolder.btn_apply_loan.setOnClickListener {

                            if(CommonMethod.isNetworkAvailable(contextOBJ))
                            {
                                callBackObj.onClickLoanList()
                            }
                            else
                            {
                                CommonMethod.customSnackBarError(rootLayout,contextOBJ,contextOBJ.resources.getString(R.string.no_internet))
                            }

                        }

                    }

                }
                else
                {

                    viewHolder.txtLoanDetails.text                  =al_loadOBJ[position].nextRepay!!.leftDaysText
                    viewHolder.loanBtn.text                             =al_loadOBJ[position].status
                    viewHolder.txt_amount_first.text =al_loadOBJ[position].repay!!.amount
                    viewHolder.txt_amount_second.text =al_loadOBJ[position].nextRepay!!.amount
                    viewHolder.verticalDivider.visibility =View.INVISIBLE

                    when {
                        al_loadOBJ[position].status=="Pending" //YELLOW
                        -> viewHolder.loanBtn.setBackgroundResource(R.drawable.ic_yellow_btn)
                        al_loadOBJ[position].status=="Due" //RED
                        -> {
                            viewHolder.loanBtn.setBackgroundResource(R.drawable.ic_red_btn)
                            viewHolder.ll_top.setBackgroundColor(Color.parseColor("#e95758"))
                        }
                        else //GREEN
                        -> viewHolder.loanBtn.setBackgroundResource(R.drawable.ic_approve_button)
                    }

                }

            }
           else if (al_loadOBJ[position].name == ("Bitcoin"))
            {

                viewHolder.imgLoan.setImageResource(R.drawable.menu_lpk_icon)
                viewHolder.loanName.text                          = contextOBJ!!.resources.getString(R.string.cash_out_lpk)
                viewHolder.txtLoanDetails.text                    ="You have 500 LPK"
                viewHolder.btn_apply_loan.visibility              =View.VISIBLE
                viewHolder.btn_apply_loan.text                    =contextOBJ.resources.getString(R.string.lets_do)
                viewHolder.btn_apply_loan.setBackgroundResource(R.drawable.ic_approve_button)
                viewHolder.txt_amount_first.visibility            =View.GONE
                viewHolder.txt_amount_second.visibility           =View.GONE
                viewHolder.ll_center.visibility                   =View.GONE


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

            val imgLoan         : ImageView                          = itemView.findViewById(R.id.loan_image)
            val loanName        : CommonTextRegular                 = itemView.findViewById(R.id.txt_loan_type)
            val loanBtn         : Button                            = itemView.findViewById(R.id.btn_pay_now)
            val txtLoanDetails  : CommonTextRegular                 = itemView.findViewById(R.id.txt_loan_details)
            val txt_amount_first : CommonTextRegular             = itemView.findViewById(R.id.txt_amount_first)
            val txt_amount_second : CommonTextRegular            = itemView.findViewById(R.id.txt_amount_second)
            val btn_apply_loan : Button                          = itemView.findViewById(R.id.btn_apply_loan)
            val ll_center : LinearLayout                        = itemView.findViewById(R.id.ll_center)
            val ll_top : LinearLayout                           = itemView.findViewById(R.id.ll_top)
            val verticalDivider : View                    = itemView.findViewById(R.id.view_vertical_divider)


        }

    }

}