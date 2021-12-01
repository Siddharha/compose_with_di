package com.app.l_pesa.profile.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.util.*

class ResUserInfo {

    @SerializedName("status")
    @Expose
    var status: Status? = null
    @SerializedName("data")
    @Expose
    var data: Data? = null

    inner class Data {

        @SerializedName("business_info")
        @Expose
        var businessInfo: Boolean? = true

        @SerializedName("employee_info")
        @Expose
        var employeeInfo: Boolean? = true

        @SerializedName("user_info")
        @Expose
        var userInfo: UserInfo? = null
        @SerializedName("user_contact_info")
        @Expose
        var userContactInfo: UserContactInfo? = null
        @SerializedName("user_business_info")
        @Expose
        var userBusinessInfo: UserBusinessInfo? = null
        @SerializedName("user_employment_info")
        @Expose
        var userEmploymentInfo: UserEmploymentInfo? = null
        @SerializedName("user_personal_info")
        @Expose
        var userPersonalInfo: UserPersonalInfo? = null
        @SerializedName("user_ids_personal_info")
        @Expose
        var userIdsPersonalInfo: ArrayList<UserIdsPersonalInfo>? = null
        @SerializedName("user_ids_business_info")
        @Expose
        var userIdsBusinessInfo: ArrayList<UserIdsBusinessInfo>? = null

        @SerializedName("total_statements")
        @Expose
        var totalStatements: Int?=0



    }

    inner class Status {

        @SerializedName("statusCode")
        @Expose
        var statusCode: Int = 0
        @SerializedName("isSuccess")
        @Expose
        var isSuccess: Boolean = false
        @SerializedName("message")
        @Expose
        var message: String = ""

    }

    inner class UserBusinessInfo {

        @SerializedName("id")
        @Expose
        var id: Int = 0
        @SerializedName("business_name")
        @Expose
        var businessName: String = ""
        @SerializedName("tin_number")
        @Expose
        var tinNumber: String = ""
        @SerializedName("id_type")
        @Expose
        var idType: String = ""
        @SerializedName("id_number")
        @Expose
        var idNumber: String = ""

    }

    inner class UserContactInfo {

        @SerializedName("id")
        @Expose
        var id: Int = 0
        @SerializedName("street_address")
        @Expose
        var streetAddress: String = ""
        @SerializedName("postal_address")
        @Expose
        var postalAddress: String = ""
        @SerializedName("city")
        @Expose
        var city: String = ""
        @SerializedName("phone_number")
        @Expose
        var phoneNumber: String = ""

    }

    inner class UserEmploymentInfo {

        @SerializedName("id")
        @Expose
        var id: Int = 0
        @SerializedName("employer_type")
        @Expose
        var employerType: String = ""
        @SerializedName("employer_name")
        @Expose
        var employerName: String = ""
        @SerializedName("department")
        @Expose
        var department: String = ""
        @SerializedName("position")
        @Expose
        var position: String = ""
        @SerializedName("employees_id_number")
        @Expose
        var employeesIdNumber: String = ""
        @SerializedName("city")
        @Expose
        var city: String = ""

    }

    inner class UserIdsBusinessInfo {

        @SerializedName("id")
        @Expose
        var id: Int = 0
        @SerializedName("user_id")
        @Expose
        var userId: Int = 0
        @SerializedName("id_number")
        @Expose
        var idNumber: String = ""
        @SerializedName("type_name")
        @Expose
        var typeName: String = ""
        @SerializedName("id_type_unique")
        @Expose
        var idTypeUnique: String = ""
        @SerializedName("id_type_name")
        @Expose
        var idTypeName: String = ""
        @SerializedName("file_name")
        @Expose
        var fileName: String = ""
        @SerializedName("verified")
        @Expose
        var verified: Int = 0
        @SerializedName("created")
        @Expose
        var created: String = ""

    }

    inner class UserIdsPersonalInfo {

        @SerializedName("id")
        @Expose
        var id: Int = 0
        @SerializedName("user_id")
        @Expose
        var userId: Int = 0
        @SerializedName("id_number")
        @Expose
        var idNumber: String = ""
        @SerializedName("type_name")
        @Expose
        var typeName: String = ""
        @SerializedName("id_type_unique")
        @Expose
        var idTypeUnique: String = ""
        @SerializedName("id_type_name")
        @Expose
        var idTypeName: String = ""
        @SerializedName("file_name")
        @Expose
        var fileName: String = ""
        @SerializedName("verified")
        @Expose
        var verified: Int = 0
        @SerializedName("created")
        @Expose
        var created: String = ""

    }

    inner class UserInfo {

        @SerializedName("id")
        @Expose
        var id: Int = 0
        @SerializedName("phone_number")
        @Expose
        var phoneNumber: String = ""
        @SerializedName("credit_score")
        @Expose
        var creditScore: Int = 0
        @SerializedName("profile_image")
        @Expose
        var profileImage: String = ""
        @SerializedName("register_step")
        @Expose
        var registerStep: String = ""

    }

    inner class UserPersonalInfo {

        @SerializedName("title")
        @Expose
        var title: String = ""
        @SerializedName("first_name")
        @Expose
        var firstName: String = ""
        @SerializedName("middle_name")
        @Expose
        var middleName: String = ""
        @SerializedName("last_name")
        @Expose
        var lastName: String = ""
        @SerializedName("email_address")
        @Expose
        var emailAddress: String = ""
        @SerializedName("mother_maiden_name")
        @Expose
        var motherMaidenName: String = ""
        @SerializedName("dob")
        @Expose
        var dob: String = ""
        @SerializedName("sex")
        @Expose
        var sex: String = ""
        @SerializedName("merital_status")
        @Expose
        var meritalStatus: String = ""
        @SerializedName("profile_image")
        @Expose
        var profileImage: String = ""

        @SerializedName("email_address_verify")
        @Expose
        var emailVerify:Int = 0

        @SerializedName("profile_image_verify")
        @Expose
        var profileImageVerify:Int = 0

    }
}
