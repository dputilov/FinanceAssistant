package com.example.financeassistant.useCase.services

import android.content.Context
import com.example.financeassistant.classes.Credit
import com.example.financeassistant.classes.ExchangeItem
import com.example.financeassistant.classes.ExchangeItemType
import com.example.financeassistant.classes.Flat
import com.example.financeassistant.classes.FlatPayment
import com.example.financeassistant.classes.Payment
import com.example.financeassistant.manager.DatabaseManager
import com.example.financeassistant.manager.RoomDatabaseManager
import com.example.financeassistant.room.database.toEntity
import io.reactivex.subjects.PublishSubject
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class ExchangeApplyUseCase(val context: Context) {

    var exchangeSubject: PublishSubject<ExchangeItem> = PublishSubject.create<ExchangeItem>()

    private var threadJob: Job? = null

    fun cancel() {
        if (threadJob != null && threadJob!!.isActive) {
            threadJob?.cancel()
        }

        exchangeSubject = PublishSubject.create<ExchangeItem>()
    }

    fun applyExchange(exchangeItemList : List<ExchangeItem>) {
        threadJob?.cancel()

        threadJob = GlobalScope.launch {

            deleteGraphic(exchangeItemList)

            exchangeItemList.filter { it.isCredit() }.forEachIndexed { index, exchangeItem ->
                applyItemExchange(index, exchangeItem)
            }

            exchangeItemList.filter { it.isFlat() }.forEachIndexed { index, exchangeItem ->
                applyItemExchange(index, exchangeItem)
            }

            exchangeItemList.filter { !it.isCredit() && !it.isFlat() }.forEachIndexed { index, exchangeItem ->
                applyItemExchange(index, exchangeItem)
            }

            exchangeSubject.onComplete()

        }
    }

    private fun applyItemExchange(index: Int, exchangeItem : ExchangeItem) {
        val item = exchangeItem.item
        when {
            item is Credit -> checkAndCreateCredit(item)
            item is Payment && exchangeItem.type == ExchangeItemType.CreditPayment -> checkAndCreateCreditPayment(item)
            item is Payment && exchangeItem.type == ExchangeItemType.CreditGraphic -> checkAndCreateCreditGraphic(item)
            item is Flat -> checkAndCreateFlat(item)
            item is FlatPayment -> checkAndCreateFlatPayment(item)
        }
        exchangeSubject.onNext(exchangeItem)
    }

    private fun deleteGraphic(exchangeItemList : List<ExchangeItem>) {
        val graphicCreditUid = mutableListOf<String>()
        exchangeItemList.forEach { exchangeItem ->
            val item = exchangeItem.item
            if (item is Payment && exchangeItem.type == ExchangeItemType.CreditGraphic) {
                val creditUid = item.getCreditUid()
                if (graphicCreditUid.firstOrNull { it == creditUid } == null) {
                    DatabaseManager.instance.exchangeDeleteGraphic(creditUid)
                    graphicCreditUid.add(creditUid)
                }
            }
        }
    }

    private fun checkAndCreateCreditPayment(payment: Payment) {
        DatabaseManager.instance.exchangeCreateCreditPayment(payment)
    }

    private fun checkAndCreateCreditGraphic(payment: Payment) {
        DatabaseManager.instance.exchangeCreateCreditGraphic(payment)
    }

    private fun checkAndCreateCredit(credit: Credit) {
        DatabaseManager.instance.exchangeCreateCredit(credit)
    }

    private fun checkAndCreateFlat(flat: Flat) {
        RoomDatabaseManager.instance.database.flatDao().insert(listOf(flat.toEntity()))
        //DatabaseManager.instance.exchangeCreateFlat(flat)
    }

    private fun checkAndCreateFlatPayment(flatPayment: FlatPayment) {
        RoomDatabaseManager.instance.database.flatAccountDao().insert(listOf(flatPayment.toEntity()))
        //DatabaseManager.instance.exchangeCreateFlatPayment(flatPayment)
    }

}

