package com.example.financeassistant.internet.services.credit

import com.example.financeassistant.classes.Credit
import com.example.financeassistant.classes.Payment
import com.example.financeassistant.classes.ServerResponse
import io.reactivex.Completable
import io.reactivex.Observable

interface ApiCreditService {

    fun loadCredits(): Observable<List<Credit>>

    fun loadAllCreditPayments(): Observable<List<Payment>>

    fun loadAllCreditGraphic(): Observable<List<Payment>>

    fun loadCreditGraphic(creditUid: String): Observable<List<Payment>>

    fun loadCreditPayments(creditUid: String): Observable<List<Payment>>

    // TODO JUST FOR TEST
    fun loadCreditsAndFlats(): Observable<List<ServerResponse>>

}