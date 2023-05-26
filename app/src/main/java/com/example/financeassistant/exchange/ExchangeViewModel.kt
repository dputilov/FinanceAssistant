package com.example.financeassistant.exchange

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.financeassistant.base.BaseAndroidViewModel
import com.example.financeassistant.classes.BroadcastActionType
import com.example.financeassistant.classes.ExchangeFilterItem
import com.example.financeassistant.classes.ExchangeItem
import com.example.financeassistant.classes.ExchangeItemType
import com.example.financeassistant.useCase.assembly.CreateExchangeFilterItemsUseCase
import com.example.financeassistant.useCase.services.ExchangeApplyUseCase
import com.example.financeassistant.useCase.services.ExchangeCreditUseCase
import com.example.financeassistant.useCase.services.ExchangeFlatUseCase
import com.example.financeassistant.useCase.services.ExchangeLoadAllUseCase
import com.example.financeassistant.utils.SingleLiveEvent
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

/**
 * Exchange
 */

class ExchangeViewModel(application: Application): BaseAndroidViewModel(application) {

    @Inject
    lateinit var exchangeLoadAllUseCase: dagger.Lazy<ExchangeLoadAllUseCase>

    @Inject
    lateinit var exchangeCreditUseCase: dagger.Lazy<ExchangeCreditUseCase>

    @Inject
    lateinit var exchangeFlatUseCase: dagger.Lazy<ExchangeFlatUseCase>

    @Inject
    lateinit var exchangeApplyUseCase: dagger.Lazy<ExchangeApplyUseCase>

    @Inject
    lateinit var createExchangeFilterItemsUseCase: CreateExchangeFilterItemsUseCase

    private var exchangeApplySubscription: Disposable? = null
    private var exchangeCreditSubscription: Disposable? = null
    private var exchangeCreditPaymentsSubscription: Disposable? = null
    private var exchangeCreditPGraphicsSubscription: Disposable? = null
    private var exchangeFlatSubscription: Disposable? = null
    private var exchangeFlatPaymentsSubscription: Disposable? = null
    private var exchangeAllSubscription: Disposable? = null

    var exchangeFilterItemList = MutableLiveData<List<ExchangeFilterItem>>()

    var exchangeItemList = MutableLiveData<List<ExchangeItem>>()
    var onlyUpdated = MutableLiveData<Boolean>()

    val showCloseTaskMessageEvent = SingleLiveEvent<Void>()

    // Data binding
    val resultText = MutableLiveData<String>()
    val applyLayoutVisibility = MutableLiveData(false)
    val obtainingDataInProgress = MutableLiveData(false)
    val applyProgressBarVisibility = MutableLiveData(false)
    val maxApplyProgress = MutableLiveData<Int>(0)
    val currentApplyProgress = MutableLiveData<Int>(0)
    val currentApplySecondaryProgress = MutableLiveData<Int>(0)
    val exchangeApplyProgressBarText = MutableLiveData("")
    val exchangeApplyProgressBarPercentText = MutableLiveData("")

    init {
        onlyUpdated.value = true
        registerGroupBroadcastReceiver()

        createExchangeFilterItems()
    }

    override fun onCleared() {
        super.onCleared()

        unregisterGroupBroadcastReceiver()

        exchangeApplySubscription?.dispose()

        exchangeCreditSubscription?.dispose()
        exchangeCreditPaymentsSubscription?.dispose()
        exchangeCreditPGraphicsSubscription?.dispose()
        exchangeFlatSubscription?.dispose()
        exchangeFlatPaymentsSubscription?.dispose()
        exchangeAllSubscription?.dispose()

    }


    private fun getOnlyUpdated() : Boolean = onlyUpdated.value ?: false

    override fun onGroupBroadcastReceive(action: BroadcastActionType) {
        showCloseTaskMessageEvent.call()
    }

    private fun getCurrentApplyProgress() : Int {
        return currentApplyProgress.value ?: 0
    }

    // Create filter items list
    private fun createExchangeFilterItems(){
        exchangeFilterItemList.value = createExchangeFilterItemsUseCase.createExchangeFilterItems()
    }

    fun onStartClick() {
        startExchange(null)
    }

    fun startExchange(type : ExchangeItemType?) {
        resultText.value = ""
        exchangeItemList.value = null
        when(type) {
            ExchangeItemType.Credit -> {
                exchangeCredit()
            }
            ExchangeItemType.CreditPayment -> {
                exchangeCreditPayment()
            }
            ExchangeItemType.CreditGraphic -> {
                exchangeCreditGraphic()
            }
            ExchangeItemType.Flat -> {
                exchangeFlat()
            }
            ExchangeItemType.FlatPayment -> {
                exchangeFlatPayment()
            }
            null -> {
                exchangeAll()
            }
            else -> {

            }
        }
    }

    fun cancelApply() {
        exchangeApplySubscription?.dispose()
        exchangeApplyUseCase.get().cancel()

        stopExchangeProcess()
    }

    private fun getProgressPercent(progress: Int): String {
        val percent = maxApplyProgress.value?.let { max ->
            if (max != 0) {
                (progress * 100) / max
            } else {
                0
            }
        } ?: 0
        return "${percent}%"
    }

    private fun initProgressBar() {
        applyProgressBarVisibility.value = true
        maxApplyProgress.value = exchangeItemList.value?.size
        currentApplyProgress.value = 0
        exchangeApplyProgressBarText.value = ""
        exchangeApplyProgressBarPercentText.value = getProgressPercent(0)
    }

    fun applyExchange(){
        cancelApply()

        exchangeItemList.value?.let { exchangeItemList ->

            exchangeApplySubscription = exchangeApplyUseCase.get().exchangeSubject
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { onApplyExchangeStart() }
                .doOnNext {item: ExchangeItem -> onApplyExchangeNext(item) }
                .doOnComplete { onApplyExchangeComplete() }
                .subscribe(
                    { _ -> onApplyExchangeSuccess() },
                    { error: Throwable -> onApplyExchangeError(error) }
                )

            exchangeApplyUseCase.get().applyExchange(exchangeItemList)

        }

    }

    private fun onApplyExchangeStart() {
        initProgressBar()
    }

    private fun onApplyExchangeNext(item: ExchangeItem) {
        val progress = getCurrentApplyProgress() + 1

        currentApplyProgress.value = progress
        currentApplySecondaryProgress.value = progress + 5
        exchangeApplyProgressBarPercentText.value = getProgressPercent(progress)
        exchangeApplyProgressBarText.value = item.type.name
    }

    private fun onApplyExchangeComplete() {
        applyProgressBarVisibility.value = false

        checkExchangeList()

        sendGroupBroadcastMessage(BroadcastActionType.Exchange)
    }

    private fun onApplyExchangeSuccess() {
    }

    private fun onApplyExchangeError(error: Throwable) {
    }

    private fun startExchangeProcess() {
        obtainingDataInProgress.value = true
    }
    
    private fun stopExchangeProcess() {
        obtainingDataInProgress.value = false
    }

    private fun exchangeCredit() {
        exchangeCreditSubscription?.dispose()

        exchangeCreditSubscription = exchangeCreditUseCase.get().loadCredits()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe{ startExchangeProcess() }
            .doOnComplete{ stopExchangeProcess() }
            .subscribe(
                { data -> onExchangeSuccess(data) },
                { error: Throwable -> onExchangeFail(error) }
            )
    }

    private fun onExchangeSuccess(data: List<ExchangeItem>) {
        val newList = mutableListOf<ExchangeItem>()

        exchangeItemList.value?.also {
            newList.addAll(it)
        }

        data.map { it.isSelected = it.isChanged() }

        if (getOnlyUpdated()) {
            newList.addAll(data.filter { it.isChanged() })
        } else {
            newList.addAll(data)
        }

        exchangeItemList.value = newList

        checkExchangeList()
    }

    private fun checkExchangeList() {
        if (exchangeItemList.value?.isEmpty() == true) {
            resultText.value = "No new data received"
            applyLayoutVisibility.value = false
        } else {
            applyLayoutVisibility.value = true
        }
    }

    private fun onExchangeFail(error: Throwable) {
        exchangeItemList.value = listOf()
        stopExchangeProcess()
        resultText.value = "http error = $error";
        Log.d("DMS_NETWORK", "http error = $error")
    }

    private fun exchangeCreditPayment() {
        exchangeCreditPaymentsSubscription?.dispose()

        exchangeCreditPaymentsSubscription = exchangeCreditUseCase.get().loadCreditPayments()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe{ startExchangeProcess() }
            .doOnComplete{ stopExchangeProcess() }
            .subscribe(
                { data -> onExchangeSuccess(data) },
                { error: Throwable -> onExchangeFail(error) }
            )
    }

    private fun exchangeCreditGraphic() {
        exchangeCreditPGraphicsSubscription?.dispose()

        exchangeCreditPGraphicsSubscription = exchangeCreditUseCase.get().loadCreditGraphic()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe{ startExchangeProcess() }
            .doOnComplete{ stopExchangeProcess() }
            .subscribe(
                { data -> onExchangeSuccess(data) },
                { error: Throwable -> onExchangeFail(error) }
            )
    }

    private fun exchangeFlat() {
        exchangeFlatSubscription?.dispose()

        exchangeFlatSubscription = exchangeFlatUseCase.get().loadFlats()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe{ startExchangeProcess() }
            .doOnComplete{ stopExchangeProcess() }
            .subscribe(
                { data -> onExchangeSuccess(data) },
                { error: Throwable -> onExchangeFail(error) }
            )
    }

    private fun exchangeFlatPayment() {
        exchangeFlatPaymentsSubscription?.dispose()

        exchangeFlatPaymentsSubscription = exchangeFlatUseCase.get().loadFlatPayments()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe{ startExchangeProcess() }
            .doOnComplete{ stopExchangeProcess() }
            .subscribe(
                { data -> onExchangeSuccess(data) },
                { error: Throwable -> onExchangeFail(error) }
            )
    }

    private fun exchangeAll() {
        exchangeAllSubscription?.dispose()

        exchangeAllSubscription = exchangeLoadAllUseCase.get().loadAll()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe{ startExchangeProcess() }
            .doOnComplete{ stopExchangeProcess() }
            .subscribe(
                { data -> onExchangeSuccess(data) },
                { error: Throwable -> onExchangeFail(error) }
            )
    }

}
