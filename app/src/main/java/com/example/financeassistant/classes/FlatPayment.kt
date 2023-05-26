package com.example.financeassistant.classes

import com.google.gson.annotations.SerializedName
import java.util.Date

/**
 * Created by dima on 09.03.2018.
 */

// Операции с Квартирами
data class FlatPayment(

    @SerializedName("Id")
    var id: Int = 0,

    @SerializedName("Uid")
    var uid: String = "",

    @SerializedName("Flat")
    var flat: Flat? = null,

    @SerializedName("OperationType")
    var operation: FlatPaymentOperationType = FlatPaymentOperationType.NONE,

    @SerializedName("Type")
    var paymentType: FlatPaymentType = FlatPaymentType.None, // 1- приход, -1 - расход

    @SerializedName("DateLong")
    var date: Long = 0,

    @SerializedName("Summa")
    var summa: Double = 0.0,

    @SerializedName("Comment")
    var comment: String = "",

    @SerializedName("Date")
    var dateDoc: Date? = null,

    @SerializedName("Period")
    var period: Date? = null

) {
    fun getFlatId() : Int {
        return flat?.id ?: -1
    }

    fun getFlatUid() : String {
        return flat?.uid ?: ""
    }

    fun areItemsTheSame(flatPayment: FlatPayment?): Boolean {
        return this.uid == flatPayment?.uid
    }
}