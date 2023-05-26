package com.example.financeassistant.useCase

import android.content.Context
import com.example.financeassistant.database.DB
import com.example.financeassistant.classes.Payment

class CreditUseCase {

    fun getNextCreditPayment(context: Context, creditId: Long) : Payment {

            val db = DB(context)

            db.open()

            val payment = db.credit_GetNextPayment(creditId)

            db.close()

            return payment
    }
}

