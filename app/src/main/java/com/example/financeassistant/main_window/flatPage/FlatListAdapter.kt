package com.example.financeassistant.main_window.flatPage

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Paint
import androidx.recyclerview.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.financeassistant.R
import android.text.Html
import android.transition.TransitionManager
import android.widget.LinearLayout
import android.widget.TextView
import com.example.financeassistant.classes.FlatPayment
import com.example.financeassistant.classes.FlatItem
import com.example.financeassistant.classes.FlatPaymentOperationType.PROFIT
import com.example.financeassistant.classes.FlatPaymentOperationType.RENT
import com.example.financeassistant.classes.FlatPaymentState
import com.example.financeassistant.classes.FlatPaymentType.Outlay
import com.example.financeassistant.classes.FlatPaymentType.Profit
import com.example.financeassistant.classes.Flat
import com.example.financeassistant.classes.HomeType
import com.example.financeassistant.databinding.FlatProgressBarBinding
import com.example.financeassistant.databinding.ItemFlatBinding
import com.example.financeassistant.utils.D2L
import com.example.financeassistant.utils.gone
import com.example.financeassistant.utils.visible
import java.io.ByteArrayInputStream
import java.text.DecimalFormat

/**
 * Flat list adapter delegate
 */
interface FlatListAdapterDelegate{
    fun onFlatItemClick(flat: Flat)

    fun onArendaActionClick(flatPayment: FlatPayment)

    fun onRentActionClick(flatPayment: FlatPayment)
}


/**
 * Flat list adapter
 */
class FlatListAdapter constructor(private var context: Context, val delegate: FlatListAdapterDelegate? = null) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>(),
        FlatViewHolderDelegate {

    var flatItemList: List<FlatItem> = listOf()

    private var recyclerView: RecyclerView? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = ItemFlatBinding.inflate(LayoutInflater.from(context), parent, false)
        return FlatViewHolder(binding, context, this)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (flatItemList.isEmpty()) return

        val flat = flatItemList.get(position)

        (holder as FlatViewHolder).updateUI(flat)
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)

        //recyclerView.addItemDecoration(RecyclerViewItemDecoration(context))
        this.recyclerView = recyclerView
    }

    override fun getItemCount(): Int {
        return flatItemList.count()
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    // TaskViewHolderDelegate methods
    override fun onFlatItemClick(flat: Flat) {
        delegate?.onFlatItemClick(flat)
    }

    override fun onArendaActionClick(flatPayment: FlatPayment) {
        delegate?.onArendaActionClick(flatPayment)
    }

    override fun onRentActionClick(flatPayment: FlatPayment) {
        delegate?.onRentActionClick(flatPayment)
    }

}

/**
 * Flat item view holder delegate
 */

interface FlatViewHolderDelegate{
    fun onFlatItemClick(flat: Flat)

    fun onArendaActionClick(flatPayment: FlatPayment)

    fun onRentActionClick(flatPayment: FlatPayment)
}

class FlatViewHolder constructor(binding: ItemFlatBinding, val context: Context, val delegate: FlatViewHolderDelegate? = null): RecyclerView.ViewHolder(binding.root) {

    private val rootLayoutFlatItem = binding.rootLayoutFlatItem

    private val tvName = binding.tvName
    private val tvAdres = binding.tvAdres
    private val tvParam = binding.tvParam
    private val imgIsCounter = binding.imgIsCounter
    private val imgisPay = binding.imgIsPay
    private val imgNoTask = binding.imgNoTask
    private val imgType = binding.imgType
    private val closeFlatStamp = binding.closeFlatStamp
    private val imgFoto = binding.imgFoto
    private val rlArenda = binding.rlArenda
    private val tvSummaArenda = binding.tvSummaArenda
    private val llMain = binding.llProgressMain

    private val tvFinRes = binding.tvFinRes

    private val clOperation = binding.clOperation
    private val clControlPanel = binding.clControlPanel


    private val ivOpenOperation = binding.ivOpenOperation

    private val ivAddPaymentArenda = binding.ivAddPaymentArenda
    private val ivAddPaymentRent = binding.ivAddPaymentRent


    fun updateUI(flatItem: FlatItem) {

        val RUB = Html.fromHtml(" &#x20bd")

        val decFormat = DecimalFormat("###,###")

        imgFoto.setImageBitmap(null)
        imgFoto.visibility = View.GONE

        val flat = flatItem.flat
        flat.foto?.also { foto ->
            imgFoto.visibility = View.VISIBLE
            try {
                val imageStream = ByteArrayInputStream(foto)
                val bitmap = BitmapFactory.decodeStream(imageStream)

                imgFoto.setImageBitmap(bitmap)
                imgFoto.visibility = View.VISIBLE
            } catch (e: Throwable) {
                Log.d("DMS_CREDIT", "error load foto $e")
            }
        }

        tvName.text = flat.name
        tvAdres.text = flat.adres
        tvParam.text = flat.param

        imgNoTask.visible()
        imgIsCounter.gone()
        imgisPay.gone()
        rlArenda.gone()

        if (flat.isCounter || flat.isPay) {
            imgNoTask.gone()

            if (flat.isCounter) {
                imgIsCounter.visible()
            }

            if (flat.isPay) {
                imgisPay.visible()
            }

            if (flat.isArenda) {
                rlArenda.visible()
                tvSummaArenda.text = D2L(flat.summa_arenda/100).toString()
            }
        }

        imgType.visible()
        // Тип кредита (1-ипотека, 2-авто-кредит, 3-потребительский, 4-страхование и 5-прочие)

        val flatType = flat.type

        when (flatType ) {
            HomeType.Flat -> imgType.setImageResource(R.drawable.type_credit_flat)
            HomeType.Parking -> imgType.setImageResource(R.drawable.type_credit_parking)
            HomeType.Building -> imgType.setImageResource(R.drawable.type_credit_flat)
            HomeType.Automobile -> imgType.setImageResource(R.drawable.type_credit_auto)
            else -> {
                imgType.setImageDrawable(null)
                imgType.gone()
            }
        }

        ////////////////////////////
        /// PROGRESS INDICATORS

        llMain.removeAllViews()

        flatItem.flatPaymentStateList?.also { flatPaymentStateList ->
            flatPaymentStateList.forEach {
                addProgressIndicator(llMain, it)
            }
        }

        val summaFinRes = flat.summaFinRes

        if (summaFinRes != null && summaFinRes < 0)
            tvFinRes.setTextColor(Color.RED)
        else
            tvFinRes.setTextColor(Color.BLUE)

        val strFinRes = decFormat.format(summaFinRes) + RUB
        tvFinRes.text = strFinRes

        tvName.paintFlags = Paint.FAKE_BOLD_TEXT_FLAG

        if (!flat.finish) { // Действующий

            closeFlatStamp.visibility = View.GONE

            //tvName.paintFlags = (0)

        } else { // Выбывший объект

            // Типа зачеркнутый заголовок
            val myText = "<s><strike>${flat.name}</strike></s>"
            tvName.setText(Html.fromHtml(myText), TextView.BufferType.SPANNABLE)

            tvName.paintFlags = (tvName.paintFlags + Paint.STRIKE_THRU_TEXT_FLAG)

            closeFlatStamp.visibility = View.VISIBLE

        }

        rootLayoutFlatItem.setOnClickListener {
            delegate?.onFlatItemClick(flat)
        }

        ivAddPaymentArenda.setOnClickListener {
            val account = FlatPayment(flat = flat, operation = PROFIT, paymentType = Profit)
            delegate?.onArendaActionClick(account)
        }

        ivAddPaymentRent.setOnClickListener {
            val account = FlatPayment(flat = flat, operation = RENT, paymentType = Outlay)
            delegate?.onRentActionClick(account)
        }

        clOperation.setOnClickListener {
            if (clControlPanel.visibility == View.GONE) {
                //TransitionManager.beginDelayedTransition(rootLayoutFlatItem, Rotate())
                ivOpenOperation.rotation = 90F
                clControlPanel.visibility = View.VISIBLE
            } else {
                //TransitionManager.beginDelayedTransition(rootLayoutFlatItem, Rotate())
                ivOpenOperation.rotation = 0F
                clControlPanel.visibility = View.GONE
            }
        }
    }

    ////////////////////////////
    /// INFLATE PROGRESS BAR
    private fun addProgressIndicator(llMain: LinearLayout, progressState: FlatPaymentState) {

        val progressDrawable: Int? = progressState.color
        val summaProgress: Double = progressState.summaProgressFirst
        val secondSummaProgress: Double? = progressState.summaProgressSecond
        val summaMax: Double = progressState.summaMaximum
        val title: String = progressState.title
        val summaProgressText: String? = progressState.summaProgressText
        val summaMaxText: String? = progressState.summaMaximumText
        val isVisible = progressState.isVisible

        if (isVisible) {
            //val inflater = LayoutInflater.from(context)
            //val inflatedLayout = inflater.inflate(R.layout.flat_progress_bar, llMain, false)

            val inflatedLayout = FlatProgressBarBinding.inflate(LayoutInflater.from(context), llMain, false)

            inflatedLayout.tvInProgressText.text = title

            if (summaProgressText != null) {
                inflatedLayout.tvInProgressSummaText.text = summaProgressText
            }

            if (summaMaxText != null) {
                inflatedLayout.tvInProgressMaxSummaText.text = summaMaxText
            }

            // progress bar settings
            inflatedLayout.pbProgress.max = Math.round(summaMax).toInt()
            inflatedLayout.pbProgress.progress = Math.round(summaProgress).toInt()

            if (secondSummaProgress != null) {
                inflatedLayout.pbProgress.secondaryProgress = Math.round(secondSummaProgress).toInt()
            }

            if (progressDrawable != null) {
                inflatedLayout.pbProgress.progressDrawable = context.getDrawable(
                        progressDrawable)
            }

            llMain.addView(inflatedLayout.root)
        }
    }
}