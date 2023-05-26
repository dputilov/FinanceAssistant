package com.example.financeassistant.flat

import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import com.example.financeassistant.R
import com.example.financeassistant.utils.ToolbarUtils
import com.example.financeassistant.base.ViewBindingActivity
import com.example.financeassistant.databinding.FlatActivityBinding

/**
 * Created by dima on 08.11.2018.
 */

class FlatActivity : ViewBindingActivity<FlatActivityBinding>() {

    override val bindingInflater: (LayoutInflater) -> FlatActivityBinding
        = FlatActivityBinding::inflate

    override fun setup() {
        ToolbarUtils.initToolbar(this, true, R.string.toolbar_flat, R.color.FlatItemToolbar, R.color.FlatItemWindowsBar)
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
        menuInflater.inflate(R.menu.menu_toolbar_item, menu)
        return true
    }
}