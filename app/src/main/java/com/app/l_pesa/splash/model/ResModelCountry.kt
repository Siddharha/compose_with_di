package com.app.l_pesa.splash.model

import android.support.annotation.Keep
import com.app.l_pesa.common.CommonStatusModel


/**
 * Created by Intellij Amiya on 23-01-2019.
 * A good programmer is someone who looks both ways before crossing a One-way street.
 * Kindly follow https://source.android.com/setup/code-style
 */

@Keep
data class ResModelCountry(val status:CommonStatusModel,val data:ResModelData)
@Keep
data class ResModelData(val countries_list:ArrayList<ResModelCountryList>)
@Keep
data class ResModelCountryList(

                                val id:Int,
                                val country_name:String,
                                val language: String,
                                val code: String,
                                val country_code: String,
                                val image: String,
                                val currency_title:String,
                                val currency_code:String,
                                val dollar_value:String,
                                val is_service_available:String,
                                val dashboard_certificate_file:String,
                                val dashboard_certificate_file_2:String,
                                val status: String
                                )


/* "id": 2,
        "country_name": "Tanzania",
        "language": "Tanzania",
        "code": "tz",
        "country_code": "+255",
        "image": "language_flages/1416191311tn.jpg",
        "currency_title": "Tanzanian Shilling",
        "currency_code": "TZS",
        "dollar_value": "2231.00",
        "is_service_available": "Y",
        "dashboard_certificate_file": "1453293604_certificate_2.jpg",
        "dashboard_certificate_file_2": "1453298812_certificate_2_2.jpg",
        "status": "1"*/