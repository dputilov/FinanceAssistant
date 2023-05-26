package com.example.financeassistant.diagram

import android.os.Bundle
import android.view.*
import com.example.financeassistant.base.BaseFragment
import com.example.financeassistant.classes.*
import com.example.financeassistant.databinding.CreditDiagramItemFragmentBinding

class DiagramItemFragment : BaseFragment<CreditDiagramItemFragmentBinding>() {

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> CreditDiagramItemFragmentBinding
        = CreditDiagramItemFragmentBinding::inflate

    var pageNumber: Int = 0
    var diagramItem: DiagramItem? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        updateUI()

        setListener()
    }

    fun updateUI(isInitial: Boolean = true){

        diagramItem?.also {
            binding.circleDiagramView.setDiagramData(it)
            if (isInitial) {
                binding.creditParamsView.setDiagramData(it)
            }
            binding.diagramView.setDiagramData(it)
        }

    }

    fun setListener() {
        binding.diagramsLayout.setOnClickListener{
            updateUI(isInitial = false)
        }
    }

}
