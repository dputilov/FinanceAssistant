package com.example.financeassistant.graphic

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.text.format.DateUtils
import android.util.Log
import com.example.financeassistant.R
import android.text.Editable
import android.text.TextWatcher
import android.view.ContextMenu
import android.view.ContextMenu.ContextMenuInfo
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.financeassistant.base.BaseFragment
import com.example.financeassistant.classes.Credit
import com.example.financeassistant.classes.Payment
import com.example.financeassistant.databinding.GraphicFragmentBinding
import com.example.financeassistant.utils.D2L
import com.example.financeassistant.utils.Navigator
import com.example.financeassistant.utils.NavigatorResultCode
import com.example.financeassistant.utils.formatD
import com.example.financeassistant.utils.formatD2
import com.example.financeassistant.utils.getCurrentDateStr
import com.example.financeassistant.utils.str2Double
import com.google.gson.Gson
import java.util.Calendar

class GraphicListFragment : BaseFragment<GraphicFragmentBinding>(), GraphicListAdapterDelegate {

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> GraphicFragmentBinding
        = GraphicFragmentBinding::inflate

    val CM_DELETE_ID = 1
    val CM_PAYMENT_ID = 2
    val CM_EDIT_ID = 3

    // Дата  - определяет дату с которой считать плановые платежи
    // следующие даты будут = +1 месяц и т.д.
    var dateAndTime = Calendar.getInstance()

    private val viewModel: GraphicListViewModel by activityViewModels()

    // Основной список - график платежей
    // включает как уже совешенные платежи, так и плановые платежи
    private var graphicAdapter : GraphicListAdapter? = null

    // установка обработчика выбора даты
    var d: DatePickerDialog.OnDateSetListener = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
        dateAndTime.set(Calendar.YEAR, year)
        dateAndTime.set(Calendar.MONTH, monthOfYear)
        dateAndTime.set(Calendar.DAY_OF_MONTH, dayOfMonth)

        viewModel.setNextPaymentDate(dateAndTime.timeInMillis)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        var needInitDate = true

        bindViewModel()

        // === Инициализация текущего кредита, по которому открывается график платежей ===
        activity?.intent?.let {
            val creditGson = it.getStringExtra(Navigator.EXTRA_CREDIT_KEY)
            val credit = Gson().fromJson(creditGson, Credit::class.java)

            initAdapter()

            setListeners()

            viewModel.Init(credit)
        }

    }

    override fun onDestroy() {
        super.onDestroy()

        graphicAdapter = null
    }

    private fun bindViewModel() {
        viewModel.graphicListData.observe(viewLifecycleOwner, Observer { graphicListData ->
            graphicListData?.also { graphicListData ->
                updateAdapter(graphicListData)
            }
        })

        viewModel.credit.observe(viewLifecycleOwner, Observer { credit ->
            setCreditHeader(credit)
        })

        viewModel.nextPaymentDate.observe(viewLifecycleOwner, Observer { nextPaymentDate ->
            nextPaymentDate?.also { nextPaymentDate ->
                setNextPaymentDate(nextPaymentDate)
            }
        })

        viewModel.summaPayAll.observe(viewLifecycleOwner, Observer { summa ->
            updateSummaPayAll(summa ?: 0.0)
        })

        viewModel.summaCreditAll.observe(viewLifecycleOwner, Observer { summa ->
            updateSummaCreditAll(summa ?: 0.0)
        })

        viewModel.summaProcentAll.observe(viewLifecycleOwner, Observer { summa ->
            updateSummaProcentAll(summa ?: 0.0)
        })

        viewModel.summaPayFactAll.observe(viewLifecycleOwner, Observer { summa ->
            updateSummaPayFactAll(summa ?: 0.0)
        })

        viewModel.summaCreditFactAll.observe(viewLifecycleOwner, Observer { summa ->
            updateSummaCreditFactAll(summa ?: 0.0)
        })

        viewModel.summaProcentFactAll.observe(viewLifecycleOwner, Observer { summa ->
            updateSummaProcentFactAll(summa ?: 0.0)
        })

        viewModel.summaNextPay.observe(viewLifecycleOwner, Observer { summa ->
            updateSummaNextPay()
        })

        viewModel.onlyProcentOnFirstPay.observe(viewLifecycleOwner, Observer { onlyProcentOnFirstPay ->
            updateOnlyProcentOnFirstPay(onlyProcentOnFirstPay ?: false)
        })

        viewModel.currentPayPosition.observe(viewLifecycleOwner, Observer { currentPayPosition ->
            setCurrentPosition(currentPayPosition ?: 0)
        })

        viewModel.showGraphicLoadingIndicatorEvent.observe(viewLifecycleOwner, Observer {
            showGraphicLoadingIndicator()
        })

        viewModel.hideGraphicLoadingIndicatorEvent.observe(viewLifecycleOwner, Observer {
            hideGraphicLoadingIndicator()
        })

        viewModel.showSaveGraphicLoadingIndicatorEvent.observe(viewLifecycleOwner, Observer {
            showSaveGraphicLoadingIndicator()
        })

        viewModel.hideSaveGraphicLoadingIndicatorEvent.observe(viewLifecycleOwner, Observer {
            hideSaveGraphicLoadingIndicator()
        })

        viewModel.graphicSavedEvent.observe(viewLifecycleOwner, Observer {
            onGraphicSaved()
        })

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == NavigatorResultCode.Payment.resultCode) {
            if (resultCode == Activity.RESULT_OK) {
                viewModel.getGraphicList()
            }
        }
    }

    private fun initAdapter(){
        graphicAdapter = GraphicListAdapter(requireActivity())
        binding.lvGraphic.layoutManager = LinearLayoutManager(activity)
        binding.lvGraphic.adapter = graphicAdapter

    }

    private fun updateAdapter(graphicListData: List<Payment>){
        graphicAdapter?.listData = graphicListData
        graphicAdapter?.notifyDataSetChanged()
    }


    fun setListeners(){
        //  ==== Обработчики событий ====

//        // Set an OnMenuItemClickListener to handle menu item clicks
        val toolbar = requireActivity().findViewById<Toolbar>(R.id.toolbar_actionbar_item)
        toolbar.setOnMenuItemClickListener(Toolbar.OnMenuItemClickListener { item ->
            Log.d("DMS", "ПУНКТ МЕНЮ ###" + item.itemId)

            val id = item.itemId

            when (id) {
                android.R.id.home    //button home
                -> {
                    Navigator.exitFromGraphicActivity(requireActivity())
                    return@OnMenuItemClickListener true
                }
                R.id.action_OK -> {
                    onClickAdd(null)
                    return@OnMenuItemClickListener true
                }
                R.id.action_delete -> {
                    onDeleteClick()
                    return@OnMenuItemClickListener true
                }
                R.id.toolbar_payment_add -> {
                    onAddNewPaymentClick()
                    return@OnMenuItemClickListener true
                }
                R.id.toolbar_graphic_add -> {
                    onAddNewGraphicClick()
                    return@OnMenuItemClickListener true
                }
                R.id.toolbar_payment_add_all -> {
                    onAddAllPaymentsClick()
                    return@OnMenuItemClickListener true
                }

                R.id.toolbar_delete_all -> {
                    onDeleteAllClick()
                    return@OnMenuItemClickListener true
                }
            }// return super.onOptionsItemSelected(item);

            // Handle the menu item
            true
        })

        binding.switchOnlyProcentOnFirstPay.setOnCheckedChangeListener({v, isChecked ->
            viewModel.setOnlyProcentOnFirstPay(isChecked)
        })

        binding.etSummaPay.addTextChangedListener(object : TextWatcher {

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                if (requireActivity().currentFocus != binding.etSummaPay) {
                    return
                }

                val text = s.toString()

                viewModel.setSummaNextPay(str2Double(text))

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
            }

        })

        binding.etSummaPayClearButton.setOnClickListener {
            onClickSummaPayClearButton()
        }

        binding.btExec.setOnClickListener {
            execGraphic()
        }

        binding.btSetDate.setOnClickListener {
            setDate()
        }

        // добавляем контекстное меню к списку
        registerForContextMenu(binding.lvGraphic)

    }

    fun getCurrentCredit(): Credit{
        return viewModel.getCredit()?.let {
            it
        } ?: Credit()
    }

    private fun onAddNewPaymentClick() {
        viewModel.getCredit()?.also {
            Navigator.navigateToPaymentActivity(requireActivity(), this, Payment(credit = it))
        }
    }

    private fun onAddNewGraphicClick() {
        viewModel.getCredit()?.also {
            Navigator.navigateToGraphicItemActivity(requireActivity(), this, Payment(credit = it))
        }
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        Log.d("DMS_CREDIT", "ПУНКТ МЕНЮ " + item.itemId)

        val id = item.itemId

        if (id == R.id.toolbar_payment_add) {
            onAddNewPaymentClick()
        }

        if (id == R.id.toolbar_graphic_add) {
            onAddNewGraphicClick()
        }

        if (id == R.id.toolbar_delete_all) {
            onDeleteAllClick()
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onCreateContextMenu(menu: ContextMenu, v: View, menuInfo: ContextMenuInfo?) {
        super.onCreateContextMenu(menu, v, menuInfo)

        menu.add(0, CM_PAYMENT_ID, 1, R.string.action_credit_addpayment)

        menu.add(0, CM_DELETE_ID, 100, R.string.action_payment_delete)

    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        if (item.itemId == CM_DELETE_ID) {
            // получаем из пункта контекстного меню данные по пункту списка
            val acmi = item.menuInfo as AdapterView.AdapterContextMenuInfo

            val index = acmi.position
            val listData = viewModel.getGraphicListData()

            val payment = listData[index]

            onDeletePaymentClick(payment)

            return true
        }

        if (item.itemId == CM_PAYMENT_ID) {
            // получаем из пункта контекстного меню данные по пункту списка
            val acmi = item.menuInfo as AdapterView.AdapterContextMenuInfo

            val listData = viewModel.getGraphicListData()
            val index = acmi.position

            val payment = listData[index]

            onAddPaymentClick(payment)

            return true
        }

        return super.onContextItemSelected(item)
    }

    // Внимание! Объект cred (текущий кредит) должен быть инициализирован
    fun setCreditHeader(credit : Credit?) {
        credit?.also {credit ->
            val name = credit.name + " от " + DateUtils.formatDateTime(activity,
                    credit.date, DateUtils.FORMAT_SHOW_DATE or DateUtils.FORMAT_SHOW_YEAR)

            val param = "Сумма: " + String.format("%.0f", credit.summa) + ", Процент: " + String.format("%.2f%%", credit.procent) + ", Срок: " + credit.period.toString()

            binding.tvName.text = name
            binding.tvParam.text = param
        }
    }

    // отображаем диалоговое окно для выбора даты
    fun setDate() {
        DatePickerDialog(requireActivity(), d,
                dateAndTime.get(Calendar.YEAR),
                dateAndTime.get(Calendar.MONTH),
                dateAndTime.get(Calendar.DAY_OF_MONTH))
                .show()
    }

    // установка начальных даты и времени
    private fun setInitialDateTime(date: Long) {
        dateAndTime.timeInMillis = date
        binding.etDate.setText(DateUtils.formatDateTime(activity,
                date,
                //                DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR | DateUtils.FORMAT_SHOW_TIME));
                DateUtils.FORMAT_SHOW_DATE or DateUtils.FORMAT_SHOW_YEAR))
    }


    // Обработчик кнопки Отмена / Закрыть / Назад
    fun onClickCancel(v: View) {
        Navigator.exitFromGraphicActivity(requireActivity())
    }

    // Обработчик кнопки Записать / ОК - сохранение графика в БД
    fun onClickAdd(v: View?) {
        viewModel.saveGraphic()
    }

    fun execGraphic(){
        hideKeyboard()
        viewModel.calculateCredit()
    }

    fun hideKeyboard(){
//        val inputManager = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//        inputManager.hideSoftInputFromWindow(requireActivity().currentFocus!!.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
    }

    fun showKeyboard(){
//        val inputManager = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//        inputManager.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0)
    }

    private fun setCurrentPosition(currentPayPosition: Int){

        graphicAdapter?.currentPayPosition = currentPayPosition
        graphicAdapter?.notifyDataSetChanged()

//        if (currentPayPosition>0) {
//            try {
//                binding.lvGraphic.set(currentPayPosition - 1)
//            } catch (e: Throwable) {
//
//            }
//        }

    }

    private fun setNextPaymentDate(date: Long){
        setInitialDateTime(date)
    }

    private fun updateSummaPayAll(summa: Double){
        binding.infoSummaValue.text = formatD2(summa)
    }

    private fun updateSummaCreditAll(summa: Double){
        binding.infoSummaCreditValue.text = formatD2(summa)
    }

    private fun updateSummaProcentFactAll(summa: Double){
        binding.infoSummaProcentFactValue.text = formatD2(summa)
    }

    private fun updateSummaPayFactAll(summa: Double){
        binding.infoSummaFactValue.text = formatD2(summa)
    }

    private fun updateSummaCreditFactAll(summa: Double){
        binding.infoSummaCreditFactValue.text = formatD2(summa)
    }

    private fun updateSummaProcentAll(summa: Double){
        binding.infoSummaProcentValue.text = formatD2(summa)
    }

    private fun showGraphicLoadingIndicator(){
        binding.lvGraphic.visibility = View.INVISIBLE
        binding.graphicProgressBar.visibility = View.VISIBLE
    }

    private fun hideGraphicLoadingIndicator(){
        binding.lvGraphic.visibility = View.VISIBLE
        binding.graphicProgressBar.visibility = View.GONE
    }

    private fun showSaveGraphicLoadingIndicator(){
        binding.calcLayout.visibility = View.INVISIBLE
        binding.saveIndicatorLayout.visibility = View.VISIBLE
    }

    private fun hideSaveGraphicLoadingIndicator(){
        binding.calcLayout.visibility = View.VISIBLE
        binding.saveIndicatorLayout.visibility = View.GONE
    }

    private fun updateSummaNextPay() {
        val summaNextPay = viewModel.getSummaNextPay()
        val currentSummaStr = binding.etSummaPay.text.toString()
        val currentSumma =
                if (currentSummaStr.isNotEmpty())
                    str2Double(currentSummaStr)
                else {
                    0.0
                }

        if (D2L(currentSumma) != D2L(summaNextPay)) {
            //Log.d("DMS_CREDIT", "etSummaPay.text.toString() = ${etSummaPay.text.toString()}  and summaNextPayStr = ${summaNextPay}" )
            binding.etSummaPay.setText(formatD(summaNextPay))
        }
    }

    private fun updateOnlyProcentOnFirstPay(flag: Boolean){
        if (binding.switchOnlyProcentOnFirstPay.isChecked != flag) {
            binding.switchOnlyProcentOnFirstPay.isChecked = flag
        }
    }

    private fun onGraphicSaved(){
        Navigator.exitFromGraphicListActivity(requireActivity(), getCurrentCredit())
    }

    private fun onAddAllPaymentsClick(){
        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle(getAlertTitle())
                .setMessage("Ввести все платежи до текущей даты ${getCurrentDateStr()}?")
                .setIcon(R.drawable.ic_launcher)
                .setCancelable(true)
                .setPositiveButton("Добавить платежи",
                        {dialog, id ->
                            viewModel.addAllPaymentsToCurrentDate()
                        })
                .setNegativeButton("Отмена",
                        { dialog, id ->
                            dialog.cancel()
                        })
        val alert = builder.create()
        alert.show()
    }

    private fun onDeleteClick(){
        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle(getAlertTitle())
                .setMessage("График платежей будет удален!\nПродолжить?")
                .setIcon(R.drawable.ic_launcher)
                .setCancelable(true)
                .setPositiveButton("Удалить график",
                        {dialog, id ->
                            viewModel.deleteGraphic()
                        })
                .setNegativeButton("Отмена",
                        { dialog, id ->
                            dialog.cancel()
                        })
        val alert = builder.create()
        alert.show()
    }

    private fun onDeleteAllClick(){
        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle(getAlertTitle())
                .setMessage("Все платежи будут удалены!\nПродолжить?")
                .setIcon(R.drawable.ic_launcher)
                .setCancelable(true)
                .setPositiveButton("Удалить все платежи",
                        {dialog, id ->
                            viewModel.deleteAllPayments()
                        })
                .setNegativeButton("Отмена",
                        { dialog, id ->
                            dialog.cancel()
                        })
        val alert = builder.create()
        alert.show()
    }

    private fun onDeletePaymentClick(payment: Payment){
        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle(getAlertTitle())
                .setMessage("Удалить платеж?")
                .setIcon(R.drawable.ic_launcher)
                .setCancelable(true)
                .setPositiveButton("Удалить платеж",
                        {dialog, id ->
                            viewModel.deletePayment(payment)
                        })
                .setNegativeButton("Отмена",
                        { dialog, id ->
                            dialog.cancel()
                        })
        val alert = builder.create()
        alert.show()
    }

    private fun onAddPaymentClick(payment: Payment){
        val done = payment.done

        if (done == 0) {
            payment.id = -1
            Navigator.navigateToPaymentActivity(requireActivity(), this, payment)
        } else {
            Toast.makeText(activity, "Платеж уже существует", Toast.LENGTH_LONG).show()
        }
    }

    private fun onItemPaymentClick(payment: Payment){
        // получаем ИД платежа из элемента списка, по его позиции

        if (payment.done == 1)
        // Если это факт - открываем для редактирования карточку платежа
            Navigator.navigateToPaymentActivity(requireActivity(), this, payment)
        else
        // Если это план - редактируем строку графика платежей
            Navigator.navigateToGraphicItemActivity(requireActivity(), this, payment)

    }

    private fun getAlertTitle(): String{
        return getString(R.string.graphic_alert_title)
    }

    private fun onClickSummaPayClearButton() {
        binding.etSummaPay.setText("")
        binding.etSummaPay.requestFocus()
        showKeyboard()
    }

    override fun onGraphicItemClick(item: Payment) {
        // По нажатию открывается либо текущий фактический платеж (если у него задан _id > 0)
        // либо новый платеж (_id<0) на основании параметров планового платежа

        onItemPaymentClick(item)
    }
}
