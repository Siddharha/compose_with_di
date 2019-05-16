package com.app.l_pesa.password.presenter

import android.content.Context
import com.app.l_pesa.API.BaseService
import com.app.l_pesa.API.RetrofitHelper
import com.app.l_pesa.common.CommonMethod
import com.app.l_pesa.common.SharedPref
import com.app.l_pesa.login.model.LoginData
import com.app.l_pesa.password.inter.ICallBackPassword
import com.app.l_pesa.settings.inter.ICallBackResetPassword
import com.google.gson.Gson
import com.google.gson.JsonObject
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.json.JSONObject
import retrofit2.HttpException


/**
 * Created by Intellij Amiya on 2/2/19.
 *  Who Am I- https://stackoverflow.com/users/3395198/
 * A good programmer is someone who looks both ways before crossing a One-way street.
 * Kindly follow https://source.android.com/setup/code-style
 */
class PresenterPassword {

    fun doForgetPassword(contextOBJ: Context, jsonRequest: JsonObject, callBackOBJ: ICallBackPassword) {
        RetrofitHelper.getRetrofit(BaseService::class.java).doForgetPassword(jsonRequest)

                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map { responseBody ->
                    responseBody
                }
                .subscribe({ response ->

                    try {
                        if (response.status.isSuccess) {
                            callBackOBJ.onSuccessResetPassword(response.status.message,response.data.type)

                        } else {
                            callBackOBJ.onErrorResetPassword(response.status.message)
                        }
                    } catch (e: Exception) {

                    }
                }, { error ->
                    try {
                        val errorVal = error as HttpException

                        val jsonError = JSONObject(errorVal.response().errorBody()?.string())
                        val jsonStatus = jsonError.getJSONObject("status")
                        val jsonMessage = jsonStatus.getString("message")

                        callBackOBJ.onErrorResetPassword(jsonMessage)
                    } catch (exp: Exception) {
                        val errorMessageOBJ = CommonMethod.commonCatchBlock(exp, contextOBJ)
                        callBackOBJ.onErrorResetPassword(errorMessageOBJ)
                    }

                })
    }

    fun doResetPassword(contextOBJ: Context, jsonRequest: JsonObject, callBackOBJ: ICallBackResetPassword)
    {

        val sharedPrefOBJ= SharedPref(contextOBJ)
        val userData = Gson().fromJson<LoginData>(sharedPrefOBJ.userInfo, LoginData::class.java)

        RetrofitHelper.getRetrofitToken(BaseService::class.java,userData.access_token).doChangePassword(jsonRequest)

                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map { responseBody ->
                    responseBody
                }
                .subscribe({ response ->

                    try {
                        if (response.status.isSuccess)
                        {
                            callBackOBJ.onSuccessResetPassword(response.status.message)

                        } else {
                            callBackOBJ.onErrorResetPassword(response.status.message)
                        }
                    } catch (e: Exception) {

                    }
                }, { error ->
                    try {
                        val errorVal = error as HttpException

                        val jsonError = JSONObject(errorVal.response().errorBody()?.string())
                        val jsonStatus = jsonError.getJSONObject("status")
                        val jsonMessage = jsonStatus.getString("message")

                        callBackOBJ.onErrorResetPassword(jsonMessage)
                    } catch (exp: Exception) {
                        val errorMessageOBJ = CommonMethod.commonCatchBlock(exp, contextOBJ)
                        callBackOBJ.onErrorResetPassword(errorMessageOBJ)
                    }

                })
    }
}