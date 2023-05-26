package com.example.financeassistant.classes

import com.google.gson.annotations.SerializedName
import java.util.Date

data class Payment(

    @SerializedName("Id")
    var id : Int = -1,

    @SerializedName("DateLong")
    var date: Long = 0,

    @SerializedName("Summa")
    var summa: Double = 0.0,

    @SerializedName("SummaCredit")
    var summa_credit: Double = 0.0,

    @SerializedName("SummaPercent")
    var summa_procent: Double = 0.0,

    @SerializedName("SummaAddon")
    var summa_addon: Double = 0.0,

    @SerializedName("SummaPlus")
    var summa_plus: Double = 0.0,

    @SerializedName("SummaMinus")
    var summa_minus: Double = 0.0,

    @SerializedName("Done")
    var done: Int = 0,

    @SerializedName("Comment")
    var comment: String = "",

    @SerializedName("Uid")
    var uid: String = "",

    @SerializedName("Date")
    var dateDoc: Date? = null,

    @SerializedName("Credit")
    var credit: Credit? = null

) {

    var rest : Double = 0.0

    constructor(credit: Credit, date: Long, summa: Double, summa_credit: Double, summa_procent: Double, summa_addon: Double, summa_plus: Double, summa_minus: Double) : this(){
            this.credit         = credit
            this.date           = date
            this.summa          = summa
            this.summa_credit   = summa_credit
            this.summa_procent  = summa_procent
            this.summa_addon    = summa_addon
            this.summa_plus     = summa_plus

        }

    constructor(credit: Credit) : this(){
        this.credit      = credit
    }

    fun getCreditId(): Int {
        return credit?.id ?: -1
    }

    fun getCreditUid(): String {
        return credit?.uid ?: ""
    }

    operator fun plus(p: Payment) : Payment {
        val result = Payment()
        result.date = this.date
        result.rest = Math.min(this.rest , p.rest)

        result.done = Math.max(this.done, p.done)

        result.summa = this.summa + p.summa
        result.summa_credit = this.summa_credit + p.summa_credit
        result.summa_procent = this.summa_procent + p.summa_procent
        result.summa_addon = this.summa_addon + p.summa_addon
        result.summa_plus = this.summa_plus + p.summa_plus
        result.summa_minus = this.summa_minus + p.summa_minus

        return result
    }

    fun areItemsTheSame(payment: Payment?): Boolean {
        return this.uid == payment?.uid
    }
}

fun List<Payment>.groupByDate() : List<Payment> {

//    Log.d("DIAGRAM", "1 - this = ${Gson().toJson(this)}")

    return this.groupBy { hashMapOf(it.date to it.done) }
        .let {

//            Log.d("DIAGRAM", "2 - map = ${Gson().toJson(it)}")

            val reducedList = mutableListOf<Payment>()
            for((key, value) in it) {
                reducedList.add(value.reduce { acc, payment ->
                    acc + payment
                })
            }

//            Log.d("DIAGRAM", "3 - reducedList = ${Gson().toJson(reducedList)}")

            reducedList
        }

}