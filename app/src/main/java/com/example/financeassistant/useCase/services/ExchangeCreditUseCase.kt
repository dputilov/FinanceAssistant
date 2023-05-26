package com.example.financeassistant.useCase.services

import com.example.financeassistant.classes.Credit
import com.example.financeassistant.classes.ExchangeItem
import com.example.financeassistant.classes.ExchangeItemType
import com.example.financeassistant.classes.Flat
import com.example.financeassistant.internet.services.credit.ApiCreditService
import com.example.financeassistant.manager.DatabaseManager
import com.example.financeassistant.utils.formatDate
import io.reactivex.Observable
import java.util.Date
import javax.inject.Inject

/**
 * Exchange credit use case
 */

interface ExchangeCreditUseCaseInterface {
    fun loadCredits(): Observable<List<ExchangeItem>>

    fun loadCreditGraphic(creditUid: String? = null): Observable<List<ExchangeItem>>

    fun loadCreditPayments(creditUid: String? = null): Observable<List<ExchangeItem>>

    fun loadCreditsAndFlats(): Observable<List<ExchangeItem>>
}

class ExchangeCreditUseCase @Inject constructor(private val apiCreditService: ApiCreditService) : ExchangeCreditUseCaseInterface {

    override fun loadCredits(): Observable<List<ExchangeItem>> {
        return apiCreditService.loadCredits()
            .flatMap { dataList ->
                val newExchangeItemList = mutableListOf<ExchangeItem>()
                val databaseManager = DatabaseManager.instance

                dataList.forEach {
                    it.dateDoc?.also { dateDoc ->
                        it.date = dateDoc.time
                    }

                    val baseItem = databaseManager.getCreditByUid(it)
                    if (baseItem != null) {
                        it.id = baseItem.id
                    }

                    val item = ExchangeItem(
                        title = "Кредит",
                        objectName = it.name,
                        type = ExchangeItemType.Credit,
                        sum = it.summa,
                        item = it,
                        baseItem = baseItem
                    )

                    item.updateStatus()
                    newExchangeItemList.add(item)
                }

                Observable.just(newExchangeItemList)
            }
    }

    override fun loadCreditPayments(creditUid: String?): Observable<List<ExchangeItem>> {
        return if (creditUid == null) {
                    apiCreditService.loadAllCreditPayments()
                } else {
                    apiCreditService.loadCreditPayments(creditUid)
                }
                .flatMap { dataList ->
                    val newExchangeItemList = mutableListOf<ExchangeItem>()
                    val databaseManager = DatabaseManager.instance

                    dataList.forEach {
                        it.dateDoc?.also { dateDoc ->
                            it.date = dateDoc.time
                        }

                        val baseItem = databaseManager.getCreditPaymentByUid(it)
                        if (baseItem != null) {
                            it.id = baseItem.id
                        }

                        val item = ExchangeItem(
                            title = "Платеж по кредиту",
                            objectName = "${it.credit?.name}" +
                                if (it.comment.isNullOrEmpty()) {
                                    ""
                                } else {
                                    "\n${it.comment}"
                                },
                            sum = it.summa,
                            date = Date(it.date),
                            type = ExchangeItemType.CreditPayment,
                            item = it,
                            baseItem = baseItem)

                        item.updateStatus()

                        newExchangeItemList.add(item)
                    }

                    Observable.just(newExchangeItemList)
                }

    }

    override fun loadCreditGraphic(creditUid: String?): Observable<List<ExchangeItem>> {
        return if (creditUid == null) {
            apiCreditService.loadAllCreditGraphic()
        } else {
            apiCreditService.loadCreditGraphic(creditUid)
        }
            .flatMap { dataList ->
                val newExchangeItemList = mutableListOf<ExchangeItem>()
                val databaseManager = DatabaseManager.instance

                dataList.forEach {
                    it.dateDoc?.also { dateDoc ->
                        it.date = dateDoc.time
                    }

                    val baseItem = databaseManager.getGraphicPaymentByUid(it)
                    if (baseItem != null) {
                        it.id = baseItem.id
                    }

                    val item = ExchangeItem(
                        title = "Платеж по кредиту (график)",
                        objectName = "${it.credit?.name}"
                            + if (it.comment.isNullOrEmpty()) {
                                ""
                            } else {
                                "\n${it.comment}"
                            },
                        sum = it.summa,
                        date = Date(it.date),
                        type = ExchangeItemType.CreditGraphic,
                        item = it,
                        baseItem = baseItem)

                    item.updateStatus()

                    newExchangeItemList.add(item)
                }

                Observable.just(newExchangeItemList)
            }

    }

    //TODO For Test Sealed Class [ServerResponse]
    override fun loadCreditsAndFlats(): Observable<List<ExchangeItem>> {
        return apiCreditService.loadCreditsAndFlats()
            .flatMap { dataList ->
                val newExchangeItemList = mutableListOf<ExchangeItem>()
                val databaseManager = DatabaseManager.instance

                dataList.forEach {
                    val item = when (it) {
                        is Credit -> {
                            it.dateDoc?.also { dateDoc ->
                                it.date = dateDoc.time
                            }

                            val baseItem = databaseManager.getCreditByUid(it)
                            if (baseItem != null) {
                                it.id = baseItem.id
                            }

                            ExchangeItem(
                                title = "Кредит",
                                objectName = it.name,
                                type = ExchangeItemType.Credit,
                                sum = it.summa,
                                item = it,
                                baseItem = baseItem
                            )

                        }

                        is Flat -> {
                            it.credit?.also { credit ->
                                val credit = databaseManager.getCreditByUid(credit)
                                it.credit_id = credit?.id ?: -1
                            }

                            val baseItem = databaseManager.getFlatByUid(it)
                            if (baseItem != null) {
                                it.id = baseItem.id
                            }

                            ExchangeItem(
                                title = "Квартира",
                                objectName = it.name,
                                sum = it.summa,
                                type = ExchangeItemType.Flat,
                                item = it,
                                baseItem = baseItem
                            )

                        }
                    }

                    item.updateStatus()
                    newExchangeItemList.add(item)
                }

                Observable.just(newExchangeItemList)
            }
    }

}
