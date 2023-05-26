package com.example.financeassistant.main

import android.view.LayoutInflater
import com.example.financeassistant.base.ViewBindingActivity
import com.example.financeassistant.databinding.FinanceAssistantActivityBinding

class FinanceAssistantActivity : ViewBindingActivity<FinanceAssistantActivityBinding>() {

    override val bindingInflater: (LayoutInflater) -> FinanceAssistantActivityBinding
        = FinanceAssistantActivityBinding::inflate

    override fun setup() {

    }

}
