package com.example.financeassistant.room.dao


import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.financeassistant.room.database.RoomDatabaseSettings
import com.example.financeassistant.room.entity.FlatEntity

@Dao
interface FlatDao {
    @Query("SELECT * FROM ${RoomDatabaseSettings.ROOM_DB_FLAT_TABLE}")
    fun getAll(): List<FlatEntity>

    @Query("SELECT * FROM ${RoomDatabaseSettings.ROOM_DB_FLAT_TABLE} WHERE uid IN (:flatUids)")
    fun loadAllByIds(flatUids: List<String>): List<FlatEntity>

    @Insert
    fun insertAll(flats: List<FlatEntity>)

    @Delete
    fun delete(flats: FlatEntity)
}