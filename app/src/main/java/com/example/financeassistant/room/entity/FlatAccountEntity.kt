package com.example.financeassistant.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.PrimaryKey
import com.example.financeassistant.classes.FlatPaymentOperationType
import com.example.financeassistant.room.database.RoomDatabaseSettings
import java.util.Date

// == Таблица Квартиры операции ==
//val CL_FLAT_ACC_FLAT_ID = "flat_id"
//val CL_FLAT_ACC_FLAT_UID = "flat_uid"
//val CL_FLAT_ACC_TYPE = "type"   //  1 - приход, -1 - расход
//val CL_FLAT_ACC_OPERATION = "operation"
//val CL_FLAT_ACC_DATE = "date"
//val CL_FLAT_ACC_PERIOD = "period"
//val CL_FLAT_ACC_SUMMA = "summa"
//val CL_FLAT_ACC_COMMENT = "comment"

@Entity(
    tableName = RoomDatabaseSettings.ROOM_DB_FLAT_ACCOUNT_TABLE,
    //foreignKeys = [ForeignKey(entity = FlatEntity::class, parentColumns = ["uid"], childColumns = ["flat_uid"], onDelete = CASCADE)]
)
data class FlatAccountEntity(
    @PrimaryKey
    val uid: String,

    @ColumnInfo(name = "flat_uid")
    val flatUid: String?,

    @ColumnInfo(name = "type")
    val type: Int?,

    @ColumnInfo(name = "operation")
    val operation: Int?,

    @ColumnInfo(name = "date")
    val date: Long?,

    @ColumnInfo(name = "date_doc")
    val dateDoc: Long?,

    @ColumnInfo(name = "period")
    val period: Long?,

    @ColumnInfo(name = "summa")
    val summa: Double?,

    @ColumnInfo(name = "comment")
    val comment: String?,

)