package com.example.financeassistant.flat

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.financeassistant.classes.Flat
import com.example.financeassistant.classes.FlatPayment
import com.example.financeassistant.database.DB
import com.example.financeassistant.manager.RoomDatabaseManager
import com.example.financeassistant.room.database.toFlat
import com.example.financeassistant.room.database.toFlatPayment
import com.example.financeassistant.room.entity.FlatAccountEntity
import com.example.financeassistant.room.entity.FlatEntity
import com.example.financeassistant.utils.SingleLiveEvent
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class FlatViewModel(application: Application): AndroidViewModel(application) {

    var flatList = MutableLiveData<List<Flat>>()

    var reloadToFlatEvent = SingleLiveEvent<Flat>()
    var currentPage = SingleLiveEvent<Int>()

    var currentflat: Flat? = null

    private var getFlatSubscription: Disposable? = null

    override fun onCleared() {
        super.onCleared()

        getFlatSubscription?.dispose()
    }

    fun initInstance(flat: Flat?) {
        this.currentflat = flat

        initFlatList(flat)

    }

    fun initFlatList(flat: Flat?){

        loadFlatList(flat)

//        val db = DB(getApplication())
//        db.open()
//        db.getAllFlats()?.also { flatList ->
//            this.flatList.value = flatList
//            currentPage.value = flatList.indexOfFirst { it.id == flat?.id }
//        }
//        db.close()
    }

    fun loadFlatList(currentFlat: Flat?){
        getFlatSubscription?.dispose()

        getFlatSubscription = RoomDatabaseManager.instance.database.flatDao().getAll()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { listData: List<FlatEntity> -> onGetEntitiesSuccess(currentFlat, listData) },
                { error: Throwable -> onGetDatasError(error) }
            )
    }

    fun onGetEntitiesSuccess(currentFlat: Flat?, flatAccountEntityList: List<FlatEntity>) {

        val listData = mutableListOf<Flat>()

        flatAccountEntityList.forEach {
            listData.add(it.toFlat())
        }

        this.flatList.value = listData
        currentPage.value = listData.indexOfFirst { it.uid == currentFlat?.uid }
    }

    private fun onGetDatasError(error: Throwable) {
    }

    fun updateFlat(flat: Flat) {
        val db = DB(getApplication())
        db.open()
        db.flat_Update(flat)
        db.close()
    }

    fun addFlat(flat: Flat) {
        val db = DB(getApplication())
        db.open()
        db.flat_Add(flat)
        db.close()
    }

    fun getFlatList() : List<Flat> {
        return flatList.value ?: listOf()
    }

    fun reloadInstance() {
        reloadToFlatEvent.value = currentflat
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

}