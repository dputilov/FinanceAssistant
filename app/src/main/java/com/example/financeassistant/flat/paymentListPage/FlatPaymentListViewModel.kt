package com.example.financeassistant.flat.paymentListPage

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.financeassistant.useCase.GetFlatPaymentsUseCase
import com.example.financeassistant.classes.FlatPayment
import com.example.financeassistant.classes.FlatPaymentOperationType
import com.example.financeassistant.classes.FlatPaymentOperationType.PROFIT
import com.example.financeassistant.classes.FlatPaymentOperationType.RENT
import com.example.financeassistant.classes.FlatPaymentType.Outlay
import com.example.financeassistant.classes.FlatPaymentType.Profit
import com.example.financeassistant.classes.Flat
import com.example.financeassistant.manager.RoomDatabaseManager
import com.example.financeassistant.room.database.toFlatPayment
import com.example.financeassistant.room.entity.FlatAccountEntity
import com.example.financeassistant.utils.SingleLiveEvent
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class FlatPaymentListViewModel(application: Application): AndroidViewModel(application) {

    var getFlatPaymentListUseCase = GetFlatPaymentsUseCase()

    var flatPaymentList = MutableLiveData<List<FlatPayment>>()

    private var flat : Flat? = null

    var accountToPayment = SingleLiveEvent<FlatPayment>()
//    var flatPayToOpen = SingleLiveEvent<FlatPayment>()

    var showFlatPaymentListLoadingIndicatorEvent = SingleLiveEvent<Void>()
    var hideFlatPaymentListLoadingIndicatorEvent = SingleLiveEvent<Void>()

    private var getFlatPaymentSubscription: Disposable? = null

    override fun onCleared() {
        super.onCleared()

        getFlatPaymentSubscription?.dispose()
    }

    fun setFlat(flat: Flat) {
        this.flat = flat
        loadFlatPaymentList()
    }

    fun onPaymentClick(type: FlatPaymentOperationType) {
        flat?.also {
            when (type) {
                FlatPaymentOperationType.PROFIT -> {
                    accountToPayment.value = FlatPayment(flat = Flat(id = it.id), operation = PROFIT,
                        paymentType = Profit)
                }
                FlatPaymentOperationType.RENT -> {
                    accountToPayment.value = FlatPayment(flat = Flat(id = it.id), operation = RENT,
                        paymentType = Outlay)
                }
                else -> {}
            }
        }

    }

    fun loadFlatPaymentList(){

        flat?.also { flat ->

            getFlatPaymentSubscription?.dispose()

            getFlatPaymentSubscription = RoomDatabaseManager.instance.database.flatAccountDao().getAllByFlatUid(listOf(flat.uid))
            //getFlatPaymentSubscription = RoomDatabaseManager.instance.database.flatAccountDao().getAll()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { onGetDataStart() }
                .doOnComplete { onGetDataStop() }
                .subscribe(
                    { listData: List<FlatAccountEntity> -> onGetEntitiesSuccess(listData) },
                    { error: Throwable -> onGetDataError(error) }
                )
        }

    }

    fun onGetEntitiesSuccess(flatAccountEntityList: List<FlatAccountEntity>) {

        val listData = mutableListOf<FlatPayment>()

        flatAccountEntityList.forEach {
            listData.add(it.toFlatPayment())
        }

        flatPaymentList.value = listData.sortedByDescending { it.date }

        onGetDataStop()
    }



    fun loadFlatPaymentList_old(){

        flat?.also { flat ->

            getFlatPaymentSubscription?.dispose()
            getFlatPaymentListUseCase.init()

            getFlatPaymentSubscription = getFlatPaymentListUseCase.getFlatPaymentListSubject
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { onGetDataStart() }
                .doOnComplete { onGetDataStop() }
                .subscribe(
                    { listData: List<FlatPayment> -> onGetDataSuccess(listData) },
                    { error: Throwable -> onGetDataError(error) }
                )

            getFlatPaymentListUseCase.getFlatPaymentList(getApplication(), flat.id)

        }

    }

    private fun onGetDataStart() {
        showFlatPaymentListLoadingIndicatorEvent.call()
    }

    private fun onGetDataStop() {
        hideFlatPaymentListLoadingIndicatorEvent.call()
    }

    private fun onGetDataSuccess(listData: List<FlatPayment>) {
//        hideFlatPaymentListLoadingIndicatorEvent.call()

        flatPaymentList.value = listData.sortedByDescending { it.date }

    }

    private fun onGetDataError(error: Throwable) {
        hideFlatPaymentListLoadingIndicatorEvent.call()
        //showGraphicLoadErrorEvent.call()
    }

}