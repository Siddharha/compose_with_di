package com.app.l_pesa.dev_options.inter

import com.app.l_pesa.dev_options.models.UserLocationUpdateResponse

interface ICallBackUserLocationUpdate {
    fun onSuccessLocationUpdate(status: UserLocationUpdateResponse.Status)
    fun onErrorLocationUpdate(message: String)
    fun onIncompleteLocationUpdate(jsonMessage: String)
    fun onFailureLocationUpdate(jsonMessage: String)

}