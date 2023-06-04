package com.example.financeassistant.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.financeassistant.room.database.RoomDatabaseSettings

// == Таблица Квартиры операции ==
@Entity(tableName = RoomDatabaseSettings.ROOM_DB_FLAT_TABLE)
data class FlatEntity(
    @PrimaryKey
    val uid: String,

    @ColumnInfo(name = "type")
    var type: Int,

    var name: String = "",

    var adres: String = "",

    var param: String = "",

//    var isCounter: Boolean = false,
//
//    var isPay: Boolean = false,
//
//    @ColumnInfo("is_arenda")
//    var isArenda: Boolean = false,
//
//    @ColumnInfo("day_arenda")
//    var dayArenda: Int = 0,
//
//    @ColumnInfo("summa_arenda")
//    var summaArenda: Double = 0.0,
//
//    @ColumnInfo("day_beg")
//    var dayBeg: Int = 0,
//
//    @ColumnInfo("day_end")
//    var day_end: Int = 0,
//
//    @ColumnInfo("credit_uid")
//    var creditId: String?,
//
//    @ColumnInfo("lic")
//    var lic: String? = null,
//
//    @ColumnInfo("summa")
//    var summa: Double = 0.0,
//
//    @ColumnInfo("is_finish")
//    var isFinish: Boolean = false

)