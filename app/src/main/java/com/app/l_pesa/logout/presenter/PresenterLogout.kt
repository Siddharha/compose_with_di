package com.app.l_pesa.logout.presenter

import android.annotation.SuppressLint
import android.content.Context
import com.app.l_pesa.API.BaseService
import com.app.l_pesa.API.RetrofitHelper
import com.app.l_pesa.common.CommonMethod
import com.app.l_pesa.common.SharedPref
import com.app.l_pesa.logout.inter.ICallBackLogout
import com.google.gson.JsonObject
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.json.JSONObject
import retrofit2.HttpException

/**
 * Created by Intellij Amiya on 3/2/19.
 *  Who Am I- https://stackoverflow.com/users/3395198/
 * A good programmer is someone who looks both ways before crossing a One-way street.
 * Kindly follow https://source.android.com/setup/code-style
 */
class PresenterLogout {


    @SuppressLint("CheckResult")
    fun doLogout(contextOBJ: Context, jsonRequest : JsonObject, callBackOBJ: ICallBackLogout)
    {
        val sharedPrefOBJ=SharedPref(contextOBJ)
        //val userData = Gson().fromJson<LoginData>(sharedPrefOBJ.userInfo, LoginData::class.java)

        RetrofitHelper.getRetrofitToken(BaseService::class.java,sharedPrefOBJ.accessToken).doLogout(jsonRequest)

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
                          callBackOBJ.onSuccessLogout()
                        }
                        else
                        {
                            callBackOBJ.onErrorLogout(response.status.message)
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
                            val jsonError        =    JSONObject(errorVal.response().errorBody()?.string()!!)
                            val  jsonStatus      =    jsonError.getJSONObject("status")
                            val jsonMessage      =    jsonStatus.getString("message")
                            val jsonStatusCode   =    jsonStatus.getInt("statusCode")

                            callBackOBJ.onErrorLogout(jsonMessage)
                            if(jsonStatusCode==50002)
                            {
                                callBackOBJ.onSessionTimeOut()

                            }
                            else
                            {
                                callBackOBJ.onErrorLogout(jsonMessage)
                            }
                        }


                    }
                    catch (exp: Exception)
                    {
                        val errorMessageOBJ= CommonMethod.commonCatchBlock(exp,contextOBJ)
                        callBackOBJ.onErrorLogout(errorMessageOBJ)
                    }

                })
    }
}