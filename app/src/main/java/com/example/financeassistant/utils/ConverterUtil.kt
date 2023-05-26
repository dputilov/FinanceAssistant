package com.example.financeassistant.utils

import android.view.View
import androidx.databinding.BindingConversion

// Binding converters applied automatically
object BindingConverters{

    @BindingConversion
    @JvmStatic fun booleanToVisibility(isVisible: Boolean): Int {
        return if (isVisible) View.VISIBLE else View.GONE
    }

}

object ConverterUtil {
    @JvmStatic fun isZero(number: Int): Boolean {
        return number == 0
    }
}