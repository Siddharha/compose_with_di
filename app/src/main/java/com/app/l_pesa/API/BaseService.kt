package com.app.l_pesa.API


import com.app.l_pesa.dashboard.model.ResDashboard
import com.app.l_pesa.loanHistory.model.ResLoanHistory
import com.app.l_pesa.loanplan.model.ResLoanPlans
import com.app.l_pesa.loanHistory.model.ResPaybackSchedule
import com.app.l_pesa.login.model.ResLogin
import com.app.l_pesa.logout.model.ResLogout
import com.app.l_pesa.password.model.ResChangePassword
import com.app.l_pesa.password.model.ResForgetPassword
import com.app.l_pesa.profile.model.ResPersonalInfo
import com.app.l_pesa.profile.model.ResUserInfo
import com.app.l_pesa.registration.model.ResRegistrationOne
import com.app.l_pesa.splash.model.ResModelCountry
import com.google.gson.JsonObject
import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

/**
 * Created by Intellij Amiya on 23-01-2019.
 * A good programmer is someone who looks both ways before crossing a One-way street.
 * Kindly follow https://source.android.com/setup/code-style
 */
interface BaseService{

    @GET("countries_list?offset=0&limit=20")
    fun getCountryList(): Observable<ResModelCountry>

    @POST("user/login")
    fun doLogin(@Body request: JsonObject): Observable<ResLogin>

    @GET("user/info")
    fun getUserInfo(): Observable<ResUserInfo>

    @GET("user/dashboard")
    fun getDashboard(): Observable<ResDashboard>

    @POST("forget_password")
    fun doForgetPassword(@Body request: JsonObject): Observable<ResForgetPassword>

    @POST("settings/change_password")
    fun doChangePassword(@Body request: JsonObject): Observable<ResChangePassword>

    @POST("user/register")
    fun doRegister(@Body request: JsonObject): Observable<ResRegistrationOne>

    @POST("user/logout")
    fun doLogout(@Body request: JsonObject): Observable<ResLogout>

    /*@POST("loan/active")
    fun doLoanList(@Body request: JsonObject): Observable<ResLoanHistory>*/

    @POST("products/list")
    fun doLoanList(@Body request: JsonObject): Observable<ResLoanPlans>

    @POST("loan/history")
    fun doLoanHistory(@Body request: JsonObject, @Query("cursors") cursors:String ): Observable<ResLoanHistory>

    @POST("loan/paybackschedule")
    fun doPaybackSchedule(@Body request: JsonObject): Observable<ResPaybackSchedule>

    @POST("user/personal_info")
    fun doChangePersonalInfo(@Body request: JsonObject): Observable<ResPersonalInfo>

}


