package com.example.financeassistant.manager

import android.content.Context

class SettingsManager {

    var context: Context? = null

    private object Holder { val INSTANCE = SettingsManager() }

    companion object {
        const val DMS_FINANCE_MANAGER_PREFERENCES_KEY = "DMS_FINANCE_MANAGER_PREFERENCES_KEY"
        const val PREFERENCES_SHOW_CLOSE_CREDIT = "PREFERENCES_SHOW_CLOSE_CREDIT"
        const val PREFERENCES_SHOW_CLOSE_FLAT = "PREFERENCES_SHOW_CLOSE_FLAT"

        val instance: SettingsManager by lazy { Holder.INSTANCE }
    }

    fun initManager(context: Context) {
        this.context = context.applicationContext
    }

    fun setShowCloseCreditSettings(value: Boolean) {
        context?.also { context ->
            val settingPref = context.getSharedPreferences(DMS_FINANCE_MANAGER_PREFERENCES_KEY, Context.MODE_PRIVATE).edit()
            settingPref.putBoolean(PREFERENCES_SHOW_CLOSE_CREDIT, value)
            settingPref.apply()
        }
    }

    fun getShowCloseCreditSettings(): Boolean {
        return context?.let { context ->
            context.getSharedPreferences(DMS_FINANCE_MANAGER_PREFERENCES_KEY, Context.MODE_PRIVATE).getBoolean(PREFERENCES_SHOW_CLOSE_CREDIT, true)
        } ?: false
    }


    fun setShowCloseFlatSettings(value: Boolean) {
        context?.also { context ->
            val settingPref = context.getSharedPreferences(DMS_FINANCE_MANAGER_PREFERENCES_KEY, Context.MODE_PRIVATE).edit()
            settingPref.putBoolean(PREFERENCES_SHOW_CLOSE_FLAT, value)
            settingPref.apply()
        }
    }

    fun getShowCloseFlatSettings(): Boolean {
        return context?.let { context ->
            context.getSharedPreferences(DMS_FINANCE_MANAGER_PREFERENCES_KEY, Context.MODE_PRIVATE).getBoolean(PREFERENCES_SHOW_CLOSE_FLAT,true)
        } ?: false
    }

}