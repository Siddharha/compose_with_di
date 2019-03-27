package com.app.l_pesa.pin.presenter

import android.content.Context
import com.app.l_pesa.API.BaseService
import com.app.l_pesa.API.RetrofitHelper
import com.app.l_pesa.common.CommonMethod
import com.app.l_pesa.common.SharedPref
import com.app.l_pesa.login.model.LoginData
import com.app.l_pesa.pin.inter.ICallBackPin
import com.google.gson.Gson
import com.google.gson.JsonObject
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.json.JSONObject
import retrofit2.HttpException

class PresenterPin {

    fun doChangePin(contextOBJ: Context, jsonRequest: JsonObject, callBackOBJ: ICallBackPin) {

        val sharedPrefOBJ= SharedPref(contextOBJ)
        val userData = Gson().fromJson<LoginData>(sharedPrefOBJ.userInfo, LoginData::class.java)

        RetrofitHelper.getRetrofitToken(BaseService::class.java,userData.access_token).doChangePin(jsonRequest)

                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map { responseBody ->
                    responseBody
                }
                .subscribe({ response ->

                    try {
                        if (response.status.isSuccess)
                        {
                            callBackOBJ.onSuccessChangePin()

                        } else {
                            callBackOBJ.onFailureChangePin(response.status.message)
                        }
                    } catch (e: Exception) {

                    }
                }, { error ->
                    try {
                        val errorVal = error as HttpException

                        val jsonError = JSONObject(errorVal.response().errorBody()?.string())
                        val jsonStatus = jsonError.getJSONObject("status")
                        val jsonMessage = jsonStatus.getString("message")

                        callBackOBJ.onFailureChangePin(jsonMessage)
                    } catch (exp: Exception) {
                        val errorMessageOBJ = CommonMethod.commonCatchBlock(exp, contextOBJ)
                        callBackOBJ.onFailureChangePin(errorMessageOBJ)
                    }

                })
    }

}