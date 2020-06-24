package com.app.l_pesa.registration.model

data class ReqVerifyMobile(
    val country_code: String? = null,
    val phone_no: String? = null,
    val email_address: String? = null,
    val category: String? = null,
    val social_id: String? = " ",
    val device_token: String? = null,
    val platform_type: String? = null,
    val device_data: DeviceData? = null
)

data class DeviceData(
    val device_id: String? = null,
    val screen_height: String? = null,
    val screen_width: String? = null,
    val device: String? = null,
    val model: String? = null,
    val product: String? = null,
    val manufacturer: String? = null,
    val app_version: String? = null,
    val app_version_code: String? = null
)
/*

val device_id: String? = null,
val sdk: String? = null,
val imei: String? = null,
val imsi: String? = null,
val simSerial_no: String? = null,
val sim_operator_Name: String? = null,*/
