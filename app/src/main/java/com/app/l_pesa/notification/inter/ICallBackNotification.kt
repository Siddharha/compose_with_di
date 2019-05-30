package com.app.l_pesa.notification.inter

import com.app.l_pesa.notification.model.ResNotification
import java.util.ArrayList

interface ICallBackNotification {

    fun onSuccessNotification(notification_history: ArrayList<ResNotification.NotificationHistory>, cursors: ResNotification.Cursors)
    fun onSuccessNotificationPaginate(notification_history: ArrayList<ResNotification.NotificationHistory>, cursors: ResNotification.Cursors)
    fun onEmptyNotification()
    fun onFailureNotification(message: String)
    fun onSessionTimeOut(message: String)
}