package com.app.l_pesa.loanplan.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import com.app.l_pesa.R
import com.app.l_pesa.common.CustomButtonRegular
import com.app.l_pesa.loanHistory.model.GlobalLoanHistoryModel
import com.app.l_pesa.loanHistory.model.ResLoanHistoryCurrent
import com.app.l_pesa.loanplan.inter.ICallBackCurrentLoan
import com.app.l_pesa.loanplan.model.ResLoanPlans
import com.facebook.shimmer.ShimmerFrameLayout
import com.haozhang.lib.SlantedTextView
import java.text.DecimalFormat


class CurrentLoanPlanAdapter (val context: Context, private val loanPlanList: ArrayList<ResLoanPlans.Item>,private val appliedProduct: ResLoanPlans.AppliedProduct, private val callBackObj: ICallBackCurrentLoan) : androidx.recyclerview.widget.RecyclerView.Adapter<androidx.recyclerview.widget.RecyclerView.ViewHolder>() {

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: androidx.recyclerview.widget.RecyclerView.ViewHolder, position: Int) {

        val format = DecimalFormat()
        format.isDecimalSeparatorAlwaysShown = false

        val viewHolder = holder as SelectViewHolder

        viewHolder.txtRequiredScore.text    = context.resources.getString(R.string.credit_score)+" "+loanPlanList[position].details!!.requiredCreditScore.toString()
        viewHolder.txtLoanAmount.text       = loanPlanList[position].details!!.loanAmountTxt

        if(loanPlanList[position].details!!.loanPeriodType=="D")
        {
            viewHolder.txtDuration.text    = context.resources.getString(R.string.duration)+" "+loanPlanList[position].details!!.loanPeriod+" "+context.resources.getString(R.string.days)
        }
        else
        {
            viewHolder.txtDuration.text    = context.resources.getString(R.string.duration)+" "+loanPlanList[position].details!!.loanPeriod+" "+context.resources.getString(R.string.weeks)

        }

        viewHolder.txtRate.text            = context.resources.getString(R.string.rate)+" "+loanPlanList[position].details!!.loanInterestRate.toString()+"%"

            //viewHolder.buttonLoanStatus.visibility = View.VISIBLE

        try {
            if (loanPlanList[position].details?.bannerObject!=null) {
                viewHolder.smBanner.visibility = View.VISIBLE
                //viewHolder.stApproval.visibility = View.GONE
                viewHolder.bannerText.text = loanPlanList[position].details?.bannerObject?.text
                viewHolder.bannerBaseText.text = loanPlanList[position].details?.bannerObject?.text
                viewHolder.flBanner.setCardBackgroundColor(Color.parseColor(loanPlanList[position].details?.bannerObject?.backColor))
                viewHolder.bannerText.setTextColor(Color.parseColor(loanPlanList[position].details?.bannerObject?.fontColor))
                viewHolder.bannerText.textSize = loanPlanList[position].details?.bannerObject?.fontSize?.toFloat()!!

                if(loanPlanList[position].details?.bannerObject?.text.isNullOrEmpty()){
                    viewHolder.flBanner.visibility = View.GONE
                   // viewHolder.stApproval.visibility = View.GONE
                }else{
                    viewHolder.flBanner.visibility = View.VISIBLE
                   // viewHolder.stApproval.visibility = View.VISIBLE
                }
                if(loanPlanList[position].details?.bannerObject?.isShimmer!!){

                    viewHolder.smBanner.startShimmer()
                }else{
                    viewHolder.smBanner.stopShimmer()
                    viewHolder.smBanner.clearAnimation()
                }


            } else {
                viewHolder.flBanner.visibility = View.GONE
                viewHolder.stApproval.visibility = View.GONE
                viewHolder.smBanner.stopShimmer()
                viewHolder.smBanner.clearAnimation()
            }
        }catch (e:Exception){
            e.printStackTrace()
        }



        viewHolder.buttonLoanStatus.text   = loanPlanList[position].details!!.btnText
        viewHolder.buttonLoanStatus.setTextColor(Color.parseColor(loanPlanList[position].details!!.btnHexColor))

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
                   val loanData =   ResLoanHistoryCurrent.LoanHistory(appliedProduct.loanId,appliedProduct.identityNumber,appliedProduct.loanAmount,"",false,appliedProduct.interestRate,
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): androidx.recyclerview.widget.RecyclerView.ViewHolder {

        val recyclerView: androidx.recyclerview.widget.RecyclerView.ViewHolder

        val itemView: View = LayoutInflater.from(parent.context).inflate(R.layout.layout_loan_list, parent, false)
        recyclerView = SelectViewHolder(itemView)
        return recyclerView
    }

    companion object {
        private class SelectViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {

           var txtLoanAmount        : TextView              = itemView.findViewById(R.id.txtLoanAmount) as TextView
           var txtRequiredScore     : TextView              = itemView.findViewById(R.id.txtRequiredScore) as TextView
           var txtDuration          : TextView              = itemView.findViewById(R.id.txtDuration) as TextView
           var txtRate              : TextView              = itemView.findViewById(R.id.txtRate) as TextView
           var buttonLoanStatus     : CustomButtonRegular   = itemView.findViewById(R.id.buttonLoanStatus) as CustomButtonRegular
            var bannerText          : TextView              = itemView.findViewById(R.id.tvBanner) as TextView
            var bannerBaseText          : TextView              = itemView.findViewById(R.id.tvBanner) as TextView
            var smBanner            : ShimmerFrameLayout    = itemView.findViewById(R.id.smBanner) as ShimmerFrameLayout
            var stApproval          : SlantedTextView       = itemView.findViewById(R.id.stApproval) as SlantedTextView
            var flBanner : CardView = itemView.findViewById(R.id.flBanner) as CardView


        }


    }
}