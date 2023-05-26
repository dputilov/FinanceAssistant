package com.example.financeassistant.payment

import android.app.DatePickerDialog
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.text.format.DateUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.example.financeassistant.database.DB
import com.example.financeassistant.R
import com.example.financeassistant.base.ViewBindingActivity
import com.example.financeassistant.classes.Credit
import com.example.financeassistant.classes.Payment
import com.example.financeassistant.databinding.ActivityPaymentBinding
import com.example.financeassistant.utils.*
import com.google.gson.Gson
import java.math.BigDecimal
import java.util.*

class PaymentActivity : ViewBindingActivity<ActivityPaymentBinding>() {

    override val bindingInflater: (LayoutInflater) -> ActivityPaymentBinding
        = ActivityPaymentBinding::inflate
    
    lateinit var db: DB
    var credit_id: Int = 0
    var payment_id = -1
    lateinit var credit: Credit
    lateinit var payment: Payment

    internal var dateAndTime = Calendar.getInstance()
    internal var tvNew: TextView? = null

    // установка обработчика выбора даты
    internal var d: DatePickerDialog.OnDateSetListener = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
        dateAndTime.set(Calendar.YEAR, year)
        dateAndTime.set(Calendar.MONTH, monthOfYear)
        dateAndTime.set(Calendar.DAY_OF_MONTH, dayOfMonth)
        setInitialDateTime()
    }

    override fun setup() {

        // === ToolBar ===
        ToolbarUtils.initToolbar(this, true, R.string.toolbar_payment, R.color.PaymentItemToolbar, R.color.PaymentItemWindowsBar)

        // открываем подключение к БД
        db = DB(this)
        db.open()

        if (!intent.hasExtra(Navigator.EXTRA_PAYMENT_KEY)) return

        val paymentGson = intent.getStringExtra(Navigator.EXTRA_PAYMENT_KEY)
        payment = Gson().fromJson(paymentGson, Payment::class.java)
        credit_id = payment.getCreditId()
        payment_id = payment.id

        if (credit_id <= 0) return  // нет ссылки на кредит => выход

        initUI()

        setListeners()

        binding.etSumma.requestFocus()

    }

    // ====== Toolbar ======
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_toolbar_item, menu)
        return true
    }

    private fun initUI(){
        credit = db.getCredit(credit_id)

        val name = credit.name + " от " + DateUtils.formatDateTime(this,
                credit.date, DateUtils.FORMAT_SHOW_DATE or DateUtils.FORMAT_SHOW_YEAR)
        val param = "Сумма: " + String.format("%.0f", credit.summa) + ", Процент: " + String.format("%.2f%%", credit.procent) + ", Срок: " + credit.period.toString()

        binding.tvName.text = name
        binding.tvParam.text = param

        setInitialDateTime()


        if (payment_id > 0) {
            payment = db.GetPayment(payment_id)
        } else {
            ToolbarUtils.setNewFlag(this)
        }

        binding.etSumma.setText(round2Str(payment.summa, 2))
        binding.etSummaCredit.setText(round2Str(payment.summa_credit, 2))
        binding.etSummaProcent.setText(round2Str(payment.summa_procent, 2))
        binding.etSummaAddon.setText(round2Str(payment.summa_addon, 2))
        binding.etSummaPlus.setText(round2Str(payment.summa_plus, 2))
        binding.etSummaMinus.setText(round2Str(payment.summa_minus, 2))

        binding.etComment.setText(payment.comment)

        if (payment.date > 0) {
            dateAndTime.timeInMillis = payment.date
            setInitialDateTime()
        }

    }

    private fun setListeners(){

        // Set an OnMenuItemClickListener to handle menu item clicks
        val toolbar = findViewById<Toolbar>(R.id.toolbar_actionbar_item)
        toolbar.setOnMenuItemClickListener(Toolbar.OnMenuItemClickListener { item ->
            Log.d("DMS", "ПУНКТ МЕНЮ ###" + item.itemId)

            when (item.itemId) {
                android.R.id.home    //button home
                -> {
                    finish()
                    return@OnMenuItemClickListener true
                }
                R.id.action_OK -> {
                    onClickAdd(null)

                    return@OnMenuItemClickListener true
                }
                R.id.action_delete -> {
                    onClickDelete()
                    return@OnMenuItemClickListener true
                }
            }// return super.onOptionsItemSelected(item);

            // Handle the menu item
            true
        })

        binding.etSummaCredit.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                if(binding.etSummaCredit.hasFocus()) {
                    // Запрет на ввод более 2-х дробных знаков
                    val str = s.toString()
                    val p = str.indexOf(".")
                    if (p != -1) {
                        val tmpStr = str.substring(p)
                        if (tmpStr.length == 4) {
                            s.delete(s.length - 1, s.length)
                        }
                    }

                    val new_proc = round2Dec(binding.etSumma.text.toString(), 2).subtract(
                            round2Dec(binding.etSummaCredit.text.toString(), 2)).max(BigDecimal.ZERO)

                    binding.etSummaProcent.setText(new_proc.toString())
                }
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })

        binding.etSummaProcent.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                if(binding.etSummaProcent.hasFocus()) {
                    // Запрет на ввод более 2-х дробных знаков
                    val str = s.toString()
                    val p = str.indexOf(".")
                    if (p != -1) {
                        val tmpStr = str.substring(p)
                        if (tmpStr.length == 4) {
                            s.delete(s.length - 1, s.length)
                        }
                    }

                    val new_sum = round2Dec(binding.etSumma.text.toString(), 2).subtract(
                            round2Dec(binding.etSummaProcent.text.toString(), 2)).max(BigDecimal.ZERO)

                    binding.etSummaCredit.setText(new_sum.toString())
                }
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })


        binding.etSumma.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                if(binding.etSumma.hasFocus()) {
                    // Запрет на ввод более 2-х дробных знаков
                    val str = s.toString()
                    val p = str.indexOf(".")
                    if (p != -1) {
                        val tmpStr = str.substring(p)
                        if (tmpStr.length == 4) {
                            s.delete(s.length - 1, s.length)
                        }
                    }

                    val new_sum = round2Dec(binding.etSumma.text.toString(), 2).subtract(
                            round2Dec(binding.etSummaProcent.text.toString(), 2)).max(BigDecimal.ZERO)

                    binding.etSummaCredit.setText(new_sum.toString())

                    try {
                        val new_sum_addon = round2Dec(binding.etSumma.text.toString(), 2).subtract(
                                round2Dec(payment.summa.toString(), 2)).max(BigDecimal.ZERO)

                       // Log.d("DMS_CREDIT", "payment.summa_credit=${payment.summa_credit} (${round2Dec(payment.summa_credit.toString(), 2)}) new_sum_addon = $new_sum_addon")

                        binding.etSummaAddon.setText(new_sum_addon.toString())
                    }
                    catch (e: Throwable){

                    }
                }
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })

    }


    // отображаем диалоговое окно для выбора даты
    fun setDate(v: View) {
        DatePickerDialog(this, d,
                dateAndTime.get(Calendar.YEAR),
                dateAndTime.get(Calendar.MONTH),
                dateAndTime.get(Calendar.DAY_OF_MONTH))
                .show()
    }

    // установка начальных даты и времени
    private fun setInitialDateTime() {
        binding.etDate.setText(DateUtils.formatDateTime(this,
                dateAndTime.timeInMillis,
                DateUtils.FORMAT_NUMERIC_DATE or DateUtils.FORMAT_SHOW_YEAR
                //                DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR | DateUtils.FORMAT_SHOW_TIME));
                //                DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR
        ))
    }

    override fun onDestroy() {
        super.onDestroy()
        // закрываем подключение при выходе
        db.close()
    }

    fun onClickCancel(v: View) {
        finish()
    }

    fun onClickAddonSumm(v: View) {
        binding.etSummaAddon.text = binding.etSummaCredit.text
    }

    fun onClickSummaCreditClearButton(v: View) {
        binding.etSummaCredit.setText("")
    }

    fun onClickSummaProcentClearButton(v: View) {
        binding.etSummaProcent.setText("")
    }

    fun onClickAddonSummaClearButton(v: View) {
        binding.etSummaAddon.setText("")
    }

    fun onClickAdd(v: View?) {

        var summa: Double
        var summa_credit: Double
        var summa_procent: Double
        var summa_addon: Double
        var summa_plus: Double
        var summa_minus: Double
        summa_minus = 0.0
        summa_plus = summa_minus
        summa_addon = summa_plus
        summa_procent = summa_addon
        summa_credit = summa_procent
        summa = summa_credit

        val period = 0

//        val etSumma = findViewById<View>(R.id.etSumma) as EditText
//        val etSummaCredit = findViewById<View>(R.id.etSummaCredit) as EditText
//        val etSummaProcent = findViewById<View>(R.id.etSummaProcent) as EditText
//        val etSummaAddon = findViewById<View>(R.id.etSummaAddon) as EditText
//        val etSummaPlus = findViewById<View>(R.id.etSummaPlus) as EditText
//        val etSummaMinus = findViewById<View>(R.id.etSummaMinus) as EditText


        val str_summa = binding.etSumma.text.toString()

        var str_summa_credit = binding.etSummaCredit.text.toString()
        var str_summa_procent = binding.etSummaProcent.text.toString()
        var str_summa_addon = binding.etSummaAddon.text.toString()
        var str_summa_plus = binding.etSummaPlus.text.toString()
        var str_summa_minus = binding.etSummaMinus.text.toString()



        if (TextUtils.isEmpty(str_summa)) {
            Toast.makeText(this, R.string.credit_item_error_summa, Toast.LENGTH_LONG).show()
            return
        }

        if (TextUtils.isEmpty(str_summa_credit)) {
            str_summa_credit = "0"
        }

        if (TextUtils.isEmpty(str_summa_procent)) {
            str_summa_procent = "0"
        }

        if (TextUtils.isEmpty(str_summa_plus)) {
            str_summa_plus = "0"
        }

        if (TextUtils.isEmpty(str_summa_minus)) {
            str_summa_minus = "0"
        }

        if (TextUtils.isEmpty(str_summa_addon)) {
            str_summa_addon = "0"
        }

        summa = java.lang.Double.parseDouble(str_summa)
        summa_credit = java.lang.Double.parseDouble(str_summa_credit)
        summa_procent = java.lang.Double.parseDouble(str_summa_procent)

        summa_plus = java.lang.Double.parseDouble(str_summa_plus)
        summa_minus = java.lang.Double.parseDouble(str_summa_minus)
        summa_addon = java.lang.Double.parseDouble(str_summa_addon)

        val date = dateAndTime.timeInMillis
        val comment = binding.etComment.text.toString()

        payment = Payment(
            credit = credit,
            date = date,
            summa = summa,
            summa_credit = summa_credit,
            summa_procent = summa_procent,
            summa_addon = summa_addon,
            summa_plus = summa_plus,
            summa_minus = summa_minus,
            comment = comment
        )

        if (!checkPayment(payment)){
            return
        }

        if (payment_id > 0) {
            payment.id = payment_id
            db.payment_Update(payment)
        } else
            db.payment_Add(payment)

        //=====================================================
        // Автозакрытие задач
        //===================
        val t = db.autoCloseTask(payment)
        if (t.id > 0) {
            Toast.makeText(baseContext, "Закрыта задача " + t.name + " от " + DateUtils.formatDateTime(this,
                    t.date,
                    DateUtils.FORMAT_NUMERIC_DATE or DateUtils.FORMAT_SHOW_YEAR), Toast.LENGTH_SHORT).show()
        }
        //== Автозакрытие задач (конец)===

        Navigator.exitFromPaymentActivity(this,
                if (payment_id > 0)
                    Actions.Update.type
                else
                    Actions.Create.type,
                payment)

    }

    private fun checkPayment(payment: Payment) : Boolean {
        var result = true
        if (D2L(payment.summa) != D2L(payment.summa_credit + payment.summa_procent) ) {
            Toast.makeText(baseContext, "Общая сумма платежа должна совпадать с Кредит + Процент", Toast.LENGTH_SHORT).show()
            result = false
        }
        return result
    }

    private fun onClickDelete(){
        db.payment_Delete(payment_id)
        Toast.makeText(baseContext, "Удален платеж от " + DateUtils.formatDateTime(this,
                dateAndTime.timeInMillis,
                DateUtils.FORMAT_NUMERIC_DATE or DateUtils.FORMAT_SHOW_YEAR), Toast.LENGTH_SHORT).show()


        Navigator.exitFromPaymentActivity(this, Actions.Delete.type, payment)

    }

}
