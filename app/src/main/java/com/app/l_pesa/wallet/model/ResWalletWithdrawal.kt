package com.app.l_pesa.wallet.model

import com.app.l_pesa.common.CommonStatusModel

class ResWalletWithdrawal(val status: CommonStatusModel, val data:Data)
data class Data(val message:String)