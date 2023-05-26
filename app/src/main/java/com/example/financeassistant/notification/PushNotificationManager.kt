package com.example.financeassistant.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.os.Build
import android.provider.Settings
import androidx.core.app.NotificationCompat
import com.arenberg.eye.pushNotifications.implementation.ArendaPushNotification
import com.arenberg.eye.pushNotifications.implementation.CreditPushNotification
import com.arenberg.eye.pushNotifications.implementation.FlatCounterPushNotification
import com.arenberg.eye.pushNotifications.implementation.FlatPushNotification
import com.example.financeassistant.R
import com.example.financeassistant.classes.Task
import com.example.financeassistant.classes.TaskType
import com.example.financeassistant.main_window.FinanceManager
import com.example.financeassistant.utils.formatDate
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.text.SimpleDateFormat
import java.util.HashMap
import javax.inject.Inject


/**
 *  This provides methods to deal with the push notification setting
 */
class PushNotificationManager {

    companion object {
        val instance: PushNotificationManager by lazy { Holder.INSTANCE }

        const val PUSH_NOTIFICATION_TYPE_ID_KEY = "type"
        const val PUSH_NOTIFICATION_ID_KEY = "id"
        const val PUSH_NOTIFICATION_NAME_KEY = "name"
        const val PUSH_NOTIFICATION_DATE_KEY = "date"
        const val PUSH_NOTIFICATION_SUM_KEY = "sumId"
    }

    var context: Context? = null

    private object Holder { val INSTANCE = PushNotificationManager() }

    fun initManager(context: Context) {
        this.context = context.applicationContext
    }

    fun createNotificationChannel(pushNotification: PushNotification) {

        // Since android Oreo, a notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = pushNotification.notificationChannelId()
            val channelName = pushNotification.notificationChannelName()
            val importance = pushNotification.notificationImportance()

            val channel = NotificationChannel(channelId, channelName, importance)
            channel.setDescription(pushNotification.notificationChannelDescription())
            channel.setLockscreenVisibility(pushNotification.notificationVisibility())
            channel.enableVibration(pushNotification.notificationVibration())
            channel.enableLights(pushNotification.notificationLights())

            val audioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .setContentType(AudioAttributes.CONTENT_TYPE_UNKNOWN)
                .build()
            channel.setSound(pushNotification.notificationSoundUri(), audioAttributes)

            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            val notificationManager = context?.getSystemService(NotificationManager::class.java)
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel)
            }
        }
    }

    fun processNotificationMessage(task: Task) {
        context?.let { context ->
            val pushNotification = createPushNotification(task) ?: return

            val notificationId = task.id

            pushNotification.onRecievePushNotification()

            if (pushNotification.shouldDisplayPushNotificaiton()) {

                var text = pushNotification.notificationName()

                var title = pushNotification.notificationTitle()
                if (title.isBlank()) {
                    title = context.getString(R.string.app_name)
                }

                val pendingIntent = PendingIntent.getActivity(context, 0 /* Request code */, pushNotification.intent(),
                    PendingIntent.FLAG_ONE_SHOT)

                val notificationBuilder = NotificationCompat.Builder(context, pushNotification.notificationChannelId())
                    .setSmallIcon(R.mipmap.flat_payment_arenda)
                    .setContentTitle(title)
                    .setContentText(text)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent)
                    .setPriority(pushNotification.notificationPriority())
                    .setSound(pushNotification.notificationSoundUri())
                    .setVisibility(pushNotification.notificationVisibility())
                    .setGroup(pushNotification.notificationIdGroupKey())

                createNotificationChannel(pushNotification)

                val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
                if (notificationManager != null) {
                    notificationManager.notify(pushNotification.notificationId() /* ID of notification */, notificationBuilder.build())
                }

                // Группа
                val groupNotificationBuilder = NotificationCompat.Builder(context, pushNotification.notificationChannelId())
                    .setSmallIcon(R.mipmap.flat_payment_arenda)
                    .setContentInfo(pushNotification.notificationIdGroupName())
                    .setGroupSummary(true)
                    .setGroup(pushNotification.notificationIdGroupKey())

                createNotificationChannel(pushNotification)

                val groupNotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
                if (groupNotificationManager != null) {
                    groupNotificationManager.notify(pushNotification.groupId() /* ID of group */, groupNotificationBuilder.build())
                }
            }
        }
    }

    private fun createPushNotification(task: Task) : PushNotification? {
        return context?.let { context ->
             when (task.type) {
                TaskType.Credit -> {
                    val dateFormat = SimpleDateFormat("dd MMMM yyyy 'г.'")

                    val notificationData: Map<String, Any> = hashMapOf(
                        PUSH_NOTIFICATION_TYPE_ID_KEY to task.type.type,
                        PUSH_NOTIFICATION_ID_KEY to task.parentId,
                        PUSH_NOTIFICATION_NAME_KEY to task.name,
                        PUSH_NOTIFICATION_DATE_KEY to dateFormat.format(task.date),
                        PUSH_NOTIFICATION_SUM_KEY to task.summa.toString()
                    )

                    CreditPushNotification(context, notificationData)
                }
                TaskType.Arenda -> {

                    val notificationData: Map<String, Any> = hashMapOf(
                        PUSH_NOTIFICATION_TYPE_ID_KEY to task.type.type,
                        PUSH_NOTIFICATION_ID_KEY to task.parentId,
                        PUSH_NOTIFICATION_NAME_KEY to task.name,
                        PUSH_NOTIFICATION_DATE_KEY to formatDate(task.date),
                        PUSH_NOTIFICATION_SUM_KEY to task.summa.toString()
                    )

                    ArendaPushNotification(context, notificationData)

                }
                TaskType.Flat -> {
                    val notificationData: Map<String, Any> = hashMapOf(
                        PUSH_NOTIFICATION_TYPE_ID_KEY to task.type.type,
                        PUSH_NOTIFICATION_ID_KEY to task.parentId,
                        PUSH_NOTIFICATION_NAME_KEY to task.name,
                        PUSH_NOTIFICATION_DATE_KEY to formatDate(task.date),
                        PUSH_NOTIFICATION_SUM_KEY to task.summa.toString()
                    )

                    FlatPushNotification(context, notificationData)
                }
                TaskType.FlatCounter -> {
                    val notificationData: Map<String, Any> = hashMapOf(
                        PUSH_NOTIFICATION_TYPE_ID_KEY to task.type.type,
                        PUSH_NOTIFICATION_ID_KEY to task.parentId,
                        PUSH_NOTIFICATION_NAME_KEY to task.name,
                        PUSH_NOTIFICATION_DATE_KEY to formatDate(task.date),
                        PUSH_NOTIFICATION_SUM_KEY to task.summa.toString()
                    )

                    FlatCounterPushNotification(context, notificationData)
                }

                else -> {
                    null
                }

            }
        }
    }

}
