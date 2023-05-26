package com.example.financeassistant.diagram

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.financeassistant.useCase.GetGraphicUseCase
import com.example.financeassistant.classes.Credit
import com.example.financeassistant.classes.CreditTotals
import com.example.financeassistant.classes.DiagramItem
import com.example.financeassistant.classes.Payment
import com.example.financeassistant.classes.groupByDate
import com.example.financeassistant.utils.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class CreditDiagramViewModel(application: Application): AndroidViewModel(application) {

    companion object {
        const val MAX_APPROXIMATION = 12
    }

    var getGraphicUseCase =  GetGraphicUseCase()

    var diagramItemList = MutableLiveData<List<DiagramItem>>()
    var setCurrentCreditEvent = SingleLiveEvent<Credit>()

    private var creditFilterList : List<Credit>? = listOf()
    var initCreditFilterListEvent = MutableLiveData<List<Credit>>()

    var setCurrentPage = SingleLiveEvent<Int>()

    var isOnlyActiveCredit = SingleLiveEvent<Boolean>()

    val showGraphicLoadingIndicatorEvent = SingleLiveEvent<Void>()
    val hideGraphicLoadingIndicatorEvent = SingleLiveEvent<Void>()
    val showGraphicLoadErrorEvent = SingleLiveEvent<Void>()

    private var getGraphicListSubscription: Disposable? = null

    private var currentCredit : Credit? = null

    override fun onCleared() {
        super.onCleared()

        getGraphicListSubscription?.dispose()

    }

    fun setOnlyActive(isOnlyActive: Boolean) {
        isOnlyActiveCredit.value = isOnlyActive
    }

    fun getOnlyActive() : Boolean {
        return isOnlyActiveCredit.value ?: false
    }

    fun onOnlyActiveClick() {
        setOnlyActive(!getOnlyActive())
        getGraphicList()
    }

    fun initInstance(credit: Credit?){
        initCreditFilterList()

        currentCredit = credit
        getGraphicList()
    }

    fun setCreditFilter(newCreditFilterList: List<Credit>?) {
        creditFilterList = newCreditFilterList
        getGraphicList()
    }

    private fun setCurrentCredit() {
        val listData = diagramItemList.value

        if (listData.isNullOrEmpty()) {
            return
        }

        if (currentCredit == null
            || listData.indexOfFirst { it.creditTotals.credit == currentCredit } < 0 ) {
            currentCredit = listData[0].creditTotals?.credit
        }

        setCurrentCreditEvent.value = currentCredit
    }

    private fun setGraphicListData(listData: List<DiagramItem>) {
        diagramItemList.value = listData

        setCurrentCredit()
    }

    private fun getGraphicListData() : List<DiagramItem> {
        return diagramItemList.value ?: listOf()
    }


    private fun initGraphicList(listData: List<DiagramItem>){

        val resultList = mutableListOf<DiagramItem>()
        var cntApprox = 0

        val sourceListData = listData.toMutableList()

        // Добавляем итоговый элемент
        val allCreditTotals = CreditTotals(credit = Credit(name = "Все кредиты"))

        val allCreditItemDataList = mutableListOf<Payment>()

        sourceListData.forEach {

            allCreditTotals += it.creditTotals

            allCreditItemDataList.addAll(it.diagramData)
        }

        allCreditItemDataList.sortBy{ it.date }

        sourceListData.add(0, DiagramItem(allCreditTotals, allCreditItemDataList))

        // Формируем итоговый список
        for ( item in sourceListData) {
            val newList = mutableListOf<Payment>()

            cntApprox = 0
            val diagramData = item.diagramData
            diagramData.forEachIndexed { i, payment ->
                if (cntApprox < MAX_APPROXIMATION) {

                    payment.date = getBegMonth(payment.date)

                    newList.add(payment)

                    if (payment.done != 1) {
                        cntApprox++
                    }
                }
            }

            val reducedList = newList.groupByDate()

            resultList.add(DiagramItem(item.creditTotals, reducedList))
        }

        setGraphicListData(resultList)
    }

    fun onChangePage(position: Int){
        getGraphicListData()?.also{
            if (it.size > position) {
                currentCredit = it[position].creditTotals?.credit
                setCurrentCredit()
            }
        }
    }

    fun getCreditFilterList() : List<Credit>? {
        return creditFilterList
    }

    private fun getGraphicList(){

        getGraphicListSubscription?.dispose()
        getGraphicUseCase.cancel()

        getGraphicListSubscription = getGraphicUseCase.getGetAllCreditDataSubject
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { onGetGraphicStart() }
                .subscribe(
                        { listData: List<DiagramItem> -> onGetGraphicSuccess(listData) },
                        { error: Throwable -> onGetGraphicError(error) }
                )

        getGraphicUseCase.getAllCreditData(getApplication(), getOnlyActive(), getCreditFilterList())

    }

    private fun onGetGraphicStart() {
        showGraphicLoadingIndicatorEvent.call()
    }

    private fun onGetGraphicSuccess(listData: List<DiagramItem>) {
        hideGraphicLoadingIndicatorEvent.call()
        initGraphicList(listData)
    }

    private fun onGetGraphicError(error: Throwable) {
        Log.d("DMS_CREDIT", "onGetGraphicError = $error")
        hideGraphicLoadingIndicatorEvent.call()
        showGraphicLoadErrorEvent.call()
    }


    // Credit filter
    private fun initCreditFilterList() {
        initCreditFilterListEvent.value = getGraphicUseCase.getAllCredit(getApplication(), getOnlyActive())
    }
}