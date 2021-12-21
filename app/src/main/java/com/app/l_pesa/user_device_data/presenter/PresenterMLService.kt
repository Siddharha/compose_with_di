package com.app.l_pesa.user_device_data.presenter

import android.annotation.SuppressLint
import android.content.Context
import com.app.l_pesa.api.BaseService
import com.app.l_pesa.api.RetrofitHelper
import com.app.l_pesa.common.CommonMethod
import com.app.l_pesa.common.SharedPref
import com.app.l_pesa.user_device_data.inter.ICallBackUserCallLogUpdate
import com.app.l_pesa.user_device_data.inter.ICallBackUserLocationUpdate
import com.app.l_pesa.user_device_data.inter.ICallBackUserPackageChangeUpdate
import com.app.l_pesa.user_device_data.inter.ICallBackUserSMSUpdate
import com.app.l_pesa.user_device_data.models.UserCallLogPayload
import com.app.l_pesa.user_device_data.models.UserLocationPayload
import com.app.l_pesa.user_device_data.models.UserSMSPayload
import com.google.gson.JsonObject
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.json.JSONObject
import retrofit2.HttpException


class PresenterMLService {

    @SuppressLint("CheckResult")
    fun doUserLocationUpdate(contextOBJ: Context, userLocationPayload : UserLocationPayload, callBackOBJ: ICallBackUserLocationUpdate)
    {
        val sharedPrefOBJ = SharedPref(contextOBJ)
        RetrofitHelper.getRetrofitToken(BaseService::class.java,sharedPrefOBJ.accessToken).doUpdateLocation(userLocationPayload)
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

    @SuppressLint("CheckResult")
    fun doUserSMSUpdate(contextOBJ: Context, userSMSPayload : UserSMSPayload, callBackOBJ: ICallBackUserSMSUpdate)
    {
        val sharedPrefOBJ = SharedPref(contextOBJ)
        RetrofitHelper.getRetrofitToken(BaseService::class.java,sharedPrefOBJ.accessToken).doUpdateSMS(userSMSPayload)
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
                            callBackOBJ.onSuccessSMSUpdate(response.status)

                        }
                        else
                        {
                            callBackOBJ.onErrorSMSUpdate(response.status.message)

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
                            callBackOBJ.onIncompleteSMSUpdate(jsonMessage)
                        }
                        else
                        {
                            callBackOBJ.onFailureSMSUpdate(jsonMessage)
                        }


                    }
                    catch (exp: Exception)
                    {
                        val errorMessageOBJ= CommonMethod.commonCatchBlock(exp,contextOBJ)
                        callBackOBJ.onFailureSMSUpdate(errorMessageOBJ)
                    }

                })
    }

    //doUpdateUserInstalledApp
    @SuppressLint("CheckResult")
    fun doUserCallLogUpdate(contextOBJ: Context, userCallLogPayload: UserCallLogPayload, callBackOBJ: ICallBackUserCallLogUpdate)
    {
        val sharedPrefOBJ = SharedPref(contextOBJ)
        RetrofitHelper.getRetrofitToken(BaseService::class.java,sharedPrefOBJ.accessToken).doUpdateCallLog(userCallLogPayload)
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
                        callBackOBJ.onSuccessCalLogUpdate(response)

                    }
                    else
                    {
                        callBackOBJ.onErrorCalLogUpdate(response.status.message)

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
                        callBackOBJ.onIncompleteCalLogUpdate(jsonMessage)
                    }
                    else
                    {
                        callBackOBJ.onFailureCalLogUpdate(jsonMessage)
                    }


                }
                catch (exp: Exception)
                {
                    val errorMessageOBJ= CommonMethod.commonCatchBlock(exp,contextOBJ)
                    callBackOBJ.onFailureCalLogUpdate(errorMessageOBJ)
                }

            })
    }

    @SuppressLint("CheckResult")
    fun doPackageUpdate(contextOBJ: Context, userPackageUpdatePayload: JsonObject, callBackOBJ: ICallBackUserPackageChangeUpdate)
    {
        val sharedPrefOBJ = SharedPref(contextOBJ)
        RetrofitHelper.getRetrofitToken(BaseService::class.java,sharedPrefOBJ.accessToken).doUpdateUserInstalledApp(userPackageUpdatePayload)
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
                        callBackOBJ.onSuccessPackageUpdate(response)

                    }
                    else
                    {
                        callBackOBJ.onErrorPackagegUpdate(response.status.message)

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
                        callBackOBJ.onIncompletePackageUpdate(jsonMessage)
                    }
                    else
                    {
                        callBackOBJ.onFailurePackageUpdate(jsonMessage)
                    }


                }
                catch (exp: Exception)
                {
                    val errorMessageOBJ= CommonMethod.commonCatchBlock(exp,contextOBJ)
                    callBackOBJ.onFailurePackageUpdate(errorMessageOBJ)
                }

            })
    }

}