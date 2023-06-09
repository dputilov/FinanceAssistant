package com.example.financeassistant.flat.mainPage

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.speech.RecognizerIntent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import androidx.core.app.ActivityCompat
import androidx.core.widget.doAfterTextChanged
import androidx.databinding.adapters.AdapterViewBindingAdapter.OnItemSelected
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.example.financeassistant.R
import com.example.financeassistant.base.BaseFragment
import com.example.financeassistant.classes.ARGUMENT_PAGE_NUMBER
import com.example.financeassistant.classes.Flat
import com.example.financeassistant.classes.HomeType
import com.example.financeassistant.classes.SourceImage
import com.example.financeassistant.databinding.FlatMainFragmentBinding
import com.example.financeassistant.flat.CreditListAdapter
import com.example.financeassistant.flat.FlatFragment
import com.example.financeassistant.manager.DatabaseManager
import com.example.financeassistant.manager.FileManager
import com.example.financeassistant.utils.KeyboardUtils
import com.example.financeassistant.utils.Navigator
import com.example.financeassistant.utils.NavigatorResultCode
import com.example.financeassistant.utils.gone
import com.example.financeassistant.utils.visible
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import java.io.ByteArrayInputStream
import java.io.File

/**
 * Created by dima on 08.11.2018.
 */
class FlatMainFragment : BaseFragment<FlatMainFragmentBinding>() {

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FlatMainFragmentBinding
        = FlatMainFragmentBinding::inflate

    private val viewModel: FlatMainViewModel by activityViewModels()

//    val externalBinding : FlatMainFragmentBinding
//        get() = binding

    var pageNumber: Int = 0

    var listHomeType: List<HomeType> = HomeType.getHomeTypeList()

    private var creditListAdapter: CreditListAdapter? = null

    private val MICROPHONE_REQUEST_CODE = 121
    private val MICROPHONE_ADRES_REQUEST_CODE = 122

    companion object {
        fun newInstance(page: Int): FlatMainFragment {
            val pageFragment = FlatMainFragment()
            val arguments = Bundle()
            arguments.putInt(ARGUMENT_PAGE_NUMBER, page)
            pageFragment.arguments = arguments
            return pageFragment
        }

        fun getTitle(): String  {
            return "Объект"
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.also { arguments ->
            pageNumber = arguments.getInt(ARGUMENT_PAGE_NUMBER)
        }
    }

    override fun setup() {

        bindViewModel()

        setupAdapters()

        setListeners()

        hideKeyboard()

        initInstance()
    }

    private fun bindViewModel() {
        viewModel.currentFlat.observe(viewLifecycleOwner, Observer { currentFlat ->
            updateData(currentFlat)
        })

        viewModel.picture.observe(viewLifecycleOwner, Observer { picture ->
            updatePicture(picture)
        })
    }

    private fun initInstance() {
        val flat = activity?.intent?.let { intent ->
            if (intent.hasExtra(Navigator.EXTRA_FLAT_KEY)) {
                val taskGson = intent.getStringExtra(Navigator.EXTRA_FLAT_KEY)
                val flat = Gson().fromJson(taskGson, Flat::class.java)
                flat
            } else {
                Flat()
            }
        } ?: Flat()

        setCurrentFlat(flat)
    }

    fun setCurrentFlat(flat: Flat) {
        viewModel.setCurrentFlat(flat)
    }

    fun getCurrentFlat(): Flat? {
        return viewModel.getCurrentFlat()
    }

    private fun setupAdapters() {
        setTypeSpinnerAdapter()

        activity?.also { activity ->
            creditListAdapter = CreditListAdapter(activity)
            creditListAdapter?.let { adapter ->
                binding.spCredit.adapter = adapter.getAdapter()
         //       val listData = adapter.listData
            }
        }
    }

    private fun updateData(flat: Flat) {
        activity?.also { activity ->
            flat?.also { flat ->
                binding.etFlatName.setText(flat.name)
                binding.etAdres.setText(flat.adres)
                binding.etParam.setText(flat.param)
                binding.etSumma.setText(flat.summa.toString())

                // выделяем элемент
                creditListAdapter?.listData?.let {
                    val curpos = it.indexOf(flat.credit_id)
                    binding.spCredit.setSelection(curpos)
                }

                setCurrentType(flat.type)

                binding.cbFinish.isChecked = flat.isFinish

                updatePicture(flat.sourceImage?.sourceUrl ?: "")
            }
        }

        updateUI(flat)
    }

    fun updateUI(flat: Flat) {
        if (flat.type == HomeType.Automobile) {
            binding.adresLayout.gone()
            binding.etAdres.gone()
        } else {
            binding.adresLayout.visible()
            binding.etAdres.visible()
        }
    }

    fun updatePicture(picture: ByteArray?) {
        if (picture != null) {
            val imageStream = ByteArrayInputStream(picture)
            val bitmap = BitmapFactory.decodeStream(imageStream)

            binding.imgFoto.setImageBitmap(bitmap)
        } else {
            binding.imgFoto.setImageDrawable(null)
        }
    }

    private fun updatePicture(sourceUrl: String) {
         Picasso.get()
            .load(File(sourceUrl))
            //.load("http://94.181.95.17:9000/BaseOfJobs/hs/api/Flats/${flat?.uid}/Photo")
            .placeholder(R.drawable.plus_grey)
            //.error(R.drawable.e)
            .centerInside()
            .fit()
            .into(binding.imgFoto)
    }

    fun setListeners() {
//        spType.setOnItemSelectedListener(object : OnItemSelectedListener {
//            override fun onItemSelected(parentView: AdapterView<*>, selectedItemView: View, position: Int, id: Long) {
//                updateUI()
//            }
//
//            override fun onNothingSelected(parentView: AdapterView<*>) {
//
//            }
//        })

        binding.imgFoto.setOnClickListener { v ->

            activity?.also { activity ->
                if (ActivityCompat.checkSelfPermission(activity, READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(activity, arrayOf(READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE),
                        NavigatorResultCode.GalleryRequest.resultCode)
                } else {
                    Navigator.navigateChooseImageFromGallery(activity, this)
                }
            }
        }

        binding.imgFoto.setOnLongClickListener { v ->
            viewModel.loadPicture()
            true
        }

        binding.etAdres.doAfterTextChanged {
            viewModel.onAdresChanged(it.toString())
        }

        binding.etFlatName.doAfterTextChanged {
            viewModel.onNameChanged(it.toString())
        }

        binding.etParam.doAfterTextChanged {
            viewModel.onParamChanged(it.toString())
        }

        binding.etSumma.doAfterTextChanged {
            viewModel.onSummaChanged(it.toString().toDouble())
        }

        binding.cbFinish.setOnCheckedChangeListener { _, isChecked ->
            viewModel.onIsFinishChanged(isChecked)
        }

        binding.spCredit.onItemSelectedListener = object : OnItemSelectedListener{
            override fun onItemSelected(adapter: AdapterView<*>?, p1: View?, selectedItemPosition: Int, p3: Long) {
                creditListAdapter?.let { adapter ->
                    adapter.updateListCreditData()
                    val listData = adapter.listData
                    val creditUid = listData.get(selectedItemPosition)
                    viewModel.onCreditChanged(creditUid.toString())
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                viewModel.onCreditChanged("")
            }
        }

        binding.spType.onItemSelectedListener = object : OnItemSelectedListener{
            override fun onItemSelected(adapter: AdapterView<*>?, view: View?, selectedItemPosition: Int, p3: Long) {
                val type = HomeType.getHomeTypeList()[selectedItemPosition]
                viewModel.onHomeTypeChanged(type)
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                viewModel.onHomeTypeChanged(HomeType.None)
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == NavigatorResultCode.GalleryRequest.resultCode) {
            // If request is cancelled, the result arrays are empty.
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                activity?.also { activity ->
                    Navigator.navigateChooseImageFromGallery(activity, this)
                }
            } else {
                //do something like displaying a message that he didn't allow the app to access gallery and you wont be able to let him select from gallery
            }
        }
    }

    private fun setCurrentType(curType: HomeType) {
        var curpos = -1
        for ((index, value) in listHomeType.withIndex()) {
            if (value == curType) {
                curpos = index
            }
        }

        if (curpos >= 0) {
            binding.spType.setSelection(curpos)
        }
    }

    private fun setTypeSpinnerAdapter() {
        // адаптер
        val listTypeName = ArrayList<String>()
        for (oper in listHomeType) {
            listTypeName.add(oper.title)
        }

        activity?.also { activity ->
            val adapter = ArrayAdapter(activity, android.R.layout.simple_spinner_item, listTypeName)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spType.adapter = adapter
        }
    }

    // отображаем диалоговое окно для выбора даты
    fun nameMicro(v: View) {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, getString(R.string.search_hint))
        startActivityForResult(intent, MICROPHONE_REQUEST_CODE)

    }

    // отображаем диалоговое окно для выбора даты
    fun adresMicro(v: View) {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, getString(R.string.search_hint))
        startActivityForResult(intent, MICROPHONE_ADRES_REQUEST_CODE)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (data == null) return

        if (requestCode == MICROPHONE_REQUEST_CODE) {
            val matches = data.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS)
            if (!matches.isNullOrEmpty()) {
                val str = binding.etFlatName.getText().toString()
                binding.etFlatName.setText(str + " " + matches[0])
            }
        }

        if (requestCode == MICROPHONE_ADRES_REQUEST_CODE) {
            val matches = data.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS)
            if (!matches.isNullOrEmpty()) {
                val str = binding.etAdres.getText().toString()
                binding.etAdres.setText(str + " " + matches[0])
            }
        }

        if (requestCode == NavigatorResultCode.GalleryRequest.resultCode) {
            var bitmap: Bitmap? = null

            if (resultCode == Activity.RESULT_OK) {
                data?.data?.also { selectedImage ->
                    try {
                        //bitmap = MediaStore.Images.Media.getBitmap(activity.contentResolver, selectedImage)
                        activity?.also { activity ->
                            bitmap = FileManager.instance.decodeBitmapFromUri(selectedImage, 100, 100)
                            // TODO create small icon and save it in 'foto' param
                        }

                        FileManager.instance.copyFile(selectedImage)?.also { storedFile ->
                                val sourceImage = SourceImage(sourceUrl = storedFile.absolutePath)
                                viewModel.updatePicture(sourceImage)
                        }

                    } catch (e: Exception) {
                        Log.d("DMS_CREDIT", "BITMAP ERROR ${e.message}")

                    }

                    //binding.imgFoto.setImageBitmap(bitmap)

                    // TODO Just for test
                    viewModel.uploadPicture(selectedImage)
                    //

                }
            }

        }

        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun hideKeyboard() {
        activity?.let {
            KeyboardUtils.hideKeyboard(it, it.currentFocus)
        }
    }

}