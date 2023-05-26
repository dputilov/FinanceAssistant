package com.example.financeassistant.classes

data class FlatPaymentState (
    val title : String,
    val type: FlatPaymentOperationType,
    val summaMaximum: Double,
    val summaProgressFirst: Double,
    val summaProgressSecond: Double?,
    val lastDate: Long,
    val lastSumma: Double?,
    val summaProgressText : String,
    val summaMaximumText : String,
    val color: Int?,
    val isVisible: Boolean
)

data class FlatItem (
    val flat : Flat,
    var flatPaymentStateList: List<FlatPaymentState>? = null
) {
    var isOperationOpen: Boolean = false
}