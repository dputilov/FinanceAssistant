package com.example.financeassistant.internet.services.credit

import com.example.financeassistant.classes.Credit
import com.example.financeassistant.classes.Payment
import com.example.financeassistant.classes.ServerResponse
import com.example.financeassistant.internet.api.Api
import io.reactivex.Observable

/**
 * Credit service remote implementation
 */

class ApiCreditServiceRemote constructor(private val api: Api) : ApiCreditService {

    override fun loadCredits(): Observable<List<Credit>> {
        return api.loadCredits()
    }

    override fun loadAllCreditPayments(): Observable<List<Payment>> {
        return api.loadAllCreditPayments()
    }

    override fun loadCreditPayments(creditUid: String): Observable<List<Payment>> {
        return api.loadCreditPayments(creditUid)
    }

    override fun loadAllCreditGraphic(): Observable<List<Payment>> {
        return api.loadAllCreditGraphic()
    }

    override fun loadCreditGraphic(creditUid: String): Observable<List<Payment>> {
        return api.loadCreditGraphic(creditUid)
    }

    // TODO FOR TEST
    override fun loadCreditsAndFlats(): Observable<List<ServerResponse>> {
        return api.loadCreditsAndFlats()
    }
}