package com.example.financeassistant.useCase.assembly

import android.content.Context
import com.example.financeassistant.R
import com.example.financeassistant.classes.ExchangeFilterItem
import com.example.financeassistant.classes.ExchangeItemType
import javax.inject.Inject

class CreateExchangeFilterItemsUseCase @Inject constructor(val context: Context) {

        fun createExchangeFilterItems(): List<ExchangeFilterItem> {
            return listOf(
                ExchangeFilterItem(ExchangeItemType.Undefined, context.getString(R.string.exchange_action_load_all)),
                ExchangeFilterItem(ExchangeItemType.Credit, context.getString(R.string.exchange_action_load_credits)),
                ExchangeFilterItem(ExchangeItemType.CreditPayment, context.getString(R.string.exchange_action_load_payments)),
                ExchangeFilterItem(ExchangeItemType.CreditGraphic, context.getString(R.string.exchange_action_load_graphics)),
                ExchangeFilterItem(ExchangeItemType.Flat, context.getString(R.string.exchange_action_load_flats)),
                ExchangeFilterItem(ExchangeItemType.FlatPayment, context.getString(R.string.exchange_action_load_flat_payments)),
                ExchangeFilterItem(ExchangeItemType.FlatPhoto, context.getString(R.string.exchange_action_load_flat_photo))
            )
        }

}