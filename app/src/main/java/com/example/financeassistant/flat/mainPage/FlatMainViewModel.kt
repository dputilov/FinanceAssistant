package com.example.financeassistant.flat.mainPage

import android.app.Application
import android.media.Image
import android.net.Uri
import android.util.Log
import androidx.core.net.toUri
import androidx.lifecycle.MutableLiveData
import com.example.financeassistant.app.FinanceAssistantApp
import com.example.financeassistant.base.BaseAndroidViewModel
import com.example.financeassistant.classes.SourceImage
import com.example.financeassistant.useCase.services.ExchangeFlatUseCase
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.attribute.BasicFileAttributes
import java.util.Date
import javax.inject.Inject

class FlatMainViewModel(application: Application): BaseAndroidViewModel(application) {

    @Inject
    lateinit var exchangeFlatUseCase: dagger.Lazy<ExchangeFlatUseCase>

    val picture = MutableLiveData<ByteArray?>()
    private var exchangeUpdatePictureSubscription: Disposable? = null

    override fun onCleared() {
        super.onCleared()

        exchangeUpdatePictureSubscription?.dispose()


    }

    fun loadPicture(flatUid: String){

        exchangeUpdatePictureSubscription = exchangeFlatUseCase.get().getFlatPicture(flatUid)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe{ startExchangeProcess() }
            .doOnComplete{ stopExchangeProcess() }
            .subscribe(
                { data -> onLoadPictureSuccess(data) },
                { error: Throwable -> onLoadPictureFail(error) }
            )
    }

    private fun onLoadPictureSuccess(data: ByteArray?) {
        picture.value = data
    }

    private fun onLoadPictureFail(error: Throwable) {
    }

    private fun startExchangeProcess() {

    }

    private fun stopExchangeProcess() {

    }

    fun uploadPicture(flatUid: String, imageUri: Uri){

        val file = File(imageUri.path)

//        val filePath = Paths.get(imageUri.path)
//        val attributes = Files.readAttributes(filePath, BasicFileAttributes::class.java)
//        val creationTime = attributes.creationTime()

        val creationTime = file.lastModified()

        val image = SourceImage(
            path = file.path,
            filename = file.name,
            extension = file.extension,
            size = file.length() / 1024,
            createAt = Date(creationTime)
        )

        val inputStream = getApplication<FinanceAssistantApp>().contentResolver.openInputStream(imageUri)
        inputStream?.readBytes()?.also { byteArray ->
            exchangeUpdatePictureSubscription = exchangeFlatUseCase.get().updateFlatPicture(flatUid, image, byteArray)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe{ startExchangeProcess() }
                .doOnComplete{ stopExchangeProcess() }
                .subscribe(
                    { onUploadPictureSuccess() },
                    { error: Throwable -> onUploadPictureFail(error) }
                )
        }
        inputStream?.close()
    }

    private fun onUploadPictureSuccess() {
        val t=1
    }

    private fun onUploadPictureFail(error: Throwable) {
        Log.d("HTTP", "http ERROR = ${error.message}")
    }

}