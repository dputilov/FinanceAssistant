package com.example.financeassistant.injection.module

import com.example.financeassistant.internet.api.Api
import com.example.financeassistant.internet.services.credit.ApiCreditService
import com.example.financeassistant.internet.services.credit.ApiCreditServiceRemote
import com.example.financeassistant.internet.services.flat.ApiFlatService
import com.example.financeassistant.internet.services.flat.ApiFlatServiceRemote
import dagger.Module
import dagger.Provides

/**
 * Module which provides all required dependencies about Remote services
 */
@Module
class ServiceRemoteModule {

    @Provides
    fun provideApiCreditService(api: Api): ApiCreditService = ApiCreditServiceRemote(api)

    @Provides
    fun provideApiFlatService(api: Api): ApiFlatService = ApiFlatServiceRemote(api)

}
