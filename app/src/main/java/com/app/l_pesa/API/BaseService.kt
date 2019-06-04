package com.app.l_pesa.API


import com.app.l_pesa.dashboard.model.ResDashboard
import com.app.l_pesa.help.model.ResHelp
import com.app.l_pesa.investment.model.*
import com.app.l_pesa.loanHistory.model.*
import com.app.l_pesa.loanplan.model.ResLoanPlans
import com.app.l_pesa.loanplan.model.ResLoanApply
import com.app.l_pesa.login.model.ResLogin
import com.app.l_pesa.logout.model.ResLogout
import com.app.l_pesa.lpk.model.*
import com.app.l_pesa.notification.model.ResNotification
import com.app.l_pesa.otpview.model.ResSetOTP
import com.app.l_pesa.pin.model.ResChangeLoginPin
import com.app.l_pesa.pin.model.ResForgetPassword
import com.app.l_pesa.pin.model.ResChangePin
import com.app.l_pesa.pin.model.ResSetUpPin
import com.app.l_pesa.pinview.model.ResSetPin
import com.app.l_pesa.profile.model.*
import com.app.l_pesa.registration.model.ResRegistrationOne
import com.app.l_pesa.registration.model.ResRegistrationThree
import com.app.l_pesa.registration.model.ResRegistrationTwo
import com.app.l_pesa.splash.model.ResModelCountry
import com.app.l_pesa.wallet.model.ResWalletHistory
import com.app.l_pesa.wallet.model.ResWalletWithdrawal
import com.app.l_pesa.wallet.model.ResWalletWithdrawalHistory
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

    @POST("user/login_pin_step1")
    fun doLogin(@Body request: JsonObject): Observable<ResLogin>

    @POST("user/login_pin_step2")
    fun doCheckPin(@Body request: JsonObject): Observable<ResSetPin>

    @POST("user/login_otp_step2")
    fun doCheckOTP(@Body request: JsonObject): Observable<ResSetOTP>

    @POST("user/login_otp_resend")
    fun doResendOTP(@Body request: JsonObject): Observable<ResSetOTP>

    @GET("user/info")
    fun getUserInfo(): Observable<ResUserInfo>

    @GET("user/dashboard")
    fun getDashboard(): Observable<ResDashboard>

    @POST("forget_password")
    fun doForgetPassword(@Body request: JsonObject): Observable<ResForgetPassword>

    @POST("settings/change_pin")
    fun doChangePin(@Body request: JsonObject): Observable<ResChangePin>

    @POST("settings/setup_pin")
    fun doSetUpPin(@Body request: JsonObject): Observable<ResSetUpPin>

    @POST("settings/change_apps_pin")
    fun doChangeLoginPin(@Body request: JsonObject): Observable<ResChangeLoginPin>

    @POST("user/register")
    fun doRegister(@Body request: JsonObject): Observable<ResRegistrationOne>

    @POST("user/step_2")
    fun doRegisterTwo(@Body request: JsonObject): Observable<ResRegistrationTwo>

    @POST("user/step_3")
    fun doRegisterThree(@Body request: JsonObject): Observable<ResRegistrationThree>

    @POST("user/logout")
    fun doLogout(@Body request: JsonObject): Observable<ResLogout>

    @POST("products/list")
    fun doLoanList(@Body request: JsonObject): Observable<ResLoanPlans>

    @POST("loan/history")
    fun doLoanHistory(@Body request: JsonObject,@Query("cursors") cursors:String,@Query("from_date") from_date:String,@Query("to_date") to_date:String ): Observable<ResLoanHistoryCurrent>

    @POST("loan/cancel")
    fun doCancelLoan(@Body request: JsonObject): Observable<ResLoanCancel>

    @POST("loan/history")
    fun doLoanHistoryBusiness(@Body request: JsonObject, @Query("cursors") cursors:String,@Query("from_date") from_date:String,@Query("to_date") to_date:String): Observable<ResLoanHistoryBusiness>

    @POST("loan/paybackschedule")
    fun doPaybackSchedule(@Body request: JsonObject,@Query("cursors") cursors:String ): Observable<ResPaybackSchedule>

    @GET("investment/plans")
    fun getInvestmentPlan(): Observable<ResInvestmentPlan>

    @GET("investment/user_list")
    fun getInvestmentHistory(@Query("cursors") cursors:String,@Query("from_date") from_date:String,@Query("to_date") to_date:String): Observable<ResInvestmentHistory>

    @POST("user/personal_info")
    fun doChangePersonalInfo(@Body request: JsonObject): Observable<ResPersonalInfo>

    @POST("user/contact_info")
    fun doChangeContactInfo(@Body request: JsonObject): Observable<ResContactInfo>

    @POST("user/employment_info")
    fun doChangeEmpInfo(@Body request: JsonObject): Observable<ResEmpInfo>

    @POST("user/business_info")
    fun doChangeBusinessInfo(@Body request: JsonObject): Observable<ResBusinessInfo>

    @POST("loan/apply")
    fun doLoanApply(@Body request: JsonObject): Observable<ResLoanApply>

    @POST("user/add_proof")
    fun doAddProof(@Body request: JsonObject): Observable<ResProof>

    @POST("user/delete_proof")
    fun doDeleteProof(@Body request: JsonObject): Observable<ResProof>

    @POST("investment/apply")
    fun doInvestmentApply(@Body request: JsonObject): Observable<ResApplyInvestment>

    @POST("investment/delete")
    fun doInvestmentRemove(@Body request: JsonObject): Observable<ResInvestmentDelete>

    @POST("investment/withdrawal")
    fun doInvestmentWithdrawal(@Body request: JsonObject): Observable<ResInvestmentWithdrawal>

    @POST("investment/reinvest")
    fun doInvestmentReinvestment(@Body request: JsonObject): Observable<ResInvestmentReinvest>

    @POST("investment/exitpoint")
    fun doInvestmentExitPoint(@Body request: JsonObject): Observable<ResInvestmentExitPoint>

    @POST("lpk_savings/token_transfer")
    fun doTokenTransfer(@Body request: JsonObject): Observable<ResTokenTransfer>

    @POST("lpk_withdrawal/apply")
    fun doTokenWithdrawal(@Body request: JsonObject): Observable<ResTokenWithdrawal>

    @GET("lpk_savings/token_user_history")
    fun getTokenHistory(@Query("cursors") cursors:String,@Query("from_date") from_date:String,@Query("to_date") to_date:String): Observable<ResTransferHistory>

    @GET("lpk_savings/interest_history")
    fun getInterestHistory(@Query("cursors") cursors:String,@Query("from_date") from_date:String,@Query("to_date") to_date:String): Observable<ResInterestHistory>

    @GET("lpk_withdrawal/user_history")
    fun getWithdrawalHistory(@Query("cursors") cursors:String,@Query("from_date") from_date:String,@Query("to_date") to_date:String): Observable<ResWithdrawalHistory>

    @POST("user/wallet_address")
    fun doWalletAddress(@Body request: JsonObject): Observable<ResWalletAddress>

    @POST("lpesa_wallet/withdrawal")
    fun doWalletWithdrawal(@Body request: JsonObject): Observable<ResWalletWithdrawal>

    @GET("user/payment_history")
    fun getPaymentHistory(@Query("type_name") type_name:String,@Query("type_id") type_id:String): Observable<ResPaymentHistory>

    @GET("lpesa_wallet/history")
    fun getWalletHistory(@Query("cursors") cursors:String,@Query("from_date") from_date:String,@Query("to_date") to_date:String): Observable<ResWalletHistory>

    @GET("user/lpk_info")
    fun getInfoLPK(): Observable<ResInfoLPK>

    @POST("investment/steadyincomestatus")
    fun doInvestmentStatus(@Body request: JsonObject): Observable<ResInvestmentStatus>

    @POST("lpk_savings/unlock")
    fun doSavingsUnlock(@Body request: JsonObject): Observable<ResSavingsUnlock>

    @GET("lpesa_wallet/withdrawal_history")
    fun getWalletWithdrawalHistory(@Query("cursors") cursors:String,@Query("from_date") from_date:String,@Query("to_date") to_date:String): Observable<ResWalletWithdrawalHistory>

    @GET("user/notifications")
    fun getNotification(@Query("cursors") cursors:String): Observable<ResNotification>

    @POST("settings/help")
    fun getHelp(): Observable<ResHelp>

}


