package com.example.financeassistant.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding

abstract class BaseFragment<ViewBindingType : ViewBinding> : Fragment() {

    private var _binding: ViewBinding? = null
    abstract val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> ViewBindingType

    // This property is only valid between onCreateView and onDestroyView.
    @Suppress("UNCHECKED_CAST")
    protected val binding: ViewBindingType
        get() = _binding as ViewBindingType

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = bindingInflater.invoke(inflater, container, false)
        setupDataBinding()
        return requireNotNull(_binding).root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setup()
    }

    open fun setupDataBinding() {
    }

    open fun setup() {
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

