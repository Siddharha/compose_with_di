package com.app.l_pesa.lpk.presenter

import android.content.Context
import com.app.l_pesa.API.BaseService
import com.app.l_pesa.API.RetrofitHelper
import com.app.l_pesa.common.CommonMethod
import com.app.l_pesa.common.SharedPref
import com.app.l_pesa.lpk.inter.ICallBackTransferHistory
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.json.JSONObject
import retrofit2.HttpException

class PresenterTransferHistory {

    fun getTokenHistory(contextOBJ: Context, from_date:String,to_date:String,callBackOBJ: ICallBackTransferHistory)
    {
        val sharedPrefOBJ = SharedPref(contextOBJ)
        RetrofitHelper.getRetrofitToken(BaseService::class.java,sharedPrefOBJ.accessToken).getTokenHistory("",from_date,to_date)
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
                           if(response.data!!.userTransferHistory!!.size>0)
                           {
                               callBackOBJ.onSuccessTransferHistory(response.data!!.userTransferHistory!!,response.data!!.cursors,from_date,to_date)
                           }
                           else
                           {
                               callBackOBJ.onEmptyTransferHistory()
                           }
                        }
                        else
                        {
                            callBackOBJ.onErrorTransferHistory(response.status!!.message)
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

                        val jsonError             =    JSONObject(errorVal.response().errorBody()?.string())
                        val  jsonStatus           =    jsonError.getJSONObject("status")
                        val jsonMessage           =    jsonStatus.getString("message")

                        callBackOBJ.onErrorTransferHistory(jsonMessage)
                    }
                    catch (exp: Exception)
                    {
                        val errorMessageOBJ= CommonMethod.commonCatchBlock(exp,contextOBJ)
                        callBackOBJ.onErrorTransferHistory(errorMessageOBJ)
                    }

                })
    }

    fun getTokenHistoryPaginate(contextOBJ: Context, cursor:String,from_date:String,to_date:String, callBackOBJ: ICallBackTransferHistory)
    {
        val sharedPrefOBJ = SharedPref(contextOBJ)
        RetrofitHelper.getRetrofitToken(BaseService::class.java,sharedPrefOBJ.accessToken).getTokenHistory(cursor,from_date,to_date)
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
                            if(response.data!!.userTransferHistory!!.size>0)
                            {
                                callBackOBJ.onSuccessTransferHistoryPaginate(response.data!!.userTransferHistory!!, response.data!!.cursors!!,from_date,to_date)
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

                        val jsonError             =    JSONObject(errorVal.response().errorBody()?.string())
                        val  jsonStatus           =    jsonError.getJSONObject("status")
                        val jsonMessage           =    jsonStatus.getString("message")

                        callBackOBJ.onErrorTransferHistory(jsonMessage)
                    }
                    catch (exp: Exception)
                    {
                        val errorMessageOBJ= CommonMethod.commonCatchBlock(exp,contextOBJ)
                        callBackOBJ.onErrorTransferHistory(errorMessageOBJ)
                    }

                })
    }
}