package com.app.l_pesa.allservices.presenter

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import com.app.l_pesa.API.BaseService
import com.app.l_pesa.API.RetrofitHelper
import com.app.l_pesa.allservices.inter.ICallBackSasaPayment
import com.app.l_pesa.allservices.inter.ICallBackSasaUser
import com.app.l_pesa.common.CommonMethod
import com.app.l_pesa.common.SharedPref
import com.google.gson.Gson
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.json.JSONObject
import retrofit2.HttpException

class PresenterSasaDoctor {
    @SuppressLint("CheckResult")
    fun getUserInfo(contextOBJ: Context, callBackOBJ: ICallBackSasaUser) {

        val sharedPrefOBJ = SharedPref(contextOBJ)
        RetrofitHelper.getRetrofitToken(BaseService::class.java,sharedPrefOBJ.accessToken).getSasaUserInfo()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map { responseBody ->
                    responseBody

                }
                .subscribe({ response ->

                    try {
                       // Log.e("response",Gson().toJson(response))
                  //      callBackOBJ.onSuccessUserInfo(response)
                        if (response.status.isSuccess)
                        {
                            //  Log.e("response",response.data.toString())
                            callBackOBJ.onSuccessUserInfo(response.data)

                        } else
                        {
                            callBackOBJ.onErrorUserInfo(response.status.message)
                        }
                    } catch (e: Exception) {

                    }
                }, { error ->
                    try {
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
                                callBackOBJ.onErrorUserInfo(jsonMessage)
                            }


                        }
                    } catch (exp: Exception) {
                        val errorMessageOBJ = CommonMethod.commonCatchBlock(exp, contextOBJ)
                        callBackOBJ.onErrorUserInfo(errorMessageOBJ)
                    }

                })
    }

    @SuppressLint("CheckResult")
    fun doPayment(contextOBJ: Context, callBackOBJ: ICallBackSasaPayment) {

        val sharedPrefOBJ = SharedPref(contextOBJ)
        RetrofitHelper.getRetrofitToken(BaseService::class.java,sharedPrefOBJ.accessToken).doSasaDrPayment()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map { responseBody ->
                    responseBody

                }
                .subscribe({ response ->

                    try {
                     //    Log.e("response",Gson().toJson(response))
                        //      callBackOBJ.onSuccessUserInfo(response)
                        if (response.status.isSuccess)
                        {
                            //  Log.e("response",response.data.toString())
                            callBackOBJ.onSuccessPayment(response.data)

                        } else
                        {
                            callBackOBJ.onErrorUserPayment(response.status.message)
                        }
                    } catch (e: Exception) {

                    }
                }, { error ->
                    try {
                        val errorVal         =    error as HttpException
                        if(errorVal.code()>=400)
                        {
                            val jsonError        =    JSONObject(errorVal.response()?.errorBody()?.string()!!)
                            val  jsonStatus      =    jsonError.getJSONObject("status")
                            val jsonMessage      =    jsonStatus.getString("message")
                            val jsonStatusCode   =    jsonStatus.getInt("statusCode")

                            if(jsonStatusCode==50002)
                            {
                                callBackOBJ.onSessionPaymentTimeOut(jsonMessage)
                            }
                            else
                            {
                                callBackOBJ.onErrorUserPayment(jsonMessage)
                            }


                        }
                    } catch (exp: Exception) {
                        val errorMessageOBJ = CommonMethod.commonCatchBlock(exp, contextOBJ)
                        callBackOBJ.onErrorUserPayment(errorMessageOBJ)
                    }

                })
    }
}