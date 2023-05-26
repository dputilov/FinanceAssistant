package com.example.financeassistant.flat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.graphics.BitmapFactory
import java.io.ByteArrayInputStream
import android.os.Bundle
import com.example.financeassistant.base.BaseFragment
import com.example.financeassistant.classes.Flat
import com.example.financeassistant.databinding.FlatPagerItemBinding

/**
 * Created by dima on 08.11.2018.
 */
class FlatPagerListItemFragment : BaseFragment<FlatPagerItemBinding>() {

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FlatPagerItemBinding
        = FlatPagerItemBinding::inflate

    var pageNumber: Int = 0
    var flat: Flat? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        updateUI()
    }

    fun updateUI(){
        if (view == null) {
            return
        }

        flat?.also { flat ->
            binding.tvNamePage.text = flat.name
            binding.tvTypePage.text = flat.type.title
            binding.tvAdresPage.text = flat.adres

            try {
                val imageStream = ByteArrayInputStream(flat.foto)
                val bitmap = BitmapFactory.decodeStream(imageStream)

                binding.imgFotoPage.setImageBitmap(bitmap)
            } catch (e: Throwable) {
            }
        }

    }

}