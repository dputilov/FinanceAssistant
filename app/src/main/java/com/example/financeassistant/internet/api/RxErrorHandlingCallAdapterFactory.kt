package com.example.financeassistant.internet.api

import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import java.lang.reflect.Type


/**
 * Wraps "regular" Retrofit errors in custom ApiException class
 */
class RxErrorHandlingCallAdapterFactory: CallAdapter.Factory() {

    private val original by lazy {
        RxJava2CallAdapterFactory.create()
    }

    companion object {
        fun create() : CallAdapter.Factory = RxErrorHandlingCallAdapterFactory()
    }

    override fun get(returnType: Type, annotations: Array<Annotation>, retrofit: Retrofit): CallAdapter<*, *> {
        val wrapped = original.get(returnType, annotations, retrofit) as CallAdapter<out Any, *>

        return RxCallAdapterWrapper(retrofit, wrapped)
    }

    private class RxCallAdapterWrapper<R>(val retrofit: Retrofit,
                                          val wrappedCallAdapter: CallAdapter<R, *>
                                          ): CallAdapter<R, Any> {

        override fun responseType(): Type = wrappedCallAdapter.responseType()

        override fun adapt(call: Call<R>): Any {

            val adapted = wrappedCallAdapter.adapt(call)
            if (adapted is Single<*>) {
                return adapted.onErrorResumeNext { throwable: Throwable ->
                    val apiException = ApiException.errorFrom(throwable, retrofit)
                    return@onErrorResumeNext Single.error(apiException)
                }
            }

            if (adapted is Observable<*>) {
                return adapted.onErrorResumeNext { throwable: Throwable ->
                    val apiException = ApiException.errorFrom(throwable, retrofit)
                    return@onErrorResumeNext Observable.error(apiException)
                }
            }

            if (adapted is Completable) {
                return adapted.onErrorResumeNext { throwable: Throwable ->
                    val apiException = ApiException.errorFrom(throwable, retrofit)
                    return@onErrorResumeNext Completable.error(apiException)
                }
            }

            return adapted

        }
    }
}