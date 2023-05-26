package com.example.financeassistant.useCase.services


import com.example.financeassistant.classes.ExchangeItem
import com.example.financeassistant.internet.services.credit.ApiCreditService
import com.example.financeassistant.internet.services.flat.ApiFlatService
import io.reactivex.Observable
import io.reactivex.functions.Function
import javax.inject.Inject

/**
 * Exchange all use case
 */
class ExchangeLoadAllUseCase @Inject constructor (
    private val apiCreditService: ApiCreditService,
    private val apiFlatService: ApiFlatService,
    private val creditUseCase: ExchangeCreditUseCaseInterface = ExchangeCreditUseCase(apiCreditService),
    private val flatUseCase: ExchangeFlatUseCaseInterface = ExchangeFlatUseCase(apiFlatService)) :
    ExchangeCreditUseCaseInterface by creditUseCase,
    ExchangeFlatUseCaseInterface by flatUseCase
{

    fun loadAll(): Observable<List<ExchangeItem>> {
        val functionList: MutableList<Observable<List<ExchangeItem>>> = mutableListOf()

        functionList.add(creditUseCase.loadCredits())
        functionList.add(creditUseCase.loadCreditPayments())
        functionList.add(creditUseCase.loadCreditGraphic())
        functionList.add(flatUseCase.loadFlats())
        functionList.add(flatUseCase.loadFlatPayments())

        val convertToExchangeItemListFunction = Function<Array<Any>, List<ExchangeItem>> { sourceList ->
            val exchangeList = mutableListOf<ExchangeItem>()
            sourceList.forEach { resultList ->
                if (resultList is List<*>) {
                    exchangeList.addAll(resultList as List<ExchangeItem>)
                }
            }
            exchangeList
        }

        return Observable.zip(functionList, convertToExchangeItemListFunction)
    }

}
