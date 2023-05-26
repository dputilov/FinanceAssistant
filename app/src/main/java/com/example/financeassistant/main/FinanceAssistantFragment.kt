package com.example.financeassistant.main

import android.util.Log
import com.example.financeassistant.classes.Flat
import android.view.MenuItem
import com.example.financeassistant.utils.ToolbarUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.example.financeassistant.R
import com.example.financeassistant.base.BaseFragment
import com.example.financeassistant.databinding.FinanceAssistantFragmentBinding
import com.example.financeassistant.flat.ObjectListPagerAdapter
import com.example.financeassistant.utils.Navigator
import java.lang.Math.abs

/**
 * Created by dima on 08.11.2018.
 */

class FinanceAssistantFragment : BaseFragment<FinanceAssistantFragmentBinding>() {

    private val viewModel: FinanceAssistantViewModel by activityViewModels()

    lateinit var pagerAdapter : FinanceAssistantPagerAdapter
    lateinit var actionListPagerAdapter : ObjectListPagerAdapter

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FinanceAssistantFragmentBinding
        = FinanceAssistantFragmentBinding::inflate

    override fun setupDataBinding() {

//        // Bind layout with ViewModel
//        binding.viewModel = viewModel
//
//        // LiveData needs the lifecycle owner
//        binding.lifecycleOwner = this

    }

    override fun setup() {

        bindViewModel()

        setListener()

        setMainPager()

        setActionListPager()
    }

    private fun bindViewModel() {
//        viewModel.flatList.observe(viewLifecycleOwner, Observer { flatList ->
//            flatList?.also { flatList ->
//                initFlatListPager(flatList)
//            }
//        })

        viewModel.currentPage.observe(viewLifecycleOwner, Observer { currentPage ->
            currentPage?.also { currentPage ->
                setCurrentListActionPage(currentPage)
            }
        })

//        viewModel.reloadToFlatEvent.observe(viewLifecycleOwner, Observer { flat ->
//            flat?.also { flat ->
//                reloadInstance(flat)
//            }
//        })

    }


    private fun setActionListPager() {
        var currentPosition = 0

        actionListPagerAdapter = ObjectListPagerAdapter(requireActivity().supportFragmentManager)
        binding.actionListPager.adapter = actionListPagerAdapter
        actionListPagerAdapter.notifyDataSetChanged()

        binding.actionListPager.addOnPageChangeListener(object : OnPageChangeListener {

            override fun onPageSelected(position: Int) {
                Log.d("RELOAD", "1 position = $position object =${actionListPagerAdapter.flatItemsList}")

                viewModel.onChangeObjectPage(position)

            }

            override fun onPageScrolled(position: Int, positionOffset: Float,
                positionOffsetPixels: Int) {

                val newPosition = if (positionOffset > 0.5) {
                    position + 1
                } else {
                    position
                }

                if (currentPosition != newPosition) {
                    currentPosition = newPosition
                    //viewModel.setCurrentDevice(currentPosition)
                }

                val alpha = abs(1 - positionOffset / 0.5).toFloat()

                //flatPager.alpha = alpha
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })

        binding.actionListPager.currentItem = 0
    }

    private fun setMainPager() {
        pagerAdapter = FinanceAssistantPagerAdapter(requireActivity().supportFragmentManager)
        binding.pager.adapter = pagerAdapter
        pagerAdapter.notifyDataSetChanged()

        binding.pager.addOnPageChangeListener(object : OnPageChangeListener {

            override fun onPageSelected(position: Int) {
                //Log.d(main.TAG, "onFlatPageSelected, position = $position")

                val menu = binding.navigation.menu
                if (menu.size() > position) {
                    val item = binding.navigation.menu.getItem(position)
                    binding.navigation.selectedItemId = item.itemId
                }

                //viewModel.reloadInstance()

            }

            override fun onPageScrolled(position: Int, positionOffset: Float,
                positionOffsetPixels: Int) {
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })

        binding.pager.currentItem = 0
    }

    private fun initActionListPager(flatList: List<Flat>) {
        actionListPagerAdapter.flatItemsList = flatList
        actionListPagerAdapter.notifyDataSetChanged()
    }

    private fun setCurrentListActionPage(position : Int) {
        binding.actionListPager.currentItem = position
    }


    private fun setListener(){
        // === ToolBar ===
        val toolbar = ToolbarUtils.getToolbar(requireActivity())
        toolbar?.setOnMenuItemClickListener(Toolbar.OnMenuItemClickListener { item ->
                Log.d("FLAT TOOL BAR ", "ПУНКТ МЕНЮ ###" + item.itemId)

                when (item.itemId) {
                    android.R.id.home    //button home
                    -> {
                        activity?.finish()
                        return@OnMenuItemClickListener true
                    }

                    R.id.action_delete    //button del
                    -> {
                        onClickDelete()
                        activity?.finish()
                        return@OnMenuItemClickListener true
                    }

                    R.id.action_OK -> {
                        //onClickAdd()
                        return@OnMenuItemClickListener true
                    }
                }// return super.onOptionsItemSelected(item);

                // Handle the menu item
                true
            })

            // Inflate a menu to be displayed in the toolbar
           // toolbar.inflateMenu(R.menu.menu_graphic)

        binding.navigation.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.action_home -> {
                    binding.pager.currentItem = 0
                    true
                }
                R.id.action_settings -> {
                    binding.pager.currentItem = 1
                    true
                }
                R.id.action_payment -> {
                    binding.pager.currentItem = 2
                    true
                }
                R.id.action_event -> {
                    activity?.also { activity ->
                        Navigator.navigateToExchangeActivity(activity, this, null)
                    }
                    true
                }
                else -> {
                    false
                }

            }
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.getItemId()) {
            android.R.id.home -> requireActivity().onBackPressed()
        }

        return true
    }


    private fun onClickDelete() {


    }

}