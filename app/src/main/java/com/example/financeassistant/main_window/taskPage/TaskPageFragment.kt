package com.example.financeassistant.main_window.taskPage

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import com.example.financeassistant.R
import com.example.financeassistant.base.BaseFragment
import com.example.financeassistant.classes.ARGUMENT_PAGE_NUMBER
import com.example.financeassistant.classes.FlatPayment
import com.example.financeassistant.classes.Payment
import com.example.financeassistant.classes.SWIPE_REFRESH_DISTANCE_TO_TRIGGER
import com.example.financeassistant.classes.Task
import com.example.financeassistant.classes.TaskGroup
import com.example.financeassistant.classes.TaskItem
import com.example.financeassistant.databinding.ActivityMaintaskBinding
import com.example.financeassistant.task.TaskItemActivity
import com.example.financeassistant.utils.Navigator
import com.example.financeassistant.utils.NavigatorResultCode

class TaskPageFragment : BaseFragment<ActivityMaintaskBinding>(), TaskListAdapterDelegate {

    companion object {
        fun newInstance(page: Int): TaskPageFragment {
            val pageFragment = TaskPageFragment()
            val arguments = Bundle()
            arguments.putInt(ARGUMENT_PAGE_NUMBER, page)
            pageFragment.arguments = arguments
            return pageFragment
        }
    }

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> ActivityMaintaskBinding
        = ActivityMaintaskBinding::inflate

    val PAGE = 0
    private val CM_DELETE_ID = 1

    var pageNumber: Int = 0
    var backColor: Int = 0

    private val viewModel: TaskViewModel by activityViewModels()
    lateinit var taskListAdapter: TaskListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.also { activity ->
            arguments?.also { arguments ->
                pageNumber = arguments.getInt(ARGUMENT_PAGE_NUMBER)

                // ========= Work with data base ==============
                // открываем подключение к БД
//                val db = DB(activity)
//                db.open()
//
//                // автоформирование задач
//                db.autoTask()
//                db.autoFlatTask()
//
//                db.close()

                setHasOptionsMenu(true)
            }
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // добавляем контекстное меню к списку
        registerForContextMenu(binding.taskRecyclerView)

        bindViewModel()

        initAdapter()

        setListeners()

        initSwipeRefresh()

        viewModel.getTaskList()

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == NavigatorResultCode.CreateTask.resultCode) {
            if (resultCode == Activity.RESULT_OK) {

                //val name = data!!.getStringExtra("name")

                Toast.makeText(activity, "Создана новая задача", Toast.LENGTH_LONG).show()
            }
        }

        if (requestCode == NavigatorResultCode.EditTask.resultCode) {
            if (resultCode == Activity.RESULT_OK) {

//                val summa = 0.0
//                val name = data!!.getStringExtra("name")

                Toast.makeText(activity, "Изменена задача", Toast.LENGTH_LONG).show()
            }
        }

        viewModel.getTaskList()

    }

    private fun bindViewModel() {

        viewModel.taskItemList.observe(viewLifecycleOwner, Observer { taskItemList ->
            taskItemList?.also { taskItemList ->
                updateAdapter(taskItemList)
            }
        })

        viewModel.taskToOpen.observe(viewLifecycleOwner, Observer { taskToOpen ->
            taskToOpen?.also { taskToOpen ->
                showTaskView(taskToOpen)
            }
        })

        viewModel.taskToClose.observe(viewLifecycleOwner, Observer { taskToClose ->
            taskToClose?.also { taskToClose ->
                beforeCloseTask(taskToClose)
            }
        })

        viewModel.taskIsClosed.observe(viewLifecycleOwner, Observer { task ->
            task?.also { task ->
                onTaskIsClosed(task)
            }
        })

        viewModel.creditTaskToClose.observe(viewLifecycleOwner, Observer { creditId ->
            creditId?.also { creditId ->
                onCreditCloseClick(creditId)
            }
        })

        viewModel.creditPayToOpen.observe(viewLifecycleOwner, Observer { creditPayToOpen ->
            creditPayToOpen?.also { creditPayToOpen ->
                showCurrentPaymentView(creditPayToOpen)
            }
        })

        viewModel.flatArendaTaskToClose.observe(viewLifecycleOwner, Observer { flatArendaTaskToClose ->
            flatArendaTaskToClose?.also { flatArendaTaskToClose ->
                onFlatArendaCloseClick(flatArendaTaskToClose)
            }
        })

        viewModel.flatTaskToClose.observe(viewLifecycleOwner, Observer { flatTaskToClose ->
            flatTaskToClose?.also { flatTaskToClose ->
                onFlatPayCloseClick(flatTaskToClose)
            }
        })

        viewModel.flatPayToOpen.observe(viewLifecycleOwner, Observer { flatPayToOpen ->
            flatPayToOpen?.also { flatPayToOpen ->
                showFlatPayView(flatPayToOpen)
            }
        })

        viewModel.showTaskLoadingIndicatorEvent.observe(viewLifecycleOwner, Observer {
            showTaskLoadingIndicator()
        })

        viewModel.hideTaskLoadingIndicatorEvent.observe(viewLifecycleOwner, Observer {
            hideTaskLoadingIndicator()
        })

    }

    private fun initSwipeRefresh() {
        binding.taskSwipeRefreshLayout.setOnRefreshListener(object : OnRefreshListener {
            override fun onRefresh() {
                viewModel.getTaskList()
            }
        })

        binding.taskSwipeRefreshLayout.setDistanceToTriggerSync(SWIPE_REFRESH_DISTANCE_TO_TRIGGER)
    }

    private fun initAdapter(){
        activity?.also { activity ->
            taskListAdapter = TaskListAdapter(activity, this)
            binding.taskRecyclerView.layoutManager = LinearLayoutManager(activity)
            binding.taskRecyclerView.adapter = taskListAdapter

            //(taskRecyclerView.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false

        }
    }

    private fun updateAdapter(taskItemList: List<TaskItem>){
        taskListAdapter.updateList(taskItemList)
    }

    private fun setListeners() {
        binding.taskFloatingActionButton.setOnClickListener {
            activity?.also { activity ->
                val task = Task(id = -1)
                Navigator.navigateToTaskActivity(activity, this, task, NavigatorResultCode.CreateTask.resultCode)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item!!.itemId


        if (id == R.id.action_add) {
            val intent = Intent(activity, TaskItemActivity::class.java)
            startActivityForResult(intent, 1)
        }

        if (id == R.id.action_option_add) {
            val intent = Intent(activity, TaskItemActivity::class.java)
            startActivityForResult(intent, 1)
        }

        if (id == R.id.action_option_delete_all) {

            onDeleteAllTaskClick()

        }

        return super.onOptionsItemSelected(item)
    }

    fun DeleteTaskAll() {
       // db.deleteAllTask()
    }

    private fun getAlertTitle(): String{
        return getString(R.string.task_alert_title)
    }

    fun onCreditCloseClick(task: Task){
        val builder = AlertDialog.Builder(activity, R.style.AlertDialogStyle)
        builder.setTitle(getAlertTitle())
                .setMessage("Ввести очередной платеж?")
                .setIcon(R.drawable.ic_launcher)
                .setCancelable(true)
                .setPositiveButton("Создать платеж",
                        {dialog, id ->
                            viewModel.onCreditCloseApprove(task)
                        })
                .setNegativeButton("Отмена",
                        { dialog, id ->
                            dialog.cancel()
                        })
        val alert = builder.create()
        alert.show()
    }

    private fun onFlatArendaCloseClick(task: Task){

        val builder = AlertDialog.Builder(activity, R.style.AlertDialogStyle)
        builder.setTitle(getAlertTitle())
                .setMessage("Ввести поступление по аренде?")
                .setIcon(R.drawable.ic_launcher)
                .setCancelable(true)
                .setPositiveButton("Платеж за аренду",
                        {dialog, id ->
                            viewModel.onFlatArendaCloseApprove(task)
                        })
                .setNegativeButton("Отмена",
                        { dialog, id ->
                            dialog.cancel()
                        })
        val alert = builder.create()
        alert.show()
    }

    private fun onFlatPayCloseClick(task: Task){

        val builder = AlertDialog.Builder(activity, R.style.AlertDialogStyle)
        builder.setTitle(getAlertTitle())
                .setMessage("Ввести платеж за коммунальные услуги?")
                .setIcon(R.drawable.ic_launcher)
                .setCancelable(true)
                .setPositiveButton("Оплатить квартплату",
                        {dialog, id ->
                            viewModel.onFlatCloseApprove(task)
                        })
                .setNegativeButton("Отмена",
                        { dialog, id ->
                            dialog.cancel()
                        })
        val alert = builder.create()
        alert.show()
    }

    private fun onDeleteAllTaskClick(){

        val builder = AlertDialog.Builder(activity, R.style.AlertDialogStyle)
        builder.setTitle(getAlertTitle())
                .setMessage("Удалить все задачи?")
                .setIcon(R.drawable.ic_launcher)
                .setCancelable(true)
                .setPositiveButton("Удалить все задачи",
                        {dialog, id ->
                            DeleteTaskAll()
                        })
                .setNegativeButton("Отмена",
                        { dialog, id ->
                            dialog.cancel()
                        })
        val alert = builder.create()
        alert.show()
    }

    private fun showCurrentPaymentView(payment: Payment){
        activity?.also { activity ->
            Navigator.navigateToPaymentActivity(activity, this, payment)
        }
    }

    private fun showFlatPayView(flatPayment: FlatPayment) {
        activity?.also { activity ->
            Navigator.navigateToFlatPaymentActivity(activity, this, flatPayment)
        }
    }

    private fun showTaskView(task: Task) {
        activity?.also { activity ->
            Navigator.navigateToTaskActivity(activity, this, task, NavigatorResultCode.EditTask.resultCode)
        }
    }

    private fun beforeCloseTask(task: Task) {
        val ad = AlertDialog.Builder(context, R.style.AlertDialogStyle)
        ad.setTitle(getAlertTitle())  // заголовок
        ad.setMessage("Закрыть задачу?") // сообщение
        ad.setPositiveButton("Да") { dialog, arg1 ->
            task.isFinishChecked = true
            viewModel.closeTask(task)
        }
        ad.setNegativeButton("Отмена") { dialog, arg1 ->
            task.isFinishChecked = false
            viewModel.closeTask(task)
        }

        ad.setCancelable(true)
        ad.setOnCancelListener { }

        ad.show()
    }

    private fun onTaskIsClosed(task: Task) {
        Toast.makeText(activity, "Завершена ${task.id}", Toast.LENGTH_LONG).show()
    }

    private fun showTaskLoadingIndicator() {
//        taskRecyclerView.visibility = View.GONE
//        taskProgressBar.visibility = View.VISIBLE
    }

    private fun hideTaskLoadingIndicator() {
        binding.taskSwipeRefreshLayout.isRefreshing = false
//        taskRecyclerView.visibility = View.VISIBLE
//        taskProgressBar.visibility = View.GONE
    }

    override fun onTaskGroupClick(taskGroup: TaskGroup) {
        viewModel.onGroupClick(taskGroup)
    }

    override fun onTaskItemClick(task: Task) {
        viewModel.onTaskItemClick(task)
    }

    override fun onFinishCheckBoxClick(task: Task) {
        viewModel.onFinishTaskClick(task)
    }
}