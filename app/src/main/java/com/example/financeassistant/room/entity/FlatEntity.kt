package com.example.financeassistant.room.entity

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.example.financeassistant.classes.Credit
import com.example.financeassistant.classes.HomeType
import com.example.financeassistant.classes.SourceImage
import com.example.financeassistant.room.database.RoomDatabaseSettings
import com.google.gson.annotations.SerializedName
import java.util.Date

// == Таблица Квартиры операции ==
@Entity(tableName = RoomDatabaseSettings.ROOM_DB_FLAT_TABLE)
data class FlatEntity(
    @PrimaryKey
    @SerializedName("Uid")
    var uid: String,

    @ColumnInfo(name = "type")
    @SerializedName("Type")
    var type: HomeType = HomeType.None,

    @ColumnInfo("name")
    @SerializedName("Name")
    var name: String = "",

    @ColumnInfo("adres")
    @SerializedName("Address")
    var adres: String = "",

    @ColumnInfo("param")
    @SerializedName("Parameters")
    var param: String = "",

    @ColumnInfo("day_arenda")
    @SerializedName("DayArenda")
    var dayArenda: Int = 0,

    @ColumnInfo("summa_arenda")
    @SerializedName("SummaArenda")
    var summaArenda: Double = 0.0,

    @ColumnInfo("day_start")
    @SerializedName("CounterDayStart")
    var dayStart: Int = 0,

    @ColumnInfo("day_end")
    @SerializedName("CounterDayEnd")
    var dayEnd: Int = 0,

    @ColumnInfo("credit_uid")
    var creditUid: String?,

    @ColumnInfo("lic")
    @SerializedName("LicenceNumber")
    var lic: String? = null,

    @ColumnInfo("summa")
    @SerializedName("Summa")
    var summa: Double = 0.0,

    @ColumnInfo("is_counter")
    @SerializedName("IsCounter")
    var isCounter: Boolean = false,

    @ColumnInfo("is_pay")
    @SerializedName("IsPay")
    var isPay: Boolean = false,

    @ColumnInfo("is_arenda")
    @SerializedName("IsArenda")
    var isArenda: Boolean = false,

    @ColumnInfo("is_finish")
    @SerializedName("IsFinish")
    var isFinish: Boolean = false,

    @ColumnInfo("issue_year")
    @SerializedName("IssueYear")
    var issueYear: Int? = 0,

    @ColumnInfo("purchase_date")
    @SerializedName("PurchaseDate")
    var purchaseDate: Date? = null,

    @Embedded(prefix = "image_")
    @SerializedName("SourceImage")
    var sourceImage: SourceImageEntity? = null,

//    @Ignore
//    @SerializedName("SummaFinRes")
//    var summaFinRes: Double = 0.0,
//
//    @Ignore
//    @SerializedName("Credit")
//    var credit: Credit? = null,

)