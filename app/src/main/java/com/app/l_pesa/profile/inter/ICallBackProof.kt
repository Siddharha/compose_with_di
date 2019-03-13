package com.app.l_pesa.profile.inter

interface ICallBackProof {

    fun onSuccessAddProof()
    fun onFailureAddProof(message: String)

    fun onSuccessDeleteProof(position: Int)
    fun onFailureDeleteProof(message: String)
}