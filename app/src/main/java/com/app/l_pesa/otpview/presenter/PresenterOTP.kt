package com.app.l_pesa.otpview.presenter

import android.annotation.SuppressLint
import android.content.Context
import com.app.l_pesa.api.BaseService
import com.app.l_pesa.api.RetrofitHelper
import com.app.l_pesa.common.CommonMethod
import com.app.l_pesa.otpview.inter.ICallBackVerifyOTP
import com.google.gson.JsonObject
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.json.JSONObject
import retrofit2.HttpException

class PresenterOTP {

    @SuppressLint("CheckResult")
    fun doVerifyOTP(contextOBJ: Context, jsonRequest: JsonObject, callBackOBJ: ICallBackVerifyOTP) {

         RetrofitHelper.getRetrofit(BaseService::class.java).doCheckOTP(jsonRequest)

                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map { responseBody ->
                    responseBody
                }
                .subscribe({ response ->

                    try {
                        if (response.status.isSuccess) {
                            callBackOBJ.onSuccessVerifyOTP(response.data)

                        } else {
                            callBackOBJ.onErrorVerifyOTP(response.status.message)
                        }
                    } catch (e: Exception) {

                    }
                }, { error ->
                    try {
                        val errorVal = error as HttpException

                        val jsonError = JSONObject(errorVal.response()?.errorBody()?.string()!!)
                        val jsonStatus = jsonError.getJSONObject("status")
                        val jsonMessage = jsonStatus.getString("message")

                        callBackOBJ.onErrorVerifyOTP(jsonMessage)

                    } catch (exp: Exception) {
                        val errorMessageOBJ = CommonMethod.commonCatchBlock(exp, contextOBJ)
                        callBackOBJ.onErrorVerifyOTP(errorMessageOBJ)
                    }

                })
    }

    @SuppressLint("CheckResult")
    fun doResendOTP(contextOBJ: Context, jsonRequest: JsonObject, callBackOBJ: ICallBackVerifyOTP) {

        RetrofitHelper.getRetrofit(BaseService::class.java).doResendOTP(jsonRequest)

                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map { responseBody ->
                    responseBody
                }
                .subscribe({ response ->

                    try {
                        if (response.status.isSuccess) {
                            callBackOBJ.onSuccessResendOTP()

                        } else {
                            callBackOBJ.onErrorResendOTP(response.status.message)
                        }
                    } catch (e: Exception) {

                    }
                }, { error ->
                    try {
                        val errorVal = error as HttpException

                        val jsonError = JSONObject(errorVal.response()?.errorBody()?.string()!!)
                        val jsonStatus = jsonError.getJSONObject("status")
                        val jsonMessage = jsonStatus.getString("message")

                        callBackOBJ.onErrorResendOTP(jsonMessage)
                    } catch (exp: Exception) {
                        val errorMessageOBJ = CommonMethod.commonCatchBlock(exp, contextOBJ)
                        callBackOBJ.onErrorResendOTP(errorMessageOBJ)
                    }

                })
    }
}