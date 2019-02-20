package com.app.l_pesa.dashboard.inter

import com.app.l_pesa.dashboard.model.ResDashboard


/**
 * Created by Intellij Amiya on 20-02-2019.
 * A good programmer is someone who looks both ways before crossing a One-way street.
 * Kindly follow https://source.android.com/setup/code-style
 */
interface ICallBackDashboard {

    fun onSuccessDashboard(data: ResDashboard.Data)
    fun onFailureDashboard(jsonMessage: String)
}