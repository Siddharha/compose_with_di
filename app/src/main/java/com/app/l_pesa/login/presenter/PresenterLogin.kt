package com.app.l_pesa.login.presenter

import android.annotation.SuppressLint
import android.content.Context
import com.app.l_pesa.api.BaseService
import com.app.l_pesa.api.RetrofitHelper
import com.app.l_pesa.common.CommonMethod
import com.app.l_pesa.login.inter.ICallBackLogin
import com.google.gson.JsonObject
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.json.JSONObject
import retrofit2.HttpException


class PresenterLogin {

    @SuppressLint("CheckResult")
    fun doLogin(contextOBJ: Context, jsonRequest : JsonObject, callBackOBJ: ICallBackLogin)
    {
        RetrofitHelper.getRetrofit(BaseService::class.java).doLogin(jsonRequest)
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
                           callBackOBJ.onSuccessLogin(response.data)

                      }
                       else
                      {
                           callBackOBJ.onErrorLogin(response.status.message)

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
                        val  jsonStatus           =    jsonError.getJSONObject("status")
                        val jsonMessage           =    jsonStatus.getString("message")
                        val jsonStatusCode        =    jsonStatus.getInt("statusCode")

                        if(jsonStatusCode==10008)
                        {
                            callBackOBJ.onIncompleteLogin(jsonMessage)
                        }
                        else
                        {
                            callBackOBJ.onFailureLogin(jsonMessage)
                        }


                    }
                    catch (exp: Exception)
                    {
                        val errorMessageOBJ= CommonMethod.commonCatchBlock(exp,contextOBJ)
                        callBackOBJ.onFailureLogin(errorMessageOBJ)
                    }

                })
    }

}