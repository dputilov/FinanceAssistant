package com.example.financeassistant.internet.api

import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

/**
 * ConnectivityInceptor incapsulates the following logic:
 * For any errors that should be handled before being handed off to RxJava.
 * In other words global error logic.
*/
class ConnectivityInterceptor() : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {


        val builder = chain.request().newBuilder()
        return chain.proceed(builder.build())
    }

}