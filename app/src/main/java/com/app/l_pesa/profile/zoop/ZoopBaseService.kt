package com.app.l_pesa.profile.zoop

import org.json.JSONObject
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface ZoopBaseService{


    @POST("gateway/offline-aadhaar/init")
    fun doOfflineAadharInit(@Body bodyInp:ZoopInitPayload) : Call<ZoopInitResponse>
}


