package com.example.dmsflatmanager.main_window.flatPage

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.ContextMenu
import android.view.ContextMenu.ContextMenuInfo
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import com.example.financeassistant.R
import com.example.financeassistant.base.BaseFragment
import com.example.financeassistant.classes.FlatPayment
import com.example.financeassistant.classes.ARGUMENT_PAGE_NUMBER
import com.example.financeassistant.classes.FlatItem
import com.example.financeassistant.classes.Flat
import com.example.financeassistant.classes.SWIPE_REFRESH_DISTANCE_TO_TRIGGER
import com.example.financeassistant.database.DB
import com.example.financeassistant.databinding.ActivityFlatBinding
import com.example.financeassistant.flat.FlatActivity
import com.example.financeassistant.flatPayment.FlatPaymentActivity
import com.example.financeassistant.flatPayment.FlatPaymentListActivity
import com.example.financeassistant.main_window.flatPage.FlatListAdapter
import com.example.financeassistant.main_window.flatPage.FlatListAdapterDelegate
import com.example.financeassistant.main_window.flatPage.FlatListViewModel
import com.example.financeassistant.manager.SettingsManager
import com.example.financeassistant.utils.Navigator
import com.example.financeassistant.utils.NavigatorResultCode
import com.example.financeassistant.utils.ToolbarUtils
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson

class FlatPageFragment : BaseFragment<ActivityFlatBinding>(), FlatListAdapterDelegate {

    companion object {
        private val CM_DELETE_ID = 1
        private val CM_PAYLIST_ID = 5
        private val CM_PAY_ID = 4

        val PAGE = 2

        fun newInstance(page: Int): FlatPageFragment {
            val pageFragment = FlatPageFragment()
            val arguments = Bundle()
            arguments.putInt(ARGUMENT_PAGE_NUMBER, page)
            pageFragment.arguments = arguments
            return pageFragment
        }
    }

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> ActivityFlatBinding
        = ActivityFlatBinding::inflate

    var pageNumber: Int = 0
    var backColor: Int = 0

    var prevVisibleItem = -1

    lateinit var db: DB

    private val viewModel: FlatListViewModel by activityViewModels()
    lateinit var flatListAdapter: FlatListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.also { activity ->
            arguments?.also { arguments ->
                pageNumber = arguments.getInt(ARGUMENT_PAGE_NUMBER)
            }
            setHasOptionsMenu(true)

            // ========= Work with data base ==============
            // открываем подключение к БД
            db = DB(activity)
            db.open()
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // добавляем контекстное меню к списку
        registerForContextMenu(binding.flatRecyclerView)

        bindViewModel()

        initAdapter()

        setListeners()

        initSwipeRefresh()

        updateList()

    }

    fun updateList() {
        viewModel.getFlatList()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item!!.itemId

        if (id == R.id.action_option_show_closed) {
            SettingsManager.instance.setShowCloseFlatSettings(true)
            updateOptionalMenu()
            updateList()
        }

        if (id == R.id.action_option_hide_closed) {
            SettingsManager.instance.setShowCloseFlatSettings(false)
            updateOptionalMenu()
            updateList()
        }

        if (id == R.id.action_add) {
            activity?.also { activity ->
                Navigator.navigateToFlatActivity(activity, this, Flat())
            }
        }

        if (id == R.id.action_option_add) {
            activity?.also { activity ->
                Navigator.navigateToFlatActivity(activity, this, Flat())
            }
        }
        if (id == R.id.action_option_delete_all) {

            //  DeleteAll();

        }

        return super.onOptionsItemSelected(item)
    }

    private fun updateOptionalMenu() {
        ToolbarUtils.setupActionBar(activity as AppCompatActivity, PAGE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {

                val name = data!!.getStringExtra("name")

                Toast.makeText(activity, "Создан $name", Toast.LENGTH_LONG).show()

            }
        }

        if (requestCode == NavigatorResultCode.FlatPayment.resultCode) {
            if (resultCode == Activity.RESULT_OK) {

                val name = data!!.getStringExtra(Navigator.EXTRA_FLAT_KEY)
                if (name != null) {
                    val flat = Gson().fromJson(name, Flat::class.java)
                    Toast.makeText(activity, "Изменен $flat.name", Toast.LENGTH_LONG).show()
                }

            }
        }

        viewModel.getFlatList()
    }

    override fun onCreateContextMenu(menu: ContextMenu, v: View, menuInfo: ContextMenuInfo?) {
        super.onCreateContextMenu(menu, v, menuInfo)
        //menu.add(0, CM_DELETE_ID, 100, R.string.action_flat_delete);
        menu.add(1, CM_PAY_ID, 100, R.string.action_flat_pay)
        menu.add(2, CM_PAYLIST_ID, 100, R.string.action_flat_paylist)

        menu.setHeaderTitle("Операции")
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        if (item!!.itemId == CM_DELETE_ID) {
            // получаем из пункта контекстного меню данные по пункту списка
            val acmi = item.menuInfo as AdapterView.AdapterContextMenuInfo
            // извлекаем id записи и удаляем соответствующую запись в БД
            db.flat_Delete(acmi.id)

            // получаем новый курсор с данными
            viewModel.getFlatList()
            return true
        }

        if (item.itemId == CM_PAYLIST_ID) {
            // получаем из пункта контекстного меню данные по пункту списка
            val acmi = item.menuInfo as AdapterView.AdapterContextMenuInfo

            val intent = Intent(activity, FlatPaymentListActivity::class.java)
            intent.putExtra("id", "" + acmi.id)
            startActivityForResult(intent, 7)

            return true
        }

        if (item.itemId == CM_PAY_ID) {
            // получаем из пункта контекстного меню данные по пункту списка
            val acmi = item.menuInfo as AdapterView.AdapterContextMenuInfo

            val intent = Intent(activity, FlatPaymentActivity::class.java)
            intent.putExtra("id", "" + acmi.id)
            startActivityForResult(intent, 5)

            return true
        }

        return super.onContextItemSelected(item)
    }

    override fun onStart() {
        super.onStart()

    }

    override fun onDestroy() {
        super.onDestroy()
        // закрываем подключение при выходе
        db.close()
    }


    private fun bindViewModel() {
        viewModel.flatItemList.observe(viewLifecycleOwner, Observer { flatItemList ->
            flatItemList?.also { flatItemList ->
                updateAdapter(flatItemList)
            }
        })

        viewModel.showFlatLoadingIndicatorEvent.observe(viewLifecycleOwner, Observer {
            showFlatLoadingIndicator()
        })

        viewModel.hideFlatLoadingIndicatorEvent.observe(viewLifecycleOwner, Observer {
            hideFlatLoadingIndicator()
        })
    }

    private fun initAdapter(){
        activity?.also { activity ->
            flatListAdapter = FlatListAdapter(activity, this)
            binding.flatRecyclerView.layoutManager = LinearLayoutManager(activity)
            binding.flatRecyclerView.adapter = flatListAdapter
            binding.flatRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {

                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)

                    val MOTION_DRAG_SCALE = 5

                    val deltaProgress = dy / 100f / MOTION_DRAG_SCALE
                    var progress = binding.flatMoutionlayout.progress + deltaProgress

                    if (progress < 0f) progress = 0f
                    if (progress > 1f) progress = 1f

                    binding.flatMoutionlayout.progress = progress

                }

                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {

                        val firstVisibleItem = getCurrentItem()

//                        if (prevVisibleItem != firstVisibleItem) {
//                            if (prevVisibleItem < firstVisibleItem)
//                                PageFlatsFloatingActionButton.show()
//                            else
//                                PageFlatsFloatingActionButton.show()
//
//                            prevVisibleItem = firstVisibleItem
//                        }

                    }
                }
            })
        }
    }

    private fun getCurrentItem(): Int {
        return (binding.flatRecyclerView.getLayoutManager() as LinearLayoutManager)
            .findFirstVisibleItemPosition()
    }

    private fun updateAdapter(flatItemList: List<FlatItem>){
        flatListAdapter.flatItemList = flatItemList
        flatListAdapter.notifyDataSetChanged()
    }

    private fun setListeners() {
        binding.PageFlatsFloatingActionButton.setOnClickListener { view ->
            val intent = Intent(activity, FlatActivity::class.java)
            startActivityForResult(intent, 1)

            Snackbar.make(view, "Flat added №", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

    }

    private fun initSwipeRefresh() {
        binding.flatSwipeRefreshLayout.setOnRefreshListener( object : OnRefreshListener {
                override fun onRefresh() {
                    updateList()
                }
            })

        binding.flatSwipeRefreshLayout.setDistanceToTriggerSync(SWIPE_REFRESH_DISTANCE_TO_TRIGGER)
    }

    fun DeleteAll() {
        db.flat_DeleteAll()
    }

    private fun showFlatLoadingIndicator() {

//        taskRecyclerView.visibility = View.GONE
//        taskProgressBar.visibility = View.VISIBLE
    }

    private fun hideFlatLoadingIndicator() {
        binding.flatSwipeRefreshLayout.isRefreshing = false
//        taskRecyclerView.visibility = View.VISIBLE
//        taskProgressBar.visibility = View.GONE
    }

    // Delegate methods
    override fun onFlatItemClick(flat: Flat) {
        activity?.also { activity ->
            Navigator.navigateToFlatActivity(activity, this, flat)
        }
    }

    override fun onArendaActionClick(flatPayment: FlatPayment) {
        activity?.also { activity ->
            Navigator.navigateToFlatPaymentActivity(activity, this, flatPayment)
        }
    }

    override fun onRentActionClick(flatPayment: FlatPayment) {
        navigateToFlatPaymentActivity(flatPayment)
    }

    // Navigator methods
    private fun navigateToFlatPaymentActivity(acc: FlatPayment) {
        activity?.also { activity ->
            Navigator.navigateToFlatPaymentActivity(activity, this, acc)
        }
    }

}