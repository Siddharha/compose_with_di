package com.app.l_pesa.registration.presenter

import android.annotation.SuppressLint
import android.content.Context
import com.app.l_pesa.API.BaseService
import com.app.l_pesa.API.RetrofitHelper
import com.app.l_pesa.common.CommonMethod
import com.app.l_pesa.registration.inter.EmailVerifyListener
import com.app.l_pesa.registration.inter.ICallBackEmailVerify
import com.app.l_pesa.registration.inter.MobileVerifyListener
import com.app.l_pesa.registration.model.EmailVerifyRequest
import com.app.l_pesa.registration.model.EmailVerifyResponse
import com.app.l_pesa.registration.model.ReqVerifyMobile
import com.google.gson.JsonObject
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response


class PresenterVerify {
    @SuppressLint("CheckResult")
    fun doEmailVerify(contextOBJ: Context, emailVerifyRequest: EmailVerifyRequest, iCallBackEmailVerify: ICallBackEmailVerify) {
        RetrofitHelper.getRetrofit(BaseService::class.java).doEmailVerify(emailVerifyRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map { responseBody ->
                    responseBody
                }
                .subscribe({ response ->

                    try {
                        if (response.status!!.isSuccess) {
                            iCallBackEmailVerify.onSuccessEmailVerify(response.data!!)
                        } else {
                            iCallBackEmailVerify.onErrorEmailVerify(response.status.message)
                        }

                    } catch (e: Exception) {

                    }
                }, { error ->
                    try {
                        val errorVal = error as HttpException

                        val jsonError = JSONObject(errorVal.response()?.errorBody()?.string()!!)
                        val jsonStatus = jsonError.getJSONObject("status")
                        val jsonMessage = jsonStatus.getString("message")

                        iCallBackEmailVerify.onErrorEmailVerify(jsonMessage)
                        iCallBackEmailVerify.onErrorEmailCode(jsonStatus)
                    } catch (exp: Exception) {
                        val errorMessageOBJ = CommonMethod.commonCatchBlock(exp, contextOBJ)
                        iCallBackEmailVerify.onErrorEmailVerify(errorMessageOBJ)
                    }

                })
    }

    @SuppressLint("CheckResult")
    fun doMobileVerify(contextOBJ: Context, reqVerifyMobile: JsonObject, mobileVerifyListener: MobileVerifyListener) {
        RetrofitHelper.getRetrofit(BaseService::class.java).doMobileVerify(reqVerifyMobile)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map { responseBody ->
                    responseBody
                }
                .subscribe({ response ->

                    try {
                        if (response.status!!.isSuccess) {
                            mobileVerifyListener.onResponseVerifyMobile(response.data!!)
                        } else {
                            mobileVerifyListener.onFailure(response.status.message)
                        }

                    } catch (e: Exception) {
                        mobileVerifyListener.onFailure("Unable to verify your number! please contact L-Pesa team")
                    }
                }, { error ->
                    try {
                        val errorVal = error as HttpException

                        val jsonError = JSONObject(errorVal.response()?.errorBody()?.string()!!)
                        val jsonStatus = jsonError.getJSONObject("status")
                        val jsonMessage = jsonStatus.getString("message")

                        /*if (jsonStatusCode == 50002){
                            mobileVerifyListener.onFailure(jsonMessage)
                        }else{
                            mobileVerifyListener.onFailure(jsonMessage)
                        }*/

                        mobileVerifyListener.onFailure(jsonMessage)

                    } catch (exp: Exception) {
                        val errorMessageOBJ = CommonMethod.commonCatchBlock(exp, contextOBJ)
                        mobileVerifyListener.onFailure(errorMessageOBJ)
                    }

                })
    }

   /* fun doEmail(
            emailVerifyRequest: EmailVerifyRequest,
            emailVerifyListener: EmailVerifyListener?
    ) {
        RetrofitHelper.getRetrofit(BaseService::class.java).doEmailVerify(emailVerifyRequest)
                .enqueue(object : Callback<EmailVerifyResponse> {
                    override fun onFailure(call: Call<EmailVerifyResponse>, t: Throwable) {
                        emailVerifyListener?.onEmailVerifyResponse(com.app.l_pesa.API.Result.Error(Exception(t.localizedMessage)))
                    }

                    override fun onResponse(call: Call<EmailVerifyResponse>, response: Response<EmailVerifyResponse>) {
                        when{
                            response.isSuccessful -> {
                                emailVerifyListener?.onEmailVerifyResponse(com.app.l_pesa.API.Result.Success(response.body()))
                            }

                        }
                    }

                })

    }*/
}