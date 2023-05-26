package com.example.financeassistant.classes

import com.example.financeassistant.utils.toBoolean
import java.io.Serializable

// Итоговые показатели
data class CreditTotals (
    var credit: Credit,
    var fact: Payment = Payment(),
    var plan: Payment = Payment()
) : Serializable {

    operator fun plusAssign(creditTotals: CreditTotals?) {
        creditTotals?.also {
            this.credit += it.credit
            this.fact += it.fact
            this.plan += it.plan
        }
    }

    operator fun plusAssign(payment: Payment?) {
        payment?.also {
            if (it.done.toBoolean()) {
                this.fact += it
            } else {
                this.plan += it
            }

        }
    }

    fun getFinanceResult(): Double {
        return - (fact.summa_procent + fact.summa_minus - fact.summa_plus)
    }

    fun getActualRest(): Double {
        var rest = credit.summa
        rest -= fact.summa_credit
        return rest
    }
}
