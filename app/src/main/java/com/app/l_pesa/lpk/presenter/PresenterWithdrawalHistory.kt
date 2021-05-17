package com.app.l_pesa.lpk.presenter

import android.annotation.SuppressLint
import android.content.Context
import com.app.l_pesa.API.BaseService
import com.app.l_pesa.API.RetrofitHelper
import com.app.l_pesa.common.CommonMethod
import com.app.l_pesa.common.SharedPref
import com.app.l_pesa.lpk.inter.ICallBackWithdrawalHistory
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.json.JSONObject
import retrofit2.HttpException

class PresenterWithdrawalHistory {

    @SuppressLint("CheckResult")
    fun getWithdrawalHistory(contextOBJ: Context, from_date:String, to_date:String, type:String, callBackOBJ: ICallBackWithdrawalHistory)
    {
        val sharedPrefOBJ = SharedPref(contextOBJ)
        RetrofitHelper.getRetrofitToken(BaseService::class.java,sharedPrefOBJ.accessToken).getWithdrawalHistory("",from_date,to_date)
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
                            if(response.data!!.userWithdrawalHistory!!.size>0)
                            {
                                callBackOBJ.onSuccessWithdrawalHistory(response.data!!.userWithdrawalHistory!!,response.data!!.cursors,from_date,to_date)
                            }
                            else
                            {
                                callBackOBJ.onEmptyWithdrawalHistory(type)
                            }
                        }
                        else
                        {
                            callBackOBJ.onErrorWithdrawalHistory(response.status!!.message)
                        }

                    }
                    catch (e: Exception)
                    {

                    }
                }, {
                    error ->
                    try
                    {
                        val errorVal              = error as HttpException

                        val jsonError             =    JSONObject(errorVal.response()?.errorBody()?.string()!!)
                        val  jsonStatus           =    jsonError.getJSONObject("status")
                        val jsonMessage           =    jsonStatus.getString("message")

                        callBackOBJ.onErrorWithdrawalHistory(jsonMessage)
                    }
                    catch (exp: Exception)
                    {
                        val errorMessageOBJ= CommonMethod.commonCatchBlock(exp,contextOBJ)
                        callBackOBJ.onErrorWithdrawalHistory(errorMessageOBJ)
                    }

                })
    }

    @SuppressLint("CheckResult")
    fun getWithdrawalHistoryPaginate(contextOBJ: Context, cursor:String, from_date:String, to_date:String, callBackOBJ: ICallBackWithdrawalHistory)
    {
        val sharedPrefOBJ = SharedPref(contextOBJ)
        RetrofitHelper.getRetrofitToken(BaseService::class.java,sharedPrefOBJ.accessToken).getWithdrawalHistory(cursor,from_date,to_date)
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
                            if(response.data!!.userWithdrawalHistory!!.size>0)
                            {
                                callBackOBJ.onSuccessWithdrawalHistoryPaginate(response.data!!.userWithdrawalHistory!!,response.data!!.cursors,from_date,to_date)
                            }

                        }
                        else
                        {
                            callBackOBJ.onErrorWithdrawalHistory(response.status!!.message)
                        }

                    }
                    catch (e: Exception)
                    {

                    }
                }, {
                    error ->
                    try
                    {
                        val errorVal              = error as HttpException

                        val jsonError             =    JSONObject(errorVal.response()?.errorBody()?.string())
                        val  jsonStatus           =    jsonError.getJSONObject("status")
                        val jsonMessage           =    jsonStatus.getString("message")

                        callBackOBJ.onErrorWithdrawalHistory(jsonMessage)
                    }
                    catch (exp: Exception)
                    {
                        val errorMessageOBJ= CommonMethod.commonCatchBlock(exp,contextOBJ)
                        callBackOBJ.onErrorWithdrawalHistory(errorMessageOBJ)
                    }

                })
    }


}