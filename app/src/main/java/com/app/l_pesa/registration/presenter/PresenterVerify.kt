package com.app.l_pesa.registration.presenter

import android.annotation.SuppressLint
import android.content.Context
import com.app.l_pesa.API.BaseService
import com.app.l_pesa.API.RetrofitHelper
import com.app.l_pesa.common.CommonMethod
import com.app.l_pesa.registration.inter.ICallBackEmailVerify
import com.app.l_pesa.registration.model.EmailVerifyRequest
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.json.JSONObject
import retrofit2.HttpException

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

                        val jsonError = JSONObject(errorVal.response().errorBody()?.string()!!)
                        val jsonStatus = jsonError.getJSONObject("status")
                        val jsonMessage = jsonStatus.getString("message")

                        iCallBackEmailVerify.onErrorEmailVerify(jsonMessage)
                    } catch (exp: Exception) {
                        val errorMessageOBJ = CommonMethod.commonCatchBlock(exp, contextOBJ)
                        iCallBackEmailVerify.onErrorEmailVerify(errorMessageOBJ)
                    }

                })
    }
}