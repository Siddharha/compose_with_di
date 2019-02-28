package com.app.l_pesa.loanHistory.model

import com.app.l_pesa.common.CommonStatusModel
import java.util.ArrayList

class ResLoanHistory(val status: CommonStatusModel, val data: Data) {

  data class Data(var loan_history: ArrayList<LoanHistory>, var user_credit_score:Int)

  /*"loan_history": [
      {
        "loan_id": 124970,
        "identity_number": "11100124970",
        "loan_amount": 1,
        "interest_rate": "0.7%",
        "convertion_dollar_value": "TZS 2260.4",
        "convertion_loan_amount": "TZS 2260",
        "actual_loan_amount": "TZS 1913",
        "applied_date": "05/04/2018",
        "sanctioned_date": "05/04/2018",
        "finished_date": "05/04/2018",
        "disapprove_date": "",
        "loan_status": "C",
        "currency_code": "TZS",
        "loan_status_txt": [
          "Completed",
          false
        ],
        "due_date": "05/04/2018"
      }*/

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
           var due_date:String)



}
