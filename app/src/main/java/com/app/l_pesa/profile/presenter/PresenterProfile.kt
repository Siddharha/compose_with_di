package com.app.l_pesa.profile.presenter

import android.annotation.SuppressLint
import android.content.Context
import com.app.l_pesa.api.BaseService
import com.app.l_pesa.api.RetrofitHelper
import com.app.l_pesa.common.CommonMethod
import com.app.l_pesa.common.SharedPref
import com.app.l_pesa.profile.inter.ICallBackProfileAdditionalInfo
import com.app.l_pesa.profile.inter.ICallBackProfileFinValidate
import com.google.gson.JsonObject
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.json.JSONObject
import retrofit2.HttpException

class PresenterProfile {
//getAdditionalUserInfoDropdowns
    @SuppressLint("CheckResult")
    fun setIsEmp(contextOBJ: Context, jsonRequest: JsonObject, callBackOBJ: ICallBackProfileFinValidate) {

        val sharedPrefOBJ = SharedPref(contextOBJ)
        RetrofitHelper.getRetrofitToken(BaseService::class.java,sharedPrefOBJ.accessToken).setFinStatus(jsonRequest)

                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map { responseBody ->
                    responseBody
                }
                .subscribe({ response ->

                    try {
                        if (response.status.isSuccess)
                        {
                            callBackOBJ.onSuccessIsEmp(response.status.message)

                        } else
                        {
                            callBackOBJ.onFailureIsEmp(response.status.message)
                        }
                    } catch (e: Exception) {

                    }
                }, { error ->
                    try
                    {
                        val errorVal         =    error as HttpException
                        if(errorVal.code()>=400)
                        {
                            val jsonError        =    JSONObject(errorVal.response()?.errorBody()?.string()!!)
                            val  jsonStatus      =    jsonError.getJSONObject("status")
                            val jsonMessage      =    jsonStatus.getString("message")
                            val jsonStatusCode   =    jsonStatus.getInt("statusCode")

                            if(jsonStatusCode==50002)
                            {
                                callBackOBJ.onSessionTimeOut(jsonMessage)
                            }
                            else
                            {
                                callBackOBJ.onFailureHasBusiness(jsonMessage)
                            }

                        }

                    } catch (exp: Exception) {
                        val errorMessageOBJ = CommonMethod.commonCatchBlock(exp, contextOBJ)
                        callBackOBJ.onFailureHasBusiness(errorMessageOBJ)
                    }

                })
    }

    @SuppressLint("CheckResult")
    fun setHasBusiness(contextOBJ: Context, jsonRequest: JsonObject, callBackOBJ: ICallBackProfileFinValidate) {

        val sharedPrefOBJ = SharedPref(contextOBJ)
        RetrofitHelper.getRetrofitToken(BaseService::class.java,sharedPrefOBJ.accessToken).setFinStatus(jsonRequest)

            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .map { responseBody ->
                responseBody
            }
            .subscribe({ response ->

                try {
                    if (response.status.isSuccess)
                    {
                        callBackOBJ.onSuccessHasBusiness(response.status.message)

                    } else
                    {
                        callBackOBJ.onFailureHasBusiness(response.status.message)
                    }
                } catch (e: Exception) {

                }
            }, { error ->
                try
                {
                    val errorVal         =    error as HttpException
                    if(errorVal.code()>=400)
                    {
                        val jsonError        =    JSONObject(errorVal.response()?.errorBody()?.string()!!)
                        val  jsonStatus      =    jsonError.getJSONObject("status")
                        val jsonMessage      =    jsonStatus.getString("message")
                        val jsonStatusCode   =    jsonStatus.getInt("statusCode")

                        if(jsonStatusCode==50002)
                        {
                            callBackOBJ.onSessionTimeOut(jsonMessage)
                        }
                        else
                        {
                            callBackOBJ.onFailureHasBusiness(jsonMessage)
                        }

                    }

                } catch (exp: Exception) {
                    val errorMessageOBJ = CommonMethod.commonCatchBlock(exp, contextOBJ)
                    callBackOBJ.onFailureHasBusiness(errorMessageOBJ)
                }

            })
    }

    @SuppressLint("CheckResult")
    fun getAdditionalUserInfoDropdowns(contextOBJ: Context, callBackOBJ: ICallBackProfileAdditionalInfo) {

        val sharedPrefOBJ = SharedPref(contextOBJ)
        RetrofitHelper.getRetrofitToken(BaseService::class.java,sharedPrefOBJ.accessToken).getAdditionalUserInfoDropdowns()

            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .map { responseBody ->
                responseBody
            }
            .subscribe({ response ->

                    try {
                        if (response.status.isSuccess)
                        {
                            callBackOBJ.onSuccessAdditionalInfo(response.data)

                        } else
                        {
                            callBackOBJ.onFailureAdditionalInfo(response.status.message)
                        }
                    } catch (e: Exception) {

                    }
            }, { error ->
                try
                {
                    val errorVal         =    error as HttpException
                    if(errorVal.code()>=400)
                    {
                        val jsonError        =    JSONObject(errorVal.response()?.errorBody()?.string()!!)
                        val  jsonStatus      =    jsonError.getJSONObject("status")
                        val jsonMessage      =    jsonStatus.getString("message")
                        val jsonStatusCode   =    jsonStatus.getInt("statusCode")

                        if(jsonStatusCode==50002)
                        {
                            callBackOBJ.onSessionTimeOut(jsonMessage)
                        }
                        else
                        {
                            callBackOBJ.onFailureAdditionalInfo(jsonMessage)
                        }

                    }

                } catch (exp: Exception) {
                    val errorMessageOBJ = CommonMethod.commonCatchBlock(exp, contextOBJ)
                    callBackOBJ.onFailureAdditionalInfo(errorMessageOBJ)
                }

            })
    }

}