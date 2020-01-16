package com.app.l_pesa.dashboard.inter

import com.app.l_pesa.dashboard.model.ResDashboard


interface ICallBackDashboard {

    fun onSuccessDashboard(data: ResDashboard.Data)
    fun onSessionTimeOut(jsonMessage: String)
    fun onFailureDashboard(jsonMessage: String)
}