package com.example.financeassistant.classes

import java.util.Date

enum class ExchangeItemType(type : Int) {
    Undefined(0),
    Credit(1),
    CreditPayment(2),
    Flat(3),
    FlatPayment(4),
    CreditGraphic(5),
    FlatPhoto(6)
}

enum class ExchangeItemStatus(type : Int) {
    Undefined(0),
    New(1),
    Update(2),
    Delete(3)
}

data class ExchangeItem (
    var title : String = "",
    var objectName : String = "",
    var sum : Double = 0.00,
    var date : Date? = null,
    var type : ExchangeItemType = ExchangeItemType.Undefined,
    var item : Any? = null,
    var status : ExchangeItemStatus = ExchangeItemStatus.Undefined,
    var baseItem : Any? = null
) {
    var isSelected: Boolean = false

    fun isCredit() : Boolean = type == ExchangeItemType.Credit

    fun isFlatPhoto() : Boolean = type == ExchangeItemType.FlatPhoto

    fun isFlat() : Boolean = type == ExchangeItemType.Flat

    fun isAdded(): Boolean = status == ExchangeItemStatus.New

    fun isUpdated(): Boolean = status == ExchangeItemStatus.Update

    fun isDeleted(): Boolean = status == ExchangeItemStatus.Delete

    fun isChanged() : Boolean = isAdded() || isUpdated() || isDeleted()

    fun areItemsTheSame(): Boolean {
        return this.item?.let { item ->
            this.baseItem?.let { baseItem ->
                when {
                    item is Credit && baseItem is Credit -> item.areContentsTheSame(baseItem)
                    item is Payment && baseItem is Payment -> item.areItemsTheSame(baseItem)
                    item is Flat && baseItem is Flat -> item.areContentsTheSame(baseItem)
                    item is FlatPayment && baseItem is FlatPayment -> item.areItemsTheSame(baseItem)
                    else -> false
                }
            } ?: false
        } ?: false
    }

    fun updateStatus() {
        val isItem = item != null
        val isBaseItem = baseItem != null

        if (isItem && !isBaseItem) {
            status = ExchangeItemStatus.New
        }

        if (!isItem && isBaseItem) {
            status = ExchangeItemStatus.Delete
        }

        val isUpdated = (isItem && isBaseItem && !this.areItemsTheSame())

        if (isUpdated) {
            status = ExchangeItemStatus.Update
        }
    }

}

class ExchangeFilterItem(
    val type : ExchangeItemType,
    val title: String)