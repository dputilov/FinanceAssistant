package com.example.financeassistant.main_window.creditPage

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.financeassistant.base.BaseAndroidViewModel
import com.example.financeassistant.classes.BroadcastActionType
import com.example.financeassistant.classes.Credit
import com.example.financeassistant.useCase.GetCreditsUseCase
import com.example.financeassistant.classes.CreditItem
import com.example.financeassistant.classes.Payment
import com.example.financeassistant.manager.DatabaseManager
import com.example.financeassistant.useCase.CreditUseCase
import com.example.financeassistant.utils.SingleLiveEvent
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class CreditListViewModel(application: Application): BaseAndroidViewModel(application) {

    var creditUseCase =  CreditUseCase()

    var getCreditListUseCase =  GetCreditsUseCase()

    var creditItemList = MutableLiveData<List<CreditItem>>()

    var creditToOpen = SingleLiveEvent<Credit>()
    var creditPayToOpen = SingleLiveEvent<Payment>()

    var showCreditLoadingIndicatorEvent = SingleLiveEvent<Void>()
    var hideCreditLoadingIndicatorEvent = SingleLiveEvent<Void>()

    private var getCreditsSubscription: Disposable? = null

    init {
        registerGroupBroadcastReceiver()
    }

    override fun onCleared() {
        super.onCleared()

        getCreditsSubscription?.dispose()

        unregisterGroupBroadcastReceiver()
    }

    override fun onGroupBroadcastReceive(action: BroadcastActionType) {
        if (action == BroadcastActionType.Exchange) {
            getCreditList()
        }
    }

    fun deleteAllCredits(){
        DatabaseManager.instance.deleteAllCredits()
    }

    fun onCreditPaymentClick(credit: Credit) {
        creditPayToOpen.value = creditUseCase.getNextCreditPayment(getApplication(), credit.id.toLong())
    }

    fun onCreditEditClick(credit: Credit) {
        creditToOpen.value = credit
    }

    fun deleteCredit(credit: Credit){

        DatabaseManager.instance.deleteCredit(credit)

        creditItemList.value?.toMutableList()?.also { newList ->
            newList.removeAll { it.credit.id == credit.id }
            creditItemList.value = newList
        }
    }

    fun getCreditList(){

        getCreditsSubscription?.dispose()

        getCreditsSubscription = getCreditListUseCase.getCreditListSubject
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { onGetCreditItemsListStart() }
                .subscribe(
                        { listData: List<CreditItem> -> onGetDataSuccess(listData) },
                        { error: Throwable -> onGetDatasError(error) }
                )

        getCreditListUseCase.getCreditList(getApplication())

    }

    private fun onGetCreditItemsListStart() {
        showCreditLoadingIndicatorEvent.call()
    }

    private fun onGetDataSuccess(listData: List<CreditItem>) {
        hideCreditLoadingIndicatorEvent.call()

        creditItemList.value = listData

    }

    private fun onGetDatasError(error: Throwable) {
        Log.d("DMS_TASK", "onGetDatasError = $error")
        //hideGraphicLoadingIndicatorEvent.call()
        //showGraphicLoadErrorEvent.call()
    }

}