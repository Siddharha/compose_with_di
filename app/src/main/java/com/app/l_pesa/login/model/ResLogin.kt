package com.app.l_pesa.login.model

import android.support.annotation.Keep
import com.app.l_pesa.common.CommonStatus


/**
 * Created by Intellij Amiya on 29-01-2019.
 * A good programmer is someone who looks both ways before crossing a One-way street.
 * Kindly follow https://source.android.com/setup/code-style
 */

@Keep
data class ResLogin(val status: CommonStatus, val data: LoginData)

@Keep
data class LoginData(val user_info:UserInfo,val user_personal_info:UserPersonalInfo, val access_token:String)

@Keep
data class UserInfo(val id:Int,val profile_image:String,val phone_number:String,val credit_score:String,val register_step:String)

@Keep
data class UserPersonalInfo(val title:String,val first_name:String,
                            val middle_name:String,val last_name:String,
                            val dob:String,val sex:String,val merital_status:String,
                            val profile_image:String)

