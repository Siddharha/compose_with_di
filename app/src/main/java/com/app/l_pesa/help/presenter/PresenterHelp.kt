package com.app.l_pesa.help.presenter

import android.annotation.SuppressLint
import android.content.Context
import com.app.l_pesa.api.BaseService
import com.app.l_pesa.api.RetrofitHelper
import com.app.l_pesa.common.CommonMethod
import com.app.l_pesa.common.SharedPref
import com.app.l_pesa.help.inter.ICallBackHelp
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.json.JSONObject
import retrofit2.HttpException

class PresenterHelp {

    @SuppressLint("CheckResult")
    fun getHelp(contextOBJ: Context, callBackOBJ: ICallBackHelp)
    {
        val sharedPref=SharedPref(contextOBJ)
        RetrofitHelper.getRetrofitToken(BaseService::class.java,sharedPref.accessToken).getHelp()
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
                            callBackOBJ.onSuccessHelp(response.data)

                        }
                        else
                        {
                            callBackOBJ.onErrorHelp(response.status.message)

                        }

                    }
                    catch (e: Exception)
                    {

                    }
                }, {
                    error ->
                    try
                    {
                        val errorVal       = error as HttpException
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
                                callBackOBJ.onErrorHelp(jsonMessage)
                            }
                        }
                    }
                    catch (exp: Exception)
                    {
                        val errorMessageOBJ= CommonMethod.commonCatchBlock(exp,contextOBJ)
                        callBackOBJ.onErrorHelp(errorMessageOBJ)
                    }

                })
    }
}