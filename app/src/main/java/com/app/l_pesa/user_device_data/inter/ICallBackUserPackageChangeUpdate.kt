package com.app.l_pesa.user_device_data.inter

import com.app.l_pesa.user_device_data.models.UserInstalledPackageResponse

interface ICallBackUserPackageChangeUpdate {
    fun onSuccessPackageUpdate(response: UserInstalledPackageResponse?)
    fun onErrorPackagegUpdate(message: String)
    fun onFailurePackageUpdate(jsonMessage: String)
    fun onIncompletePackageUpdate(jsonMessage: String)
}