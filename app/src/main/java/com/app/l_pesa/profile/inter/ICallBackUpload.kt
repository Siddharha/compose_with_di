package com.app.l_pesa.profile.inter

import com.app.l_pesa.profile.model.ResUserInfo

interface ICallBackUpload {

    fun onSuccessUploadAWS(url: String)
    fun onFailureUploadAWS(string: String)
    fun onSucessDeleteUploadAWS(UserIdsPersonalInfo: ResUserInfo.UserIdsPersonalInfo, pos:Int)
    fun onFailureDeleteAWS(message:String)
    fun onSucessProfileImgDeleteAWS()
}