package com.example.financeassistant.classes

import com.example.financeassistant.database.DB.Companion.UNDEFINED_ID
import com.google.gson.annotations.SerializedName
import java.util.Date

/**
 * Created by dima on 09.03.2018.
 */

enum class HomeType(val type: Int, val title: String){
    @SerializedName("-1")
    None(-1, "<Пусто>"),

    @SerializedName("0")
    Flat(0, "Квартира"),

    @SerializedName("1")
    Parking(1, "Паркинг"),

    @SerializedName("2")
    Building(2, "Гараж"),

    @SerializedName("3")
    Automobile(3, "Автомобиль");


    companion object {
        fun getById(id: Int): HomeType {
            for (e in values()) {
                if (e.type.equals(id)) return e
            }
            return None
        }

        fun getHomeTypeList(): List<HomeType> {
            return listOf(None, Flat, Parking, Building, Automobile)
        }
    }

}

data class Flat(
    @SerializedName("Id")
    var id: Int = UNDEFINED_ID,

    @SerializedName("Type")
    var type: HomeType = HomeType.None,

    @SerializedName("Name")
    var name: String = "",

    @SerializedName("Address")
    var adres: String = "",

    @SerializedName("Parameters")
    var param: String = "",

    @SerializedName("IsCounter")
    var isCounter: Boolean = false,

    @SerializedName("IsPay")
    var isPay: Boolean = false,

    @SerializedName("IsArenda")
    var isArenda: Boolean = false,

    @SerializedName("DayArenda")
    var dayArenda: Int = 0,

    @SerializedName("SummaArenda")
    var summaArenda: Double = 0.0,

    @SerializedName("CounterDayStart")
    var dayStart: Int = 0,

    @SerializedName("CounterDayEnd")
    var dayEnd: Int = 0,

    @SerializedName("CreditId")
    var credit_id: Int = 0,

    @SerializedName("LicenceNumber")
    var lic: String? = null,

    @SerializedName("Summa")
    var summa: Double = 0.0,

    @SerializedName("IsFinish")
    var isFinish: Boolean = false,

    @SerializedName("Foto")
    var foto: ByteArray? = null,

    @SerializedName("SummaFinRes")
    var summaFinRes: Double = 0.0,

    @SerializedName("Uid")
    var uid: String = "",

    @SerializedName("Credit")
    var credit: Credit? = null,

    @SerializedName("IssueYear")
    var issueYear: Int? = 0,

    @SerializedName("PurchaseDate")
    var purchaseDate: Date? = null,

    @SerializedName("IsPhoto")
    var isPhoto: Boolean = false,

    @SerializedName("SourceImage")
    var sourceImage: SourceImage? = null,

    @SerializedName("CreditUid")
    var creditUid: String? = "",

) : ServerResponse() {

    fun isNew() : Boolean = (id == UNDEFINED_ID)

    fun areItemsTheSame(flat: Flat?): Boolean {
        return this.uid == flat?.uid
    }

    fun areContentsTheSame(flat: Flat?): Boolean {
        return this.areItemsTheSame(flat)
            && this.name == flat?.name
            && this.adres == flat?.adres
            && this.param == flat?.param
            && this.summa == flat?.summa
            && this.isFinish == flat?.isFinish
    }
}