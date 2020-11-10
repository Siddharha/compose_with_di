package com.app.l_pesa.registration.model
import com.google.gson.annotations.SerializedName


data class RegisterPageIdListResp(
    @SerializedName("data")
    val `data`: Data,
    @SerializedName("status")
    val status: Status
) {
    data class Data(
        @SerializedName("id_type_list")
        val idTypeList: List<IdType>
    ) {
        data class IdType(
            @SerializedName("id")
            val id: Int,
            @SerializedName("name")
            val name: String,
            @SerializedName("type")
            val type: String
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