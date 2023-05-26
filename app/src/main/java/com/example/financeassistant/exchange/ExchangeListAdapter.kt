package com.example.financeassistant.exchange

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.financeassistant.R
import com.example.financeassistant.classes.ExchangeItem
import com.example.financeassistant.classes.ExchangeItemStatus
import com.example.financeassistant.databinding.ExchangeItemBinding
import com.example.financeassistant.utils.RecyclerViewItemDecoration
import com.example.financeassistant.utils.formatDate
import com.example.financeassistant.utils.gone
import com.example.financeassistant.utils.visible

/**
 * Exchange list adapter
 */
interface ExchangeAdapterDelegate {
    fun onExchangeItemClick(item : ExchangeItem)
}

class ExchangeListAdapter constructor(private var context: Context, private val delegate: ExchangeAdapterDelegate? = null) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>(),
    ExchangeHolderDelegate {

    var listData: List<ExchangeItem> = listOf()

    private var recyclerView: RecyclerView? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        // Old way
        //val view = LayoutInflater.from(context).inflate(R.layout.exchange_item, parent, false)
        //return ExchangeViewHolder(view, context, this)

        // New way via View binding
        val binding = ExchangeItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return ExchangeViewHolder(binding, context, this)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = listData.get(position)
        (holder as ExchangeViewHolder).updateUI(item)
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

    override fun onExchangeItemClick(item: ExchangeItem) {
        delegate?.onExchangeItemClick(item)
    }

}

interface ExchangeHolderDelegate {
    fun onExchangeItemClick(item : ExchangeItem)
}

class ExchangeViewHolder constructor(binding: ExchangeItemBinding, val context: Context, private val delegate: ExchangeHolderDelegate? = null): RecyclerView.ViewHolder(binding.root) {

    var itemLayout = binding.itemLayout
    var summaTextView = binding.summaTextView
    var titleTextView = binding.titleTextView
    var nameTextView = binding.nameTextView
    var dateTextView = binding.dateTextView
    var typeTextView = binding.typeTextView
    var statusTextView = binding.statusTextView
    var processCheckBox = binding.processCheckBox

    fun updateUI(item : ExchangeItem){

        itemLayout.setOnClickListener {
            delegate?.onExchangeItemClick(item)
        }

        titleTextView.text = item.title
        nameTextView.text = item.objectName

        if (item.sum > 0.01) {
            summaTextView.text = "${item.sum}"
            summaTextView.visible()
        } else {
            summaTextView.gone()
        }

        item.date?.let {
            dateTextView.text = formatDate(it.time, "dd.MM.yyyy")
            dateTextView.visible()
        } ?: dateTextView.gone()

        typeTextView.text = item.type.name

        processCheckBox.isChecked = item.isSelected

        statusTextView.text = ""

        when (item.status) {
            ExchangeItemStatus.New -> {
                statusTextView.text = "NEW"
                statusTextView.setTextColor(ContextCompat.getColor(context, R.color.orange_color))
            }
            ExchangeItemStatus.Update -> {
                statusTextView.text = "UPDATE"
                statusTextView.setTextColor(ContextCompat.getColor(context, R.color.blue_color))
            }
            ExchangeItemStatus.Delete -> {
                statusTextView.text = "DELETE"
                statusTextView.setTextColor(ContextCompat.getColor(context, R.color.red_color))
            }
            else -> {
                statusTextView.gone()
            }
        }

    }
}