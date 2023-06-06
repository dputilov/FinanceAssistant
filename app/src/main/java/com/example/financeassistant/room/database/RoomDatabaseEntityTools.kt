package com.example.financeassistant.room.database

import com.example.financeassistant.classes.Credit
import com.example.financeassistant.classes.Flat
import com.example.financeassistant.classes.FlatPayment
import com.example.financeassistant.classes.SourceImage
import com.example.financeassistant.room.entity.FlatAccountEntity
import com.example.financeassistant.room.entity.FlatEntity
import com.example.financeassistant.room.entity.SourceImageEntity
import java.util.Date

fun FlatPayment.toEntity(): FlatAccountEntity {
    return FlatAccountEntity(
        uid = this.uid,
        flatUid = this.getFlatUid(),
        summa = this.summa,
        comment = this.comment,
        type = this.paymentType,
        operation = this.operation,
        date = Date(this.date),
        dateDoc = this.dateDoc,
        period = this.period,
    )
}

fun FlatAccountEntity.toFlatPayment(): FlatPayment {
    return FlatPayment(
        uid = this.uid,
        flat = Flat(uid = this.flatUid),
        date = this.date?.time ?: 0L,
        dateDoc = this.dateDoc,
        period = this.period,
        summa = this.summa ?: 0.00,
        paymentType = this.type,
        operation = this.operation,
        comment = (this.comment ?: "") + "\nuid='${this.uid}' \nflatUid = '${this.flatUid}'"
    )
}

fun Flat.toEntity(): FlatEntity {
    return FlatEntity(
        uid = this.uid,
        summa = this.summa,
        name = this.name,
        type = this.type,
        adres = this.adres,
        param = this.param,
        lic = this.lic,
        summaArenda = this.summaArenda,
        creditUid = this.credit?.uid,
        dayArenda = this.dayArenda,
        dayStart = this.dayStart,
        dayEnd = this.dayEnd,
        isArenda = this.isArenda,
        isCounter = this.isCounter,
        isPay = this.isPay,
        isFinish = this.isFinish,
        sourceImage = this.sourceImage?.toEntity(),
        issueYear = this.issueYear,
        purchaseDate = this.purchaseDate,
//        summaFinRes = this.summaFinRes,
//        credit = this.credit,
    )
}

fun FlatEntity.toFlat(): Flat {
    return Flat(
        uid = this.uid,
        summa = this.summa,
        name = this.name,
        type = this.type,
        adres = this.adres,
        param = this.param + "\nuid='${this.uid}'",
        lic = this.lic,
        summaArenda = this.summaArenda,
        credit = Credit(uid = this.creditUid ?: ""),
        dayArenda = this.dayArenda,
        dayStart = this.dayStart,
        dayEnd = this.dayEnd,
        isArenda = this.isArenda,
        isCounter = this.isCounter,
        isPay = this.isPay,
        isFinish = this.isFinish,
        sourceImage = this.sourceImage?.toSourceImage(),
        issueYear = this.issueYear,
        purchaseDate = this.purchaseDate,
//        summaFinRes = this.summaFinRes,
    )
}

fun SourceImage.toEntity(): SourceImageEntity {
    return SourceImageEntity(
        uid = this.pictureUid,
        sourceUrl = this.sourceUrl,
        filename = this.filename,
        extension = this.extension,
        createAt = this.createAt,
        size = this.size,
    )
}

fun SourceImageEntity.toSourceImage(): SourceImage {
    return SourceImage(
        pictureUid = this.uid,
        sourceUrl = this.sourceUrl,
        filename = this.filename,
        extension = this.extension,
        createAt = this.createAt,
        size = this.size,
    )
}