package com.example.financeassistant.flat

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.example.financeassistant.classes.Flat

/**
 * Flat pager adapter
 */
class ObjectListPagerAdapter constructor(fragmentManager : FragmentManager): FragmentStatePagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    var flatItemsList: List<Flat> = listOf()
        set(value) {
            createFlatItemFragmentsList(value)
        }

    private var flatItemFragmentsList : List<FlatPagerListItemFragment> = listOf()

    override fun getCount(): Int {
        return flatItemFragmentsList.count()
    }

    override fun getItem(position: Int) : Fragment {
        return flatItemFragmentsList.get(position)
    }

    override fun getItemPosition(`object`: Any): Int {
        return POSITION_NONE
    }

    private fun createFlatItemFragmentsList(flatItemsList : List<Flat>){
        val fragmentsList = mutableListOf<FlatPagerListItemFragment>()
        for ((index, flat) in flatItemsList.withIndex()) {
            val fragment = FlatPagerListItemFragment()
            fragment.flat = flat
            fragment.pageNumber = index
            fragmentsList.add(fragment)
        }
        flatItemFragmentsList = fragmentsList
    }

}
