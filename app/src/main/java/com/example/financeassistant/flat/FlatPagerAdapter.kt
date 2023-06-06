package com.example.financeassistant.flat

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.financeassistant.classes.Flat
import com.example.financeassistant.flat.mainPage.FlatMainFragment
import com.example.financeassistant.flat.paymentListPage.FlatPaymentListFragment
import com.example.financeassistant.flat.settingPage.FlatSettingsFragment

/**
 * Flat pager adapter
 */
class FlatPagerAdapter constructor(fragmentManager : FragmentManager): FragmentPagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    val PAGE_COUNT = 3

    val pageList = listOf<Fragment>(FlatMainFragment.newInstance(0), FlatSettingsFragment.newInstance(1), FlatPaymentListFragment.newInstance(2))
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
