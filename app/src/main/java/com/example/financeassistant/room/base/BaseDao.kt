package com.example.financeassistant.room.base

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import io.reactivex.Flowable
import io.reactivex.Maybe

@Dao
interface BaseDao<T> {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(entity: List<T>)

    @Update
    fun update(entity: List<T>)

    @Delete
    fun delete(entity: List<T>)

}