package com.example.financeassistant.flat.paymentListPage

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import com.example.financeassistant.base.BaseFragment
import com.example.financeassistant.classes.FlatPayment
import com.example.financeassistant.classes.ARGUMENT_PAGE_NUMBER
import com.example.financeassistant.classes.FlatPaymentOperationType
import com.example.financeassistant.classes.Flat
import com.example.financeassistant.classes.SWIPE_REFRESH_DISTANCE_TO_TRIGGER
import com.example.financeassistant.databinding.FlatPaymentListFragmentBinding
import com.example.financeassistant.utils.Navigator
import com.example.financeassistant.utils.Navigator.Companion.navigateToFlatPaymentActivity
import com.example.financeassistant.utils.NavigatorResultCode
import com.example.financeassistant.utils.gone
import com.example.financeassistant.utils.visible
import com.google.gson.Gson

/**
 * Created by dima on 08.11.2018.
 */
class FlatPaymentListFragment : BaseFragment<FlatPaymentListFragmentBinding>(), FlatPaymentListAdapterDelegate {

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FlatPaymentListFragmentBinding
        = FlatPaymentListFragmentBinding::inflate

    var pageNumber: Int = 0
    private var flat_id : Int = -1
    var prevVisibleItem = 1

    var flat: Flat? = null

    private val viewModel: FlatPaymentListViewModel by activityViewModels()
    lateinit var flatPaymentListAdapter: FlatPaymentListAdapter

    companion object {

        fun newInstance(page: Int): FlatPaymentListFragment {
            val pageFragment = FlatPaymentListFragment()
            val arguments = Bundle()
            arguments.putInt(ARGUMENT_PAGE_NUMBER, page)
            pageFragment.setArguments(arguments)
            return pageFragment
        }

        fun getTitle(): String  {
            return "Платежи"
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.also { arguments ->
            pageNumber = arguments.getInt(ARGUMENT_PAGE_NUMBER)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initAdapter()

        setListeners()

        initSwipeRefresh()

        bindViewModel()

        activity?.intent?.also { intent ->
            if (intent.hasExtra(Navigator.EXTRA_FLAT_KEY)) {
                val taskGson = intent.getStringExtra(Navigator.EXTRA_FLAT_KEY)
                val flat = Gson().fromJson(taskGson, Flat::class.java)
                setCurrentFlat(flat)
            }
        }

    }

    fun setCurrentFlat(flat: Flat) {
        if (this.flat_id != flat.id) {
            this.flat = flat
            loadData()
        }
    }

    private fun loadData() {

        Log.d("RELOAD", "loadData flat = ${Gson().toJson(flat)}")

        flat?.also {
            Log.d("RELOAD", "reload")
            viewModel.setFlat(it)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == NavigatorResultCode.FlatPayment.resultCode) {
            viewModel.loadFlatPaymentList()
        }
    }

    private fun bindViewModel() {

        viewModel.flatPaymentList.observe(viewLifecycleOwner, Observer { flatPaymentList ->
            flatPaymentList?.also { flatPaymentList ->
                updateAdapter(flatPaymentList)
            }
        })

        viewModel.accountToPayment.observe(viewLifecycleOwner, Observer { accountToPayment ->
            accountToPayment?.also { accountToPayment ->
                createPayment(accountToPayment)
            }
        })

        viewModel.showFlatPaymemtListLoadingIndicatorEvent.observe(viewLifecycleOwner, Observer {
            showFlatPaymentLoadingIndicator()
        })

        viewModel.hideFlatPaymemtListLoadingIndicatorEvent.observe(viewLifecycleOwner, Observer {
            hidePaymentFlatLoadingIndicator()
        })

    }

    private fun initAdapter(){
        activity?.also { activity ->
            flatPaymentListAdapter = FlatPaymentListAdapter(activity, this)

            binding.flatPaymentRecyclerView.layoutManager = LinearLayoutManager(activity)
            binding.flatPaymentRecyclerView.adapter = flatPaymentListAdapter
            binding.flatPaymentRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {

                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)

                    val MOTION_DRAG_SCALE = 5

                    val deltaProgress = dy / 100f / MOTION_DRAG_SCALE
                    var progress = binding.flatPaymentMoutionlayout.progress + deltaProgress

                    if (progress < 0f) progress = 0f
                    if (progress > 1f) progress = 1f

                    binding.flatPaymentMoutionlayout.progress = progress

                }
            })
        }
    }

    private fun updateAdapter(flatPaymentItemList: List<FlatPayment>){

        Log.d("RELOAD", "flatPaymentItemList = ${Gson().toJson(flatPaymentItemList)}")

        flatPaymentListAdapter.flatPaymentItemList = flatPaymentItemList
        flatPaymentListAdapter.notifyDataSetChanged()
    }

    private fun getCurrentItem(): Int {
        return (binding.flatPaymentRecyclerView.layoutManager as LinearLayoutManager)
            .findFirstVisibleItemPosition()
    }

    private fun setListeners() {
        binding.fabFlatPaymentList.setOnClickListener {
            flat?.also {
                val listOperation = FlatPaymentOperationType.getFlatOperationsByType(it.type)
                showPaymentDialog(listOperation)
            }


//            val payment = FlatPayment(flat_id = flat_id)
//            Navigator.navigateToFlatPaymentActivity(activity, this, payment)
        }
    }

    // Comment dialog
    private fun showPaymentDialog(listOperation: List<FlatPaymentOperationType>) {

        val builder = AlertDialog.Builder(activity)
        builder.setTitle(flat?.name)

        builder.setNegativeButton("Отмена", null)
        val commentDialogItems: MutableList<String> = mutableListOf()
        for (commentActionItem in listOperation) {
            commentDialogItems.add(commentActionItem.title)
        }
        builder.setItems(commentDialogItems.toTypedArray()) { dialog, which ->
                viewModel.onPaymentClick(listOperation[which])
        }
        builder.show()
    }

    private fun initSwipeRefresh() {
        binding.flatSwipeRefreshLayout.setOnRefreshListener(object : OnRefreshListener {
            override fun onRefresh() {
                viewModel.loadFlatPaymentList()
            }
        })

        binding.flatSwipeRefreshLayout.setDistanceToTriggerSync(SWIPE_REFRESH_DISTANCE_TO_TRIGGER)
    }

    private fun showFlatPaymentLoadingIndicator() {
        binding.flatPaymentListProgressBar.visible()
        binding.flatPaymentRecyclerView.gone()
    }

    private fun hidePaymentFlatLoadingIndicator() {
        binding.flatSwipeRefreshLayout.isRefreshing = false
        binding.flatPaymentListProgressBar.gone()
        binding.flatPaymentRecyclerView.visible()
    }

    private fun createPayment(accountToPayment: FlatPayment) {
        activity?.also { activity ->
            navigateToFlatPaymentActivity(activity, this, accountToPayment)
        }
    }

    override fun onItemClick(payment: FlatPayment) {
        activity?.also { activity ->
            Navigator.navigateToFlatPaymentActivity(activity, this, payment)
        }
    }

}