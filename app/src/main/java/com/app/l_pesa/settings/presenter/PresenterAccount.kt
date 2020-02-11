package com.app.l_pesa.settings.presenter

import android.annotation.SuppressLint
import android.content.Context
import com.app.l_pesa.API.BaseService
import com.app.l_pesa.API.RetrofitHelper
import com.app.l_pesa.common.CommonMethod
import com.app.l_pesa.common.SharedPref
import com.app.l_pesa.settings.inter.ICallBackCloseAccount
import com.google.gson.JsonObject
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.json.JSONObject
import retrofit2.HttpException

class PresenterAccount {

    @SuppressLint("CheckResult")
    fun doCloseAccount(contextOBJ: Context, jsonRequest: JsonObject, callBackOBJ: ICallBackCloseAccount) {

        val sharedPrefOBJ = SharedPref(contextOBJ)
        RetrofitHelper.getRetrofitToken(BaseService::class.java,sharedPrefOBJ.accessToken).doCloseAccount(jsonRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map { responseBody ->
                    responseBody
                }
                .subscribe({ response ->

                    try {
                        if (response.status.isSuccess)
                        {
                            callBackOBJ.onSuccessCloseAccount(response.status.message)

                        } else
                        {
                            callBackOBJ.onErrorCloseAccount(response.status.message)
                        }
                    } catch (e: Exception) {

                    }
                }, { error ->
                    try {
                        val errorVal         =    error as HttpException
                        if(errorVal.code()>=400)
                        {
                            val jsonError               =    JSONObject(errorVal.response().errorBody()?.string()!!)
                            val  jsonStatus   =    jsonError.getJSONObject("status")
                            val jsonMessage       =    jsonStatus.getString("message")
                           // val jsonStatusCode      =    jsonStatus.getInt("statusCode")

                           /* if(jsonStatusCode==50002)
                            {
                                callBackOBJ.onSessionTimeOut(jsonMessage)
                            }
                            else
                            {
                                callBackOBJ.onFailureAddProof(jsonMessage)
                            }*/
                            callBackOBJ.onErrorCloseAccount(jsonMessage)

                        }


                    } catch (exp: Exception) {
                        val errorMessageOBJ = CommonMethod.commonCatchBlock(exp, contextOBJ)
                        callBackOBJ.onErrorCloseAccount(errorMessageOBJ)
                    }

                })
    }
}