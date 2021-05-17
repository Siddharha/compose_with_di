package com.app.l_pesa.registration.presenter

import android.annotation.SuppressLint
import android.content.Context
import com.app.l_pesa.API.BaseService
import com.app.l_pesa.API.RetrofitHelper
import com.app.l_pesa.common.CommonMethod
import com.app.l_pesa.registration.inter.ICallBackRegisterOne
import com.app.l_pesa.registration.inter.ICallVerifyCode
import com.app.l_pesa.registration.model.ReqVerifyCode
import com.google.gson.JsonObject
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.json.JSONObject
import retrofit2.HttpException


class PresenterRegistrationOne {

    @SuppressLint("CheckResult")
    fun doRegistration(contextOBJ: Context, jsonObject : JsonObject, callBackOBJ: ICallBackRegisterOne)
    {
        RetrofitHelper.getRetrofit(BaseService::class.java).doRegister(jsonObject)
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
                            callBackOBJ.onSuccessRegistrationOne(response.data)
                        }
                        else
                        {
                            callBackOBJ.onErrorRegistrationOne(response.status.message)
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
                        val  jsonStatus=    jsonError.getJSONObject("status")
                        val jsonMessage    =    jsonStatus.getString("message")

                        callBackOBJ.onErrorRegistrationOne(jsonMessage)
                    }
                    catch (exp: Exception)
                    {
                        val errorMessageOBJ= CommonMethod.commonCatchBlock(exp,contextOBJ)
                        callBackOBJ.onErrorRegistrationOne(errorMessageOBJ)
                    }

                })
    }


    @SuppressLint("CheckResult")
    fun doVerifyOtp(contextOBJ: Context, reqVerifyCode: ReqVerifyCode, callBackOBJ: ICallVerifyCode)
    {
        RetrofitHelper.getRetrofit(BaseService::class.java).doOtpVerify(reqVerifyCode)
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
                            callBackOBJ.onVerificationSuccess(response.data!!)
                        }
                        else
                        {
                            callBackOBJ.onVerifyFailure(response.status.message)
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
                        val  jsonStatus=    jsonError.getJSONObject("status")
                        val jsonMessage    =    jsonStatus.getString("message")

                        callBackOBJ.onVerifyFailure(jsonMessage)
                    }
                    catch (exp: Exception)
                    {
                        val errorMessageOBJ= CommonMethod.commonCatchBlock(exp,contextOBJ)
                        callBackOBJ.onVerifyFailure(errorMessageOBJ)
                    }

                })
    }


}