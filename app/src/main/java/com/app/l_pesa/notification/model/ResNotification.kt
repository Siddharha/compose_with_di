package com.app.l_pesa.notification.model

import com.app.l_pesa.common.CommonStatusModel
import java.util.*

class ResNotification(val status: CommonStatusModel, val data: Data) {


    data class Data(var notification_history: ArrayList<NotificationHistory>, val cursors:Cursors)

    data class Cursors(

            val hasNext:Boolean,
            val after:String
    )

    data class NotificationHistory(

            var id:Int,
            var message:String,
            var created:String

    )

}