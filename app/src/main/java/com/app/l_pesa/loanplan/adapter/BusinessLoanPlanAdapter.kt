package com.app.l_pesa.loanplan.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.app.l_pesa.R
import com.app.l_pesa.common.CustomButtonRegular
import com.app.l_pesa.loanHistory.model.GlobalLoanHistoryModel
import com.app.l_pesa.loanHistory.model.ResLoanHistoryCurrent
import com.app.l_pesa.loanplan.inter.ICallBackBusinessLoan
import com.app.l_pesa.loanplan.model.ResLoanPlans
import com.facebook.shimmer.ShimmerFrameLayout
import com.haozhang.lib.SlantedTextView
import java.text.DecimalFormat


class BusinessLoanPlanAdapter (val context: Context, private val loanPlanList: ArrayList<ResLoanPlans.Item>,private val appliedProduct: ResLoanPlans.AppliedProduct, private val callBackObj: ICallBackBusinessLoan) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val format = DecimalFormat()
        format.isDecimalSeparatorAlwaysShown = false

        val viewHolder = holder as SelectViewHolder

        viewHolder.txtRequiredScore.text    = context.resources.getString(R.string.credit_score)+" "+loanPlanList[position].details!!.requiredCreditScore.toString()
        viewHolder.txtLoanAmount.text       = "$"+format.format(loanPlanList[position].details!!.loanAmount).toString()

        if(loanPlanList[position].details!!.loanPeriodType=="D")
        {
            viewHolder.txtDuration.text    = context.resources.getString(R.string.duration)+" "+loanPlanList[position].details!!.loanPeriod+" "+context.resources.getString(R.string.days)
        }
        else
        {
            viewHolder.txtDuration.text    = context.resources.getString(R.string.duration)+" "+loanPlanList[position].details!!.loanPeriod+" "+context.resources.getString(R.string.weeks)

        }

        viewHolder.txtRate.text            = context.resources.getString(R.string.rate)+" "+loanPlanList[position].details!!.loanInterestRate.toString()+"%"

        viewHolder.buttonLoanStatus.text   = loanPlanList[position].details!!.btnText
        viewHolder.buttonLoanStatus.setTextColor(Color.parseColor(loanPlanList[position].details!!.btnHexColor))

        if(!loanPlanList[position].details?.bannerText.isNullOrBlank()) {
            viewHolder.smBanner.visibility = View.GONE
            viewHolder.stApproval.visibility = View.VISIBLE
            viewHolder.stApproval.text = loanPlanList[position].details?.bannerText
            //viewHolder.smBanner.startShimmerAnimation()

        }else{
            viewHolder.stApproval.visibility = View.GONE
            viewHolder.smBanner.visibility = View.GONE
            //viewHolder.smBanner.stopShimmerAnimation()
        }

        when {
            loanPlanList[position].details!!.btnColor.contentEquals("green")-> viewHolder.buttonLoanStatus.setBackgroundResource(R.drawable.bg_transparent_border_green_view)
            loanPlanList[position].details!!.btnColor.contentEquals("lightgreen")-> viewHolder.buttonLoanStatus.setBackgroundResource(R.drawable.bg_transparent_border_light_green_view)
            loanPlanList[position].details!!.btnColor.contentEquals("blue") -> viewHolder.buttonLoanStatus.setBackgroundResource(R.drawable.bg_transparent_border_blue_view)
            loanPlanList[position].details!!.btnColor.contentEquals("darkgrey") -> viewHolder.buttonLoanStatus.setBackgroundResource(R.drawable.bg_transparent_border_darkgrey_view)
            loanPlanList[position].details!!.btnColor.contentEquals("red") -> viewHolder.buttonLoanStatus.setBackgroundResource(R.drawable.bg_transparent_border_red_view)
            loanPlanList[position].details!!.btnColor.contentEquals("aqua") -> viewHolder.buttonLoanStatus.setBackgroundResource(R.drawable.bg_transparent_border_aqua_view)
            else -> viewHolder.buttonLoanStatus.setBackgroundResource(R.drawable.bg_transparent_border_orange_view)
        }


        viewHolder.buttonLoanStatus.setOnClickListener {

            if(loanPlanList[position].details!!.btnStatus=="enable")
            {
                callBackObj.onSuccessLoanPlansDetails(loanPlanList[position].details)

            }
            else
            {
                if(loanPlanList[position].details!!.btnStatus=="disable" && loanPlanList[position].details!!.productId==appliedProduct.productId)
                {
                    val modelData=   GlobalLoanHistoryModel.getInstance()
                    val loanData =   ResLoanHistoryCurrent.LoanHistory(appliedProduct.loanId,appliedProduct.identityNumber,appliedProduct.loanAmount,appliedProduct.interestRate,
                            appliedProduct.convertionDollarValue.toString(),appliedProduct.convertionLoanAmount.toString(),appliedProduct.actualLoanAmount.toString(),appliedProduct.appliedDate,
                            appliedProduct.sanctionedDate,appliedProduct.finishedDate,appliedProduct.disapproveDate,appliedProduct.loanStatus,appliedProduct.currencyCode,appliedProduct.dueDate,
                            appliedProduct.duration,appliedProduct.conversionCharge,appliedProduct.conversionChargeAmount,appliedProduct.loanPurposeMessage,appliedProduct.crScWhenRequestingLoan,
                            appliedProduct.processingFees,appliedProduct.processingFeesAmount,appliedProduct.disapproveReason)

                    modelData.modelData=loanData
                    callBackObj.onSuccessLoanHistory()

                }
                else
                {
                    Toast.makeText(context,loanPlanList[position].details!!.alertMgs,Toast.LENGTH_SHORT).show()
                }
            }

        }


    }

    override fun getItemCount(): Int = loanPlanList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val recyclerView: RecyclerView.ViewHolder

        val itemView: View = LayoutInflater.from(parent.context).inflate(R.layout.layout_loan_list, parent, false)
        recyclerView = SelectViewHolder(itemView)
        return recyclerView
    }

    companion object {
        private class SelectViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

            var txtLoanAmount        : TextView              = itemView.findViewById(R.id.txtLoanAmount) as TextView
            var txtRequiredScore     : TextView              = itemView.findViewById(R.id.txtRequiredScore) as TextView
            var txtDuration          : TextView              = itemView.findViewById(R.id.txtDuration) as TextView
            var txtRate              : TextView              = itemView.findViewById(R.id.txtRate) as TextView
            var buttonLoanStatus     : CustomButtonRegular   = itemView.findViewById(R.id.buttonLoanStatus) as CustomButtonRegular
            var bannerText          : TextView              = itemView.findViewById(R.id.tvBanner) as TextView
            var smBanner            : ShimmerFrameLayout = itemView.findViewById(R.id.smBanner) as ShimmerFrameLayout
            var stApproval          : SlantedTextView = itemView.findViewById(R.id.stApproval) as SlantedTextView

        }


    }
}