package com.example.financeassistant.graphic

import android.view.*
import com.example.financeassistant.R
import com.example.financeassistant.base.ViewBindingActivity
import com.example.financeassistant.databinding.GraphicActivityBinding
import com.example.financeassistant.utils.ToolbarUtils

class GraphicListActivity : ViewBindingActivity<GraphicActivityBinding>() {

    override val bindingInflater: (LayoutInflater) -> GraphicActivityBinding
        = GraphicActivityBinding::inflate

    override fun setup() {
        ToolbarUtils.initToolbar(this, true, R.string.toolbar_graphic, R.color.CreditItemToolbar, R.color.CreditItemWindowsBar)
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
        menuInflater.inflate(R.menu.menu_graphic, menu)
        return true
    }
}
