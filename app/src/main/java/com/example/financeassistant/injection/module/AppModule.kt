package com.example.financeassistant.injection.module

import android.app.Application
import android.content.Context
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * Module which provides all required dependencies about Application
 */
@Module
class AppModule(private val app: Application) {
    @Provides
    @Singleton
    fun provideContext(): Context {
        return app
    }
}