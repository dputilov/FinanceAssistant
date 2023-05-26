package com.example.financeassistant.notification

import android.content.Intent
import android.net.Uri

interface PushNotification {

    fun notificationTitle(): String

    fun notificationName(): String

    fun notificationBody() : String

    fun notificationChannelId(): String

    fun notificationChannelName(): String

    fun notificationChannelDescription(): String

    fun notificationImportance(): Int

    fun notificationSoundUri(): Uri

    fun notificationVisibility(): Int

    fun notificationPriority(): Int

    fun notificationVibration(): Boolean

    fun notificationLights(): Boolean

    fun intent() : Intent

    fun startActivity()

    fun onRecievePushNotification()

    fun shouldDisplayPushNotificaiton() : Boolean

    // Group
    fun notificationIdGroupKey() : String

    fun notificationIdGroupName() : String

    fun groupId() : Int

    fun notificationId() : Int
}
