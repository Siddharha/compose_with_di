package com.app.l_pesa.notification.presenter

import android.annotation.SuppressLint
import android.content.Context
import com.app.l_pesa.api.BaseService
import com.app.l_pesa.api.RetrofitHelper
import com.app.l_pesa.common.CommonMethod
import com.app.l_pesa.common.SharedPref
import com.app.l_pesa.notification.inter.ICallBackNotification
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.json.JSONObject
import retrofit2.HttpException

class PresenterNotification {

    @SuppressLint("CheckResult")
    fun getNotification(contextOBJ: Context, callBackOBJ: ICallBackNotification)
    {
        val sharedPrefOBJ = SharedPref(contextOBJ)
        RetrofitHelper.getRetrofitToken(BaseService::class.java,sharedPrefOBJ.accessToken).getNotification("")
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
                            if(response.data.notification_history.size>0)
                            {
                                callBackOBJ.onSuccessNotification(response.data.notification_history,response.data.cursors)
                            }
                            else
                            {
                                callBackOBJ.onEmptyNotification()
                            }
                        }
                        else
                        {
                            callBackOBJ.onFailureNotification(response.status.message)
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
                                callBackOBJ.onFailureNotification(jsonMessage)
                            }


                        }
                    }
                    catch (exp: Exception)
                    {
                        val errorMessageOBJ= CommonMethod.commonCatchBlock(exp,contextOBJ)
                        callBackOBJ.onFailureNotification(errorMessageOBJ)
                    }

                })
    }


    @SuppressLint("CheckResult")
    fun getNotificationPaginate(contextOBJ: Context, cursor:String, callBackOBJ: ICallBackNotification)
    {
        val sharedPrefOBJ = SharedPref(contextOBJ)
        RetrofitHelper.getRetrofitToken(BaseService::class.java,sharedPrefOBJ.accessToken).getNotification(cursor)
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
                            if(response.data.notification_history.size>0)
                            {
                                callBackOBJ.onSuccessNotificationPaginate(response.data.notification_history,response.data.cursors)
                            }

                        }
                        else
                        {
                            callBackOBJ.onFailureNotification(response.status.message)
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

                        callBackOBJ.onFailureNotification(jsonMessage)
                    }
                    catch (exp: Exception)
                    {
                        val errorMessageOBJ= CommonMethod.commonCatchBlock(exp,contextOBJ)
                        callBackOBJ.onFailureNotification(errorMessageOBJ)
                    }

                })
    }

}