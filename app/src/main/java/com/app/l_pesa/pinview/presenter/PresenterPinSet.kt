package com.app.l_pesa.pinview.presenter

import android.content.Context
import com.app.l_pesa.API.BaseService
import com.app.l_pesa.API.RetrofitHelper
import com.app.l_pesa.common.CommonMethod
import com.app.l_pesa.pinview.inter.ICallBackPinSet
import com.google.gson.JsonObject
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.json.JSONObject
import retrofit2.HttpException

class PresenterPinSet {

    fun dosetPin(contextOBJ: Context, jsonRequest: JsonObject, callBackOBJ: ICallBackPinSet) {

        RetrofitHelper.getRetrofit(BaseService::class.java).doCheckPin(jsonRequest)

                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map { responseBody ->
                    responseBody
                }
                .subscribe({ response ->

                    try {
                        if (response.status.isSuccess) {
                            callBackOBJ.onSuccessPinSet()

                        } else {
                            callBackOBJ.onErrorPinSet(response.status.message)
                        }
                    } catch (e: Exception) {

                    }
                }, { error ->
                    try {
                        val errorVal = error as HttpException

                        val jsonError = JSONObject(errorVal.response().errorBody()?.string())
                        val jsonStatus = jsonError.getJSONObject("status")
                        val jsonMessage = jsonStatus.getString("message")

                        callBackOBJ.onErrorPinSet(jsonMessage)
                    } catch (exp: Exception) {
                        val errorMessageOBJ = CommonMethod.commonCatchBlock(exp, contextOBJ)
                        callBackOBJ.onErrorPinSet(errorMessageOBJ)
                    }

                })
    }
}