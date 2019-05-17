package com.app.l_pesa.wallet.presenter

import android.content.Context
import com.app.l_pesa.API.BaseService
import com.app.l_pesa.API.RetrofitHelper
import com.app.l_pesa.common.CommonMethod
import com.app.l_pesa.common.SharedPref
import com.app.l_pesa.wallet.inter.ICallBackWallet
import com.google.gson.JsonObject
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.json.JSONObject
import retrofit2.HttpException

class PresenterWithdrawal {

    fun doAddWithdrawal(contextOBJ: Context, jsonRequest: JsonObject, callBackOBJ: ICallBackWallet) {

        val sharedPrefOBJ = SharedPref(contextOBJ)
        RetrofitHelper.getRetrofitToken(BaseService::class.java,sharedPrefOBJ.accessToken).doWalletWithdrawal(jsonRequest)

                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map { responseBody ->
                    responseBody
                }
                .subscribe({ response ->

                    try {
                        if (response.status.isSuccess)
                        {
                            callBackOBJ.onSuccessWalletWithdrawal(response.status.message)

                        } else
                        {
                            callBackOBJ.onErrorWalletWithdrawal(response.status.message)
                        }
                    } catch (e: Exception) {

                    }
                }, { error ->
                    try {
                        val errorVal = error as HttpException



                        val jsonError   = JSONObject(errorVal.response().errorBody()?.string())
                        val jsonStatus  = jsonError.getJSONObject("status")
                        val jsonMessage = jsonStatus.getString("message")
                        callBackOBJ.onErrorWalletWithdrawal(jsonMessage)


                    } catch (exp: Exception) {
                        val errorMessageOBJ = CommonMethod.commonCatchBlock(exp, contextOBJ)
                        callBackOBJ.onErrorWalletWithdrawal(errorMessageOBJ)
                    }

                })
    }
}