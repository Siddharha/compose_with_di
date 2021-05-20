package com.app.l_pesa.allservices.models
import com.google.gson.annotations.SerializedName


data class SasaUserInfoResponse(
    @SerializedName("data")
    val `data`: Data,
    @SerializedName("status")
    val status: Status
) {
    data class Data(
        @SerializedName("button_info")
        val buttonInfo: ButtonInfo,
        @SerializedName("user_ids_personal_info")
        val userIdsPersonalInfo: UserIdsPersonalInfo,
        @SerializedName("user_info")
        val userInfo: UserInfo
    ) {
        data class ButtonInfo(
            @SerializedName("data")
            val `data`: Data,
            @SerializedName("link")
            val link: String,
            @SerializedName("text")
            val text: String
        ) {
            data class Data(
                @SerializedName("actual_amount")
                val actualAmount: String,
                @SerializedName("created")
                val created: String,
                @SerializedName("id")
                val id: Int,
                @SerializedName("identity_number")
                val identityNumber: String,
                @SerializedName("lpesa_amount")
                val lpesaAmount: Any,
                @SerializedName("payment_status")
                val paymentStatus: String,
                @SerializedName("provider_error_code")
                val providerErrorCode: Any,
                @SerializedName("provider_error_message")
                val providerErrorMessage: Any,
                @SerializedName("provider_transfer_amount")
                val providerTransferAmount: Any,
                @SerializedName("provider_txn_id")
                val providerTxnId: Any,
                @SerializedName("provider_type")
                val providerType: Any,
                @SerializedName("refund_extra_amount")
                val refundExtraAmount: Any,
                @SerializedName("serial_number")
                val serialNumber: String,
                @SerializedName("status")
                val status: String,
                @SerializedName("updated")
                val updated: Any,
                @SerializedName("user_id")
                val userId: Int,
                @SerializedName("valid_from")
                val validFrom: Any,
                @SerializedName("valid_upto")
                val validUpto: Any
            )
        }

        data class UserIdsPersonalInfo(
            @SerializedName("created")
            val created: String,
            @SerializedName("file_name")
            val fileName: String,
            @SerializedName("id")
            val id: Int,
            @SerializedName("id_encry")
            val idEncry: Int,
            @SerializedName("id_number")
            val idNumber: String,
            @SerializedName("id_type_unique")
            val idTypeUnique: String,
            @SerializedName("type_name")
            val typeName: String,
            @SerializedName("updated")
            val updated: String,
            @SerializedName("user_id")
            val userId: Int,
            @SerializedName("verified")
            val verified: Int
        )

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