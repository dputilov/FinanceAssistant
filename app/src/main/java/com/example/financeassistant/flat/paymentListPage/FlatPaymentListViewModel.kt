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

    var showFlatPaymemtListLoadingIndicatorEvent = SingleLiveEvent<Void>()
    var hideFlatPaymemtListLoadingIndicatorEvent = SingleLiveEvent<Void>()

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
            getFlatPaymentListUseCase.init()

            getFlatPaymentSubscription = getFlatPaymentListUseCase.getFlatPaymentListSubject
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { onGetDataStart() }
                .doOnComplete { onGetDataStop() }
                .subscribe(
                    { listData: List<FlatPayment> -> onGetDataSuccess(listData) },
                    { error: Throwable -> onGetDatasError(error) }
                )

            getFlatPaymentListUseCase.getFlatPaymentList(getApplication(), flat.id)

        }

    }

    private fun onGetDataStart() {
        showFlatPaymemtListLoadingIndicatorEvent.call()
    }

    private fun onGetDataStop() {
        hideFlatPaymemtListLoadingIndicatorEvent.call()
    }

    private fun onGetDataSuccess(listData: List<FlatPayment>) {
//        hideFlatPaymemtListLoadingIndicatorEvent.call()

        flatPaymentList.value = listData.sortedByDescending { it.date }

    }

    private fun onGetDatasError(error: Throwable) {
        hideFlatPaymemtListLoadingIndicatorEvent.call()
        //showGraphicLoadErrorEvent.call()
    }

}