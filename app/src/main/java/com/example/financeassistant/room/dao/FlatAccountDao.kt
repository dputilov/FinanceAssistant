package com.example.financeassistant.room.dao


import androidx.room.Dao
import androidx.room.Query
import com.example.financeassistant.room.base.BaseDao
import com.example.financeassistant.room.database.RoomDatabaseSettings
import com.example.financeassistant.room.entity.FlatAccountEntity
import io.reactivex.Flowable

@Dao
interface FlatAccountDao: BaseDao<FlatAccountEntity> {

    @Query("SELECT * FROM ${RoomDatabaseSettings.ROOM_DB_FLAT_ACCOUNT_TABLE}")
    fun getAll(): Flowable<List<FlatAccountEntity>>

    @Query("SELECT * FROM ${RoomDatabaseSettings.ROOM_DB_FLAT_ACCOUNT_TABLE} WHERE flat_uid IN (:uidFlatList)")
    fun getAllByFlatUid(uidFlatList: List<String>): Flowable<List<FlatAccountEntity>>

}