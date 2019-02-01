package com.app.l_pesa.login.model

/**
 * Created by Intellij Amiya on 01-02-2019.
 * A good programmer is someone who looks both ways before crossing a One-way street.
 * Kindly follow https://source.android.com/setup/code-style
 */

import android.support.annotation.Keep
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Keep
class LoginRequest(@SerializedName("phone_no")
                   @Expose var phoneNo: String?, @SerializedName("country_code")
                   @Expose var countryCode: String?, @SerializedName("password")
                   @Expose var password: String?, @SerializedName("platform_type")
                   @Expose var platformType: String?, @SerializedName("device_token")
                   @Expose var deviceToken: String?, @SerializedName("device_data")
                   @Expose var deviceData: DeviceData?) {

@Keep
class DeviceData(deviceId: String, sdk: String, imei: String, imsi: String, simSerialNo: String, simOperatorName: String, screenHeight: String, screenWidth: String, device: String, model: String, product: String, manufacturer: String) {

        @SerializedName("device_id")
        @Expose
        var deviceId: String? = deviceId
        @SerializedName("sdk")
        @Expose
        var sdk: String? = sdk
        @SerializedName("imei")
        @Expose
        var imei: String? = imei
        @SerializedName("imsi")
        @Expose
        var imsi: String? = imsi
        @SerializedName("simSerial_no")
        @Expose
        var simSerialNo: String? = simSerialNo
        @SerializedName("sim_operator_Name")
        @Expose
        var simOperatorName: String? = simOperatorName
        @SerializedName("screen_height")
        @Expose
        var screenHeight: String? = screenHeight
        @SerializedName("screen_width")
        @Expose
        var screenWidth: String? = screenWidth
        @SerializedName("device")
        @Expose
        var device: String? = device
        @SerializedName("model")
        @Expose
        var model: String? = model
        @SerializedName("product")
        @Expose
        var product: String? = product
        @SerializedName("manufacturer")
        @Expose
        var manufacturer: String? = manufacturer


         override fun toString(): String {
            return "DeviceData{" +
                    "deviceId='" + deviceId + '\''.toString() +
                    ", sdk='" + sdk + '\''.toString() +
                    ", imei='" + imei + '\''.toString() +
                    ", imsi='" + imsi + '\''.toString() +
                    ", simSerialNo='" + simSerialNo + '\''.toString() +
                    ", simOperatorName='" + simOperatorName + '\''.toString() +
                    ", screenHeight='" + screenHeight + '\''.toString() +
                    ", screenWidth='" + screenWidth + '\''.toString() +
                    ", device='" + device + '\''.toString() +
                    ", model='" + model + '\''.toString() +
                    ", product='" + product + '\''.toString() +
                    ", manufacturer='" + manufacturer + '\''.toString() +
                    '}'.toString()
        }
    }

    override fun toString(): String {
        return "LoginRequest(phoneNo=$phoneNo, countryCode=$countryCode, password=$password, platformType=$platformType, deviceToken=$deviceToken, deviceData=$deviceData)"
    }

}