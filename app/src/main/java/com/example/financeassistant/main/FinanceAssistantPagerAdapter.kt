package com.example.financeassistant.main

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.dmsflatmanager.main_window.flatPage.FlatPageFragment
import com.example.financeassistant.classes.Flat
import com.example.financeassistant.exchange.ExchangeFragment
import com.example.financeassistant.flat.mainPage.FlatMainFragment
import com.example.financeassistant.flat.paymentListPage.FlatPaymentListFragment
import com.example.financeassistant.flat.settingPage.FlatSettingsFragment
import com.example.financeassistant.main_window.creditPage.CreditPageFragment
import com.example.financeassistant.main_window.taskPage.TaskPageFragment

/**
 * Flat pager adapter
 */
class FinanceAssistantPagerAdapter constructor(fragmentManager : FragmentManager): FragmentPagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    val PAGE_COUNT = 3

    val pageList = listOf<Fragment>(
        TaskPageFragment.newInstance(0),
        CreditPageFragment.newInstance(1),
        FlatPageFragment.newInstance(2))
    val TitleList = listOf<String>("Объект", "Настройки", "Платежи")

    fun reloadInstance(flat: Flat) {
        pageList.forEach {
            when (it) {
                is FlatMainFragment -> {
                    it.setCurrentFlat(flat)
                }
                is FlatSettingsFragment -> {
                    //it.setCurrentFlat(flat)
                }
                is FlatPaymentListFragment -> {
                    it.setCurrentFlat(flat)
                }
            }
        }
    }

    override fun getItem(position: Int): Fragment {
        return pageList[position]
    }

    override fun getCount(): Int {
        return PAGE_COUNT
    }

    override fun getItemPosition(`object`: Any): Int {
        return pageList.indexOf(`object`)
    }

    override fun getPageTitle(position: Int): CharSequence {
        return TitleList[position]
    }
}
