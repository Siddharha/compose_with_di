package com.app.l_pesa.loanHistory.presenter

import android.content.Context
import com.app.l_pesa.API.BaseService
import com.app.l_pesa.API.RetrofitHelper
import com.app.l_pesa.common.CommonMethod
import com.app.l_pesa.common.SharedPref
import com.app.l_pesa.loanHistory.inter.ICallBackBusinessLoanHistory
import com.app.l_pesa.loanHistory.inter.ICallBackCurrentLoanHistory
import com.google.gson.JsonObject
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.json.JSONObject
import retrofit2.HttpException

class PresenterLoanHistory {

    fun getLoanHistory(contextOBJ: Context, jsonRequest : JsonObject, from_date:String,to_date:String,cursors:String, callBackCurrentOBJ: ICallBackCurrentLoanHistory)
    {
        val sharedPrefOBJ = SharedPref(contextOBJ)
        RetrofitHelper.getRetrofitToken(BaseService::class.java,sharedPrefOBJ.accessToken).doLoanHistory(jsonRequest,cursors,from_date,to_date)
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
                            if(response.data.loan_history.size>0)
                            {
                                callBackCurrentOBJ.onSuccessLoanHistory(response.data.loan_history,response.data!!.cursors,response.data.user_credit_score,from_date,to_date)
                            }
                            else
                            {
                                callBackCurrentOBJ.onEmptyLoanHistory()
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

                        val jsonError           =    JSONObject(errorVal.response().errorBody()?.string())
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

    fun getLoanHistoryBusiness(contextOBJ: Context, jsonRequest : JsonObject, from_date:String,to_date:String,cursors:String, callBackCurrentOBJ: ICallBackBusinessLoanHistory)
    {
        val sharedPrefOBJ = SharedPref(contextOBJ)
        RetrofitHelper.getRetrofitToken(BaseService::class.java,sharedPrefOBJ.accessToken).doLoanHistoryBusiness(jsonRequest,cursors,from_date,to_date)
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
                            if(response.data.loan_history.size>0)
                            {
                                callBackCurrentOBJ.onSuccessLoanHistory(response.data.loan_history,response.data!!.cursors,response.data.user_credit_score,from_date,to_date)
                            }
                            else
                            {
                                callBackCurrentOBJ.onEmptyLoanHistory()
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

                        val jsonError           =    JSONObject(errorVal.response().errorBody()?.string())
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

    fun getLoanHistoryPaginate(contextOBJ: Context, jsonRequest : JsonObject,from_date:String,to_date:String, cursors:String, callBackCurrentOBJ: ICallBackCurrentLoanHistory)
    {
        val sharedPrefOBJ = SharedPref(contextOBJ)
        RetrofitHelper.getRetrofitToken(BaseService::class.java,sharedPrefOBJ.accessToken).doLoanHistory(jsonRequest,cursors,from_date,to_date)
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
                            if(response.data.loan_history.size>0)
                            {
                                callBackCurrentOBJ.onSuccessPaginateLoanHistory(response.data.loan_history,response.data!!.cursors,from_date,to_date)
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

                        val jsonError           =    JSONObject(errorVal.response().errorBody()?.string())
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

    fun getLoanHistoryPaginateBusiness(contextOBJ: Context, jsonRequest : JsonObject,from_date:String,to_date:String, cursors:String, callBackCurrentOBJ: ICallBackBusinessLoanHistory)
    {
        val sharedPrefOBJ = SharedPref(contextOBJ)
        RetrofitHelper.getRetrofitToken(BaseService::class.java,sharedPrefOBJ.accessToken).doLoanHistoryBusiness(jsonRequest,cursors,from_date,to_date)
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
                            if(response.data.loan_history.size>0)
                            {
                                callBackCurrentOBJ.onSuccessPaginateLoanHistory(response.data.loan_history,response.data!!.cursors,from_date,to_date)
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

                        val jsonError           =    JSONObject(errorVal.response().errorBody()?.string())
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