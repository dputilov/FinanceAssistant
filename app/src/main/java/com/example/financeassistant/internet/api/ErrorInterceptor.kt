package com.example.financeassistant.internet.api

import okhttp3.Interceptor
import okhttp3.Response

/**
 * ErrorInceptor incapsulates the following logic:
 * For any errors that should be handled before being handed off to RxJava.
 * In other words global error logic.
*/
class RetrofitErrorInterceptorConstructor() : Interceptor {

    override fun intercept(chain: Interceptor.Chain?): Response {

        var originalResponse = chain!!.proceed(chain.request())
        val mainRequest = chain.request()

        return originalResponse
    }

}