package com.example.financeassistant.room.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.financeassistant.room.dao.FlatAccountDao
import com.example.financeassistant.room.dao.FlatDao
import com.example.financeassistant.room.entity.FlatAccountEntity
import com.example.financeassistant.room.entity.FlatEntity

@Database(entities = [FlatEntity::class,
                     FlatAccountEntity::class],
    version = RoomDatabaseSettings.ROOM_DB_VERSION)
abstract class AppDatabase : RoomDatabase() {
    abstract fun flatDao(): FlatDao
    abstract fun flatAccountDao(): FlatAccountDao

}