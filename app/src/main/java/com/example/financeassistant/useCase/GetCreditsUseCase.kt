package com.example.financeassistant.useCase

import android.content.Context
import android.database.Cursor
import android.text.Html
import android.util.Log
import com.example.financeassistant.classes.CreditItem
import com.example.financeassistant.classes.Credit
import com.example.financeassistant.classes.CreditResult
import com.example.financeassistant.classes.CreditType
import com.example.financeassistant.database.DB
import com.example.financeassistant.manager.SettingsManager
import com.example.financeassistant.utils.toBoolean
import io.reactivex.subjects.PublishSubject
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Date

class GetCreditsUseCase {

    var getCreditListSubject: PublishSubject<List<CreditItem>> = PublishSubject.create<List<CreditItem>>()

    private var threadJob: Job? = null

    fun cancel() {
        if (threadJob != null && threadJob!!.isActive) {
            threadJob?.cancel()
        }
    }

    fun getCreditList(context: Context) {

        threadJob?.cancel()

        threadJob = GlobalScope.launch {

            val listData = createCreditList(context)

            getCreditListSubject.onNext(listData)

        }

    }

    private fun createCreditList(context: Context) : MutableList<CreditItem> {

        val listData: MutableList<CreditItem> = mutableListOf()

        val RUB = Html.fromHtml(" &#x20bd")

        val decFormat_2 = DecimalFormat("###,###.##")
        val decFormat = DecimalFormat("###,###")

        var summa: Double
        var summa_payment: Double
        var summa_payment_sec: Double

        // Дата последнего платежа
        var last_date: Long
        var last_date_str = ""
        var lastSumma: Double = 0.0

        val million = 1000000.0
        
        val db = DB(context)

        db.open()

        // очищаем список

        listData.clear()

        val showClosed = SettingsManager.instance.getShowCloseCreditSettings()

        val c: Cursor? = db.credit_GetCreditData(showClosed)

        if (c != null) {

            if (c.moveToFirst()) {
                do {

                    val type = Integer.parseInt(c.getString(c.getColumnIndex(DB.CL_RESULT_CREDIT_TYPE)))
                   // val creditType = CreditType.getById(type + 1)

                    var date_last_pay = 0L
                    var date_pay = 0L
                    var date = 0L

                    try {
                        date = c.getLong(c.getColumnIndex(DB.CL_RESULT_CREDIT_DATE))

                        date_pay = c.getLong(c.getColumnIndex(DB.CL_RESULT_GRAPHIC_DATE))

                        date_last_pay = c.getLong(c.getColumnIndex(DB.CL_RESULT_PAYMENT_DATE_LAST_PAY))

                        if (date_last_pay == 0L) date_last_pay = Date().time
                        
                    } catch (e: Exception) {
                        Log.d("DMS", "ERROR CONVERT DATE FROM: $date")
                    }


                    // Дата кредита
                    val dateFormatDec = SimpleDateFormat("dd.MM.yyyy 'г.'")
                    val str_date_end = dateFormatDec.format(date_last_pay)

                    val days = ((date_last_pay - date) / (24 * 60 * 60 * 1000)).toInt() // миллисекунды / (24ч * 60мин * 60сек * 1000мс)
                    val years = days / 365
                    val month = days % 365 / 30
                    var str_period = ""
                    if (years > 0) str_period += years.toString() + "г. "
                    if (month > 0) str_period += month.toString() + "м. "
                    if (str_period.isEmpty()) str_period += "<1м."


                    val summa = c.getDouble(c.getColumnIndex(DB.CL_RESULT_CREDIT_SUMMA))
                    val summa_payment = c.getDouble(c.getColumnIndex(DB.CL_RESULT_PAYMENT_SUMMA_CREDIT))
                    val param_period = c.getInt(c.getColumnIndex(DB.CL_RESULT_CREDIT_PERIOD))
                    val param_procent = c.getDouble(c.getColumnIndex(DB.CL_RESULT_CREDIT_PROCENT))
                    val summa_rest = c.getDouble(c.getColumnIndex(DB.CL_RESULT_RESULT_REST))

                    //            Log.d("DMS_CREDIT", "summa_rest = " + summa_rest +
                    //                    " "+ _cursor.getString(_cursor.getColumnIndex(DB.CL_RESULT_RESULT_REST)) +
                    //                    " = " + Double.parseDouble(_cursor.getString(_cursor.getColumnIndex(DB.CL_RESULT_RESULT_REST))) +
                    //                    " === " + _cursor.getDouble(_cursor.getColumnIndex(DB.CL_RESULT_RESULT_REST)) );

                    val credit_summa_to_pay = c.getDouble(c.getColumnIndex(DB.CL_RESULT_CREDIT_SUMMA_PAY))
                    var summa_to_pay: Double = c.getDouble(c.getColumnIndex(DB.CL_RESULT_GRAPHIC_SUMMA))

                    val summa_fin_res = c.getDouble(c.getColumnIndex(DB.CL_RESULT_RESULT_FIN_RES))


                    val credit_id = c.getInt(c.getColumnIndex(DB.CL_ID))

                    val credit = db.getCredit(credit_id)

                    val result = CreditResult (
                        date_pay = date_pay,
                        date_last_pay = date_last_pay,
                        summa = summa,
                        summa_payment = summa_payment,
                        param_period = param_period,
                        param_procent = param_procent,
                        summa_rest = summa_rest,
                        credit_summa_to_pay = credit_summa_to_pay,
                        summa_to_pay = summa_to_pay,
                        summa_fin_res = summa_fin_res
                    )

                    val newCredit = CreditItem(
                        credit = credit,
                        result = result
                    )

                    listData.add(newCredit)

                } while (c.moveToNext())
            }
            c.close()
        }

        db.close()

        return listData
    }

}

