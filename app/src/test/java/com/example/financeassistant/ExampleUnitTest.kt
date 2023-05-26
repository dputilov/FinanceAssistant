package com.example.financeassistant

import com.example.financeassistant.classes.Credit
import com.example.financeassistant.classes.CreditTotals
import com.example.financeassistant.classes.Payment
import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun paymentTest() {

        val credit = Credit(-1, )

        val p1 = Payment(0,0,100.0, 200.0, 300.0, 400.0, 500.0 )
        val p2 = Payment(0,0,100.0, 200.0, 300.0, 400.0, 500.0 )

        val res = Payment(-1,0,200.0, 400.0, 600.0, 800.0, 1000.0 )


        assertEquals(res, p1.plus(p2))

        val finRes  = - 300.0 - 0 + 500
        val sum = CreditTotals(credit, p1, p2)

        assertEquals(finRes, sum.getFinanceResult(), 0.000001)
    }
}