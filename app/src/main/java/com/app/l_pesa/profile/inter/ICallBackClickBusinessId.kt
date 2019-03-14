package com.app.l_pesa.profile.inter

import android.view.View
import com.app.l_pesa.profile.model.ResUserInfo


interface ICallBackClickBusinessId {

    fun onClickIdList(userIdsBusinessInfo: ResUserInfo.UserIdsBusinessInfo, position: Int, it: View)
    fun onSelectIdType(id: Int, name: String, type: String)
}