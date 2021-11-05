package com.app.l_pesa.loanplan.presenter

import android.annotation.SuppressLint
import android.content.Context
import com.app.l_pesa.api.BaseService
import com.app.l_pesa.api.RetrofitHelper
import com.app.l_pesa.common.CommonMethod
import com.app.l_pesa.common.SharedPref
import com.app.l_pesa.loanplan.inter.ICallBackLoanTenure
import com.google.gson.JsonObject
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.json.JSONObject
import retrofit2.HttpException

class PresenterLoanApplyTenure {

    @SuppressLint("CheckResult")
    fun getLoanTenureList(contextOBJ: Context, jsonRequest : JsonObject, callBackOBJ: ICallBackLoanTenure)
    {
        val sharedPrefOBJ = SharedPref(contextOBJ)
        RetrofitHelper.getRetrofitToken(BaseService::class.java,sharedPrefOBJ.accessToken).getLoanTenure(jsonRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map { responseBody ->
                    //Log.e("response",responseBody.toString())
                    responseBody
                }
                .subscribe({ response ->

                    try
                    {

                        if(response.status.isSuccess)
                        {
                           if(response.data.options.isNotEmpty())
                           {
                               callBackOBJ.onSuccessLoanTenureList(response.data.options)
                           }
                            else
                           {
                               callBackOBJ.onEmptyLoanTenureList()
                           }

                        }

                    }
                    catch (e: Exception)
                    {

                    }
                }, {
                    error ->
                    try
                    {
                        val errorVal         =    error as HttpException
                        if(errorVal.code()>=400)
                        {
                            val jsonError        =    JSONObject(errorVal.response()?.errorBody()?.string()!!)
                            val  jsonStatus      =    jsonError.getJSONObject("status")
                            val jsonMessage      =    jsonStatus.getString("message")
                            val jsonStatusCode   =    jsonStatus.getInt("statusCode")

                            if(jsonStatusCode==50002)
                            {
                                callBackOBJ.onSessionTimeOut(jsonMessage)
                            }
                            else
                            {
                                callBackOBJ.onFailureLoanTenureList(jsonMessage)
                            }


                        }
                    }
                    catch (exp: Exception)
                    {
                        val errorMessageOBJ= CommonMethod.commonCatchBlock(exp,contextOBJ)
                        callBackOBJ.onFailureLoanTenureList(errorMessageOBJ)
                    }

                })
    }

}