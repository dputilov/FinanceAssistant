package com.example.financeassistant.flat

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.financeassistant.classes.Flat
import com.example.financeassistant.manager.RoomDatabaseManager
import com.example.financeassistant.room.database.toEntity
import com.example.financeassistant.room.database.toFlat
import com.example.financeassistant.room.entity.FlatEntity
import com.example.financeassistant.utils.SingleLiveEvent
import com.example.financeassistant.utils.getNewUid
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class FlatViewModel(application: Application): AndroidViewModel(application) {

    var flatList = MutableLiveData<List<Flat>>()

    var reloadToFlatEvent = SingleLiveEvent<Flat>()
    var currentPage = SingleLiveEvent<Int>()

    var exitFromFlatView = SingleLiveEvent<Flat>()

    var currentflat: Flat? = null

    private var getFlatSubscription: Disposable? = null
    private var updateFlatSubscription: Disposable? = null

    val TAG = "ROOM_TEST"

    override fun onCleared() {
        super.onCleared()

        getFlatSubscription?.dispose()
        updateFlatSubscription?.dispose()
    }

    fun initInstance(flat: Flat?) {
        this.currentflat = flat

        initFlatList(flat)
    }

    fun reloadInstance() {
        reloadToFlatEvent.value = currentflat
    }

    fun initFlatList(flat: Flat?){
        loadFlatList(flat)
    }

    fun loadFlatList(currentFlat: Flat?){
        getFlatSubscription?.dispose()

        getFlatSubscription = RoomDatabaseManager.instance.database.flatDao().getAll()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { listData: List<FlatEntity> -> onGetEntitiesSuccess(currentFlat, listData) },
                { error: Throwable -> onGetDataError(error) }
            )
    }

    private fun onGetEntitiesSuccess(currentFlat: Flat?, flatAccountEntityList: List<FlatEntity>) {

        val listData = mutableListOf<Flat>()

        flatAccountEntityList.forEach {
            listData.add(it.toFlat())
        }

        this.flatList.value = listData
        currentPage.value = listData.indexOfFirst { it.uid == currentFlat?.uid }
    }

    private fun onGetDataError(error: Throwable) {
    }

    private fun updateFlat(flat: Flat) {
//        updateFlatSubscription?.dispose()
//
//        updateFlatSubscription = RoomDatabaseManager.instance.database.flatDao().update(listOf(flat.toEntity()))
//            .subscribeOn(Schedulers.io())
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribe(
//                { listData: List<String> -> onFlatUpdated(listData) },
//                { error: Throwable -> onGetDataError(error) }
//            )

        Log.d(TAG, "flat = $flat")

        RoomDatabaseManager.instance.database.flatDao().update(listOf(flat.toEntity()))

        val listFlat = RoomDatabaseManager.instance.database.flatDao().getAllTest()

        Log.d(TAG, "updated flat = ${listFlat.find { it.uid == flat.uid }}")

    }

    private fun insertFlat(flat: Flat) {
        RoomDatabaseManager.instance.database.flatDao().insert(listOf(flat.toEntity()))
    }

    private fun onFlatUpdated(listData: List<String>) {
        exitFromFlatView.value = currentflat
    }

    private fun getFlatList() : List<Flat> {
        return flatList.value ?: listOf()
    }

    fun onChangeObjectPage(position : Int) {
        getFlatList()?.also { flatList ->
            if (flatList.size > position) {
                val newCurrentFlat = flatList[position]
                if (currentflat != newCurrentFlat) {
                    currentflat = newCurrentFlat
                    reloadInstance()
                }
            }
        }
    }

    fun onClickAddFlat(flat: Flat) {
        currentflat = flat

        if (flat.uid.isNullOrEmpty()) {
            flat.uid = getNewUid()
            insertFlat(flat)
        } else {
            updateFlat(flat)
        }

        exitFromFlatView.value = currentflat
    }

}