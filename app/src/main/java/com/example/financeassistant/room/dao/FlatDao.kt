package com.example.financeassistant.room.dao


import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.financeassistant.room.base.BaseDao
import com.example.financeassistant.room.database.RoomDatabaseSettings
import com.example.financeassistant.room.entity.FlatEntity
import io.reactivex.Flowable

@Dao
interface FlatDao : BaseDao<FlatEntity>{
    @Query("SELECT * FROM ${RoomDatabaseSettings.ROOM_DB_FLAT_TABLE}")
    fun getAll(): Flowable<List<FlatEntity>>

    @Query("SELECT * FROM ${RoomDatabaseSettings.ROOM_DB_FLAT_TABLE} WHERE uid IN (:flatUids)")
    fun loadAllByUids(flatUids: List<String>): Flowable<List<FlatEntity>>

}