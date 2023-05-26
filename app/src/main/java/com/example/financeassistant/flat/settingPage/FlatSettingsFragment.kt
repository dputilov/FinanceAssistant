package com.example.financeassistant.flat.settingPage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.financeassistant.database.DB
import com.example.financeassistant.base.BaseFragment
import com.example.financeassistant.classes.ARGUMENT_PAGE_NUMBER
import com.example.financeassistant.classes.Flat
import com.example.financeassistant.databinding.FlatSettingsFragmentBinding
import com.example.financeassistant.utils.Navigator
import com.example.financeassistant.utils.gone
import com.example.financeassistant.utils.visible
import com.google.gson.Gson

/**
 * Created by dima on 08.11.2018.
 */
class FlatSettingsFragment : BaseFragment<FlatSettingsFragmentBinding>() {

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FlatSettingsFragmentBinding
        = FlatSettingsFragmentBinding::inflate

    val externalBinding: FlatSettingsFragmentBinding
        get() = binding

    var pageNumber: Int = 0
    private var flat_id : Int = -1

    companion object {

        fun newInstance(page: Int): FlatSettingsFragment {
            val pageFragment = FlatSettingsFragment()
            val arguments = Bundle()
            arguments.putInt(ARGUMENT_PAGE_NUMBER, page)
            pageFragment.setArguments(arguments)
            return pageFragment
        }

        fun getTitle(): String  {
            return "Настройки"
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.also { arguments ->
            pageNumber = arguments.getInt(ARGUMENT_PAGE_NUMBER)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setParam()

        loadData()

        setListeners()

        updateUI()
    }

    fun setListeners(){
        binding.cbIsCounter.setOnCheckedChangeListener { buttonView, isChecked ->
            updateUI()
        }

        binding.cbIsArenda.setOnCheckedChangeListener { buttonView, isChecked ->
            updateUI()
        }
    }

    private fun updateUI(){
        if (binding.cbIsCounter.isChecked) {
            binding.ltCounter.visible()
        } else {
            binding.ltCounter.gone()
        }

        if (binding.cbIsArenda.isChecked) {
            binding.ltArenda.visible()
        } else {
            binding.ltArenda.gone()
        }
    }

    private fun setParam() {
        val intent = requireActivity().intent

        if (intent.hasExtra(Navigator.EXTRA_FLAT_KEY)) {
            val taskGson = intent.getStringExtra(Navigator.EXTRA_FLAT_KEY)
            val flat = Gson().fromJson(taskGson, Flat::class.java)
            flat_id = flat.id
        }
    }

    fun setCurrentFlat(flat: Flat) {
        if (this.flat_id != flat.id) {
            this.flat_id = flat.id
            loadData()
        }
    }

    fun loadData(){
        // открываем подключение к БД
        val db = DB(requireActivity())
        db?.open()

        if (flat_id > 0) {

            val flat = db?.getFlat(flat_id)

            flat?.let { flat ->
                binding.etDayBeg.setText(flat.day_beg.toString())
                binding.etDayEnd.setText(flat.day_end.toString())
                binding.etLic.setText(flat.lic)

                binding.cbIsCounter.isChecked = flat.isCounter
                binding.cbIsPay.isChecked = flat.isPay
                binding.cbIsArenda.isChecked = flat.isArenda

                binding.etDayArenda.setText(flat.day_arenda.toString())

                binding.etSummaArenda.setText(flat.summa_arenda.toString())

            }

        }
    }

}