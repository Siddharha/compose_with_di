package com.app.l_pesa.login.model

import com.app.l_pesa.common.CommonStatusModel


/**
 * Created by Intellij Amiya on 29-01-2019.
 * A good programmer is someone who looks both ways before crossing a One-way street.
 * Kindly follow https://source.android.com/setup/code-style
 */


data class ResLogin(val status: CommonStatusModel, val data: LoginData)

data class LoginData(val user_info:UserInfo,val user_personal_info:UserPersonalInfo, val menu_services:MenuServices, val access_token:String)

data class MenuServices(val service_status:MenuServicesStatus)

data class MenuServicesStatus( val savings:Int)

data class UserInfo(val id:Int,val profile_image:String,val phone_number:String,val credit_score:Int,val mpin_password:Boolean,val register_step:String)

data class UserPersonalInfo(val title:String,val first_name:String,
                            val middle_name:String,val last_name:String,
                            val dob:String,val sex:String,val merital_status:String,
                            val profile_image:String, val email_address:String)

