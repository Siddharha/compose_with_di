package com.app.l_pesa.calculator.presenter

import android.annotation.SuppressLint
import android.content.Context
import com.app.l_pesa.API.BaseService
import com.app.l_pesa.API.RetrofitHelper
import com.app.l_pesa.calculator.inter.ICallBackProducts
import com.app.l_pesa.common.CommonMethod
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.json.JSONObject
import retrofit2.HttpException

class PresenterCalculator {

    @SuppressLint("CheckResult")
    fun getLoanProducts(contextOBJ: Context, country_code: String,loan_type: String, callBackOBJ: ICallBackProducts) {

      RetrofitHelper.getRetrofit(BaseService::class.java).getLoanProducts(country_code,loan_type)

                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map { responseBody ->
                    responseBody
                }
                .subscribe({ response ->

                    try {
                        if (response.data!!.productList!!.size>0)
                        {
                            if(loan_type=="current_loan")
                            {
                                callBackOBJ.onSuccessCurrentLoan(response.data!!)
                            }
                            else
                            {
                                callBackOBJ.onSuccessBusinessLoan(response.data!!)
                            }

                        } else
                        {
                            if(loan_type=="current_loan")
                            {
                                callBackOBJ.onEmptyCurrentLoan()
                            }
                            else
                            {
                                callBackOBJ.onEmptyBusinessLoan()
                            }
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
                                callBackOBJ.onSessionTimeOut(jsonMessage)
                            }
                            else
                            {
                                if(loan_type=="current_loan")
                                {
                                    callBackOBJ.onErrorCurrentLoan(jsonMessage)
                                }
                                else
                                {
                                    callBackOBJ.onErrorBusinessLoan(jsonMessage)
                                }
                            }


                        }


                    } catch (exp: Exception) {
                        val errorMessageOBJ = CommonMethod.commonCatchBlock(exp, contextOBJ)
                        if(loan_type=="current_loan")
                        {
                            callBackOBJ.onErrorCurrentLoan(errorMessageOBJ)
                        }
                        else
                        {
                            callBackOBJ.onErrorBusinessLoan(errorMessageOBJ)
                        }
                    }

                })
    }
}