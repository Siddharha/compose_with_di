package com.app.l_pesa.loanHistory.presenter

import android.content.Context
import com.app.l_pesa.API.BaseService
import com.app.l_pesa.API.RetrofitHelper
import com.app.l_pesa.common.CommonMethod
import com.app.l_pesa.common.SharedPref
import com.app.l_pesa.loanHistory.inter.ICallBackPaymentHistory
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.json.JSONObject
import retrofit2.HttpException

class PresenterPaymentHistory {

    fun getPaymentHistory(contextOBJ: Context,type : String,id:String, callBackPaymentHistory: ICallBackPaymentHistory)
    {
        val sharedPrefOBJ = SharedPref(contextOBJ)
        RetrofitHelper.getRetrofitToken(BaseService::class.java,sharedPrefOBJ.accessToken).getPaymentHistory(type,id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map { responseBody ->
                    responseBody
                }
                .subscribe({ response ->

                    try
                    {

                        if(response.status!!.isSuccess)
                        {
                            if(response.data!!.paymentHistory!!.size>0)
                            {
                                callBackPaymentHistory.onSuccessPaymentHistory(response.data!!.paymentHistory!!)
                            }
                            else
                            {
                                callBackPaymentHistory.onEmptyPaymentHistory()
                            }
                        }
                        else
                        {
                            callBackPaymentHistory.onErrorPaymentHistory(response.status!!.message)
                        }
                    }
                    catch (e: Exception)
                    {

                    }
                }, {
                    error ->
                    try
                    {
                        val errorVal            = error as HttpException

                        val jsonError           =    JSONObject(errorVal.response().errorBody()?.string())
                        val  jsonStatus         =    jsonError.getJSONObject("status")
                        val jsonMessage         =    jsonStatus.getString("message")

                        callBackPaymentHistory.onErrorPaymentHistory(jsonMessage)
                    }
                    catch (exp: Exception)
                    {
                        val errorMessageOBJ= CommonMethod.commonCatchBlock(exp,contextOBJ)
                        callBackPaymentHistory.onErrorPaymentHistory(errorMessageOBJ)
                    }

                })
    }
}