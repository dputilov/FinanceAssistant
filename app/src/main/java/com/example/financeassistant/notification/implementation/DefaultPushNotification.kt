package com.arenberg.eye.pushNotifications.implementation

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.core.app.NotificationCompat
import com.example.financeassistant.R
import com.example.financeassistant.classes.TaskType
import com.example.financeassistant.main_window.FinanceManager
import com.example.financeassistant.notification.PushNotification
import com.example.financeassistant.notification.PushNotificationManager.Companion.PUSH_NOTIFICATION_ID_KEY
import com.example.financeassistant.notification.PushNotificationManager.Companion.PUSH_NOTIFICATION_NAME_KEY
import com.example.financeassistant.notification.PushNotificationManager.Companion.PUSH_NOTIFICATION_TYPE_ID_KEY
import com.example.financeassistant.utils.Navigator
import com.google.gson.Gson

open class DefaultPushNotification(private val context: Context, private val notificationData: Map<String, Any>?) : PushNotification {

    override fun notificationId(): Int {
        return (notificationData?.get(PUSH_NOTIFICATION_TYPE_ID_KEY) as Int? ?: 0) * 1000 +
            (notificationData?.get(PUSH_NOTIFICATION_ID_KEY) as Int? ?: 0)
    }

    override fun groupId(): Int {
        return (notificationData?.get(PUSH_NOTIFICATION_TYPE_ID_KEY) as Int? ?: 0) * 10000
    }

    override fun notificationIdGroupName(): String {
        return TaskType.None.name
    }

    override fun notificationIdGroupKey(): String {
        return context.getString(R.string.notification_group_key_default)
    }

    override fun notificationName(): String {
        return notificationData?.get(PUSH_NOTIFICATION_NAME_KEY) as String? ?: ""
    }

    override fun notificationTitle(): String {
        return context.getString(R.string.notification_title_default)
    }

    override fun notificationBody(): String {
        return notificationData?.let {
            Gson().toJson(notificationData)
        } ?: ""
    }

    override fun notificationChannelId(): String {
        return context.getString(R.string.notification_channel_id_default)
    }

    override fun notificationChannelName(): String {
        return context.getString(R.string.app_name)
    }

    override fun notificationChannelDescription(): String {
        return ""
    }

    override fun notificationImportance(): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            NotificationManager.IMPORTANCE_HIGH
        } else 0
    }

    override fun notificationSoundUri(): Uri {
        return Settings.System.DEFAULT_NOTIFICATION_URI
    }

    override fun notificationVisibility(): Int {
        return NotificationCompat.VISIBILITY_PUBLIC
    }

    override fun notificationPriority(): Int {
        return NotificationCompat.PRIORITY_HIGH
    }

    override fun notificationVibration(): Boolean {
        return true
    }

    override fun notificationLights(): Boolean {
        return true
    }

    override fun intent() : Intent {
        val intent = Intent(context, FinanceManager::class.java)

        val extraPushNotification = Gson().toJson(notificationData)
        intent.putExtra(Navigator.EXTRA_PUSH_NOTIFICATION_KEY, extraPushNotification)

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        return intent
    }

    override fun startActivity() {

    }

    override fun onRecievePushNotification() {

    }

    override fun shouldDisplayPushNotificaiton() : Boolean{
        return true
    }

}
