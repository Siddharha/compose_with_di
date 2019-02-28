package com.app.l_pesa.loanHistory.model

import com.app.l_pesa.common.CommonStatusModel
import java.util.ArrayList

class ResLoanHistory(val status: CommonStatusModel, val data: Data) {

  data class Data(var loan_history: ArrayList<LoanHistory>, val cursors:Cursors,var user_credit_score:Int)

  data class Cursors(

                    val hasNext:Boolean,
                    val after:String
  )

  data class LoanHistory(

           var loan_id:Int,
           var identity_number:String,
           var loan_amount:Int,
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
           var duration:String)



}
