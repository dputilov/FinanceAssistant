package com.example.financeassistant.room.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import com.example.financeassistant.room.dao.FlatAccountDao
import com.example.financeassistant.room.dao.FlatDao
import com.example.financeassistant.room.entity.FlatAccountEntity
import com.example.financeassistant.room.entity.FlatEntity
import com.example.financeassistant.room.entity.SourceImageEntity

@Database(entities = [FlatEntity::class,
                     FlatAccountEntity::class,
                     SourceImageEntity::class],
    version = RoomDatabaseSettings.ROOM_DB_VERSION)
@TypeConverters(RoomDataBaseDataConverter::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun flatDao(): FlatDao
    abstract fun flatAccountDao(): FlatAccountDao

    companion object {
        val MIGRATION_1_2 = Migration(1, 2) {
        }
    }
}