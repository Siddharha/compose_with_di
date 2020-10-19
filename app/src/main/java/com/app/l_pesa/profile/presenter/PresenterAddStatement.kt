package com.app.l_pesa.profile.presenter

import android.annotation.SuppressLint
import android.content.Context
import com.app.l_pesa.API.BaseService
import com.app.l_pesa.API.RetrofitHelper
import com.app.l_pesa.common.CommonMethod
import com.app.l_pesa.common.SharedPref
import com.app.l_pesa.profile.inter.ICallBackProof
import com.app.l_pesa.profile.inter.ICallBackStatementUpload
import com.app.l_pesa.profile.model.statement.StatementAddPayload
import com.google.gson.JsonObject
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.json.JSONObject
import retrofit2.HttpException

class PresenterAddStatement {

    @SuppressLint("CheckResult")
    fun doAddStatement(contextOBJ: Context, jsonObject: JsonObject, callBackOBJ: ICallBackStatementUpload) {

        val sharedPrefOBJ = SharedPref(contextOBJ)
        RetrofitHelper.getRetrofitToken(BaseService::class.java,sharedPrefOBJ.accessToken).doAddStatement(jsonObject)

                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map { responseBody ->
                    responseBody
                }
                .subscribe({ response ->

                    try {
                        if (response.status.isSuccess)
                        {
                            callBackOBJ.onSucessUploadStatement()

                        } else
                        {
                            callBackOBJ.onFailureUploadStatement(response.status.message)
                        }
                    } catch (e: Exception) {

                    }
                }, { error ->
                    try {
                        val errorVal         =    error as HttpException
                        if(errorVal.code()>=400)
                        {
                            val jsonError        =    JSONObject(errorVal.response().errorBody()?.string()!!)
                            val  jsonStatus      =    jsonError.getJSONObject("status")
                            val jsonMessage      =    jsonStatus.getString("message")
                            val jsonStatusCode   =    jsonStatus.getInt("statusCode")

                            if(jsonStatusCode==50002)
                            {
                                callBackOBJ.onUploadTimeOut(jsonMessage)
                            }
                            else
                            {
                                callBackOBJ.onFailureUploadStatement(jsonMessage)
                            }


                        }


                    } catch (exp: Exception) {
                        val errorMessageOBJ = CommonMethod.commonCatchBlock(exp, contextOBJ)
                        callBackOBJ.onFailureUploadStatement(errorMessageOBJ)
                    }

                })
    }
}