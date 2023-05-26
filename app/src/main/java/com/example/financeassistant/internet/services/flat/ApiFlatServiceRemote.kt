package com.example.financeassistant.internet.services.flat

import android.util.Log
import com.example.financeassistant.classes.Flat
import com.example.financeassistant.classes.FlatPayment
import com.example.financeassistant.classes.SourceImage
import com.example.financeassistant.internet.api.Api
import com.example.financeassistant.utils.formatDate
import com.google.gson.Gson
import com.google.gson.JsonParser
import io.reactivex.Completable
import io.reactivex.Observable
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.Response
import okhttp3.ResponseBody
import java.io.File
import java.util.Date

/**
 * Flat service remote implementation
 */

class ApiFlatServiceRemote constructor(private val api: Api) : ApiFlatService {

    override fun loadFlats(): Observable<List<Flat>> {
        return api.loadFlats()
    }

    override fun loadAllFlatPayments(): Observable<List<FlatPayment>> {
        return api.loadAllFlatPayments()
    }

    override fun loadFlatPayments(flatUid: String): Observable<List<FlatPayment>> {
        return api.loadFlatPayments(flatUid)
    }

    override fun loadFlatPhoto(flatUid: String): Observable<ResponseBody> {
        return api.loadFlatPhoto(flatUid)
    }

    override fun updateFlatPicture(flatUid: String, image: SourceImage, byteArray: ByteArray): Completable {
//        val fileRequestBody = RequestBody.create(MediaType.parse("image/jpeg"), byteArray)
//        val fileMultipartBody = MultipartBody.Part.createFormData("avatar", "avatar.jpeg", fileRequestBody)
//        return api.updateFlatPicture(flatUid, fileMultipartBody)


        Log.d("SourceImage","image=${Gson().toJson(image)}")
        val createAt = image.createAt?.time?.let {
            formatDate(it, "yyyy.MM.dd")
        } ?: ""


        val fileRequestBody = RequestBody.create(MediaType.parse("image/jpeg"), byteArray)
        return api.updateFlatPicture(image.filename, createAt, image.size, flatUid, fileRequestBody)
    }

}