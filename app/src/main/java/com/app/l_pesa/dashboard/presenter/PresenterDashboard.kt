package com.app.l_pesa.dashboard.presenter

import android.annotation.SuppressLint
import android.content.Context
import com.app.l_pesa.API.BaseService
import com.app.l_pesa.API.RetrofitHelper
import com.app.l_pesa.common.CommonMethod
import com.app.l_pesa.dashboard.inter.ICallBackDashboard
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.json.JSONObject
import retrofit2.HttpException


/**
 * Created by Intellij Amiya on 20-02-2019.
 * A good programmer is someone who looks both ways before crossing a One-way street.
 * Kindly follow https://source.android.com/setup/code-style
 */
class PresenterDashboard {

    @SuppressLint("CheckResult")
    fun getDashboard(contextOBJ: Context, accessToken:String,callBackOBJ: ICallBackDashboard)
    {
        RetrofitHelper.getRetrofitToken(BaseService::class.java,accessToken).getDashboard()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map { responseBody ->
                    responseBody
                }
                .subscribe({ response ->

                    try
                    {
                        if(response.status!!.isSuccess!!)
                        {

                            callBackOBJ.onSuccessDashboard(response.data!!)
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

                        val jsonError             =    JSONObject(errorVal.response().errorBody()?.string())
                        val  jsonStatus=    jsonError.getJSONObject("status")
                        val jsonMessage    =    jsonStatus.getString("message")

                        callBackOBJ.onFailureDashboard(jsonMessage)
                    }
                    catch (exp: Exception)
                    {
                        val errorMessageOBJ= CommonMethod.commonCatchBlock(exp,contextOBJ)
                        callBackOBJ.onFailureDashboard(errorMessageOBJ)
                    }

                })
    }
}