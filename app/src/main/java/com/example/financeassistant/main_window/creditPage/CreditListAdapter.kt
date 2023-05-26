package com.example.financeassistant.main_window.creditPage


import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import androidx.recyclerview.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.financeassistant.R
import android.text.Html
import android.widget.TextView
import com.example.financeassistant.classes.Credit
import com.example.financeassistant.classes.CreditItem
import com.example.financeassistant.classes.CreditType
import com.example.financeassistant.databinding.ItemCreditBinding
import com.example.financeassistant.utils.diffDateInDays
import com.example.financeassistant.utils.formatD
import com.example.financeassistant.utils.getBegDay
import com.example.financeassistant.utils.invisible
import com.example.financeassistant.utils.isZero
import com.example.financeassistant.utils.visible
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Date

/**
 * Credit list adapter delegate
 */
interface CreditListAdapterDelegate{
    fun onCreditItemClick(credit: Credit)

    fun onCreditItemLongClick(credit: Credit)
}


/**
 * Credit list adapter
 */
class CreditListAdapter constructor(private var context: Context, val delegate: CreditListAdapterDelegate? = null) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>(),
    CreditViewHolderDelegate {

    var creditItemList: List<CreditItem> = listOf()

    private var recyclerView: RecyclerView? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = ItemCreditBinding.inflate(LayoutInflater.from(context), parent, false)
        return CreditViewHolder(binding, context, this)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (creditItemList.isEmpty()) return

        val credit = creditItemList.get(position)

        (holder as CreditViewHolder).updateUI(credit)
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)

        //recyclerView.addItemDecoration(RecyclerViewItemDecoration(context))
        this.recyclerView = recyclerView
    }

    override fun getItemCount(): Int {
        return creditItemList.count()
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    // TaskViewHolderDelegate methods
    override fun onCreditItemClick(credit: Credit) {
        delegate?.onCreditItemClick(credit)
    }

    override fun onCreditItemLongClick(credit: Credit) {
        delegate?.onCreditItemLongClick(credit)
    }

}

/**
 * Credit item view holder delegate
 */

interface CreditViewHolderDelegate{
    fun onCreditItemClick(credit: Credit)

    fun onCreditItemLongClick(credit: Credit)
}

class CreditViewHolder constructor(binding: ItemCreditBinding, val context: Context, val delegate: CreditViewHolderDelegate? = null): RecyclerView.ViewHolder(binding.root) {

    private val creditItemLayout = binding.creditItemLayout
    private val imgType = binding.imgType
    private val imgDate = binding.imgDate
    private val tvName = binding.tvName
    private val tvSummaPay = binding.tvSummaPay
    private val imgFinish = binding.imgFinish
    private val tvDate = binding.tvDate
    private val tvSummaFinRes = binding.tvSummaFinRes
    private val closeCreditStamp = binding.closeCreditStamp
    private val tvParam = binding.tvParam
    private val tvInProgressSumma = binding.tvInProgressSumma
    private val viewProgressBar = binding.pbProgress
    private val tvInProgressSumma_credit = binding.tvInProgressSummaCredit
    private val tvSummaRest = binding.tvSummaRest

    fun updateUI(creditItem: CreditItem) {

        creditItemLayout.setOnClickListener{
            delegate?.onCreditItemClick(creditItem.credit)
        }

        creditItemLayout.setOnLongClickListener {
            delegate?.onCreditItemLongClick(creditItem.credit)
            true
        }

        val credit = creditItem.credit
        val result = creditItem.result

        val RUB = Html.fromHtml(" &#x20bd")

        val decFormat_2 = DecimalFormat("###,###.##")
        val decFormat = DecimalFormat("###,###")

        val name = credit.name

        Log.d("DMS", "credit name = $name")

        var date: Long = 0
        var date_pay: Long = 0
        var date_last_pay: Long = 0
        val date_end: Long = 0
        var curdate = Date().time

            date = credit.date

            date_pay = result .date_pay

            date_last_pay = result.date_last_pay

            if (date_last_pay == 0L) date_last_pay = Date().time


        val summa = result.summa
        val summa_payment = result.summa_payment
        val param_period = result.param_period
        val param_procent = result.param_procent
        val summa_rest = result.summa_rest

        val credit_summa_to_pay = result.credit_summa_to_pay
        var summa_to_pay = result.summa_to_pay

        val summa_fin_res = result.summa_fin_res

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

        // Тип кредита (1-ипотека, 2-авто-кредит, 3-потребительский, 4-страхование и 5-прочие)
        val type_credit = credit.type
        Log.d("DMS", "credit type = $type_credit")
        imgType.visible()
        when (type_credit) {
            CreditType.Flat -> imgType.setImageResource(R.drawable.type_credit_flat)
            CreditType.Auto -> imgType.setImageResource(R.drawable.type_credit_auto)
            CreditType.Stuff -> imgType.setImageResource(R.drawable.type_credit_things)
            CreditType.Ensure -> imgType.setImageResource(R.drawable.type_credit_ensure)
            CreditType.Parking -> imgType.setImageResource(R.drawable.type_credit_parking)

            else -> imgType.invisible()
        }

        date_pay = getBegDay(date_pay)
        curdate = getBegDay(curdate)

        // Кол-во дней до очередного платежа
        val dayRest = diffDateInDays(date_pay, curdate)
        //(int)((date_pay - curdate) / (24 * 60 * 60 * 1000)); // миллисекунды / (24ч * 60мин * 60сек * 1000мс);

        Log.d("DMS", "dayRest = $dayRest")

        imgDate.visibility = View.VISIBLE

        if (dayRest >= 0) {
            if (dayRest <= 14) {
                imgDate.visibility = View.VISIBLE
                when (dayRest) {
                    0 -> imgDate.setImageResource(R.drawable.day0)
                    1 -> imgDate.setImageResource(R.drawable.day1)
                    2 -> imgDate.setImageResource(R.drawable.day2)
                    3 -> imgDate.setImageResource(R.drawable.day3)
                    4 -> imgDate.setImageResource(R.drawable.day4)
                    5 -> imgDate.setImageResource(R.drawable.day5)
                    6 -> imgDate.setImageResource(R.drawable.day6)
                    7 -> imgDate.setImageResource(R.drawable.day7)
                    8 -> imgDate.setImageResource(R.drawable.day8)
                    9 -> imgDate.setImageResource(R.drawable.day9)
                    else -> imgDate.setImageResource(R.drawable.day9plus)
                }
            } else {
                imgDate.visibility = View.GONE
            }
        } else {
            imgDate.setImageResource(R.drawable.day0)
        }


        // Дата последнего платежа
        val str_date_cr = dateFormatDec.format(date)

        if (summa_rest > 1) { // Действующий кредит
            // Заголовок
            tvName.text = name

            // Сумма к оплате
            if (summa_to_pay.compareTo(0.001) <= 0 && summa_rest > 0) summa_to_pay = credit_summa_to_pay
            tvSummaPay.text = "к оплате " + decFormat_2.format(summa_to_pay) + RUB

            // Очередная дата оплаты
            val dateFormat = SimpleDateFormat("E, dd MMMM yyyy 'г.'")
            val str_date = dateFormat.format(if (date_pay == 0L) date else date_pay)

            tvDate.text = str_date

            if (dayRest <= 14 && dayRest > 7)
                tvDate.setTextColor(Color.parseColor("#FF098900"))
            else if (dayRest <= 7 && dayRest > 2)
                tvDate.setTextColor(Color.parseColor("#FF0227CC"))
            else if (dayRest <= 2)
                tvDate.setTextColor(Color.parseColor("#d63301"))
            else
                tvDate.setTextColor(Color.parseColor("#FF0D0C0C"))

            imgFinish.visibility = View.GONE
            // imgDate.setVisibility(View.VISIBLE);

            closeCreditStamp.visibility = View.GONE

            tvName.paintFlags = Paint.FAKE_BOLD_TEXT_FLAG

        } else { //Считаем что кредит закрыт

            // Типа зачеркнутый заголовок
            val myText = "<s><strike>$name</strike></s>"
            Log.d("DNS", "1=$myText")
            tvName.setText(Html.fromHtml(myText), TextView.BufferType.SPANNABLE)

            tvName.paintFlags = (Paint.FAKE_BOLD_TEXT_FLAG + Paint.STRIKE_THRU_TEXT_FLAG)

            tvSummaPay.text = "Закрыт"

            tvDate.text = ""

            imgFinish.visibility = View.VISIBLE
            imgDate.visibility = View.GONE

            closeCreditStamp.visibility = View.VISIBLE

        }

        // Остаток суммы

        tvSummaRest.text = decFormat.format(summa_rest) + RUB

        // Финансовый результат
        tvSummaFinRes.text = decFormat.format(summa_fin_res) + RUB

        if (summa_fin_res < 0)
            tvSummaFinRes.setTextColor(Color.RED)
        else
            tvSummaFinRes.setTextColor(Color.BLUE)

        /// Прогресс бар



        viewProgressBar.max = Math.round(summa).toInt()
        viewProgressBar.progress = Math.round(summa_payment).toInt()


        tvInProgressSumma.text = decFormat.format(summa) + RUB


        // Процент выполнения

        val proc : Double =
            if (isZero(summa))
                0.0
            else
                summa_payment / summa * 100

        val summa_pay = decFormat.format(summa_payment) + RUB + String.format(" (%.0f%%)", proc)

        tvInProgressSumma_credit.text = summa_pay

        tvParam.text = "Сумма ${decFormat.format(summa)} под ${formatD(param_procent)}% на $param_period мес.\nДата $str_date_cr - $str_date_end ($str_period)"

    }

}


