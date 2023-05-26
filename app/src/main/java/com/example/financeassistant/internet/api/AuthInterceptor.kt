package com.example.financeassistant.internet.api

import okhttp3.Interceptor
import okhttp3.Response

/**
 * AuthInterceptor adds an Authorization header to the requests if user is logged in
*/
//class AuthInterceptor constructor(private val apiStorage: ApiStorage) : Interceptor {
class AuthInterceptor constructor() : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val ongoingRequest = chain.request().newBuilder()
//        apiStorage.getAuthToken()?.also {
//            val authorization = "bearer $it"
//            ongoingRequest.addHeader("Authorization", authorization)
//        }
        return chain.proceed(ongoingRequest.build())
    }
}