package com.example.financeassistant.main_window.creditPage

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import com.example.financeassistant.credit.CreditItemActivity
import com.example.financeassistant.R
import com.example.financeassistant.base.BaseFragment
import com.example.financeassistant.classes.ARGUMENT_PAGE_NUMBER
import com.example.financeassistant.classes.Credit
import com.example.financeassistant.classes.CreditItem
import com.example.financeassistant.classes.Payment
import com.example.financeassistant.classes.SWIPE_REFRESH_DISTANCE_TO_TRIGGER
import com.example.financeassistant.databinding.CreditFragmentBinding
import com.example.financeassistant.manager.SettingsManager
import com.example.financeassistant.utils.Navigator
import com.example.financeassistant.utils.NavigatorResultCode
import com.example.financeassistant.utils.ToolbarUtils
import com.google.gson.Gson

class CreditPageFragment : BaseFragment<CreditFragmentBinding>(), CreditListAdapterDelegate {

    companion object {

        fun newInstance(page: Int): CreditPageFragment {
            val pageFragment = CreditPageFragment()
            val arguments = Bundle()
            arguments.putInt(ARGUMENT_PAGE_NUMBER, page)
            pageFragment.arguments = arguments
            return pageFragment
        }

    }

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> CreditFragmentBinding
        = CreditFragmentBinding::inflate

    val PAGE = 1

    val DIALOG_DELETE_ALL = 1

    var pageNumber: Int = 0

    val CM_DELETE_ID = 1
    val CM_PAYMENT_ID = 2
    val CM_EDIT_ID = 3
    val CM_DIAGRAM_ID = 4

    private val viewModel: CreditListViewModel by activityViewModels()
    lateinit var creditListAdapter: CreditListAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.also { activity ->
            arguments?.also { arguments ->
                pageNumber = arguments.getInt(ARGUMENT_PAGE_NUMBER)
            }
        }

        bindViewModel()

        setHasOptionsMenu(true)

        setupCreditListAdapter()

        initSwipeRefresh()

        setListeners()

        loadCreditList()

    }

    private fun bindViewModel() {
        viewModel.creditItemList.observe(viewLifecycleOwner, Observer { creditItemList ->
            creditItemList?.also { creditItemList ->
                updateAdapter(creditItemList)
            }
        })

        viewModel.showCreditLoadingIndicatorEvent.observe(viewLifecycleOwner, Observer {
            showCreditLoadingIndicator()
        })

        viewModel.hideCreditLoadingIndicatorEvent.observe(viewLifecycleOwner, Observer {
            hideCreditLoadingIndicator()
        })

        viewModel.creditPayToOpen.observe(viewLifecycleOwner, Observer { creditPayToOpen ->
            creditPayToOpen?.also { creditPayToOpen ->
                showCurrentPaymentView(creditPayToOpen)
            }
        })

        viewModel.creditToOpen.observe(viewLifecycleOwner, Observer { creditToOpen ->
            creditToOpen?.also { creditToOpen ->
                showCreditView(creditToOpen)
            }
        })

    }

    private fun setupCreditListAdapter(){
        activity?.also { activity ->
            creditListAdapter = CreditListAdapter(activity, this)
            binding.creditRecyclerView.layoutManager = LinearLayoutManager(activity)
            binding.creditRecyclerView.adapter = creditListAdapter
        }
    }

    private fun updateAdapter(creditItemList: List<CreditItem>){
        creditListAdapter.creditItemList = creditItemList
        creditListAdapter.notifyDataSetChanged()
    }

    private fun initSwipeRefresh() {
        binding.creditSwipeRefreshLayout.setOnRefreshListener( object : OnRefreshListener {
            override fun onRefresh() {
                loadCreditList()
            }
        })

        binding.creditSwipeRefreshLayout.setDistanceToTriggerSync(SWIPE_REFRESH_DISTANCE_TO_TRIGGER)
    }

    private fun setListeners() {
    }

    fun showContextDialog(credit: Credit) {
        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle("${credit.name}")
        builder.setNegativeButton("Отмена", null)
        val creditDialogItems: MutableList<String> = mutableListOf()
        creditDialogItems.add(getString(R.string.action_credit_editcredit))
        creditDialogItems.add(getString(R.string.action_credit_addpayment))
        creditDialogItems.add(getString(R.string.action_credit_diagram))
        creditDialogItems.add(getString(R.string.action_credit_delete))
        builder.setItems(creditDialogItems.toTypedArray()) { dialog, which ->
            when (which) {
                0 -> viewModel.onCreditEditClick(credit)
                1 -> viewModel.onCreditPaymentClick(credit)
                2 -> Navigator.navigateToCreditDiagramActivity(requireActivity(), this, credit)
                3 -> showDeleteCreditDialog(credit)
                else -> {
                }
            }
        }
        builder.show()
    }

    private fun showDeleteCreditDialog(credit: Credit) {
        activity?.also { activity ->
            val ad = AlertDialog.Builder(activity)
            ad.setTitle(credit.name)  // заголовок
            ad.setMessage("Все данные по кредиту будут удалены!\n\nУдалить кредит?") // сообщение
            ad.setPositiveButton("Да") { dialog, arg1 ->
                viewModel.deleteCredit(credit)
            }

            ad.setNegativeButton("Отмена") { dialog, arg1 ->

            }

            ad.setCancelable(true)
            ad.setOnCancelListener { }

            ad.show()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item!!.itemId

        if (id == R.id.action_option_show_closed) {
            SettingsManager.instance.setShowCloseCreditSettings(true)
            ToolbarUtils.setupActionBar(activity as AppCompatActivity, PAGE)
            loadCreditList()
        }

        if (id == R.id.action_option_hide_closed) {
            SettingsManager.instance.setShowCloseCreditSettings(false)
            ToolbarUtils.setupActionBar(activity as AppCompatActivity, PAGE)
            loadCreditList()
        }

        if (id == R.id.action_add) {
            val intent = Intent(activity, CreditItemActivity::class.java)
            startActivityForResult(intent, 1)
        }

        if (id == R.id.action_option_add) {
            val intent = Intent(activity, CreditItemActivity::class.java)
            startActivityForResult(intent, 1)
        }

        if (id == R.id.action_option_delete_all) {
            deleteAllCreditsDialog()
        }

        return super.onOptionsItemSelected(item)
    }

    private fun loadCreditList() {
        viewModel.getCreditList()
    }

    private fun deleteAllCreditsDialog() {
        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle("Удалить все кредиты?")
        builder.setPositiveButton("Да") { _, _ ->
            viewModel.deleteAllCredits()
        }
        builder.setNegativeButton("Отмена", null)
        builder.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                val cred_name = data!!.getStringExtra("name")

                Toast.makeText(activity, "Создан новый кредит $cred_name", Toast.LENGTH_LONG).show()
            }
        }

        if (requestCode == NavigatorResultCode.Payment.resultCode) {
            if (resultCode == Activity.RESULT_OK) {

                data?.let { data ->
                    if (data.hasExtra(Navigator.EXTRA_PAYMENT_KEY)) {
                        val payGson = data.getStringExtra(Navigator.EXTRA_PAYMENT_KEY)
                        val pay: Payment = Gson().fromJson(payGson, Payment::class.java)
                        val summa = data.getStringExtra("summa")
                        Toast.makeText(activity, "Оплата кредита на сумму ${pay.summa}", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }

        if (requestCode == NavigatorResultCode.Graphic.resultCode) {
            if (resultCode == Activity.RESULT_OK) {

            }
        }

        loadCreditList()
    }

    private fun showCreditView(credit: Credit) {
        Navigator.navigateToCreditActivity(requireActivity(), this, credit)
    }

    private fun showCurrentPaymentView(payment: Payment) {
        Navigator.navigateToPaymentActivity(requireActivity(), this, payment)
    }

    private fun showCreditLoadingIndicator() {
//        taskRecyclerView.visibility = View.GONE
//        taskProgressBar.visibility = View.VISIBLE
    }

    private fun hideCreditLoadingIndicator() {
        binding.creditSwipeRefreshLayout.isRefreshing = false
//        taskRecyclerView.visibility = View.VISIBLE
//        taskProgressBar.visibility = View.GONE
    }

    override fun onCreditItemClick(credit: Credit) {
        activity?.also { activity ->
            Navigator.navigateToGraphicActivity(activity, this, credit)
        }
    }

    override fun onCreditItemLongClick(credit: Credit) {
        activity?.also { activity ->
            showContextDialog(credit)
        }
    }

}