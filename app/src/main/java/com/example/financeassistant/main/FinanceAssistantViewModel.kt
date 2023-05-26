package com.example.financeassistant.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.financeassistant.classes.Flat
import com.example.financeassistant.database.DB
import com.example.financeassistant.utils.SingleLiveEvent

class FinanceAssistantViewModel(application: Application): AndroidViewModel(application) {

    var currentPage = SingleLiveEvent<Int>()


//    fun initInstance(flat: Flat?) {
//        this.currentflat = flat
//
//        initFlatList(flat)
//
//    }

    fun onChangeObjectPage(position : Int) {

    }

}