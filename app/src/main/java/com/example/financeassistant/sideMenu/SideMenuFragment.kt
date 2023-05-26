package com.example.financeassistant.sideMenu

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.fragment.app.activityViewModels
import com.example.financeassistant.classes.Credit
import com.example.financeassistant.classes.Flat
import com.example.financeassistant.classes.SideMenuItem
import com.example.financeassistant.utils.Navigator
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.financeassistant.base.BaseFragment
import com.example.financeassistant.databinding.SideMenuFragmentBinding

/**
 * Side menu fragment
 */
class SideMenuFragment : BaseFragment<SideMenuFragmentBinding>(), SideMenuAdapterDelegate {

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> SideMenuFragmentBinding
        = SideMenuFragmentBinding::inflate

    private val viewModel : SideMenuViewModel by activityViewModels()

    private lateinit var sideMenuAdapter : SideMenuAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bindViewModel()

        setListeners()

        setupSideMenuAdapter()

        viewModel.createMenuItems()
    }

    var resultLauncher = registerForActivityResult(StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {

        }
    }

    // Helper methods
    private fun bindViewModel(){

        viewModel.menuItemsList.observe(viewLifecycleOwner, Observer { menuItemsList ->
           menuItemsList?.also { menuItemsList ->
               updateAdapter(menuItemsList)
            }
        })

        viewModel.activeMenuItem.observe(viewLifecycleOwner, Observer {activeMenuItem ->
            activeMenuItem?.also {
                //selectMenuItemAction?.invoke(it)
            }
        })

        viewModel.flatToOpen.observe(viewLifecycleOwner, Observer {flatToOpen ->
            flatToOpen?.also {
                showFlatView(flatToOpen)
            }
        })

        viewModel.creditToOpen.observe(viewLifecycleOwner, Observer {creditToOpen ->
            creditToOpen?.also {
                showCreditView(creditToOpen)
            }
        })

        viewModel.showSettingsEvent.observe(viewLifecycleOwner, Observer {
            showSettingsView()
        })

        viewModel.showExchangeEvent.observe(viewLifecycleOwner, Observer {
            showExchangeView()
        })

        viewModel.showDiagramEvent.observe(viewLifecycleOwner, Observer {
            showDiagramView()
        })

        viewModel.closeEvent.observe(viewLifecycleOwner, Observer {
            closeApplication()
        })
    }

    private fun setListeners(){
//        closeButton.setOnClickListener(View.OnClickListener {
//            closeDrawerAction?.invoke()
//        })
    }

    private fun setupSideMenuAdapter() {
        activity?.let {
        sideMenuAdapter = SideMenuAdapter(it, this)
            binding.sideMenuRecyclerView.layoutManager = LinearLayoutManager(activity)
            binding.sideMenuRecyclerView.adapter = sideMenuAdapter
        }
    }

    private fun updateAdapter(menuItemsList: List<SideMenuItem>){
        sideMenuAdapter.updateList(menuItemsList)
    }

    // Public methods
    fun syncWithDrawer(closeDrawerAction: () -> Unit, selectMenuItemAction: (menuItem: SideMenuItem) -> Unit){
//        this.closeDrawerAction = closeDrawerAction
//        this.selectMenuItemAction = selectMenuItemAction
    }

    private fun showFlatView(flat: Flat) {
        activity?.also { activity ->
            Navigator.navigateToFlatActivity(activity, this, flat)
        }
    }

    private fun showCreditView(credit: Credit) {
        activity?.also { activity ->
            Navigator.navigateToGraphicActivity(activity, this, credit)
        }
    }

    private fun showSettingsView() {
        activity?.also { activity ->
            Navigator.navigateToSettingsActivity(activity, this)
        }
    }

    private fun showExchangeView() {
        activity?.also { activity ->
            Navigator.navigateToExchangeActivity(activity, this, resultLauncher)
        }
    }


    private fun showDiagramView() {
        activity?.also { activity->
            Navigator.navigateToCreditDiagramActivity(activity, this)
        }
    }

    private fun closeApplication() {
        activity?.finish()
    }

    override fun onSideMenuGroupClick(item: SideMenuItem) {
        viewModel.onItemClick(item)
    }

    override fun onSideMenuFlatItemClick(item: SideMenuItem) {
        viewModel.onItemClick(item)
    }

    override fun onSideMenuCreditItemClick(item: SideMenuItem) {
        viewModel.onItemClick(item)
    }

    override fun onSideMenuItemClick(item: SideMenuItem) {
        viewModel.onItemClick(item)
    }

}
