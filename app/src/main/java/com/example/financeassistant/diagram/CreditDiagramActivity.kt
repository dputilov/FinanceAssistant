package com.example.financeassistant.diagram

import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import com.example.financeassistant.R
import com.example.financeassistant.base.ViewBindingActivity
import com.example.financeassistant.databinding.CreditDiagramActivityBinding
import com.example.financeassistant.utils.ToolbarUtils

class CreditDiagramActivity : ViewBindingActivity<CreditDiagramActivityBinding>() {

    override val bindingInflater: (LayoutInflater) -> CreditDiagramActivityBinding
        = CreditDiagramActivityBinding::inflate

    override fun setup() {
        ToolbarUtils.initToolbar(this, true, R.string.toolbar_diagram, R.color.CreditDiagramToolbar, R.color.CreditDiagramWindowsBar)
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
        menuInflater.inflate(R.menu.menu_diargam, menu)
        return true
    }

}
