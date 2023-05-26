package com.example.financeassistant.graphic

import android.app.DatePickerDialog
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.text.format.DateUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.example.financeassistant.database.DB
import com.example.financeassistant.R
import com.example.financeassistant.base.ViewBindingActivity
import com.example.financeassistant.classes.Credit
import com.example.financeassistant.classes.Payment
import com.example.financeassistant.databinding.ActivityGraphicItemBinding
import com.example.financeassistant.utils.*
import com.google.gson.Gson
import java.util.*

class GraphicItemActivity : ViewBindingActivity<ActivityGraphicItemBinding>() {

    override val bindingInflater: (LayoutInflater) -> ActivityGraphicItemBinding
        = ActivityGraphicItemBinding::inflate

    lateinit var db: DB
    var credit_id: Int = 0
    var payment_id = -1
    lateinit var credit: Credit
    lateinit var payment: Payment

    internal var dateAndTime = Calendar.getInstance()

    // установка обработчика выбора даты
    internal var d: DatePickerDialog.OnDateSetListener = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
        dateAndTime.set(Calendar.YEAR, year)
        dateAndTime.set(Calendar.MONTH, monthOfYear)
        dateAndTime.set(Calendar.DAY_OF_MONTH, dayOfMonth)
        setInitialDateTime()
    }

    override fun setup() {

        ToolbarUtils.initToolbar(this, true, R.string.toolbar_credit, R.color.CreditItemToolbar, R.color.CreditItemWindowsBar)

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
            payment = db.GetGraphicItem(payment_id)
        } else {
            ToolbarUtils.setNewFlag(this)
        }

        binding.etSumma.setText(round2Str(payment.summa, 2))
        binding.etSummaCredit.setText(round2Str(payment.summa_credit, 2))
        binding.etSummaProcent.setText(round2Str(payment.summa_procent, 2))

        binding.etComment.setText(payment.comment)

        if (payment.date > 0) {
            dateAndTime.timeInMillis = payment.date
            setInitialDateTime()
        }

    }

    // ====== Toolbar ======
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_toolbar_item, menu)
        return true
    }

    fun setListeners() {
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
                    db.graphic_Delete(credit_id)
                    finish()
                    return@OnMenuItemClickListener true
                }
            }// return super.onOptionsItemSelected(item);

            // Handle the menu item
            true
        })

        binding.etSummaCredit.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
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
                        round2Dec(binding.etSummaCredit.text.toString(), 2))

                binding.etSummaProcent.setText(new_proc.toString())

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

    fun onClickAdd(v: View?) {

        var summa: Double
        var summa_credit: Double
        var summa_procent: Double
        summa_procent = 0.0
        summa_credit = summa_procent
        summa = summa_credit

        val period = 0

        val str_summa = binding.etSumma.text.toString()

        var str_summa_credit = binding.etSummaCredit.text.toString()
        var str_summa_procent = binding.etSummaProcent.text.toString()


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


        summa = java.lang.Double.parseDouble(str_summa)
        summa_credit = java.lang.Double.parseDouble(str_summa_credit)
        summa_procent = java.lang.Double.parseDouble(str_summa_procent)

        val date = dateAndTime.timeInMillis
        val comment = binding.etComment.text.toString()

        payment = Payment(
            credit = credit,
            date = date,
            summa = summa,
            summa_credit = summa_credit,
            summa_procent = summa_procent,
            comment = comment
        )

        if (payment_id > 0) {
            payment.id = payment_id
            db.graphic_Update(payment)
        } else
            db.graphic_Add(payment)

        Navigator.exitFromGraphicItemActivity(this,
                if (payment_id > 0)
                    Actions.Update.type
                else
                    Actions.Create.type,
                payment
        )

    }


}
