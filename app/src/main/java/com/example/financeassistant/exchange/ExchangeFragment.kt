package com.example.financeassistant.exchange

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.financeassistant.R
import com.example.financeassistant.base.BaseFragment
import com.example.financeassistant.classes.ExchangeFilterItem
import com.example.financeassistant.classes.ExchangeItem
import com.example.financeassistant.classes.ExchangeItemType
import com.example.financeassistant.databinding.ExchangeFragmentBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior

class ExchangeFragment : BaseFragment<ExchangeFragmentBinding>(), ExchangeAdapterDelegate, ExchangeFilterListAdapterDelegate {

    private val viewModel: ExchangeViewModel by activityViewModels()

    private var exchangeAdapter : ExchangeListAdapter? = null
    private var exchangeFilterAdapter : ExchangeFilterListAdapter? = null

    private var exchangeItemInfoFragment: ExchangeItemInfoFragment? = null

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> ExchangeFragmentBinding
        = ExchangeFragmentBinding::inflate

    override fun setupDataBinding() {

        // Bind layout with ViewModel
        binding.viewModel = viewModel

        // LiveData needs the lifecycle owner
        binding.lifecycleOwner = this

    }

//    override fun onActivityCreated(savedInstanceState: Bundle?) {
//        super.onActivityCreated(savedInstanceState)
//       // setToolBar()
//    }

    override fun setup() {
        setupExchangeListAdapter()

        setupFilterExchangeListAdapter()

        bindViewModel()

        setListeners()
    }

    override fun onDestroyView() {
        super.onDestroyView()

        exchangeAdapter = null
        exchangeFilterAdapter = null
    }

    private fun bindViewModel() {
        viewModel.exchangeItemList.observe(viewLifecycleOwner, Observer { exchangeItemList ->
            exchangeItemList?.also {
                updateAdapter(it)
            }
        })

        viewModel.exchangeFilterItemList.observe(viewLifecycleOwner, Observer { exchangeFilterItemList ->
            exchangeFilterItemList?.also {
                updateFilterAdapter(it)
            }
        })

        viewModel.showCloseTaskMessageEvent.observe(viewLifecycleOwner, Observer {
            showCloseTaskMessage()
        })
    }

    private fun setupExchangeListAdapter(){
        exchangeAdapter = ExchangeListAdapter(requireActivity(), this)
        binding.exchangeRecyclerView.layoutManager = LinearLayoutManager(activity)
        binding.exchangeRecyclerView.adapter = exchangeAdapter
    }

    private fun setupFilterExchangeListAdapter(){
        exchangeFilterAdapter = ExchangeFilterListAdapter(requireActivity(), this)
        binding.exchangeFilterRecyclerView.layoutManager = LinearLayoutManager(activity, RecyclerView.HORIZONTAL, false)
        binding.exchangeFilterRecyclerView.adapter = exchangeFilterAdapter
    }

    private fun updateAdapter(exchangeItemList: List<ExchangeItem>){
        exchangeAdapter?.listData = exchangeItemList
        exchangeAdapter?.notifyDataSetChanged()
    }

    private fun updateFilterAdapter(exchangeFilterItemList: List<ExchangeFilterItem>){
        exchangeFilterAdapter?.listData = exchangeFilterItemList
        exchangeFilterAdapter?.notifyDataSetChanged()
    }

    fun setToolBar() {
        val toolbar = requireActivity().findViewById<Toolbar>(R.id.toolbar_actionbar_item)
        toolbar.setOnMenuItemClickListener(Toolbar.OnMenuItemClickListener { item ->
            when (item.itemId) {
//                android.R.id.home    //button home
//                -> {
//                    Navigator.exitFromGraphicActivity(requireActivity())
//                    return@OnMenuItemClickListener true
//                }
                R.id.exchange_action_load_all -> {
                    viewModel.startExchange(null)
                    return@OnMenuItemClickListener true
                }

                R.id.exchange_action_load_credits -> {
                    viewModel.startExchange(ExchangeItemType.Credit)
                    return@OnMenuItemClickListener true
                }

                R.id.exchange_action_load_payments -> {
                    viewModel.startExchange(ExchangeItemType.CreditPayment)
                    return@OnMenuItemClickListener true
                }

                R.id.exchange_action_load_graphics -> {
                    viewModel.startExchange(ExchangeItemType.CreditGraphic)
                    return@OnMenuItemClickListener true
                }

                R.id.exchange_action_load_flats -> {
                    viewModel.startExchange(ExchangeItemType.Flat)
                    return@OnMenuItemClickListener true
                }

                R.id.exchange_action_load_flat_payments -> {
                    viewModel.startExchange(ExchangeItemType.FlatPayment)
                    return@OnMenuItemClickListener true
                }
            }
            // Handle the menu item
            true
        })

    }

    fun setListeners() = with(binding) {
        exchangeApplyButton.setOnClickListener {
            viewModel?.applyExchange()
        }

        exchangeApplyCancel.setOnClickListener {
            viewModel?.cancelApply()
        }

        exchangeRecyclerView.setOnScrollChangeListener { view, newState, i2, i3, i4 ->
            if (newState == RecyclerView.SCROLL_STATE_IDLE
                && exchangeRecyclerView.computeVerticalScrollOffset() == 0
            ) {
                if (!exchangeFloatingActionButton.isExtended) {
                    exchangeFloatingActionButton.extend()
                }
            } else {
                if (exchangeFloatingActionButton.isExtended) {
                    exchangeFloatingActionButton.shrink()
                }
            }

        }
    }

    private fun showCloseTaskMessage() {
        //Toast.makeText(activity, "Закрыта задача ${t.name}  от " + formatDate(t.date), Toast.LENGTH_SHORT).show()
        Toast.makeText(activity, "Закрыта задача ", Toast.LENGTH_SHORT).show()
    }

    override fun onFilterItemClick(item: ExchangeFilterItem) {

        exchangeFilterAdapter?.currentItem = item
        exchangeFilterAdapter?.notifyDataSetChanged()

        when (item.type) {
            ExchangeItemType.Undefined -> {
                viewModel.startExchange(null)
            }

            ExchangeItemType.Credit -> {
                viewModel.startExchange(ExchangeItemType.Credit)
            }

            ExchangeItemType.CreditPayment -> {
                viewModel.startExchange(ExchangeItemType.CreditPayment)
            }

            ExchangeItemType.CreditGraphic -> {
                viewModel.startExchange(ExchangeItemType.CreditGraphic)
            }

            ExchangeItemType.Flat -> {
                viewModel.startExchange(ExchangeItemType.Flat)
            }

            ExchangeItemType.FlatPayment-> {
                viewModel.startExchange(ExchangeItemType.FlatPayment)
            }
            else -> {

            }

        }
    }

    private fun showSlideBottomFragment(item: ExchangeItem, onCloseClick: (item: ExchangeItem) -> Unit) {
        activity?.let {
            if (exchangeItemInfoFragment == null) {
                exchangeItemInfoFragment = ExchangeItemInfoFragment()
            }

            exchangeItemInfoFragment?.item = item
            exchangeItemInfoFragment?.onCloseClick = onCloseClick
            exchangeItemInfoFragment?.show(it.supportFragmentManager, ExchangeItemInfoFragment.TAG)
        }
    }


    override fun onExchangeItemClick(item: ExchangeItem) {
        showSlideBottomFragment(item) {
            showInfo(item)
        }
    }

    private fun showInfo(item: ExchangeItem) {
        Toast.makeText(context, "ЗАКРЫТ ${item.objectName}", Toast.LENGTH_SHORT).show()
    }

}
