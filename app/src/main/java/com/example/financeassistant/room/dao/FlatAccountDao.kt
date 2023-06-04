package com.example.financeassistant.room.dao


import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.financeassistant.room.database.RoomDatabaseSettings
import com.example.financeassistant.room.entity.FlatAccountEntity
import io.reactivex.Flowable

@Dao
interface FlatAccountDao {
    @Query("SELECT * FROM ${RoomDatabaseSettings.ROOM_DB_FLAT_ACCOUNT_TABLE}")
    fun getAll(): Flowable<List<FlatAccountEntity>>

    @Query("SELECT * FROM ${RoomDatabaseSettings.ROOM_DB_FLAT_ACCOUNT_TABLE} WHERE flat_uid IN (:uidFlatList)")
    fun getAllByFlatUid(uidFlatList: List<String>): Flowable<List<FlatAccountEntity>>

//    @Query("SELECT * FROM ${RoomDatabaseSettings.ROOM_DB_FLAT_ACCOUNT_TABLE} WHERE uid IN (:userUids)")
//    fun loadAllByIds(userUids: List<String>): List<FlatAccountEntity>

    @Insert
    fun insertAll(flatAccounts: List<FlatAccountEntity>)

    @Delete
    fun delete(flatAccounts: FlatAccountEntity)
}