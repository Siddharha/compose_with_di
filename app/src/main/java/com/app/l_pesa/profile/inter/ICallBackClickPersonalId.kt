package com.app.l_pesa.profile.inter

import android.view.View
import com.app.l_pesa.profile.model.ResUserInfo

interface ICallBackClickPersonalId {

    fun onClickIdList(userIdsPersonalInfo: ResUserInfo.UserIdsPersonalInfo, position: Int, it: View)
}