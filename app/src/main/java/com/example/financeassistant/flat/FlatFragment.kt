package com.example.financeassistant.flat

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import com.example.financeassistant.classes.Flat
import android.view.MenuItem
import com.example.financeassistant.classes.HomeType
import com.example.financeassistant.utils.Navigator
import com.example.financeassistant.utils.Str2Int
import com.example.financeassistant.utils.ToolbarUtils
import com.google.gson.Gson
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.example.financeassistant.R
import com.example.financeassistant.base.BaseFragment
import com.example.financeassistant.databinding.FlatFragmentBinding
import com.example.financeassistant.flat.mainPage.FlatMainFragment
import com.example.financeassistant.flat.paymentListPage.FlatPaymentListFragment
import com.example.financeassistant.flat.settingPage.FlatSettingsFragment
import java.io.ByteArrayOutputStream
import kotlin.math.abs

/**
 * Created by dima on 08.11.2018.
 */

class FlatFragment : BaseFragment<FlatFragmentBinding>() {

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FlatFragmentBinding
        = FlatFragmentBinding::inflate

    private val FOTO_COMPRESS_VALUE: Int = 85
    var flat_uid = ""
    // открываем подключение к БД
    //var db : DB? = null

    private val viewModel: FlatViewModel by activityViewModels()

    private var pagerAdapter : FlatPagerAdapter? = null
    private var objectListPagerAdapter : ObjectListPagerAdapter? = null

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        bindViewModel()

        setListener()

        setFlatPager()

        setObjectListPager()

        initInstance()
    }

    override fun onDestroyView() {
        super.onDestroyView()

        pagerAdapter  = null
        objectListPagerAdapter  = null

    }

    private fun bindViewModel() {
        viewModel.flatList.observe(viewLifecycleOwner, Observer { flatList ->
            flatList?.also { flatList ->
                initFlatListPager(flatList)
            }
        })

        viewModel.currentPage.observe(viewLifecycleOwner, Observer { currentPage ->
            currentPage?.also { currentPage ->
                setCurrentListFlatPage(currentPage)
            }
        })

        viewModel.reloadToFlatEvent.observe(viewLifecycleOwner, Observer { flat ->
            flat?.also { flat ->
                reloadInstance(flat)
            }
        })

        viewModel.exitFromFlatView.observe(viewLifecycleOwner, Observer { flat ->
            flat?.also { flat ->
                exitFromFlatView(flat)
            }
        })

    }

    fun getCurrentFlat() : Flat? {
        return viewModel.currentflat
    }

    private fun setObjectListPager() {
        var currentPosition = 0

        objectListPagerAdapter = ObjectListPagerAdapter(requireActivity().supportFragmentManager)
        binding.objectListPager.adapter = objectListPagerAdapter
        objectListPagerAdapter?.notifyDataSetChanged()

//        binding.objectListPager.addOnPageChangeListener(object : OnPageChangeListener {
//
//            override fun onPageSelected(position: Int) {
//                Log.d("RELOAD", "1 position = $position object =${objectListPagerAdapter?.flatItemsList}")
//
//                viewModel.onChangeObjectPage(position)
//
//            }
//
//            override fun onPageScrolled(position: Int, positionOffset: Float,
//                positionOffsetPixels: Int) {
//
//                val newPosition = if (positionOffset > 0.5) {
//                    position + 1
//                } else {
//                    position
//                }
//
//                if (currentPosition != newPosition) {
//                    currentPosition = newPosition
//                    //viewModel.setCurrentDevice(currentPosition)
//                }
//
//                val alpha = abs(1 - positionOffset / 0.5).toFloat()
//
//                //flatPager.alpha = alpha
//            }
//
//            override fun onPageScrollStateChanged(state: Int) {}
//        })

        binding.objectListPager.currentItem = 0
    }

    private fun setFlatPager() {
        pagerAdapter = FlatPagerAdapter(requireActivity().supportFragmentManager)
        binding.flatPager.adapter = pagerAdapter
        pagerAdapter?.notifyDataSetChanged()

        binding.flatPager.addOnPageChangeListener(object : OnPageChangeListener {

            override fun onPageSelected(position: Int) {
                //Log.d(main.TAG, "onFlatPageSelected, position = $position")

                val menu = binding.navigation.menu
                if (menu.size() > position) {
                    val item = binding.navigation.menu.getItem(position)
                    binding.navigation.selectedItemId = item.itemId
                }

                viewModel.reloadInstance()

            }

            override fun onPageScrolled(position: Int, positionOffset: Float,
                positionOffsetPixels: Int) {
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })

        binding.flatPager.currentItem = 0
    }


    private fun reloadInstance(flat: Flat) {
        //if (flat_id != flat._id) {
            flat_uid = flat.uid

            val flatJson = Gson().toJson(flat)
            requireActivity().intent?.extras?.putString(Navigator.EXTRA_FLAT_KEY, flatJson)

            requireActivity().supportFragmentManager.fragments.forEach {
                when (it) {
                    is FlatMainFragment -> {
                        it.setCurrentFlat(flat)
                    }
                    is FlatSettingsFragment -> {
                        //it.setCurrentFlat(flat)
                    }
                    is FlatPaymentListFragment -> {
                        it.setCurrentFlat(flat)
                    }

                }
            }

            //pagerAdapter.reloadInstance(flat)

            pagerAdapter?.notifyDataSetChanged()

       // }

    }

    private fun initInstance() {
        requireActivity().also { activity ->
            val flat = if (activity.intent.hasExtra(Navigator.EXTRA_FLAT_KEY)) {
                val flatGson = activity.intent.getStringExtra(Navigator.EXTRA_FLAT_KEY)
                val flat = Gson().fromJson(flatGson, Flat::class.java)
                flat_uid = flat.uid
                flat
            } else {
                null
            }

            viewModel.initInstance(flat)

            if (flat_uid.isNullOrEmpty()) {
                ToolbarUtils.setNewFlag(activity as AppCompatActivity)
            }
        }
    }

    private fun initFlatListPager(flatList: List<Flat>) {
        objectListPagerAdapter?.flatItemsList = flatList
        objectListPagerAdapter?.notifyDataSetChanged()
    }

    private fun setCurrentListFlatPage(position : Int) {
        binding.objectListPager.currentItem = position
    }


    private fun setListener(){
        // === ToolBar ===
        val toolbar = ToolbarUtils.getToolbar(requireActivity())
        toolbar?.setOnMenuItemClickListener(Toolbar.OnMenuItemClickListener { item ->
                Log.d("FLAT TOOL BAR ", "ПУНКТ МЕНЮ ###" + item.itemId)

                when (item.itemId) {
                    android.R.id.home    //button home
                    -> {
                        activity?.finish()
                        return@OnMenuItemClickListener true
                    }

                    R.id.action_delete    //button del
                    -> {
                        onClickDelete()
                        activity?.finish()
                        return@OnMenuItemClickListener true
                    }

                    R.id.action_OK -> {
                        onClickAdd()
                        return@OnMenuItemClickListener true
                    }
                }// return super.onOptionsItemSelected(item);

                // Handle the menu item
                true
            })

            // Inflate a menu to be displayed in the toolbar
           // toolbar.inflateMenu(R.menu.menu_graphic)

        binding.navigation.setOnItemReselectedListener {
            when (it.itemId) {
                R.id.action_home -> {
                    binding.flatPager.currentItem = 0
                }
                R.id.action_settings -> {
                    binding.flatPager.currentItem = 1
                }
                R.id.action_payment -> {
                    binding.flatPager.currentItem = 2
                }
                else -> {
                }
            }
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.getItemId()) {
            android.R.id.home -> requireActivity().onBackPressed()
        }

        return true
    }


    private fun onClickDelete() {


    }

    private fun onClickAdd() {

        // Main fragment
        val currentFlat = (pagerAdapter?.getItem(0) as FlatMainFragment)?.let { flatMainFragment ->

            flatMainFragment.getCurrentFlat()


//            cur_flat.name = flatMainFragmentView.etFlatName.text.toString()
//            cur_flat.adres = flatMainFragmentView.etAdres.text.toString()
//            cur_flat.param = flatMainFragmentView.etParam.text.toString()
//
//
//            // TODO realise save image
////            flatMainFragment.flat?.sourceImage?.also {
////                cur_flat.sourceImage = it
////            }
//
//            // TODO : переделать на нормальный возврат ИД кредита
//            val creditListAdapter = CreditListAdapter(requireContext())
//            creditListAdapter?.let { adapter ->
//                adapter.updateListCreditData()
//                val listData = adapter.listData
//                cur_flat.credit_id = listData.get(flatMainFragmentView.spCredit.selectedItemPosition)
//            }
//
//
//            val listHomeTypes = HomeType.getHomeTypeList()
//            cur_flat.type = listHomeTypes.get(flatMainFragmentView.spType.selectedItemPosition)
//
//            var str_summa = flatMainFragmentView.etSumma.text.toString()
//            if (TextUtils.isEmpty(str_summa)) {
//                str_summa = "0"
//            }
//            cur_flat.summa = java.lang.Double.parseDouble(str_summa)
//
//            cur_flat.isFinish = flatMainFragmentView.cbFinish.isChecked
//
//            if (flatMainFragmentView.imgFoto.drawable != null) {
//                val baos = ByteArrayOutputStream()
//                val bitmap = (flatMainFragmentView.imgFoto.drawable as BitmapDrawable).bitmap
//                bitmap.compress(Bitmap.CompressFormat.JPEG, FOTO_COMPRESS_VALUE, baos)
//                cur_flat.foto = baos.toByteArray()
//            }

        } ?: return

        if (currentFlat == null) {
            return
        }

//        // Settings fragment
//        (pagerAdapter?.getItem(1) as FlatSettingsFragment)?.let { flatSettingsFragment ->
//
//            val settingFlat = flatSettingsFragment.getCurrentFlat()
//
//            currentFlat.apply {
//                this.isCounter = settingFlat.isCounter
//                this.isPay = settingFlat.isPay
//                this.isArenda = settingFlat.isArenda
//
//                this.lic = settingFlat.lic
//
//                this.dayStart = settingFlat.dayStart
//                this.dayEnd = settingFlat.dayEnd
//
//                this.dayArenda = settingFlat.dayArenda
//
//                this.summaArenda = settingFlat.summaArenda
//            }
//
////            //fragmentManager.findFragmentByTag("flatSettingsFragment")?.view?.let{ flatSettingsFragmentView ->
////            cur_flat.isCounter = flatSettingsFragmentView.cbIsCounter.isChecked
////            cur_flat.isPay = flatSettingsFragmentView.cbIsPay.isChecked
////            cur_flat.isArenda = flatSettingsFragmentView.cbIsArenda.isChecked
////
////            cur_flat.lic = flatSettingsFragmentView.etLic.text.toString()
////
////            cur_flat.dayStart = Str2Int(flatSettingsFragmentView.etDayBeg.text.toString())
////            cur_flat.dayEnd = Str2Int(flatSettingsFragmentView.etDayEnd.text.toString())
////
////            cur_flat.dayArenda = Str2Int(flatSettingsFragmentView.etDayArenda.text.toString())
////
////            var str_summa_arenda = flatSettingsFragmentView.etSummaArenda.text.toString()
////            if (TextUtils.isEmpty(str_summa_arenda)) {
////                str_summa_arenda = "0.0"
////            }
////            cur_flat.summaArenda = java.lang.Double.parseDouble(str_summa_arenda)
//        }

        viewModel.onClickAddFlat(currentFlat)

    }

    private fun exitFromFlatView(flat: Flat) {
        activity?.also { activity ->
            Navigator.exitFromFlatActivity(activity, flat)
        }
    }

}