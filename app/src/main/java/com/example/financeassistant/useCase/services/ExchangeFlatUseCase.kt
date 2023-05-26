package com.example.financeassistant.useCase.services

import com.example.financeassistant.classes.ExchangeItem
import com.example.financeassistant.classes.ExchangeItemType
import com.example.financeassistant.classes.FlatPaymentOperationType
import com.example.financeassistant.classes.FlatPaymentType
import com.example.financeassistant.classes.SourceImage
import com.example.financeassistant.internet.services.flat.ApiFlatService
import com.example.financeassistant.manager.DatabaseManager
import io.reactivex.Completable
import io.reactivex.Observable
import okhttp3.Response
import okhttp3.ResponseBody
import java.io.File
import java.util.Date
import javax.inject.Inject

/**
 * Exchange flat use case
 */

interface ExchangeFlatUseCaseInterface {

    fun loadFlats(): Observable<List<ExchangeItem>>

    fun loadFlatPayments(creditUid: String? = null): Observable<List<ExchangeItem>>

    fun updateFlatPicture(flatUid: String, image: SourceImage, byteArray: ByteArray) : Completable

    fun getFlatPicture(flatUid: String) : Observable<ByteArray?>

}

class ExchangeFlatUseCase @Inject constructor(private val apiFlatService: ApiFlatService): ExchangeFlatUseCaseInterface {

    override fun loadFlats(): Observable<List<ExchangeItem>> {
        return apiFlatService.loadFlats()
            .flatMap { dataList ->
                val newExchangeItemList = mutableListOf<ExchangeItem>()
                val databaseManager = DatabaseManager.instance

                dataList.forEach {

                    it.credit?.also { credit ->
                        val credit = databaseManager.getCreditByUid(credit)
                        it.credit_id = credit?.id ?: -1
                    }

                    val baseItem = databaseManager.getFlatByUid(it)
                    if (baseItem != null) {
                        it.id = baseItem.id
                    }

//                    if (it.isPhoto) {
//                        apiFlatService.loadFlatPhoto(it.uid)
//                            .flatMap { photo ->
//                                it.foto = photo
//                            }
//                    }

                    val item = ExchangeItem(
                        title = "Квартира",
                        objectName = it.name,
                        sum = it.summa,
                        type = ExchangeItemType.Flat,
                        item = it,
                        baseItem = baseItem)

                    item.updateStatus()

                    newExchangeItemList.add(item)

                }

                Observable.just(newExchangeItemList)
            }
    }

    override fun loadFlatPayments(flatUid: String?): Observable<List<ExchangeItem>> {
        return if (flatUid == null) {
                    apiFlatService.loadAllFlatPayments()
                } else {
                    apiFlatService.loadFlatPayments(flatUid)
                }
                .flatMap { dataList ->
                    val newExchangeItemList = mutableListOf<ExchangeItem>()
                    val databaseManager = DatabaseManager.instance

                    dataList.forEach {
                        it.period?.also { dateDoc ->
                            it.date = dateDoc.time
                        }

                        val baseItem = databaseManager.getFlatPaymentByUid(it)
                        if (baseItem != null) {
                            it.id = baseItem.id
                        }

                        val item = ExchangeItem(
                            title = when (it.operation) {
                                FlatPaymentOperationType.RENT -> "Квартплата"
                                FlatPaymentOperationType.PROFIT -> "Аренда"
                                else  -> "Прочие платежи" },
                            objectName = "${it.flat?.name}",
                            sum = it.summa,
                            date = it.dateDoc,
                            type = ExchangeItemType.FlatPayment,
                            item = it,
                            baseItem = baseItem)

                        item.updateStatus()

                        newExchangeItemList.add(item)
                    }

                    Observable.just(newExchangeItemList)
                }

    }

    override fun updateFlatPicture(flatUid: String, image: SourceImage, byteArray: ByteArray): Completable {
        return apiFlatService.updateFlatPicture(flatUid, image, byteArray)
    }

    override fun getFlatPicture(flatUid: String): Observable<ByteArray?> {
        return apiFlatService.loadFlatPhoto(flatUid)
            .flatMap { data ->
                Observable.just(data.bytes())
            }
    }
}
