package com.example.financeassistant.exchange

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.financeassistant.R
import com.example.financeassistant.classes.ExchangeItem
import com.example.financeassistant.databinding.ExchangeBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ExchangeItemInfoFragment : BottomSheetDialogFragment() {

    private var _binding: ExchangeBottomSheetBinding? = null

    // This property is only valid between onCreateView and onDestroyView.
    private val binding
        get() = _binding?: throw IllegalStateException("Cannot access view in after view destroyed and before view creation")

    lateinit var onCloseClick: (item: ExchangeItem) -> Unit
    var item: ExchangeItem? = null

    companion object {
        val TAG = this::class.simpleName
    }

    init {
        setStyle(DialogFragment.STYLE_NORMAL, R.style.BottomSheetDialog)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = ExchangeBottomSheetBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupListeners()

        initView()
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }

    private fun initView() {
        item?.also {
            binding.title.text = it.objectName
            binding.summa.text = it.sum.toString()
        }
    }

    private fun setupListeners(){
        binding.closeButton.setOnClickListener {
            item?.also {
                onCloseClick.invoke(it)
            }
            dismiss()
        }
    }

}