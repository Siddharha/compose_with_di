package com.app.l_pesa.registration.presenter

import android.annotation.SuppressLint
import android.content.Context
import com.app.l_pesa.API.BaseService
import com.app.l_pesa.API.RetrofitHelper
import com.app.l_pesa.common.CommonMethod
import com.app.l_pesa.registration.inter.ICallBackRegisterFour
import com.app.l_pesa.registration.inter.ICallBackRegisterOne
import com.app.l_pesa.registration.inter.ICallVerifyCode
import com.app.l_pesa.registration.model.ReqVerifyCode
import com.google.gson.JsonObject
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.json.JSONObject
import retrofit2.HttpException


class PresenterRegistrationFour {

    @SuppressLint("CheckResult")
    fun doGetIdList(contextOBJ: Context, countryCode : String, callBackOBJ: ICallBackRegisterFour) {
        RetrofitHelper.getRetrofit(BaseService::class.java).doGetIdList("personal","identity",countryCode)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map { responseBody ->
                    responseBody
                }
                .subscribe({ response ->

                    try {
                        if (response.status.isSuccess) {
                            callBackOBJ.onSuccessIdListResp(response.data.idTypeList)
                        } else {
                            callBackOBJ.onErrorIdListResp(response.status.message)
                        }

                    } catch (e: Exception) {

                    }
                }, { error ->
                    try {
                        val errorVal = error as HttpException

                        val jsonError = JSONObject(errorVal.response()?.errorBody()?.string()!!)
                        val jsonStatus = jsonError.getJSONObject("status")
                        val jsonMessage = jsonStatus.getString("message")

                        callBackOBJ.onErrorIdListResp(jsonMessage)
                    } catch (exp: Exception) {
                        val errorMessageOBJ = CommonMethod.commonCatchBlock(exp, contextOBJ)
                        callBackOBJ.onErrorIdListResp(errorMessageOBJ)
                    }

                })
    }


}