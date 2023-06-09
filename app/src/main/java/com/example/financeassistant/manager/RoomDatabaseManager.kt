package com.example.financeassistant.manager

import android.content.Context
import android.content.Intent
import androidx.room.Room
import com.example.financeassistant.classes.BROADCAST_ACTION
import com.example.financeassistant.classes.BROADCAST_ACTION_TYPE
import com.example.financeassistant.classes.BROADCAST_SEND_FROM
import com.example.financeassistant.classes.BroadcastActionType
import com.example.financeassistant.room.database.AppDatabase
import com.example.financeassistant.room.database.RoomDatabaseSettings

class RoomDatabaseManager {

    var context: Context? = null

    lateinit var database : AppDatabase

    private object Holder { val INSTANCE = RoomDatabaseManager() }

    companion object {
        val instance: RoomDatabaseManager by lazy { Holder.INSTANCE }
    }

    fun initManager(context: Context) {
        this.context = context.applicationContext

        database = Room.databaseBuilder(
                context,
                AppDatabase::class.java,
                RoomDatabaseSettings.ROOM_DB_NAME
            )
            .allowMainThreadQueries() // // TODO REMOVE ON PROD. Allow Main Tread Query
            .fallbackToDestructiveMigration() // TODO REMOVE ON PROD. Clear data when database has been changed
            .addMigrations(AppDatabase.MIGRATION_1_2)
            .build()
    }

    fun sendGroupBroadcastMessage(action: BroadcastActionType) {
        val intent = Intent(BROADCAST_ACTION)
        intent.putExtra(BROADCAST_SEND_FROM, "DatabaseManager")
        intent.putExtra(BROADCAST_ACTION_TYPE, action)
//        intent.putExtra(BROADCAST_ENTITY, Gson().toJson(group))
        context?.sendBroadcast(intent)
    }

}