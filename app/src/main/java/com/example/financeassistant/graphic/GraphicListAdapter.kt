package com.example.financeassistant.graphic

import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.financeassistant.R
import com.example.financeassistant.classes.ExchangeItem
import com.example.financeassistant.classes.Payment
import com.example.financeassistant.databinding.ItemGraphicBinding
import com.example.financeassistant.utils.RecyclerViewItemDecoration
import com.example.financeassistant.utils.formatD
import com.example.financeassistant.utils.formatDate

/**
 * Graphic list adapter
 */
interface GraphicListAdapterDelegate {
    fun onGraphicItemClick(item : Payment)
}

class GraphicListAdapter constructor(private var context: Context, private val delegate: GraphicListAdapterDelegate? = null) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>(),
    GraphicListViewHolderDelegate
{

    var listData: List<Payment> = listOf()
    var currentPayPosition: Int = -1

    private var recyclerView: RecyclerView? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = ItemGraphicBinding.inflate(LayoutInflater.from(context), parent, false)
        return GraphicItemListViewHolder(binding, context, this)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (listData.isEmpty()) return

        val payment = listData.get(position)

        (holder as GraphicItemListViewHolder).updateUI(payment, position == currentPayPosition)
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)

        recyclerView.addItemDecoration(RecyclerViewItemDecoration(context))
        this.recyclerView = recyclerView
    }

    override fun getItemCount(): Int {
        return listData.count()
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun onGraphicItemClick(item: Payment) {
        delegate?.onGraphicItemClick(item)
    }
}

interface GraphicListViewHolderDelegate {
    fun onGraphicItemClick(item : Payment)
}

private class GraphicItemListViewHolder(binding: ItemGraphicBinding, val context: Context, private val delegate: GraphicListViewHolderDelegate? = null) : RecyclerView.ViewHolder(binding.root) {

    val color_done = ContextCompat.getColor(context, R.color.color_done)
    val color_other = ContextCompat.getColor(context, R.color.color_other)
    val color_current_pay = ContextCompat.getColor(context, R.color.color_current_pay)
    val color_text_new = ContextCompat.getColor(context, R.color.color_text_new)
    val color_text = ContextCompat.getColor(context, R.color.color_text)
    val color_text_default = ContextCompat.getColor(context, R.color.color_text_default)

    val llGraphic = binding.llGraphic
    val imgCheck = binding.imgCheck
    val tvGrDate = binding.tvGrDate
    val tvRest = binding.tvRest
    val tvSumma = binding.tvSumma
    val tvSumma_credit = binding.tvSummaCredit
    val tvSumma_procent = binding.tvSummaProcent

    fun updateUI(payment : Payment, isCurrentPayPosition: Boolean){

        llGraphic.setOnClickListener {
            delegate?.onGraphicItemClick(payment)
        }

        if (payment.done == 1) {
            imgCheck.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.check))
            imgCheck.visibility = View.VISIBLE
        }
        else {
            if (isCurrentPayPosition){
                imgCheck.setImageDrawable(ContextCompat.getDrawable(context, R.mipmap.arrow_to_right))
                imgCheck.visibility = View.VISIBLE
            } else {
                imgCheck.visibility = View.INVISIBLE
            }
        }

        tvGrDate.text  = formatDate(payment.date, "dd.MM.yy")

        tvRest.text = formatD(payment.rest)

        //! Сделать в виде глобальной настройки "Отображать остаток по кредиту"
        tvRest.visibility = View.GONE

        tvSumma.text = formatD(payment.summa)
        tvSumma_credit.text = formatD(payment.summa_credit)
        tvSumma_procent.text = formatD(payment.summa_procent)

        val currentBackGroundColor = if (payment.done == 1) {
            color_done
        } else {
            color_other
        }

        val currentTextColor =  if (payment.id < 0){
            color_text_new
        } else {
            if (isCurrentPayPosition){
                color_text
            } else {
                color_text_default
            }
        }

        val currentTextTypeface = if (isCurrentPayPosition) {
            Typeface.BOLD_ITALIC
        } else {
            Typeface.NORMAL
        }

        llGraphic.setBackgroundColor(currentBackGroundColor)

        tvGrDate.setTextColor(currentTextColor)
        tvRest.setTextColor(currentTextColor)
        tvSumma.setTextColor(currentTextColor)
        tvSumma_credit.setTextColor(currentTextColor)
        tvSumma_procent.setTextColor(currentTextColor)

        tvGrDate.typeface = Typeface.create(Typeface.DEFAULT, currentTextTypeface)
        tvRest.setTypeface(null, currentTextTypeface)
        tvSumma.setTypeface(null, currentTextTypeface)
        tvSumma_credit.setTypeface(null, currentTextTypeface)
        tvSumma_procent.setTypeface(null, currentTextTypeface)

    }
}