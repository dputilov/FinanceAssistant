package com.example.financeassistant.exchange

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.financeassistant.classes.ExchangeFilterItem
import com.example.financeassistant.databinding.ExchangeFilterItemBinding
import com.example.financeassistant.utils.RecyclerViewItemDecoration

/**
 * Exchange list adapter
 */

interface ExchangeFilterListAdapterDelegate {
    fun onFilterItemClick(item: ExchangeFilterItem)
}

class ExchangeFilterListAdapter constructor(private var context: Context, private val delegate: ExchangeFilterListAdapterDelegate? = null) : RecyclerView.Adapter<RecyclerView.ViewHolder>(), ExchangeFilterHolderDelegate {

    var listData: List<ExchangeFilterItem> = listOf()

    private var recyclerView: RecyclerView? = null

    var currentItem : ExchangeFilterItem? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
//        val view = LayoutInflater.from(context).inflate(R.layout.exchange_filter_item, parent, false)
//        return ExchangeFilterViewHolder(view, context, this)

        val binding = ExchangeFilterItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return ExchangeFilterViewHolder(binding, context, this)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = listData.get(position)
        (holder as ExchangeFilterViewHolder).updateUI(item, item.type == currentItem?.type)
    }

    override fun getItemCount(): Int {
        return listData.count()
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)

        recyclerView.addItemDecoration(RecyclerViewItemDecoration(context))
        this.recyclerView = recyclerView
    }

    override fun onFilterItemClick(item: ExchangeFilterItem) {
        delegate?.onFilterItemClick(item)
    }

}

interface ExchangeFilterHolderDelegate {
    fun onFilterItemClick(item: ExchangeFilterItem)
}

class ExchangeFilterViewHolder constructor(binding: ExchangeFilterItemBinding, val context: Context, private val delegate: ExchangeFilterHolderDelegate? = null): RecyclerView.ViewHolder(binding.root) {

    var itemLayout = binding.itemLayout
    var titleTextView = binding.titleTextView

    fun updateUI(item : ExchangeFilterItem, isCurrentPosition: Boolean){
        titleTextView.text = item.title

        itemLayout.isSelected = isCurrentPosition
        titleTextView.isSelected = isCurrentPosition

        titleTextView.setOnClickListener {
            delegate?.onFilterItemClick(item)
        }
    }
}