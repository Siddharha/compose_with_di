package com.app.l_pesa.loanplan.presenter

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import com.app.l_pesa.api.BaseService
import com.app.l_pesa.api.RetrofitHelper
import com.app.l_pesa.common.CommonMethod
import com.app.l_pesa.common.SharedPref
import com.app.l_pesa.loanplan.inter.ICallBackBusinessLoan
import com.app.l_pesa.loanplan.inter.ICallBackCurrentLoan
import com.app.l_pesa.loanplan.inter.ICallBackLoanDetails
import com.google.gson.JsonObject
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.json.JSONObject
import retrofit2.HttpException

class PresenterLoanPlans {

    @SuppressLint("CheckResult")
    fun doLoanPlans(contextOBJ: Context, jsonRequest : JsonObject, callBackOBJ: ICallBackCurrentLoan)
    {
        val sharedPrefOBJ = SharedPref(contextOBJ)
        RetrofitHelper.getRetrofitToken(BaseService::class.java,sharedPrefOBJ.accessToken).doLoanList(jsonRequest)
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
                           if(response.data!!.item!!.size>0)
                           {
                               callBackOBJ.onSuccessLoanPlans(response.data!!.item!!,response.data!!.appliedProduct)
                           }
                            else
                           {
                               callBackOBJ.onEmptyLoanPlans()
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
                                callBackOBJ.onFailureLoanPlans(jsonMessage)
                            }


                        }
                    }
                    catch (exp: Exception)
                    {
                        val errorMessageOBJ= CommonMethod.commonCatchBlock(exp,contextOBJ)
                        callBackOBJ.onFailureLoanPlans(errorMessageOBJ)
                    }

                })
    }


    @SuppressLint("CheckResult")
    fun doLoanPlansBusiness(contextOBJ: Context, jsonRequest : JsonObject, callBackOBJ: ICallBackBusinessLoan)
    {
        val sharedPrefOBJ = SharedPref(contextOBJ)
        RetrofitHelper.getRetrofitToken(BaseService::class.java,sharedPrefOBJ.accessToken).doLoanList(jsonRequest)
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
                            if(response.data!!.item!!.size>0)
                            {
                                callBackOBJ.onSuccessLoanPlans(response.data!!.item!!,response.data!!.appliedProduct)
                            }
                            else
                            {
                                callBackOBJ.onEmptyLoanPlans()
                            }

                        }

                    }
                    catch (e: Exception)
                    {

                    }
                }, {
                    error ->
                    try
                    { val errorVal         =    error as HttpException
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
                                callBackOBJ.onFailureLoanPlans(jsonMessage)
                            }


                        }
                    }
                    catch (exp: Exception)
                    {
                        val errorMessageOBJ= CommonMethod.commonCatchBlock(exp,contextOBJ)
                        callBackOBJ.onFailureLoanPlans(errorMessageOBJ)
                    }

                })
    }

    @SuppressLint("CheckResult")
    fun getLoanDetails(contextOBJ: Context, jsonRequest: JsonObject, callBackOBJ: ICallBackLoanDetails)
    {
        val sharedPrefOBJ = SharedPref(contextOBJ)
        RetrofitHelper.getRetrofitToken(BaseService::class.java,sharedPrefOBJ.accessToken).getLoanDetails(jsonRequest)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .map { responseBody ->
                responseBody
            }
            .subscribe({ response ->
            //    Log.e("response", response.toString())

                try
                {

                    if(response.status.isSuccess)
                    {

                            callBackOBJ.onSuccessLoanPlansDetails(response.data)

                    }

                }
                catch (e: Exception)
                {

                }
            }, {
                    error ->
                try
                { val errorVal         =    error as HttpException
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
                          //  callBackOBJ.onFailureLoanPlans(jsonMessage)
                        }


                    }
                }
                catch (exp: Exception)
                {
                    val errorMessageOBJ= CommonMethod.commonCatchBlock(exp,contextOBJ)
                   // callBackOBJ.onFailureLoanPlans(errorMessageOBJ)
                }

            })
    }
}