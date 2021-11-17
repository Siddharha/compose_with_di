package com.app.l_pesa.loanHistory.model

import com.app.l_pesa.common.CommonStatusModel
import java.util.*

class ResLoanHistoryCurrent(val status: CommonStatusModel, val data: Data) {

  data class Data(var loan_history: ArrayList<LoanHistory>, val cursors:Cursors,var user_credit_score:Int)

  data class Cursors(

                    val hasNext:Boolean,
                    val after:String
  )

  data class LoanHistory(

           var loan_id:Int,
           var identity_number:String,
           var loan_amount:Double,
           var loan_amount_txt:String,
           var currency_flag:Boolean,
           var interest_rate:String,
           var convertion_dollar_value:String,
           var convertion_loan_amount:String,
           var actual_loan_amount:String,
           var applied_date:String,
           var sanctioned_date:String,
           var finished_date:String,
           var disapprove_date:String,
           var loan_status:String,
           var currency_code:String,
           var due_date:String,
           var duration:String,
           var conversion_charge:String,
           var conversion_charge_amount:String,
           var loan_purpose_message:String,
           var cr_sc_when_requesting_loan:String,
           var processing_fees:String,
           var processing_fees_amount:String,
           var disapprove_reason:String,
           var is_loan_history:Boolean =false

  )


}
