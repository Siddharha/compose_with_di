package com.app.l_pesa.investment.presenter

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

    fun getInvestmentHistory(contextOBJ: Context, callBackOBJ: ICallBackInvestmentHistory)
    {
        val sharedPrefOBJ = SharedPref(contextOBJ)
        RetrofitHelper.getRetrofitToken(BaseService::class.java,sharedPrefOBJ.accessToken).getInvestmentHistory("")
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
                                callBackOBJ.onSuccessInvestmentHistory(response.data!!.userInvestment!!,response.data!!.cursors)
                            }
                            else
                            {
                                callBackOBJ.onEmptyInvestmentHistory()
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

                        val jsonError             =    JSONObject(errorVal.response().errorBody()?.string())
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

    fun getInvestmentHistoryPaginate(contextOBJ: Context,cursor:String, callBackOBJ: ICallBackInvestmentHistory)
    {
        val sharedPrefOBJ = SharedPref(contextOBJ)
        RetrofitHelper.getRetrofitToken(BaseService::class.java,sharedPrefOBJ.accessToken).getInvestmentHistory(cursor)
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
                                callBackOBJ.onSuccessInvestmentHistoryPaginate(response.data!!.userInvestment!!,response.data!!.cursors)
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

                        val jsonError             =    JSONObject(errorVal.response().errorBody()?.string())
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