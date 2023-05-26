package com.example.financeassistant.internet.api

import com.example.financeassistant.classes.Credit
import com.example.financeassistant.classes.Flat
import com.example.financeassistant.classes.FlatPayment
import com.example.financeassistant.classes.Payment
import com.example.financeassistant.classes.ServerResponse
import io.reactivex.Completable
import io.reactivex.Observable
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.Response
import okhttp3.ResponseBody
import retrofit2.http.*
import java.util.Date

/**
 * API class representing all the public EYE API methods
 */

interface Api {

    @GET("api/Credits")
    fun loadCredits(): Observable<List<Credit>>

    @GET("api/Credits/All/Payments")
    fun loadAllCreditPayments(): Observable<List<Payment>>

    @GET("api/Credits/{CreditUid}/Payments")
    fun loadCreditPayments(@Path("CreditUid") creditUid: String) : Observable<List<Payment>>

    @GET("api/Credits/All/Graphics")
    fun loadAllCreditGraphic(): Observable<List<Payment>>

    @GET("api/Credits/{CreditUid}/Graphics")
    fun loadCreditGraphic(@Path("CreditUid") creditUid: String) : Observable<List<Payment>>

    @GET("api/Flats")
    fun loadFlats(): Observable<List<Flat>>

    @GET("api/Flats/All/Payments")
    fun loadAllFlatPayments(): Observable<List<FlatPayment>>

    @GET("api/Flats/{FlatUid}/Payments")
    fun loadFlatPayments(@Path("FlatUid") creditUid: String) : Observable<List<FlatPayment>>

    @GET("api/Flats/{FlatUid}/Photo")
    fun loadFlatPhoto(@Path("FlatUid") creditUid: String) : Observable<ResponseBody>

//    @Multipart
//    @POST("api/Flats/{FlatUid}/Photo")
//    fun updateFlatPicture1(@Path("FlatUid") creditUid: String,
//                          @Part image: MultipartBody.Part): Observable<ResponseBody>

    @POST("api/Flats/{FlatUid}/Photo")
    fun updateFlatPicture(
        @Header("Filename") filename: String,
        @Header("CreateDate") createAt: String,
        @Header("FileSize") fileSize: Long,
        @Path("FlatUid") creditUid: String,
        @Body image: RequestBody): Completable

//    @GET("api/Explore/ByLocation/{latitude}/{longitude}/{radius}/{commentsAmount}/{pageSize}")
//    fun loadPosts(@Path("latitude") latitude: Float,
//                  @Path("longitude") longitude: Float,
//                  @Path("radius") radius: Int,
//                  @Path("commentsAmount") commentsAmount: Int,
//                  @Path("pageSize") pageSize: Int): Observable<List<Post>>


//    @DELETE("api/Posts/{postId}")
//    fun deletePost(@Path("postId") postId: String): Completable

//    @PUT("api/Posts/{id}")
//    fun updatePost(@Path("id") id: String,
//                   @Body body: UpdatePostBody): Observable<Post>

    //TODO For Test Sealed Class [ServerResponse]
    @GET("api/CreditsAndFlats")
    fun loadCreditsAndFlats(): Observable<List<ServerResponse>>
}
