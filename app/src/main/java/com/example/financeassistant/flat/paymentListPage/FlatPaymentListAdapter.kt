package com.example.financeassistant.flat.paymentListPage

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.financeassistant.classes.FlatPayment
import com.example.financeassistant.databinding.ItemFlatPayBinding
import com.example.financeassistant.utils.RecyclerViewItemDecoration
import com.example.financeassistant.utils.gone
import com.example.financeassistant.utils.months
import com.example.financeassistant.utils.visible
import java.util.Calendar

/**
 * Flat payment list adapter delegate
 */
interface FlatPaymentListAdapterDelegate{
    fun onItemClick(payment: FlatPayment)
}

/**
 * Flat payment list adapter
 */
class FlatPaymentListAdapter constructor(private var context: Context, val delegate: FlatPaymentListAdapterDelegate? = null) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>(),
    FlatPaymentViewHolderDelegate {

    var flatPaymentItemList: List<FlatPayment> = listOf()

    private var recyclerView: RecyclerView? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = ItemFlatPayBinding.inflate(LayoutInflater.from(context), parent, false)
        return FlatPaymentViewHolder(binding, context, this)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (flatPaymentItemList.isEmpty()) return

        val flatPayment = flatPaymentItemList.get(position)

        (holder as FlatPaymentViewHolder).updateUI(flatPayment)
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)

        recyclerView.addItemDecoration(RecyclerViewItemDecoration(context))
        this.recyclerView = recyclerView
    }

    override fun getItemCount(): Int {
        return flatPaymentItemList.count()
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    // Holder Delegate methods
    override fun onItemClick(payment: FlatPayment) {
        delegate?.onItemClick(payment)
    }

}

/**
 * Flat payment item view holder delegate
 */

interface FlatPaymentViewHolderDelegate{
    fun onItemClick(flatPayment: FlatPayment)
}

class FlatPaymentViewHolder constructor(binding: ItemFlatPayBinding, val context: Context, val delegate: FlatPaymentViewHolderDelegate? = null): RecyclerView.ViewHolder(binding.root) {

    private val llFlatPay = binding.llFlatPay
    private val tvFPDate = binding.tvFPDate
    private val tvFPOper = binding.tvFPOper
    private val tvSumma = binding.tvSumma
    private val tvComment = binding.tvComment

    fun updateUI(flatPayment: FlatPayment) {
        llFlatPay.setOnClickListener {
            delegate?.onItemClick(flatPayment)
        }

        val dateAndTime = Calendar.getInstance()
        dateAndTime.timeInMillis = flatPayment.date
        val str_date = months[dateAndTime.get(Calendar.MONTH)] + " " + dateAndTime.get(
            Calendar.YEAR).toString() + " г."

        tvFPDate.text = str_date
        tvFPOper.text = flatPayment.operation.titleShort
        tvSumma.text = String.format("%.2f", flatPayment.summa)

        tvComment.text = flatPayment.comment
        if (flatPayment.comment.isNotEmpty()) {
            tvComment.visible()
        } else {
            tvComment.gone()
        }

    }
}