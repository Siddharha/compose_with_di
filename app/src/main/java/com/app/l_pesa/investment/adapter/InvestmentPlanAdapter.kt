package com.app.l_pesa.investment.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import android.text.Html
import android.text.Spanned
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.app.l_pesa.R
import com.app.l_pesa.investment.inter.ICallBackInvestmentPlan
import com.app.l_pesa.investment.model.ResInvestmentPlan
import java.text.DecimalFormat

class InvestmentPlanAdapter (val context: Context, private val investmentList: ArrayList<ResInvestmentPlan.InvestmentPlan>, private val callBack: ICallBackInvestmentPlan) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {


        val viewHolder = holder as SelectViewHolder

        val format = DecimalFormat()
        format.isDecimalSeparatorAlwaysShown = false

        viewHolder.txtInterest.text = format.format(investmentList[position].depositInterestRate)+"%"

        viewHolder.txtTitle.text = investmentList[position].planName
        viewHolder.txtDuration.text = fromHtml(context.resources.getString(R.string.months)+"<font color='#333333'>"+" "+investmentList[position].depositMonth+"</font>")
        viewHolder.txtRate.text = fromHtml(context.resources.getString(R.string.interest_rate)+"<font color='#333333'>"+" "+format.format(investmentList[position].depositInterestRate)+"%"+"</font>")

        viewHolder.rootLayout.setOnClickListener {

            callBack.onClickInvestmentPlan(investmentList[position])
        }
    }

    override fun getItemCount(): Int = investmentList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val recyclerView:RecyclerView.ViewHolder

        val itemView: View = LayoutInflater.from(parent.context).inflate(R.layout.layout_investment_plan_list, parent, false)
        recyclerView = SelectViewHolder(itemView)
        return recyclerView
    }

    private fun fromHtml(source: String): Spanned {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml(source, Html.FROM_HTML_MODE_LEGACY)
        } else {
            Html.fromHtml(source)
        }
    }

    companion object {
        private class SelectViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

            var txtInterest          : TextView              = itemView.findViewById(R.id.txtInterest) as TextView
            var txtTitle             : TextView              = itemView.findViewById(R.id.txtTitle) as TextView
            var txtDuration          : TextView              = itemView.findViewById(R.id.txtDuration) as TextView
            var txtRate              : TextView              = itemView.findViewById(R.id.txtRate) as TextView
            var rootLayout           : CardView = itemView.findViewById(R.id.rootLayout) as CardView



        }


    }
}