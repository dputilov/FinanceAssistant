package com.example.financeassistant.classes

import com.google.gson.annotations.SerializedName

data class CreditItem (
    val credit : Credit,
    var result: CreditResult
)

data class CreditResult (
    var date_pay : Long = 0,
    var date_last_pay : Long = 0,
    var summa: Double = 0.0,
    var summa_payment: Double = 0.0,
    var param_period : Int = 0,
    var param_procent : Double = 0.0,
    var summa_rest: Double = 0.0,
    var credit_summa_to_pay: Double = 0.0,
    var summa_to_pay: Double = 0.0,
    var summa_fin_res : Double = 0.0
)