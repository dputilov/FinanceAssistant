package com.example.financeassistant.flat

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.financeassistant.classes.Flat
import com.example.financeassistant.database.DB
import com.example.financeassistant.utils.SingleLiveEvent

class FlatViewModel(application: Application): AndroidViewModel(application) {

    var flatList = MutableLiveData<List<Flat>>()

    var reloadToFlatEvent = SingleLiveEvent<Flat>()
    var currentPage = SingleLiveEvent<Int>()

    var currentflat: Flat? = null

    fun initInstance(flat: Flat?) {
        this.currentflat = flat

        initFlatList(flat)

    }

    fun initFlatList(flat: Flat?){
        val db = DB(getApplication())
        db.open()
        db.getAllFlats()?.also { flatList ->
            this.flatList.value = flatList
            currentPage.value = flatList.indexOfFirst { it.id == flat?.id }
        }
        db.close()
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