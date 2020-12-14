package com.app.l_pesa.zoop

import android.annotation.SuppressLint
import android.content.Context
import retrofit2.Call
import retrofit2.Response

class PresenterZoop {

    @SuppressLint("CheckResult")
    fun doOfflineAadharInit(contextOBJ: Context, callBackOBJ: ICallBackZoop) {

        val bodyInp = ZoopInitPayload("N","Testing purpose","http://54.93.191.91/zoop/endpoint")
        val apiClient = ApiClient().getClient().create(ZoopBaseService::class.java)
        val call = apiClient.doOfflineAadharInit(bodyInp)

        call.enqueue(object : retrofit2.Callback<ZoopInitResponse> {
            override fun onFailure(call: Call<ZoopInitResponse>, t: Throwable) {
                callBackOBJ.onUnknownErr(t.message!!)
            }

            override fun onResponse(call: Call<ZoopInitResponse>, response: Response<ZoopInitResponse>) {
               // val rr = response.body()
               // Log.e("resp",rr.toString())
                callBackOBJ.onSucessInit(response.body()!!)
            }
        })
    }
}