package com.example.financeassistant.sideMenu

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.example.financeassistant.base.BaseAndroidViewModel
import com.example.financeassistant.useCase.GetSideMenuUseCase
import com.example.financeassistant.classes.Credit
import com.example.financeassistant.classes.Flat
import com.example.financeassistant.classes.SideMenuItem
import com.example.financeassistant.classes.SideMenuItemType
import com.example.financeassistant.classes.SideMenu
import com.example.financeassistant.useCase.services.ExchangeCreditUseCase
import com.example.financeassistant.utils.SingleLiveEvent
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

/**
 * Side menu
 */

class SideMenuViewModel(application: Application): BaseAndroidViewModel(application) {

    @Inject
    lateinit var exchangeUseCase: dagger.Lazy<ExchangeCreditUseCase>

    var createMenuItemsUseCase = GetSideMenuUseCase()

    var menuItemsList = MutableLiveData<List<SideMenuItem>>()
    var sideMenu : List<SideMenu> = listOf()

    var activeMenuItem = MutableLiveData<SideMenuItem>()

    var creditToOpen = SingleLiveEvent<Credit>()
    var flatToOpen = SingleLiveEvent<Flat>()


    val showExchangeEvent = SingleLiveEvent<Void>()
    val showSettingsEvent = SingleLiveEvent<Void>()
    val showDiagramEvent = SingleLiveEvent<Void>()
    val closeEvent = SingleLiveEvent<Void>()

    private var exchangeSubscription: Disposable? = null

    private var getSideMenuSubscription: Disposable? = null

    override fun onCleared() {
        super.onCleared()

        getSideMenuSubscription?.dispose()
    }

    fun onItemClick(itemMenu: SideMenuItem) {
        when (itemMenu.type) {
            SideMenuItemType.CreditGroup, SideMenuItemType.FlatGroup -> {
                onGroupClick(itemMenu)
            }
            SideMenuItemType.FlatItem, SideMenuItemType.CreditItem -> {
                itemMenu.item?.also { item ->
                    when (item) {
                        is Credit -> creditToOpen.value = item
                        is Flat -> flatToOpen.value = item
                    }
                }
            }
            SideMenuItemType.Diagram -> showDiagramEvent.call()
            SideMenuItemType.Share -> showExchangeEvent.call()
            SideMenuItemType.Settings -> showSettingsEvent.call()
            SideMenuItemType.Exit -> closeEvent.call()
            else -> {}
        }
    }

    fun onGroupClick(itemMenu: SideMenuItem) {
        for (group in sideMenu) {
            if (group.id == itemMenu.id) {
                group.isExpanded = !group.isExpanded
            }
        }
        updateMenuList()
    }

    fun createMenuItems(){

        getSideMenuSubscription?.dispose()

        getSideMenuSubscription = createMenuItemsUseCase.getMenuListSubject
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { onGetFlatItemsListStart() }
            .subscribe(
                { listData: List<SideMenu> -> onGetDataSuccess(listData) },
                { error: Throwable -> onGetDatasError(error) }
            )

        createMenuItemsUseCase.getSideMenuList(getApplication())

    }

    private fun onGetFlatItemsListStart() {
       // showFlatLoadingIndicatorEvent.call()
    }

    private fun onGetDataSuccess(listData: List<SideMenu>) {
        //hideFlatLoadingIndicatorEvent.call()

        this.sideMenu = listData

        updateMenuList()
    }

    private fun updateMenuList() {
        val newList = mutableListOf<SideMenuItem>()
        for (group in sideMenu) {
            val newGroup = group.copy()
            newGroup.isExpanded = group.isExpanded
            val newGroupItem = SideMenuItem(
                id = group.id,
                title = group.title,
                icon = group.icon,
                item = newGroup,
                type = group.type,
                itemsCountAll = group.itemsList?.size,
                itemsCountActive = group.itemsList?.count {
                    it.item?.let { item ->
                        when (item) {
                            is Credit -> !item.finish
                            is Flat -> !item.isFinish
                            else -> false
                        }
                    } ?: false
                })

            newGroupItem.isExpanded = newGroup.isExpanded

            newList.add(newGroupItem)

            if(group.isExpanded) {
                group.itemsList?.forEach {
                    newList.add(SideMenuItem(it.id, title = it.title, item = it.item, type = it.type))
                }
            }
        }

        menuItemsList.value = newList
    }

    private fun onGetDatasError(error: Throwable) {
        //Log.d("DMS_TASK", "onGetDatasError = $error")
        //hideGraphicLoadingIndicatorEvent.call()
        //showGraphicLoadErrorEvent.call()
    }

}
