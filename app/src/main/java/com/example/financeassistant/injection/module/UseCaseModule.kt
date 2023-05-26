package com.example.financeassistant.injection.module

import android.content.Context
import com.example.financeassistant.internet.services.credit.ApiCreditService
import com.example.financeassistant.internet.services.flat.ApiFlatService
import com.example.financeassistant.useCase.services.ExchangeApplyUseCase
import com.example.financeassistant.useCase.services.ExchangeCreditUseCase
import com.example.financeassistant.useCase.services.ExchangeFlatUseCase
import com.example.financeassistant.useCase.services.ExchangeLoadAllUseCase
import dagger.Module
import dagger.Provides

/**
 * Module which provides all required dependencies of Use Cases
 */
@Module
class UseCaseModule {

    @Provides
    fun provideExchangeCreditUseCase(apiCreditService : ApiCreditService): ExchangeCreditUseCase = ExchangeCreditUseCase(apiCreditService)

    @Provides
    fun provideExchangeFlatUseCase(apiFlatService : ApiFlatService): ExchangeFlatUseCase = ExchangeFlatUseCase(apiFlatService)

    @Provides
    fun provideExchangeApplyUseCase(context : Context): ExchangeApplyUseCase = ExchangeApplyUseCase(context)

    @Provides
    fun provideExchangeLoadAllUseCase(apiCreditService : ApiCreditService, apiFlatService : ApiFlatService): ExchangeLoadAllUseCase = ExchangeLoadAllUseCase(apiCreditService, apiFlatService)

}
