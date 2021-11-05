package com.app.l_pesa.login.presenter

import android.annotation.SuppressLint
import android.content.Context
import com.app.l_pesa.api.BaseService
import com.app.l_pesa.api.RetrofitHelper
import com.app.l_pesa.common.CommonMethod
import com.app.l_pesa.common.SharedPref
import com.app.l_pesa.login.inter.ICallBackCode
import com.app.l_pesa.login.inter.ICallBackEmail
import com.app.l_pesa.login.inter.ICallBackResend
import com.google.gson.JsonObject
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.json.JSONObject
import retrofit2.HttpException

class PresenterEmail {

    @SuppressLint("CheckResult")
    fun doVerifyEmail(contextOBJ: Context, jsonRequest : JsonObject, callBackOBJ: ICallBackEmail)
    {
        val shared= SharedPref(contextOBJ)
        RetrofitHelper.getRetrofitToken(BaseService::class.java,shared.accessToken).doAddEditEmail(jsonRequest)
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
                            callBackOBJ.onSuccessEmail()

                        }
                        else
                        {
                            callBackOBJ.onErrorEmail(response.status.message)

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

                        val jsonError                  =    JSONObject(errorVal.response()?.errorBody()?.string()!!)
                        val  jsonStatus      =    jsonError.getJSONObject("status")
                        val jsonMessage          =    jsonStatus.getString("message")
                        //val jsonStatusCode         =    jsonStatus.getInt("statusCode")


                         callBackOBJ.onErrorEmail(jsonMessage)

                    }
                    catch (exp: Exception)
                    {
                        val errorMessageOBJ= CommonMethod.commonCatchBlock(exp,contextOBJ)
                        callBackOBJ.onErrorEmail(errorMessageOBJ)
                    }

                })
    }



    @SuppressLint("CheckResult")
    fun doVerifyCode(contextOBJ: Context, jsonRequest : JsonObject, callBackOBJ: ICallBackCode)
    {
        val shared= SharedPref(contextOBJ)
        println("TOKEN"+shared.accessToken)
        RetrofitHelper.getRetrofitToken(BaseService::class.java,shared.accessToken).doVerifyEmail(jsonRequest)
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
                            callBackOBJ.onSuccessVerification()

                        }
                        else
                        {
                            callBackOBJ.onErrorVerification(response.status.message)

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

                        val jsonError                  =    JSONObject(errorVal.response()?.errorBody()?.string()!!)
                        val  jsonStatus      =    jsonError.getJSONObject("status")
                        val jsonMessage          =    jsonStatus.getString("message")
                        //val jsonStatusCode         =    jsonStatus.getInt("statusCode")


                        callBackOBJ.onErrorVerification(jsonMessage)

                    }
                    catch (exp: Exception)
                    {
                        val errorMessageOBJ= CommonMethod.commonCatchBlock(exp,contextOBJ)
                        callBackOBJ.onErrorVerification(errorMessageOBJ)
                    }

                })
    }

    @SuppressLint("CheckResult")
    fun doResendCode(contextOBJ: Context, callBackOBJ: ICallBackResend)
    {
        val shared= SharedPref(contextOBJ)
       // println("TOKEN"+shared.accessToken)
        RetrofitHelper.getRetrofitToken(BaseService::class.java,shared.accessToken).doResendCodeEmail()
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
                            callBackOBJ.onSuccessResend()

                        }
                        else
                        {
                            callBackOBJ.onErrorResend(response.status.message)

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

                        val jsonError                  =    JSONObject(errorVal.response()?.errorBody()?.string()!!)
                        val  jsonStatus      =    jsonError.getJSONObject("status")
                        val jsonMessage          =    jsonStatus.getString("message")
                        //val jsonStatusCode         =    jsonStatus.getInt("statusCode")


                        callBackOBJ.onErrorResend(jsonMessage)

                    }
                    catch (exp: Exception)
                    {
                        val errorMessageOBJ= CommonMethod.commonCatchBlock(exp,contextOBJ)
                        callBackOBJ.onErrorResend(errorMessageOBJ)
                    }

                })
    }
}