package com.app.l_pesa.help.model

import com.app.l_pesa.common.CommonStatusModel

class ResHelp(val status: CommonStatusModel, val data: HelpData)

data class HelpData(val support_contact_no:String,val support_telegram_url:String,val support_whatsapp_no:String,val support_email:String)