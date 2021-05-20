package com.app.l_pesa.allservices.inter

import com.app.l_pesa.allservices.models.SasaPaymentResponse
import com.app.l_pesa.allservices.models.SasaUserInfoResponse

interface ICallBackSasaPayment {
    fun onSuccessPayment(data: SasaPaymentResponse.Data)
    fun onSessionPaymentTimeOut(jsonMessage: String)
    fun onErrorUserPayment(jsonMessage: String)
}