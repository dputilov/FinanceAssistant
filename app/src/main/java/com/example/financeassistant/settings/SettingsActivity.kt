package com.example.financeassistant.settings

import android.view.LayoutInflater
import com.example.financeassistant.R
import com.example.financeassistant.base.ViewBindingActivity
import com.example.financeassistant.databinding.ActivitySettingsBinding
import com.example.financeassistant.manager.SettingsManager
import com.example.financeassistant.utils.ToolbarUtils

class SettingsActivity : ViewBindingActivity<ActivitySettingsBinding>(){

    override val bindingInflater: (LayoutInflater) -> ActivitySettingsBinding
        = ActivitySettingsBinding::inflate

    override fun setup() {
        ToolbarUtils.initToolbar(this, true, R.string.toolbar_settings, R.color.FlatItemToolbar, R.color.FlatItemWindowsBar)

        initUI()

        setListeners()

    }

    private fun initUI() {
        binding.showClosedCreditSwitch.isChecked = SettingsManager.instance.getShowCloseCreditSettings()
        binding.showClosedFlatSwitch.isChecked = SettingsManager.instance.getShowCloseFlatSettings()
    }

    private fun setListeners() {
        binding.showClosedCreditSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            SettingsManager.instance.setShowCloseCreditSettings(isChecked)
        }

        binding.showClosedFlatSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            SettingsManager.instance.setShowCloseFlatSettings(isChecked)
        }

    }

}
