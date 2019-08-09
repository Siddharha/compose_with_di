package com.app.l_pesa.lpk.presenter

import android.annotation.SuppressLint
import android.content.Context
import com.app.l_pesa.API.BaseService
import com.app.l_pesa.API.RetrofitHelper
import com.app.l_pesa.common.CommonMethod
import com.app.l_pesa.common.SharedPref
import com.app.l_pesa.lpk.inter.ICallBackInterestHistory
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.json.JSONObject
import retrofit2.HttpException

class PresenterInterestHistory {

    @SuppressLint("CheckResult")
    fun getInterestHistory(contextOBJ: Context, from_date:String, to_date:String, type:String, callBackOBJ: ICallBackInterestHistory)
    {
        val sharedPrefOBJ = SharedPref(contextOBJ)
        RetrofitHelper.getRetrofitToken(BaseService::class.java,sharedPrefOBJ.accessToken).getInterestHistory("",from_date,to_date)
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
                            if(response.data!!.userInterestHistory!!.size>0)
                            {
                                callBackOBJ.onSuccessInterestHistory(response.data!!.userInterestHistory,response.data!!.cursors,from_date,to_date)
                            }
                            else
                            {
                                callBackOBJ.onEmptyInterestHistory(type)
                            }
                        }
                        else
                        {
                            callBackOBJ.onErrorInterestHistory(response.status!!.message)
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

                        val jsonError             =    JSONObject(errorVal.response().errorBody()?.string()!!)
                        val  jsonStatus           =    jsonError.getJSONObject("status")
                        val jsonMessage           =    jsonStatus.getString("message")

                        callBackOBJ.onErrorInterestHistory(jsonMessage)
                    }
                    catch (exp: Exception)
                    {
                        val errorMessageOBJ= CommonMethod.commonCatchBlock(exp,contextOBJ)
                        callBackOBJ.onErrorInterestHistory(errorMessageOBJ)
                    }

                })
    }

    @SuppressLint("CheckResult")
    fun getInterestHistoryPaginate(contextOBJ: Context, cursorData:String, from_date:String, to_date:String, callBackOBJ: ICallBackInterestHistory)
    {
        val sharedPrefOBJ = SharedPref(contextOBJ)
        RetrofitHelper.getRetrofitToken(BaseService::class.java,sharedPrefOBJ.accessToken).getInterestHistory(cursorData,from_date,to_date)
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
                            if(response.data!!.userInterestHistory!!.size>0)
                            {
                                callBackOBJ.onSuccessInterestHistoryPaginate(response.data!!.userInterestHistory,response.data!!.cursors,from_date,to_date)
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
                        val errorVal              = error as HttpException

                        val jsonError             =    JSONObject(errorVal.response().errorBody()?.string()!!)
                        val  jsonStatus           =    jsonError.getJSONObject("status")
                        val jsonMessage           =    jsonStatus.getString("message")

                        callBackOBJ.onErrorInterestHistory(jsonMessage)
                    }
                    catch (exp: Exception)
                    {
                        val errorMessageOBJ= CommonMethod.commonCatchBlock(exp,contextOBJ)
                        callBackOBJ.onErrorInterestHistory(errorMessageOBJ)
                    }

                })
    }
}