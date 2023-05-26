package com.example.financeassistant.exchange

import android.view.*
import com.example.financeassistant.R
import com.example.financeassistant.base.ViewBindingActivity
import com.example.financeassistant.databinding.ExchangeActivityBinding
import com.example.financeassistant.utils.ToolbarUtils

class ExchangeActivity : ViewBindingActivity<ExchangeActivityBinding>() {

    override val bindingInflater: (LayoutInflater) -> ExchangeActivityBinding
        = ExchangeActivityBinding::inflate

    override fun setup() {
        //ToolbarUtils.initToolbar(this, true, R.string.toolbar_exchange, R.color.ExchangeToolbar, R.color.ExchangeWindowsBar)
        ToolbarUtils.initToolbar(this, true, R.string.toolbar_exchange, R.color.grayLight, R.color.ExchangeWindowsBar)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.getItemId()) {
            android.R.id.home -> onBackPressed()
        }
        return true
    }

    // ====== Toolbar ======
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_exchange, menu)
        return true
    }
}
