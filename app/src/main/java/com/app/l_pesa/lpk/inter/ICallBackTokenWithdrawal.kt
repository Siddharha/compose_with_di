package com.app.l_pesa.lpk.inter

interface ICallBackTokenWithdrawal {

   fun onSuccessTokenWithdrawal()
   fun onErrorTokenWithdrawal(message: String, statusCode: Int)

}