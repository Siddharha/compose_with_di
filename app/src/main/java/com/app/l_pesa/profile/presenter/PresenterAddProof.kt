package com.app.l_pesa.profile.presenter

import android.content.Context
import com.app.l_pesa.API.BaseService
import com.app.l_pesa.API.RetrofitHelper
import com.app.l_pesa.common.CommonMethod
import com.app.l_pesa.common.SharedPref
import com.app.l_pesa.profile.inter.ICallBackProof
import com.google.gson.JsonObject
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.json.JSONObject
import retrofit2.HttpException

class PresenterAddProof {

    fun doAddProof(contextOBJ: Context, jsonRequest: JsonObject, callBackOBJ: ICallBackProof) {

        val sharedPrefOBJ = SharedPref(contextOBJ)
        RetrofitHelper.getRetrofitToken(BaseService::class.java,sharedPrefOBJ.accessToken).doAddProof(jsonRequest)

                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map { responseBody ->
                    responseBody
                }
                .subscribe({ response ->

                    try {
                        if (response.status.isSuccess)
                        {
                            callBackOBJ.onSuccessAddProof()

                        } else
                        {
                            callBackOBJ.onFailureAddProof(response.status.message)
                        }
                    } catch (e: Exception) {

                    }
                }, { error ->
                    try {
                        val errorVal = error as HttpException



                        val jsonError   = JSONObject(errorVal.response().errorBody()?.string())
                        val jsonStatus  = jsonError.getJSONObject("status")
                        val jsonMessage = jsonStatus.getString("message")
                        callBackOBJ.onFailureAddProof(jsonMessage)


                    } catch (exp: Exception) {
                        val errorMessageOBJ = CommonMethod.commonCatchBlock(exp, contextOBJ)
                        callBackOBJ.onFailureAddProof(errorMessageOBJ)
                    }

                })
    }
}