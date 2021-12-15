package com.app.l_pesa.user_device_data.inter

import com.app.l_pesa.user_device_data.models.UserLocationUpdateResponse

interface ICallBackUserLocationUpdate {
    fun onSuccessLocationUpdate(status: UserLocationUpdateResponse.Status)
    fun onErrorLocationUpdate(message: String)
    fun onIncompleteLocationUpdate(jsonMessage: String)
    fun onFailureLocationUpdate(jsonMessage: String)

}