package com.example.financeassistant.base

import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import com.example.financeassistant.app.FinanceAssistantApp
import com.example.financeassistant.injection.component.AppComponent
import java.lang.Deprecated

/**
 * Base [android.app.Activity] class for every Activity in this application.
 */
@Deprecated
abstract class BaseActivity : AppCompatActivity() {

    /**
     * Get the Main Application component for dependency injection.
     *
     * @return [ApplicationComponent]
     */
    protected val appComponent: AppComponent
        get() = (application as FinanceAssistantApp).appComponent


}

abstract class ViewBindingActivity<ViewBindingType : ViewBinding> : AppCompatActivity() {

    /**
     * Get the Main Application component for dependency injection.
     *
     * @return [ApplicationComponent]
     */
    protected val appComponent: AppComponent
        get() = (application as FinanceAssistantApp).appComponent

    private var _binding: ViewBinding? = null
    abstract val bindingInflater: (LayoutInflater) -> ViewBindingType

    @Suppress("UNCHECKED_CAST")
    protected val binding: ViewBindingType
        get() = _binding!! as ViewBindingType

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = bindingInflater.invoke(layoutInflater)
        setContentView(binding.root)
        setup()
    }

    abstract fun setup()

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
