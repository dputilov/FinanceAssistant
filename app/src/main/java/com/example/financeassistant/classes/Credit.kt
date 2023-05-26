package com.example.financeassistant.classes

import com.example.financeassistant.database.DB.Companion.UNDEFINED_ID
import com.google.gson.annotations.SerializedName
import java.util.Date

// Кредиты
enum class CreditType(val type: Int, val title: String){
    @SerializedName("-1")
    None(-1, "<Не задано>"),

    @SerializedName("0")
    Flat(0, "Квартира"),

    @SerializedName("1")
    Auto(1, "Автомобиль"),

    @SerializedName("2")
    Stuff(2, "Потребительский"),

    @SerializedName("3")
    Ensure(3, "Страховка"),

    @SerializedName("4")
    Parking(4, "Паркинг");

    companion object {
        fun getById(id: Int): CreditType {
            for (e in values()) {
                if (e.type.equals(id)) return e
            }
            return None
        }
    }

}

data class Credit (

    @SerializedName("Id")
    var id: Int = UNDEFINED_ID,

    @SerializedName("Type")
    var type: CreditType = CreditType.None,

    @SerializedName("Name")
    var name: String = "",

    @SerializedName("DateLong")
    var date: Long = 0,

    @SerializedName("Summa")
    var summa: Double = 0.toDouble(),

    @SerializedName("SummaPay")
    var summa_pay: Double = 0.toDouble(),

    @SerializedName("Percent")
    var procent: Double = 0.toDouble(),

    @SerializedName("Period")
    var period: Int = 0,

    @SerializedName("Finish")
    var finish: Boolean = false,

    @SerializedName("Uid")
    var uid: String ="",

    @SerializedName("Code")
    var code: String = "",

    @SerializedName("Date")
    var dateDoc: Date? = null

) : ServerResponse() {
    operator fun plusAssign(credit: Credit?) {
        credit?.also { credit ->
            this.summa += credit.summa
            this.summa_pay += credit.summa_pay
        }
    }

    fun areItemsTheSame(credit: Credit?): Boolean {
        return this.uid == credit?.uid
    }

    fun areContentsTheSame(credit: Credit?): Boolean {
        return this.areItemsTheSame(credit)
            && this.name == credit?.name
            && this.type == credit?.type
            && this.summa == credit?.summa
    }

    fun isNew() : Boolean = (id == UNDEFINED_ID)

}

fun List<Credit>.toSimpleAdapterList(): List<Map<String, Any?>> {
    val result = mutableListOf<Map<String, Any?>>()
    this.forEach {
        val map = mapOf("name" to it.name,
            "summa" to it.summa,
            "item" to it)
        result.add(map)
    }
    return result
}
