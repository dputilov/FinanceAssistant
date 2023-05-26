package com.example.financeassistant.main_window.taskPage

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.financeassistant.classes.Flat
import com.example.financeassistant.useCase.CreditUseCase
import com.example.financeassistant.useCase.GetTaskUseCase
import com.example.financeassistant.classes.FlatPayment
import com.example.financeassistant.classes.FlatPaymentOperationType
import com.example.financeassistant.classes.FlatPaymentOperationType.RENT
import com.example.financeassistant.classes.Payment
import com.example.financeassistant.classes.Task
import com.example.financeassistant.classes.TaskGroup
import com.example.financeassistant.classes.TaskItem
import com.example.financeassistant.classes.TaskItemType
import com.example.financeassistant.classes.TaskType
import com.example.financeassistant.utils.SingleLiveEvent
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class TaskViewModel: AndroidViewModel {

    constructor(application: Application) : super(application)

    var getTaskItemsListUseCase =  GetTaskUseCase()
    var creditUseCase =  CreditUseCase()

    var taskItemList = MutableLiveData<List<TaskItem>>()
    var groupTaskList : List<TaskGroup> = listOf()

    var taskToOpen = SingleLiveEvent<Task>()
    var taskIsClosed = SingleLiveEvent<Task>()

    var creditPayToOpen = SingleLiveEvent<Payment>()
    var flatPayToOpen = SingleLiveEvent<FlatPayment>()

    var taskToClose = SingleLiveEvent<Task>()
    var creditTaskToClose = SingleLiveEvent<Task>()
    var flatTaskToClose = SingleLiveEvent<Task>()
    var flatArendaTaskToClose = SingleLiveEvent<Task>()

    var showTaskLoadingIndicatorEvent = SingleLiveEvent<Void>()
    var hideTaskLoadingIndicatorEvent = SingleLiveEvent<Void>()

    private var getTasksSubscription: Disposable? = null

    override fun onCleared() {
        super.onCleared()

        getTasksSubscription?.dispose()

        getTaskItemsListUseCase.cancel()
    }

    fun getTaskList(){

        getTasksSubscription?.dispose()
        getTaskItemsListUseCase.cancel()

        getTasksSubscription = getTaskItemsListUseCase.getTaskItemsListSubject
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { onGetTaskItemsListStart() }
                .subscribe(
                        { listData: List<TaskGroup> -> onGetDataSuccess(listData) },
                        { error: Throwable -> onGetTaskItemsError(error) }
                )

        getTaskItemsListUseCase.getTaskItemsList(getApplication())

    }

    private fun onGetTaskItemsListStart() {
        showTaskLoadingIndicatorEvent.call()
    }

    private fun onGetDataSuccess(listData: List<TaskGroup>) {
        hideTaskLoadingIndicatorEvent.call()

        for (group in listData) {
            val oldGroup = groupTaskList.find { it.type == group.type }
            if (oldGroup != null) {
                group.isExpanded = oldGroup.isExpanded
            }
        }
        groupTaskList = listData

        updateTaskList()
    }

    fun updateTaskList() {
        val newTaskList = mutableListOf<TaskItem>()
        for (group in groupTaskList) {
            val newGroup = group.copy()
            newGroup.isExpanded = group.isExpanded
            val newGroupItem = TaskItem(item = newGroup, type = TaskItemType.Group)

            newTaskList.add(newGroupItem)

            if(group.isExpanded) {
                for (task in group.items) {
                    val newTask = task.copy()
                    newTask.isFinishChecked = task.isFinishChecked
                    newTask.updateRevision = task.updateRevision
                    val newTaskItem = TaskItem(item = newTask, type = TaskItemType.Task)
                    newTaskList.add(newTaskItem)
                }
            }
        }

        taskItemList.value = newTaskList
    }

    fun onGroupClick(taskGroup: TaskGroup) {
        for (group in groupTaskList) {
            if (group.type == taskGroup.type) {
                group.isExpanded = !group.isExpanded
            }
        }
        updateTaskList()
    }

    fun closeTask(taskToClose: Task) {

        if (taskToClose.isFinishChecked) {
            closeTaskInDB(taskToClose)
        } else {
            for (group in groupTaskList) {
                for (task in group.items) {
                    if (task.id == taskToClose.id ){
                        task.updateRevision++
                    }
                }
            }
        }

        updateTaskList()
    }

    fun onFinishTaskClick(task: Task) {
        taskToClose.value = task
    }

    fun onTaskItemClick(task: Task) {
        when(task.type) {
            TaskType.None, TaskType.Other -> taskToOpen.value = task
            TaskType.Credit -> creditTaskToClose.value = task
            TaskType.Flat -> flatTaskToClose.value = task
            TaskType.Arenda -> flatArendaTaskToClose.value = task
            else -> {}
        }
    }

    fun onCreditCloseApprove(task: Task) {
        creditPayToOpen.value = creditUseCase.getNextCreditPayment(getApplication(), task.parentId.toLong())
    }

    fun onFlatCloseApprove(task: Task) {
        val flatPayment = FlatPayment(flat = Flat(id = task.parentId),
                date = task.date,
                operation = RENT)

        flatPayToOpen.value = flatPayment
    }

    fun onFlatArendaCloseApprove(task: Task) {
        val flatPayment = FlatPayment(flat = Flat(id = task.parentId),
                date = task.date,
                summa = task.summa,
                operation = FlatPaymentOperationType.PROFIT)

        flatPayToOpen.value = flatPayment
    }

    private fun onGetTaskItemsError(error: Throwable) {
        Log.d("DMS_TASK", "onGetTaskItemsError = $error")
        //hideGraphicLoadingIndicatorEvent.call()
        //showGraphicLoadErrorEvent.call()
    }

    // Close task
    private fun closeTaskInDB(task: Task){

        getTasksSubscription?.dispose()
        getTaskItemsListUseCase.cancel()

        getTasksSubscription = getTaskItemsListUseCase.closeTaskSubject
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { onCloseTaskStart() }
                .subscribe(
                        { result: Boolean -> onCloseTaskSuccess(task, result) },
                        { error: Throwable -> onCloseTaskError(error) }
                )

        getTaskItemsListUseCase.closeTask(getApplication(), task)

    }

    private fun onCloseTaskStart() {
        //showTaskLoadingIndicatorEvent.call()
    }

    private fun onCloseTaskSuccess(task: Task, result: Boolean) {
        if (result) {
            for (group in groupTaskList) {
                group.items = group.items.filter { it.id != task.id }
                group.itemsCount = group.items.size
            }

            groupTaskList = groupTaskList.filter { it.itemsCount > 0 }

            taskIsClosed.value = task
        }

        updateTaskList()
    }

    private fun onCloseTaskError(error: Throwable) {
        Log.d("DMS_TASK", "onGetTaskItemsError = $error")
        //hideGraphicLoadingIndicatorEvent.call()
        //showGraphicLoadErrorEvent.call()
    }
}