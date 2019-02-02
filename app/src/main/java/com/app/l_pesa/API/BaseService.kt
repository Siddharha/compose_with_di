package com.app.l_pesa.API

import android.support.annotation.Keep
import com.app.l_pesa.login.model.ResLogin
import com.app.l_pesa.password.model.ResForgetPassword
import com.app.l_pesa.registration.model.ResRegistrationOne
import com.app.l_pesa.splash.model.ResModelCountry
import com.google.gson.JsonObject
import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

/**
 * Created by Intellij Amiya on 23-01-2019.
 * A good programmer is someone who looks both ways before crossing a One-way street.
 * Kindly follow https://source.android.com/setup/code-style
 */
interface BaseService{

    @GET("countries_list?offset=0&limit=20")
    fun countryList(): Observable<ResModelCountry>

    @POST("user/login")
    fun doLogin(@Body  request: JsonObject): Observable<ResLogin>

    @POST("forget_password")
    fun doForgetPassword(@Body  request: JsonObject): Observable<ResForgetPassword>

    @POST("user/register")
    fun doRegister(@Body request: JsonObject): Observable<ResRegistrationOne>

}


