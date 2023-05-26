package com.example.financeassistant.diagram

import android.os.Bundle
import android.text.format.DateUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.SimpleAdapter
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.example.financeassistant.R
import com.example.financeassistant.base.BaseFragment
import com.example.financeassistant.classes.Credit
import com.example.financeassistant.classes.DiagramItem
import com.example.financeassistant.classes.toSimpleAdapterList
import com.example.financeassistant.databinding.CreditDiagramFragmentBinding
import com.example.financeassistant.utils.Navigator
import com.example.financeassistant.utils.ToolbarUtils
import com.example.financeassistant.utils.gone
import com.example.financeassistant.utils.visible
import com.google.gson.Gson
import kotlin.math.abs

class CreditDiagramFragment : BaseFragment<CreditDiagramFragmentBinding>() {

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> CreditDiagramFragmentBinding
        = CreditDiagramFragmentBinding::inflate

    var credit_id = -1 // ИД кредита

    private val viewModel: CreditDiagramViewModel by activityViewModels()
    private var creditDiagramPagerAdapter : CreditDiagramPagerAdapter? = null

    lateinit var creditFilterAdapter : ArrayAdapter<Credit>

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        bindViewModel()

        setDiagramPager()

        setListeners()

        // === Инициализация текущего кредита, по которому открывается график платежей ===
        var credit : Credit? = null
        activity?.intent?.extras?.let { extras ->
            if (extras.containsKey(Navigator.EXTRA_CREDIT_KEY)) {
                val creditGson = extras.getString(Navigator.EXTRA_CREDIT_KEY)
                Gson().fromJson(creditGson, Credit::class.java)?.also {
                    credit = it
                    credit_id = it.id
                }
            }

        }

        viewModel.initInstance(credit)

    }

    override fun onDestroy() {
        super.onDestroy()

        creditDiagramPagerAdapter = null
    }

    override fun setup() {
        //TODO("Not yet implemented")
    }

    override fun setupDataBinding() {
        //TODO("Not yet implemented")
    }

    private fun bindViewModel() {

        viewModel.diagramItemList.observe(viewLifecycleOwner, Observer { diagramItemList ->
            diagramItemList?.also { diagramItemList ->
                initDiagramPager(diagramItemList)
            }
        })

        viewModel.initCreditFilterListEvent.observe(viewLifecycleOwner, Observer { creditFilterList ->
            creditFilterList?.also { creditFilterList ->
                setupFilterListAdapter(creditFilterList)
            }
        })

        viewModel.setCurrentCreditEvent.observe(viewLifecycleOwner, Observer { currentCredit ->
            currentCredit?.also {
                setCreditHeader(it)
            }
        })

        viewModel.isOnlyActiveCredit.observe(viewLifecycleOwner, Observer { isOnlyActiveCredit ->
            isOnlyActiveCredit?.also {
                setOnlyActiveIcon(isOnlyActiveCredit)
            }
        })

        viewModel.setCurrentPage.observe(viewLifecycleOwner, Observer { currentPage ->
            currentPage?.also {
                setCurrentDiagramPage(it)
            }
        })

        viewModel.showGraphicLoadingIndicatorEvent.observe(viewLifecycleOwner, Observer {
            showGraphicLoadingIndicator()
        })

        viewModel.hideGraphicLoadingIndicatorEvent.observe(viewLifecycleOwner, Observer {
            hideGraphicLoadingIndicator()
        })

    }

    private fun setDiagramPager() {
        var currentPosition = 0

        creditDiagramPagerAdapter = CreditDiagramPagerAdapter(requireActivity().supportFragmentManager)
        binding.diagramPager.adapter = creditDiagramPagerAdapter
        creditDiagramPagerAdapter?.notifyDataSetChanged()

        // linking pager to tab dots
        binding.creditTabs.setupWithViewPager(binding.diagramPager, true)

        binding.diagramPager.addOnPageChangeListener(object : OnPageChangeListener {

            override fun onPageSelected(position: Int) {
                viewModel.onChangePage(position)

                requireActivity().supportFragmentManager.fragments.forEach {
                    if (it is DiagramItemFragment) {
                        it.updateUI()
                    }
                }
            }

            override fun onPageScrolled(position: Int, positionOffset: Float,
                positionOffsetPixels: Int) {

                val newPosition = if (positionOffset > 0.5) {
                    position + 1
                } else {
                    position
                }

                if (currentPosition != newPosition) {
                    currentPosition = newPosition
                    //viewModel.setCurrentDevice(currentPosition)
                }

                val alpha = abs(1 - positionOffset / 0.5).toFloat()

                binding.diagramPager.alpha = alpha
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })

        binding.diagramPager.currentItem = 0
    }


    private fun initDiagramPager(diagramItemList: List<DiagramItem>) {
        creditDiagramPagerAdapter?.diagramItemsList = diagramItemList
        creditDiagramPagerAdapter?.notifyDataSetChanged()
    }

    private fun setCurrentDiagramPage(currentPage: Int) {
        binding.diagramPager.currentItem = currentPage
    }

    private fun setOnlyActiveIcon(isOnlyActiveCredit: Boolean) {
        val toolbar = ToolbarUtils.getToolbar(requireActivity())
        toolbar?.menu?.findItem(R.id.action_filter_only_active)?.also { item ->
            if (isOnlyActiveCredit) {
                item.setIcon(R.drawable.icon_toolbar_visible)
            } else {
                item.setIcon(R.drawable.icon_toolbar_invisible)
            }
        }
    }

    private fun setListeners(){
        val toolbar = ToolbarUtils.getToolbar(requireActivity())
        toolbar?.setOnMenuItemClickListener(Toolbar.OnMenuItemClickListener { item ->
            Log.d("DIAGRAM", "ПУНКТ МЕНЮ ###" + item.itemId)

            when (item.itemId) {
                android.R.id.home    //button home
                -> {
                    activity?.finish()
                    return@OnMenuItemClickListener true
                }

                R.id.action_filter_only_active    //button del
                -> {
                    viewModel.onOnlyActiveClick()
                    return@OnMenuItemClickListener true
                }

                R.id.action_filter -> {
                    onCreditFilterClick()
                    return@OnMenuItemClickListener true
                }
            }

            // Handle the menu item
            true
        })

        binding.creditFilterCancelButton.setOnClickListener {
            binding.creditFilterLayout.gone()
        }

        binding.creditFilterDoneButton.setOnClickListener {

            val filterList = mutableListOf<Credit>()

            for(i in 0..binding.creditFilterListView.checkedItemPositions.size()) {
                if (binding.creditFilterListView.checkedItemPositions.get(i)) {


                    val item = binding.creditFilterListView.getItemAtPosition(i)

                    Log.d("DIAGRAM",
                        "item $i = ${binding.creditFilterListView.getItemAtPosition(i)} (isChecked = ${binding.creditFilterListView.checkedItemPositions.get(i)})")

                    if (item is HashMap<*,*>) {
                        filterList.add((item as Map<String, Any?>).get("item") as Credit)
                    }
                }
            }

            Log.d("DIAGRAM","filterList $filterList")

            viewModel.setCreditFilter(filterList)

            binding.creditFilterLayout.gone()

        }

        binding.creditFilterListView.setOnItemClickListener( object: OnItemClickListener {
            override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

            }
        })
    }

    private fun onCreditFilterClick() {
        binding.creditFilterLayout.visible()
    }

    private fun setupFilterListAdapter(creditList : List<Credit>) {
        val adapter = SimpleAdapter(activity, creditList.toSimpleAdapterList(), R.layout.item_credit_filter,
            arrayOf("name", "summa"), intArrayOf(R.id.creditFilterName1TextView, R.id.creditFilterName2TextView))

        binding.creditFilterListView.choiceMode = ListView.CHOICE_MODE_MULTIPLE
        binding.creditFilterListView.adapter = adapter
    }


    private fun setCreditHeader(credit : Credit?) {
        credit?.also {credit ->
            val name = credit.name +
                if (credit.date > 0 ) {
                    " от " + DateUtils.formatDateTime(activity,
                        credit.date, DateUtils.FORMAT_SHOW_DATE or DateUtils.FORMAT_SHOW_YEAR)
                } else {
                    ""
                }

            val param = "Сумма: " + String.format("%.0f", credit.summa) +
                if (credit.procent > 0) {
                    ", Процент: " + String.format("%.2f%%", credit.procent) + ", Срок: " + credit.period.toString()
                } else {
                    ""
                } +
                if (credit.period > 0) {
                    ", Срок: " + credit.period.toString()
                } else {
                    ""
                }

            binding.tvName.text = name
            binding.tvParam.text = param
        }
    }

    private fun showGraphicLoadingIndicator(){
        binding.diagramPager.visibility = View.INVISIBLE
        binding.graphicProgressBar.visibility = View.VISIBLE
    }

    private fun hideGraphicLoadingIndicator(){
        binding.diagramPager.visibility = View.VISIBLE
        binding.graphicProgressBar.visibility = View.GONE
    }

}
