package com.app.l_pesa.splash.presenter

import android.annotation.SuppressLint
import android.content.Context
import com.app.l_pesa.API.BaseService
import com.app.l_pesa.API.RetrofitHelper
import com.app.l_pesa.common.CommonMethod
import com.app.l_pesa.splash.inter.ICallBackCountry
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.json.JSONObject
import retrofit2.HttpException


class PresenterCountry{

    @SuppressLint("CheckResult")
    fun getCountry(contextOBJ: Context, callBackOBJ: ICallBackCountry)
    {
        RetrofitHelper.getRetrofit(BaseService::class.java).getCountryList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map { responseBody ->
                    responseBody
                }
                .subscribe({ response ->
                    try
                    {
                        if(response.data.countries_list.size>0)
                        {
                            callBackOBJ.onSuccessCountry(response.data)
                        }
                        else
                        {
                            callBackOBJ.onEmptyCountry()
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

                        val jsonError                 =    JSONObject(errorVal.response().errorBody()?.string()!!)
                        val  jsonStatus    =    jsonError.getJSONObject("status")
                        val jsonMessage        =    jsonStatus.getString("message")

                        callBackOBJ.onFailureCountry(jsonMessage)
                    }
                    catch (exp: Exception)
                    {
                        val errorMessageOBJ= CommonMethod.commonCatchBlock(exp,contextOBJ)
                        callBackOBJ.onFailureCountry(errorMessageOBJ)
                    }

                })
    }
}