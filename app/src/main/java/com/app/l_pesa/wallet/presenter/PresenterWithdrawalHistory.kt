package com.app.l_pesa.wallet.presenter

import android.content.Context
import com.app.l_pesa.API.BaseService
import com.app.l_pesa.API.RetrofitHelper
import com.app.l_pesa.common.CommonMethod
import com.app.l_pesa.common.SharedPref
import com.app.l_pesa.wallet.inter.ICallBackWalletWithdrawalHistory
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.json.JSONObject
import retrofit2.HttpException

class PresenterWithdrawalHistory {

    fun getWithdrawalHistory(contextOBJ: Context, callBackOBJ: ICallBackWalletWithdrawalHistory) {

        val sharedPrefOBJ = SharedPref(contextOBJ)
        RetrofitHelper.getRetrofitToken(BaseService::class.java,sharedPrefOBJ.accessToken).getWalletWithdrawalHistory("")

                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map { responseBody ->
                    responseBody
                }
                .subscribe({ response ->

                    try {
                        if (response.status.isSuccess)
                        {
                            if(response.data.withdrawal_history!!.size>0)
                            {
                                callBackOBJ.onSuccessWalletWithdrawalHistory(response.data.withdrawal_history,response.data.cursors)
                            }
                            else
                            {
                                callBackOBJ.onEmptyWalletWithdrawalHistory()
                            }

                        } else
                        {
                            callBackOBJ.onErrorWalletWithdrawalHistory(response.status.message)
                        }
                    } catch (e: Exception) {

                    }
                }, { error ->
                    try {
                        val errorVal = error as HttpException



                        val jsonError   = JSONObject(errorVal.response().errorBody()?.string())
                        val jsonStatus  = jsonError.getJSONObject("status")
                        val jsonMessage = jsonStatus.getString("message")
                        callBackOBJ.onErrorWalletWithdrawalHistory(jsonMessage)


                    } catch (exp: Exception) {
                        val errorMessageOBJ = CommonMethod.commonCatchBlock(exp, contextOBJ)
                        callBackOBJ.onErrorWalletWithdrawalHistory(errorMessageOBJ)
                    }

                })
    }

    fun getWithdrawalHistoryPaginate(contextOBJ: Context, cursorAfter:String,callBackOBJ: ICallBackWalletWithdrawalHistory) {

        val sharedPrefOBJ = SharedPref(contextOBJ)
        RetrofitHelper.getRetrofitToken(BaseService::class.java,sharedPrefOBJ.accessToken).getWalletWithdrawalHistory(cursorAfter)

                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map { responseBody ->
                    responseBody
                }
                .subscribe({ response ->

                    try {
                        if (response.status.isSuccess)
                        {
                            if(response.data.withdrawal_history!!.size>0)
                            {
                                callBackOBJ.onSuccessWalletWithdrawalHistoryPaginate(response.data.withdrawal_history,response.data.cursors)
                            }


                        } else
                        {
                            callBackOBJ.onErrorWalletWithdrawalHistory(response.status.message)
                        }
                    } catch (e: Exception) {

                    }
                }, { error ->
                    try {
                        val errorVal = error as HttpException



                        val jsonError   = JSONObject(errorVal.response().errorBody()?.string())
                        val jsonStatus  = jsonError.getJSONObject("status")
                        val jsonMessage = jsonStatus.getString("message")
                        callBackOBJ.onErrorWalletWithdrawalHistory(jsonMessage)


                    } catch (exp: Exception) {
                        val errorMessageOBJ = CommonMethod.commonCatchBlock(exp, contextOBJ)
                        callBackOBJ.onErrorWalletWithdrawalHistory(errorMessageOBJ)
                    }

                })
    }
}