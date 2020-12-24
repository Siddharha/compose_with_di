package com.app.l_pesa.zoop

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ZoopBaseService{


    @GET("offline_aadhaar_verification")
    fun doOfflineAadharInit(/*@Body bodyInp:ZoopInitPayload*/) : Call<ZoopInitResponse>
}


