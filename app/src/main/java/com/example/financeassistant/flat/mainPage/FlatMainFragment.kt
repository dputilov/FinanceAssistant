package com.example.financeassistant.flat.mainPage

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.app.Activity
import android.content.Context
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
import android.widget.ArrayAdapter
import androidx.core.app.ActivityCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.example.financeassistant.R
import com.example.financeassistant.base.BaseFragment
import com.example.financeassistant.classes.ARGUMENT_PAGE_NUMBER
import com.example.financeassistant.classes.Flat
import com.example.financeassistant.classes.HomeType
import com.example.financeassistant.database.DB
import com.example.financeassistant.databinding.FlatMainFragmentBinding
import com.example.financeassistant.flat.CreditListAdapter
import com.example.financeassistant.utils.KeyboardUtils
import com.example.financeassistant.utils.Navigator
import com.example.financeassistant.utils.NavigatorResultCode
import com.google.gson.Gson
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.IOException

/**
 * Created by dima on 08.11.2018.
 */
class FlatMainFragment : BaseFragment<FlatMainFragmentBinding>() {

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FlatMainFragmentBinding
        = FlatMainFragmentBinding::inflate

    val externalBinding: FlatMainFragmentBinding
        get() = binding

    private val viewModel: FlatMainViewModel by activityViewModels()

    var pageNumber: Int = 0

    var listHomeType: List<HomeType> = HomeType.getHomeTypeList()

    private var creditListAdapter: CreditListAdapter? = null
    private var flat_id: Int = -1

    var flat: Flat? = null

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setParam()

        loadData()

        setListeners()

        updateUI()

        hideKeyboard()

        bindViewModel()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onResume() {
        super.onResume()


    }

    private fun bindViewModel() {
        viewModel.picture.observe(viewLifecycleOwner, Observer { picture ->
            setPicture(picture)
        })
    }

    fun setParam() {
        activity?.intent?.also { intent ->
            if (intent.hasExtra(Navigator.EXTRA_FLAT_KEY)) {
                val taskGson = intent.getStringExtra(Navigator.EXTRA_FLAT_KEY)
                val flat = Gson().fromJson(taskGson, Flat::class.java)
                flat_id = flat.id
            }
        }
    }

    fun setCurrentFlat(flat: Flat) {
        if (this.flat_id != flat.id) {
            this.flat_id = flat.id
            loadData()
        }
    }

    fun loadData() {

        setTypeSpinnerAdapter()

        activity?.also { activity ->
            creditListAdapter = CreditListAdapter(activity)
            creditListAdapter?.let { adapter ->
                binding.spCredit.adapter = adapter.getAdapter()
                val listData = adapter.listData
            }

            if (flat_id > 0) {

                val db = DB(activity)
                db.open()

                flat = db.getFlat(flat_id)
                db.close()

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

                    binding.cbFinish.isChecked = flat.finish

                    try {
                        setPicture(flat.foto)
//                        if (flat.foto != null) {
//                            setPicture(flat.foto)
//                        } else {
//                            imgFoto.setImageDrawable(null)

//                            val url = getPictureURL(flat_id)
//
//
//                            Glide.with(imgFoto)
//                                .asBitmap()
//                                .load(url)
//                                .into(imgFoto)
////                                .into(object : SimpleTarget<Bitmap>() {
////                                    override fun onResourceReady(
////                                        resource: Bitmap,
////                                        transition: Transition<in Bitmap>?
////                                    ) {
////                                        shareImage(listOf(resource))
////                                    }
////                                })

                    //    }
                    } catch (e: Throwable) {
                        Log.d("DMS_CREDIT", "error load foto $e")
                    }

                }
            }
        }
    }


    fun setPicture(picture: ByteArray?) {
        if (picture != null) {
            val imageStream = ByteArrayInputStream(picture)
            val bitmap = BitmapFactory.decodeStream(imageStream)

            binding.imgFoto.setImageBitmap(bitmap)
        } else {
            binding.imgFoto.setImageDrawable(null)
        }
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
            flat?.let {

                viewModel.loadPicture(it.uid)

            }
            true
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

    fun updateUI() {
        val listHomeTypes = HomeType.getHomeTypeList()
        val type = listHomeTypes.get(binding.spType.selectedItemPosition)

        binding.adresLayout.visibility =
                if (type == HomeType.Automobile)
                    View.GONE
                else
                    View.VISIBLE

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
                            bitmap = decodeSampledBitmapFromUri(activity, selectedImage, 100, 100)
                        }
                    } catch (e: Exception) {
                        Log.d("DMS_CREDIT", "BITMAP ERROR ${e.message}")

                    }
                    binding.imgFoto.setImageBitmap(bitmap)

                    // TODO Just for test
                    flat?.also { flat ->
                        viewModel.uploadPicture(flat.uid, selectedImage)
                    }
                    //

                }
            }

        }

        super.onActivityResult(requestCode, resultCode, data)
    }


   private fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight : Int) : Int {
        // Raw height and width of image
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {

            val halfHeight : Int = height / 2
            val halfWidth : Int = width / 2

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2
            }
        }

        return inSampleSize
    }

    fun getByteArrayFromFile(file: File): ByteArray? {
        var fis: FileInputStream? = null
        try {
            fis = FileInputStream(file.path)
            val bos = ByteArrayOutputStream()
            val b = ByteArray(1024)
            var readNum: Int
            while (fis.read(b).also { readNum = it } != -1) {
                bos.write(b, 0, readNum)
            }
            return bos.toByteArray()
        } catch (e: java.lang.Exception) {
            Log.d("mylog", e.toString())
        }
        return null
    }

    private fun decodeSampledBitmapFromUri(context: Context, imageUri: Uri, reqWidth: Int, reqHeight: Int) : Bitmap? {
            var bitmap : Bitmap? = null
            try {
                // Get input stream of the image
                val options = BitmapFactory.Options()
                var iStream = context.contentResolver.openInputStream(imageUri)

                // First decode with inJustDecodeBounds=true to check dimensions
                options.inJustDecodeBounds = true
                BitmapFactory.decodeStream(iStream, null, options)
                if (iStream != null) {
                    iStream.close()
                }
                iStream = context.contentResolver.openInputStream(imageUri)

                // Calculate inSampleSize
                options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight)

                // Decode bitmap with inSampleSize set
                options.inJustDecodeBounds = false
                bitmap = BitmapFactory.decodeStream(iStream, null, options)
                if (iStream != null) {
                    iStream.close()
                }
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            return bitmap
    }

    private fun hideKeyboard() {
        activity?.let {
            KeyboardUtils.hideKeyboard(it, it.currentFocus)
        }
    }
}