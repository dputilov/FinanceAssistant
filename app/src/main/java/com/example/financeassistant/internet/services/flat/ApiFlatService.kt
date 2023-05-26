package com.example.financeassistant.internet.services.flat

import com.example.financeassistant.classes.Flat
import com.example.financeassistant.classes.FlatPayment
import com.example.financeassistant.classes.SourceImage
import io.reactivex.Completable
import io.reactivex.Observable
import okhttp3.MultipartBody
import okhttp3.Response
import okhttp3.ResponseBody
import java.io.File

interface ApiFlatService {

    fun loadFlats(): Observable<List<Flat>>

     fun loadAllFlatPayments(): Observable<List<FlatPayment>>

    fun loadFlatPayments(flatUid: String): Observable<List<FlatPayment>>

    fun loadFlatPhoto(flatUid: String): Observable<ResponseBody>

    fun updateFlatPicture(flatUid: String, image: SourceImage, byteArray: ByteArray): Completable
}