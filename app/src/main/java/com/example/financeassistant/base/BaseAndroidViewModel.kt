package com.example.financeassistant.base

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import com.example.financeassistant.app.FinanceAssistantApp
import com.example.financeassistant.classes.BROADCAST_ACTION
import com.example.financeassistant.classes.BROADCAST_ACTION_TYPE
import com.example.financeassistant.classes.BROADCAST_SEND_FROM
import com.example.financeassistant.classes.BroadcastActionType
import com.example.financeassistant.injection.component.AndroidViewModelInjector
import com.example.financeassistant.main_window.FinanceManager
import com.google.gson.Gson

abstract class BaseAndroidViewModel(application: Application, daggerInjectConstructorParameter: Any? = null) : AndroidViewModel(application) {
    init {
        val appComponent = getApplication<FinanceAssistantApp>().appComponent
        AndroidViewModelInjector.inject(this, appComponent, daggerInjectConstructorParameter)
    }

    private val groupActionBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            intent?.also { intent ->
                // If broadcast send from this class ignore it!
                if (intent.getStringExtra(BROADCAST_SEND_FROM) == getModuleName()) {
                    return
                }
////                // If new group data is null ignore any updates!
////                val receivedGroup = intent.getStringExtra(BR_GROUP_ENTITY)?.let { encodedGroup ->
////                    Gson().fromJson(encodedGroup, Group::class.java)
////                } ?: return

                val action =
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
                        intent.getSerializableExtra(BROADCAST_ACTION_TYPE) as BroadcastActionType
                    } else {
                        intent.getSerializableExtra(BROADCAST_ACTION_TYPE, BroadcastActionType::class.java) as BroadcastActionType
                    }

                onGroupBroadcastReceive(action)
            }
        }
    }

    open fun onGroupBroadcastReceive(action: BroadcastActionType) {
    }

    fun registerGroupBroadcastReceiver() {
        getApplication<FinanceAssistantApp>().registerReceiver(groupActionBroadcastReceiver, IntentFilter(BROADCAST_ACTION))
    }

    fun unregisterGroupBroadcastReceiver() {
        getApplication<FinanceAssistantApp>().unregisterReceiver(groupActionBroadcastReceiver)
    }

    fun getModuleName() : String? {
        return this::class.simpleName
    }

    fun sendGroupBroadcastMessage(action: BroadcastActionType) {
        val intent = Intent(BROADCAST_ACTION)
        intent.putExtra(BROADCAST_SEND_FROM, getModuleName())
        intent.putExtra(BROADCAST_ACTION_TYPE, action)
//        intent.putExtra(BROADCAST_ENTITY, Gson().toJson(group))
        getApplication<FinanceAssistantApp>().sendBroadcast(intent)
    }

}