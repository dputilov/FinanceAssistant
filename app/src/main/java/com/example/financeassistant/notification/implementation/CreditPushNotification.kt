package com.arenberg.eye.pushNotifications.implementation

import android.content.Context
import com.example.financeassistant.R
import com.example.financeassistant.classes.TaskType
import com.example.financeassistant.notification.PushNotificationManager.Companion.PUSH_NOTIFICATION_ID_KEY
import com.example.financeassistant.utils.Navigator

class CreditPushNotification(private val context: Context, private val notificationData: Map<String, Any>?) : DefaultPushNotification(context, notificationData) {

    override fun notificationIdGroupName(): String {
        return TaskType.Credit.name
    }

    override fun notificationIdGroupKey(): String {
        return context.getString(R.string.notification_group_key_credit)
    }

    override fun notificationChannelId(): String {
        return context.getString(R.string.notification_channel_id_credit)
    }

    override fun onRecievePushNotification() {
    }

    override fun startActivity() {
        val pushNotificationPostId = notificationData?.get(PUSH_NOTIFICATION_ID_KEY) as String?
        pushNotificationPostId?.also { pushNotificationPostId ->
            //Navigator.navigateToPaymentActivity(context, null, pushNotificationPostId)
        }
    }

    override fun notificationTitle(): String {
        return context.getString(R.string.notification_title_credit)
    }
}
