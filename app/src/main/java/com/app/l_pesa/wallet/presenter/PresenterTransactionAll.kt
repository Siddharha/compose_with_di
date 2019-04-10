package com.app.l_pesa.wallet.presenter

import android.content.Context
import com.app.l_pesa.API.BaseService
import com.app.l_pesa.API.RetrofitHelper
import com.app.l_pesa.common.CommonMethod
import com.app.l_pesa.common.SharedPref
import com.app.l_pesa.wallet.inter.ICallBackTransaction
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.json.JSONObject
import retrofit2.HttpException

class PresenterTransactionAll {

    fun getTransactionAll(contextOBJ: Context, callBackOBJ: ICallBackTransaction) {

        val sharedPrefOBJ = SharedPref(contextOBJ)
        RetrofitHelper.getRetrofitToken(BaseService::class.java,sharedPrefOBJ.accessToken).getWalletHistory()

                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map { responseBody ->
                    responseBody
                }
                .subscribe({ response ->

                    try {
                       if (response.status!!.isSuccess)
                        {
                            if(response.data!!.savingsHistory!!.size>0)
                            {
                                callBackOBJ.onSuccessTransaction(response.data!!.savingsHistory!!)
                            }
                            else
                            {
                                callBackOBJ.onEmptyTransaction()
                            }


                        } else
                        {
                            callBackOBJ.onErrorTransaction(response.status!!.message)
                        }
                    } catch (e: Exception) {

                    }
                }, { error ->
                    try {
                        val errorVal = error as HttpException



                        val jsonError   = JSONObject(errorVal.response().errorBody()?.string())
                        val jsonStatus  = jsonError.getJSONObject("status")
                        val jsonMessage = jsonStatus.getString("message")
                        callBackOBJ.onErrorTransaction(jsonMessage)


                    } catch (exp: Exception) {
                        val errorMessageOBJ = CommonMethod.commonCatchBlock(exp, contextOBJ)
                        callBackOBJ.onErrorTransaction(errorMessageOBJ)
                    }

                })
    }
}