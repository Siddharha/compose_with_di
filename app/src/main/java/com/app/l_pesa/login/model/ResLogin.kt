package com.app.l_pesa.login.model

import com.app.l_pesa.common.CommonStatusModel


/**
 * Created by Intellij Amiya on 29-01-2019.
 * A good programmer is someone who looks both ways before crossing a One-way street.
 * Kindly follow https://source.android.com/setup/code-style
 */


class ResLogin(val status: CommonStatusModel, val data: PinData)

data class PinData(val post_data:PostData, val access_phone:String,val next_step:String)

data class PostData(val phone_no:String,val country_code:String,val platform_type:String,val device_token:String, val device_data:DeviceData)

data class DeviceData(val device_id:String,val sdk:String,val imei:String,val imsi:String,val simSerial_no:String,val sim_operator_Name:String,
                      val screen_height:String,val screen_width:String,val device:String,val model:String,val product:String,val manufacturer:String)

/*data class LoginData(val user_info:UserInfo,val user_personal_info:UserPersonalInfo, val menu_services:MenuServices, val access_token:String)

data class MenuServices(val service_status:MenuServicesStatus)

data class MenuServicesStatus( val savings:Int)

data class UserInfo(val id:Int,val profile_image:String,val phone_number:String,val credit_score:Int,val mpin_password:Boolean,val register_step:String)

data class UserPersonalInfo(val title:String,val first_name:String,
                            val middle_name:String,val last_name:String,
                            val dob:String,val sex:String,val merital_status:String,
                            val profile_image:String, val email_address:String)*/

