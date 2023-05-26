package com.example.financeassistant.sideMenu

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Paint
import android.graphics.Typeface
import android.os.Bundle
import androidx.recyclerview.widget.RecyclerView
import android.util.Log
import com.example.financeassistant.R
import com.example.financeassistant.classes.SideMenuItem
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.example.financeassistant.classes.Credit
import com.example.financeassistant.classes.CreditType
import com.example.financeassistant.classes.Flat
import com.example.financeassistant.classes.HomeType
import com.example.financeassistant.classes.SideMenuItemType
import com.example.financeassistant.utils.RecyclerViewItemDecoration
import com.example.financeassistant.utils.gone
import com.example.financeassistant.utils.invisible
import com.example.financeassistant.utils.visible
import java.io.ByteArrayInputStream
import android.widget.RelativeLayout
import androidx.recyclerview.widget.DiffUtil
import com.example.financeassistant.databinding.ItemTaskBinding
import com.example.financeassistant.databinding.SideMenuGroupBinding
import com.example.financeassistant.databinding.SideMenuItemBinding

/**
 * Side menu adapter
 */

class DiffSideMenuItemsListCallback(var oldList: List<SideMenuItem>, var newList: List<SideMenuItem>) :  DiffUtil.Callback() {

    companion object {
        const val GROUP_IS_CHECKED_PAYLOAD = "GROUP_IS_CHECKED_PAYLOAD"
    }

    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].areItemsTheSame(newList[newItemPosition])
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].areContentsTheSame(newList[newItemPosition])
    }

    override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
        val bundle = Bundle()
        val oldItem = oldList[oldItemPosition]
        val newItem = newList[newItemPosition]

        Log.d("DMS!@#", "oldItem=${oldItem.id} === oldItem=${newItem.id}")
        Log.d("DMS!@#", "oldItem=${oldItem.isExpanded} === oldItem=${newItem.isExpanded}")

        if (oldItem.id == newItem.id) {

            if (oldItem.isExpanded != newItem.isExpanded) {
                bundle.putBoolean(GROUP_IS_CHECKED_PAYLOAD, newItem.isExpanded)
            }
        }

        if (bundle.isEmpty) {
            return super.getChangePayload(oldItemPosition, newItemPosition)
        }

        return bundle
    }
}

/**
 * Side menu adapter delegate
 */
interface SideMenuAdapterDelegate{
    fun onSideMenuGroupClick(item: SideMenuItem)

    fun onSideMenuItemClick(item: SideMenuItem)

    fun onSideMenuCreditItemClick(item: SideMenuItem)

    fun onSideMenuFlatItemClick(item: SideMenuItem)
}

/**
 * Side menu list adapter
 */
class SideMenuAdapter constructor(private var context: Context, val delegate: SideMenuAdapterDelegate? = null) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>(),
    SideMenuGroupViewHolderDelegate,
    SideMenuFlatItemViewHolderDelegate,
    SideMenuCreditItemViewHolderDelegate,
    SideMenuItemViewHolderDelegate {

    companion object {
        private const val VIEW_TYPE_GROUP = 0
        private const val VIEW_TYPE_ITEM = 1
        private const val VIEW_TYPE_ITEM_FLAT = 2
        private const val VIEW_TYPE_ITEM_CREDIT = 3
    }

    var sideMenuItemList: List<SideMenuItem> = listOf()

    private var recyclerView: RecyclerView? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            VIEW_TYPE_GROUP -> {
                val binding = SideMenuGroupBinding.inflate(LayoutInflater.from(context), parent, false)
                return SideMenuGroupViewHolder(binding, context, this)
            }
            VIEW_TYPE_ITEM_FLAT -> {
                val binding = SideMenuItemBinding.inflate(LayoutInflater.from(context), parent, false)
                return SideMenuFlatItemViewHolder(binding, context, this)
            }
            VIEW_TYPE_ITEM_CREDIT -> {
                val binding = SideMenuItemBinding.inflate(LayoutInflater.from(context), parent, false)
                return SideMenuCreditItemViewHolder(binding, context, this)
            }
            VIEW_TYPE_ITEM -> {
                val binding = SideMenuItemBinding.inflate(LayoutInflater.from(context), parent, false)
                return SideMenuItemViewHolder(binding, context, this)
            }
        }

        val binding = SideMenuItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return SideMenuItemViewHolder(binding, context, this)

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (sideMenuItemList.isEmpty()) return

        when (holder) {
            is SideMenuGroupViewHolder -> {
                val item = sideMenuItemList.get(position)

                holder.updateUI(item)
            }
            is SideMenuItemViewHolder -> {
                val item = sideMenuItemList.get(position)

                holder.updateUI(item)
            }
            is SideMenuFlatItemViewHolder -> {
                val item = sideMenuItemList.get(position)

                holder.updateUI(item)
            }
            is SideMenuCreditItemViewHolder -> {
                val item = sideMenuItemList.get(position)

                holder.updateUI(item)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int, payloads: MutableList<Any>) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position)
        } else {
            when (holder) {
                is SideMenuGroupViewHolder -> {
                    val item = sideMenuItemList.get(position)

                    holder.updateGroup(item, payloads.last() as Bundle)
                }
                is SideMenuItemViewHolder -> {
                    val item = sideMenuItemList.get(position)

                    holder.updateUI(item)
                }
                is SideMenuFlatItemViewHolder -> {
                    val item = sideMenuItemList.get(position)

                    holder.updateUI(item)
                }
                is SideMenuCreditItemViewHolder -> {
                    val item = sideMenuItemList.get(position)

                    holder.updateUI(item)
                }
            }
        }
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)

        recyclerView.addItemDecoration(RecyclerViewItemDecoration(context))
        this.recyclerView = recyclerView
    }

    override fun getItemCount(): Int {
        return sideMenuItemList.count()
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        if (sideMenuItemList.count() > position) {
            when (sideMenuItemList[position].type) {
                SideMenuItemType.FlatGroup, SideMenuItemType.CreditGroup -> return VIEW_TYPE_GROUP
                SideMenuItemType.FlatItem -> return VIEW_TYPE_ITEM_FLAT
                SideMenuItemType.CreditItem -> return VIEW_TYPE_ITEM_CREDIT
                SideMenuItemType.Settings, SideMenuItemType.Exit -> return VIEW_TYPE_ITEM
                else -> {}
            }
        }

        return VIEW_TYPE_ITEM
    }

    fun updateList(newSideMenuItemsList: List<SideMenuItem>) {

        val diffResult = DiffUtil.calculateDiff(DiffSideMenuItemsListCallback(this.sideMenuItemList, newSideMenuItemsList), true)
        diffResult.dispatchUpdatesTo(this)

        this.sideMenuItemList = newSideMenuItemsList
    }

    // ViewHolderDelegate methods
    override fun onSideMenuGroupClick(item: SideMenuItem) {
        delegate?.onSideMenuGroupClick(item)
    }

    override fun onSideMenuItemClick(item: SideMenuItem) {
        delegate?.onSideMenuItemClick(item)
    }

    override fun onSideMenuCreditItemClick(item: SideMenuItem) {
        delegate?.onSideMenuCreditItemClick(item)
    }

    override fun onSideMenuFlatItemClick(item: SideMenuItem) {
        delegate?.onSideMenuFlatItemClick(item)
    }

}

/**
 * Task item view holder delegate
 */
interface SideMenuGroupViewHolderDelegate{
    fun onSideMenuGroupClick(item: SideMenuItem)
}

class SideMenuGroupViewHolder constructor(binding: SideMenuGroupBinding, val context: Context, val delegate: SideMenuGroupViewHolderDelegate? = null): RecyclerView.ViewHolder(binding.root) {

    private val itemLayout = binding.sideMenuItemLayout
    private val itemTitle = binding.sideMenuItemTitleTextView
    private val countText = binding.CountItemsTextView
    private val itemTypeImage = binding.sideMenuItemTypeImageView

    fun updateUI(item: SideMenuItem) {
        itemLayout.setOnClickListener {
            delegate?.onSideMenuGroupClick(item)
        }

        val layoutParams = RelativeLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT)
        layoutParams.setMargins(0, 0, 0, 0)
        itemLayout.layoutParams = layoutParams

        itemTitle.typeface = Typeface.DEFAULT_BOLD

        itemTitle.text = item.title

        countText.text = "${item?.itemsCountActive ?: 0} / ${item?.itemsCountAll ?: 0}"

        itemTypeImage.isSelected = item.isExpanded

        itemTypeImage.visible()

    }

    fun updateGroup(item: SideMenuItem, bundle: Bundle) {

        if (bundle.containsKey(DiffSideMenuItemsListCallback.GROUP_IS_CHECKED_PAYLOAD)) {
            val isSelected = bundle.getBoolean(DiffSideMenuItemsListCallback.GROUP_IS_CHECKED_PAYLOAD)

            itemTypeImage.isSelected = isSelected

        }
    }
}

/////////////////////////////////////////////
// Other simple item
//
interface SideMenuItemViewHolderDelegate{
    fun onSideMenuItemClick(item: SideMenuItem)
}

class SideMenuItemViewHolder constructor(binding: SideMenuItemBinding, val context: Context, val delegate: SideMenuItemViewHolderDelegate? = null): RecyclerView.ViewHolder(binding.root) {

    private val itemLayout = binding.sideMenuItemLayout
    private val itemTitle = binding.sideMenuItemTitleTextView
    private val itemTypeImage = binding.sideMenuItemTypeImageView
    private val itemFotoImage = binding.sideMenuItemFotoImageView
    private val itemFotoCard = binding.sideMenuItemFotoCardView

    fun updateUI(item: SideMenuItem) {
        itemLayout.setOnClickListener {
            delegate?.onSideMenuItemClick(item)
        }

        val layoutParams = RelativeLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT)
        layoutParams.setMargins(0, 0, 0, 0)
        itemLayout.layoutParams = layoutParams

        itemTitle.typeface = Typeface.DEFAULT_BOLD

        itemTitle.text = item.title

        itemFotoCard.gone()
        itemTypeImage.visible()

        val icon = item.icon
        if (icon != null) {
            itemTypeImage.setImageResource(icon)
        } else {
            itemTypeImage.setImageDrawable(null)
            itemTypeImage.invisible()
        }
    }
}

/////////////////////////////////////////////
// Credit item
//
interface SideMenuCreditItemViewHolderDelegate{
    fun onSideMenuCreditItemClick(item: SideMenuItem)
}

class SideMenuCreditItemViewHolder constructor(binding: SideMenuItemBinding, val context: Context, val delegate: SideMenuCreditItemViewHolderDelegate? = null): RecyclerView.ViewHolder(binding.root) {

    private val itemLayout = binding.sideMenuItemLayout
    private val itemTitle = binding.sideMenuItemTitleTextView
    private val itemTypeImage = binding.sideMenuItemTypeImageView
    private val itemFotoImage = binding.sideMenuItemFotoImageView
    private val itemFotoCard = binding.sideMenuItemFotoCardView

    fun updateUI(item: SideMenuItem) {

        itemLayout.setOnClickListener {
            delegate?.onSideMenuCreditItemClick(item)
        }

        when (item.type) {
            SideMenuItemType.CreditItem, SideMenuItemType.FlatItem -> {
                val layoutParams = RelativeLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                layoutParams.setMargins(110, 0, 0, 0)
                itemLayout.layoutParams = layoutParams

                itemTitle.typeface = Typeface.DEFAULT
            }
            else -> {}
        }

        itemTitle.text = item.title

        itemFotoCard.gone()
        itemTypeImage.visible()

        val itemContent = item.item

        when (itemContent) {
            is Credit -> {
                when (itemContent.type) {
                    CreditType.Flat -> itemTypeImage.setImageResource(R.drawable.type_credit_flat)
                    CreditType.Parking -> itemTypeImage.setImageResource(R.drawable.type_credit_parking)
                    CreditType.Stuff -> itemTypeImage.setImageResource(R.drawable.type_credit_things)
                    CreditType.Ensure -> itemTypeImage.setImageResource(R.drawable.type_credit_ensure)
                    CreditType.Auto -> itemTypeImage.setImageResource(R.drawable.type_credit_auto)
                    else -> {
                        itemTypeImage.setImageDrawable(null)
                        itemTypeImage.invisible()
                    }
                }

                if (itemContent.finish) {
                    itemTitle.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                } else {
                    itemTitle.paintFlags = Paint.FAKE_BOLD_TEXT_FLAG
                }
            }
        }

    }

}

/////////////////////////////////////////////
// Flat item
//
interface SideMenuFlatItemViewHolderDelegate{
    fun onSideMenuFlatItemClick(item: SideMenuItem)
}

class SideMenuFlatItemViewHolder constructor(binding: SideMenuItemBinding, val context: Context, val delegate: SideMenuFlatItemViewHolderDelegate? = null): RecyclerView.ViewHolder(binding.root) {

    private val itemLayout = binding.sideMenuItemLayout
    private val itemTitle = binding.sideMenuItemTitleTextView
    private val itemTypeImage = binding.sideMenuItemTypeImageView
    private val itemFotoImage = binding.sideMenuItemFotoImageView
    private val itemFotoCard = binding.sideMenuItemFotoCardView

    fun updateUI(item: SideMenuItem) {
        itemLayout.setOnClickListener {
            delegate?.onSideMenuFlatItemClick(item)
        }

        when (item.type) {
            SideMenuItemType.CreditItem, SideMenuItemType.FlatItem -> {
                val layoutParams = RelativeLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                layoutParams.setMargins(110, 0, 0, 0)
                itemLayout.layoutParams = layoutParams

                itemTitle.typeface = Typeface.DEFAULT
            }
            else -> {}
        }

        itemTitle.text = item.title

        itemFotoCard.gone()
        itemTypeImage.visible()

        val itemContent = item.item

        when (itemContent) {
            is Flat -> {
                when (itemContent.type) {
                    HomeType.Flat -> itemTypeImage.setImageResource(R.drawable.type_credit_flat)
                    HomeType.Parking -> itemTypeImage.setImageResource(R.drawable.type_credit_parking)
                    HomeType.Building -> itemTypeImage.setImageResource(R.drawable.type_credit_flat)
                    HomeType.Automobile -> itemTypeImage.setImageResource(R.drawable.type_credit_auto)
                    else -> {
                        itemTypeImage.setImageDrawable(null)
                        itemTypeImage.invisible()
                    }
                }

                if (itemContent.finish) {
                    itemTitle.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                } else {
                    itemTitle.paintFlags = Paint.FAKE_BOLD_TEXT_FLAG
                }

                itemContent.foto?.also { foto ->
                    itemFotoCard.visibility = View.VISIBLE
                    try {
                        val imageStream = ByteArrayInputStream(foto)
                        val bitmap = BitmapFactory.decodeStream(imageStream)

                        itemFotoImage.setImageBitmap(bitmap)
                        itemFotoImage.visible()
                    } catch (e: Throwable) {
                        itemFotoCard.gone()
                    }
                }

            }

        }

    }

}
