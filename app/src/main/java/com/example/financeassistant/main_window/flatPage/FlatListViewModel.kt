package com.example.financeassistant.main_window.flatPage

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.financeassistant.base.BaseAndroidViewModel
import com.example.financeassistant.classes.BroadcastActionType
import com.example.financeassistant.useCase.GetFlatsUseCase
import com.example.financeassistant.classes.FlatItem
import com.example.financeassistant.utils.SingleLiveEvent
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class FlatListViewModel(application: Application): BaseAndroidViewModel(application) {

    var getFlatListUseCase =  GetFlatsUseCase()

    var flatItemList = MutableLiveData<List<FlatItem>>()

//    var flatToOpen = SingleLiveEvent<HOME>()
//    var flatPayToOpen = SingleLiveEvent<FlatPayment>()

    var showFlatLoadingIndicatorEvent = SingleLiveEvent<Void>()
    var hideFlatLoadingIndicatorEvent = SingleLiveEvent<Void>()

    private var getFlatsSubscription: Disposable? = null

    init {
        registerGroupBroadcastReceiver()
    }

    override fun onCleared() {
        super.onCleared()

        getFlatsSubscription?.dispose()

        unregisterGroupBroadcastReceiver()
    }

    override fun onGroupBroadcastReceive(action: BroadcastActionType) {
        if (action == BroadcastActionType.Exchange) {
            getFlatList()
        }
    }


    fun getFlatList(){

        getFlatsSubscription?.dispose()

        getFlatsSubscription = getFlatListUseCase.getFlatListSubject
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { onGetFlatItemsListStart() }
                .subscribe(
                        { listData: List<FlatItem> -> onGetDataSuccess(listData) },
                        { error: Throwable -> onGetDatasError(error) }
                )

        getFlatListUseCase.getFlatList(getApplication())

    }

    private fun onGetFlatItemsListStart() {
        showFlatLoadingIndicatorEvent.call()
    }

    private fun onGetDataSuccess(listData: List<FlatItem>) {
        hideFlatLoadingIndicatorEvent.call()

        flatItemList.value = listData

    }

    private fun onGetDatasError(error: Throwable) {
        Log.d("DMS_TASK", "onGetDatasError = $error")
        //hideGraphicLoadingIndicatorEvent.call()
        //showGraphicLoadErrorEvent.call()
    }

}