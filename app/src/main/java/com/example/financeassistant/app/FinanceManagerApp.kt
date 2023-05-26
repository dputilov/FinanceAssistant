package com.example.financeassistant.app

import android.app.job.JobInfo
import android.app.job.JobInfo.Builder
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Intent
import android.util.Log
import androidx.multidex.MultiDexApplication
import com.arenberg.eye.pushNotifications.implementation.DefaultPushNotification
import com.example.financeassistant.injection.component.AppComponent
import com.example.financeassistant.injection.component.DaggerAppComponent
import com.example.financeassistant.injection.module.AppModule
import com.example.financeassistant.manager.DatabaseManager
import com.example.financeassistant.manager.SettingsManager
import com.example.financeassistant.notification.PushNotificationManager
import com.example.financeassistant.services.TaskService
import com.example.financeassistant.services.TaskService.Companion

/**
 * Application class
 */
class FinanceAssistantApp : MultiDexApplication(){

    lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()

        initDependencies()

        initManagers()

        initPushNotificationManager()

        initTaskJobScheduler()
    }

    private fun initDependencies() {
        appComponent = DaggerAppComponent.builder()
            .appModule(AppModule(this))
            .build()
    }

    private fun initManagers() {
        SettingsManager.instance.initManager(this)

        DatabaseManager.instance.initManager(this)
    }

    private fun initPushNotificationManager() {
        PushNotificationManager.instance.initManager(this)
        val defaultPushNotification = DefaultPushNotification(this, null)
        PushNotificationManager.instance.createNotificationChannel(defaultPushNotification)
    }

    private fun initTaskJobScheduler() {
        TaskService.initTaskJobScheduler(this)
    }
}