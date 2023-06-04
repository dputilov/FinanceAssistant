package com.example.financeassistant.room.database

import com.example.financeassistant.classes.Flat
import com.example.financeassistant.classes.FlatPayment
import com.example.financeassistant.classes.FlatPaymentOperationType
import com.example.financeassistant.classes.FlatPaymentType
import com.example.financeassistant.room.entity.FlatAccountEntity
import java.util.Date

fun FlatPayment.toEntity(): FlatAccountEntity {
    return FlatAccountEntity(
        uid = this.uid,
        flatUid = this.getFlatUid(),
        summa = this.summa,
        comment = this.comment,
        type = this.paymentType.type,
        operation = this.operation.type,
        date = this.date,
        dateDoc = this.dateDoc?.time,
        period = this.period?.time,
    )
}

fun FlatAccountEntity.toFlatPayment(): FlatPayment {
    return FlatPayment(
        uid = this.uid,
        flat = Flat(uid = this.flatUid ?: ""),
        date = this.date ?: 0L,
        dateDoc = Date(this.dateDoc ?: 0L),
        period = Date(this.period ?: 0L),
        summa = this.summa ?: 0.00,
        paymentType = FlatPaymentType.getById(this.type ?: -1),
        operation = FlatPaymentOperationType.getById(this.operation ?: -1),
        comment = (this.comment ?: "") + "\nuid='${this.uid}' \nflatUid = '${this.flatUid}'"
    )
}