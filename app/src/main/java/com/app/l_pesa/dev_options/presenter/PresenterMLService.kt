package com.app.l_pesa.dev_options.presenter

import android.annotation.SuppressLint
import android.content.Context
import com.app.l_pesa.API.BaseService
import com.app.l_pesa.API.RetrofitHelper
import com.app.l_pesa.common.CommonMethod
import com.app.l_pesa.dev_options.inter.ICallBackUserLocationUpdate
import com.app.l_pesa.dev_options.models.UserLocationPayload
import com.app.l_pesa.login.inter.ICallBackLogin
import com.google.gson.JsonObject
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.json.JSONObject
import retrofit2.HttpException


class PresenterMLService {

    @SuppressLint("CheckResult")
    fun doUserLocationUpdate(contextOBJ: Context, userLocationPayload : UserLocationPayload, callBackOBJ: ICallBackUserLocationUpdate)
    {
        RetrofitHelper.getRetrofit(BaseService::class.java).doUpdateLocation(userLocationPayload)
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
                           callBackOBJ.onSuccessLocationUpdate(response.status)

                      }
                       else
                      {
                           callBackOBJ.onErrorLocationUpdate(response.status.message)

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
                            callBackOBJ.onIncompleteLocationUpdate(jsonMessage)
                        }
                        else
                        {
                            callBackOBJ.onFailureLocationUpdate(jsonMessage)
                        }


                    }
                    catch (exp: Exception)
                    {
                        val errorMessageOBJ= CommonMethod.commonCatchBlock(exp,contextOBJ)
                        callBackOBJ.onFailureLocationUpdate(errorMessageOBJ)
                    }

                })
    }

}