package com.app.l_pesa.pin.model


import com.app.l_pesa.common.CommonStatusModel

/**
 * Created by Intellij Amiya on 2/2/19.
 *  Who Am I- https://stackoverflow.com/users/3395198/
 * A good programmer is someone who looks both ways before crossing a One-way street.
 * Kindly follow https://source.android.com/setup/code-style
 */

class ResForgetPassword(val status: CommonStatusModel, val data: PinData)

data class PinData(val post_data: PostData, val access_phone:String, val next_step:String)

data class PostData(val phone_no:String,val country_code:String,val platform_type:String,val device_token:String, val device_data: DeviceData)

data class DeviceData(val device_id:String,val sdk:String,val imei:String,val imsi:String,val simSerial_no:String,val sim_operator_Name:String,
                      val screen_height:String,val screen_width:String,val device:String,val model:String,val product:String,val manufacturer:String)