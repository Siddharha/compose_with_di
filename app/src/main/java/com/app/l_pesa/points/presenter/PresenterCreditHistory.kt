package com.app.l_pesa.points.presenter

import android.annotation.SuppressLint
import android.content.Context
import com.app.l_pesa.API.BaseService
import com.app.l_pesa.API.RetrofitHelper
import com.app.l_pesa.common.CommonMethod
import com.app.l_pesa.common.SharedPref
import com.app.l_pesa.loanHistory.inter.ICallBackBusinessLoanHistory
import com.app.l_pesa.loanHistory.inter.ICallBackCurrentLoanHistory
import com.app.l_pesa.points.inter.ICallBackCreditPlanHistory
import com.google.gson.JsonObject
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.json.JSONObject
import retrofit2.HttpException

class PresenterCreditHistory {

    @SuppressLint("CheckResult")
    fun getCreditHistory(contextOBJ: Context,from_date:String, to_date:String, type:String, cursors:String, callBackCurrentOBJ:  ICallBackCreditPlanHistory)
    {
        val sharedPrefOBJ = SharedPref(contextOBJ)
        RetrofitHelper.getRetrofitToken(BaseService::class.java,sharedPrefOBJ.accessToken).getAllCreditPlanHistory(cursors,from_date,to_date,"")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map { responseBody ->
                    responseBody
                }
                .subscribe({ response ->

                    try
                    {

                        if(response.status.isSuccess)
                        {
                            if(response.data.userBuyPointsList.isNotEmpty())
                            {
                                callBackCurrentOBJ.onSuccessCreditHistory(response.data.userBuyPointsList, response.data.cursors,from_date,to_date)
                            }
                            else
                            {
                                callBackCurrentOBJ.onEmptyCreditHistory(type)
                            }
                        }
                        else
                        {
                            callBackCurrentOBJ.onFailureLoanHistory(response.status.message)
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
                                callBackCurrentOBJ.onSessionTimeOut(jsonMessage)
                            }
                            else
                            {
                                callBackCurrentOBJ.onFailureLoanHistory(jsonMessage)
                            }


                        }
                    }
                    catch (exp: Exception)
                    {
                        val errorMessageOBJ= CommonMethod.commonCatchBlock(exp,contextOBJ)
                        callBackCurrentOBJ.onFailureLoanHistory(errorMessageOBJ)
                    }

                })
    }

    @SuppressLint("CheckResult")
    fun getCreditHistoryPaginate(contextOBJ: Context, from_date:String, to_date:String, cursors:String, callBackCurrentOBJ: ICallBackCreditPlanHistory)
    {
        val sharedPrefOBJ = SharedPref(contextOBJ)
        RetrofitHelper.getRetrofitToken(BaseService::class.java,sharedPrefOBJ.accessToken).getAllCreditPlanHistory(cursors,from_date,to_date,"")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map { responseBody ->
                    responseBody
                }
                .subscribe({ response ->

                    try
                    {

                        if(response.status.isSuccess)
                        {
                            if(response.data.userBuyPointsList.isNotEmpty())
                            {
                                callBackCurrentOBJ.onSuccessPaginateCreditHistory(response.data.userBuyPointsList, response.data.cursors,from_date,to_date)
                            }

                        }
                        else
                        {
                            callBackCurrentOBJ.onFailureLoanHistory(response.status.message)
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

                        val jsonError           =    JSONObject(errorVal.response()?.errorBody()?.string()!!)
                        val  jsonStatus         =    jsonError.getJSONObject("status")
                        val jsonMessage         =    jsonStatus.getString("message")

                        callBackCurrentOBJ.onFailureLoanHistory(jsonMessage)
                    }
                    catch (exp: Exception)
                    {
                        val errorMessageOBJ= CommonMethod.commonCatchBlock(exp,contextOBJ)
                        callBackCurrentOBJ.onFailureLoanHistory(errorMessageOBJ)
                    }

                })
    }
}