package com.app.l_pesa.investment.presenter

import android.annotation.SuppressLint
import android.content.Context
import com.app.l_pesa.API.BaseService
import com.app.l_pesa.API.RetrofitHelper
import com.app.l_pesa.common.CommonMethod
import com.app.l_pesa.common.SharedPref
import com.app.l_pesa.investment.inter.ICallBackInvestmentHistory
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.json.JSONObject
import retrofit2.HttpException

class PresenterInvestmentHistory {

    @SuppressLint("CheckResult")
    fun getInvestmentHistory(contextOBJ: Context, from_date:String, to_date:String, type:String, callBackOBJ: ICallBackInvestmentHistory)
    {
        val sharedPrefOBJ = SharedPref(contextOBJ)
        RetrofitHelper.getRetrofitToken(BaseService::class.java,sharedPrefOBJ.accessToken).getInvestmentHistory("",from_date,to_date)
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
                            if(response.data!!.userInvestment!!.size>0)
                            {
                                callBackOBJ.onSuccessInvestmentHistory(response.data!!.userInvestment!!,response.data!!.cursors,from_date,to_date)
                            }
                            else
                            {
                                callBackOBJ.onEmptyInvestmentHistory(type)
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
                                callBackOBJ.onErrorInvestmentHistory(jsonMessage)
                            }


                        }
                    }
                    catch (exp: Exception)
                    {
                        val errorMessageOBJ= CommonMethod.commonCatchBlock(exp,contextOBJ)
                        callBackOBJ.onErrorInvestmentHistory(errorMessageOBJ)
                    }

                })
    }

    @SuppressLint("CheckResult")
    fun getInvestmentHistoryPaginate(contextOBJ: Context, cursor:String, from_date:String, to_date:String, callBackOBJ: ICallBackInvestmentHistory)
    {
        val sharedPrefOBJ = SharedPref(contextOBJ)
        RetrofitHelper.getRetrofitToken(BaseService::class.java,sharedPrefOBJ.accessToken).getInvestmentHistory(cursor,from_date,to_date)
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
                            if(response.data!!.userInvestment!!.size>0)
                            {
                                callBackOBJ.onSuccessInvestmentHistoryPaginate(response.data!!.userInvestment!!,response.data!!.cursors,from_date,to_date)
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
                        val errorVal     = error as HttpException

                        val jsonError             =    JSONObject(errorVal.response()?.errorBody()?.string()!!)
                        val  jsonStatus           =    jsonError.getJSONObject("status")
                        val jsonMessage           =    jsonStatus.getString("message")

                        callBackOBJ.onErrorInvestmentHistory(jsonMessage)
                    }
                    catch (exp: Exception)
                    {
                        val errorMessageOBJ= CommonMethod.commonCatchBlock(exp,contextOBJ)
                        callBackOBJ.onErrorInvestmentHistory(errorMessageOBJ)
                    }

                })
    }
}