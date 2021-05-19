package com.app.l_pesa.allservices.models
import com.google.gson.annotations.SerializedName


data class SasaUserInfoResponse(
    @SerializedName("data")
    val `data`: Data,
    @SerializedName("status")
    val status: Status
) {
    data class Data(
        @SerializedName("user_ids_personal_info")
        val userIdsPersonalInfo: List<Any>,
        @SerializedName("user_info")
        val userInfo: UserInfo
    ) {
        data class UserInfo(
            @SerializedName("dob")
            val dob: String,
            @SerializedName("email_address")
            val emailAddress: String,
            @SerializedName("email_address_verify")
            val emailAddressVerify: Int,
            @SerializedName("first_name")
            val firstName: String,
            @SerializedName("id")
            val id: Int,
            @SerializedName("last_name")
            val lastName: String,
            @SerializedName("merital_status")
            val meritalStatus: String,
            @SerializedName("middle_name")
            val middleName: String,
            @SerializedName("phone_number")
            val phoneNumber: String,
            @SerializedName("sex")
            val sex: String,
            @SerializedName("title")
            val title: String
        )
    }

    data class Status(
        @SerializedName("isSuccess")
        val isSuccess: Boolean,
        @SerializedName("message")
        val message: String,
        @SerializedName("statusCode")
        val statusCode: Int
    )
}