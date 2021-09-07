package com.app.l_pesa.registration.presenter

import android.annotation.SuppressLint
import android.content.Context
import com.app.l_pesa.API.BaseService
import com.app.l_pesa.API.RetrofitHelper
import com.app.l_pesa.R
import com.app.l_pesa.common.CommonMethod
import com.app.l_pesa.common.SharedPref
import com.app.l_pesa.registration.inter.ICallBackRegisterThree
import com.google.gson.JsonObject
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.json.JSONObject
import retrofit2.HttpException

class PresenterRegistrationThree {

    @SuppressLint("CheckResult")
    fun doRegistrationStepThree(contextOBJ: Context, jsonObject : JsonObject, callBackOBJ: ICallBackRegisterThree)
    {
        val sharedPref= SharedPref(contextOBJ)
        RetrofitHelper.getRetrofitToken(BaseService::class.java,sharedPref.accessToken).doRegisterThree(jsonObject)
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
                            callBackOBJ.onSuccessRegistrationThree()
                        }
                        else
                        {
                            if(response?.status?.statusCode == 40000){
                                callBackOBJ.onErrorRegistrationThree(contextOBJ.getString(R.string.unable_to_verify_id))
                            }else{
                                callBackOBJ.onErrorRegistrationThree(response.status.message)
                            }
                        }

                    }
                    catch (e: Exception)
                    {

                    }
                }, {
                    error ->
                    try
                    {
                        val errorVal       =    error as HttpException
                        val jsonError      =    JSONObject(errorVal.response()?.errorBody()?.string()!!)
                        val  jsonStatus    =    jsonError.getJSONObject("status")
                        val jsonMessage    =    jsonStatus.getString("message")

                        callBackOBJ.onErrorRegistrationThree(jsonMessage)
                    }
                    catch (exp: Exception)
                    {
                        val errorMessageOBJ= CommonMethod.commonCatchBlock(exp,contextOBJ)
                        callBackOBJ.onErrorRegistrationThree(errorMessageOBJ)
                    }

                })
    }
}