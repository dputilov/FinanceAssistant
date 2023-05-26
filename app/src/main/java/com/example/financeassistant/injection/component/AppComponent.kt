package com.example.financeassistant.injection.component

import androidx.lifecycle.AndroidViewModel
import com.example.financeassistant.diagram.CreditDiagramViewModel
import com.example.financeassistant.exchange.ExchangeViewModel
import com.example.financeassistant.flat.FlatViewModel
import com.example.financeassistant.flat.mainPage.FlatMainViewModel
import com.example.financeassistant.flat.paymentListPage.FlatPaymentListViewModel
import com.example.financeassistant.graphic.GraphicListViewModel
import com.example.financeassistant.injection.module.AppModule
import com.example.financeassistant.injection.module.NetworkModule
import com.example.financeassistant.injection.module.ServiceRemoteModule
import com.example.financeassistant.injection.module.UseCaseModule
import com.example.financeassistant.main_window.creditPage.CreditListViewModel
import com.example.financeassistant.main_window.flatPage.FlatListViewModel
import com.example.financeassistant.main_window.taskPage.TaskViewModel
import com.example.financeassistant.sideMenu.SideMenuViewModel
import dagger.Component
import javax.inject.Singleton


object AndroidViewModelInjector{
    fun inject(androidViewModel: AndroidViewModel, appComponent: AppComponent, daggerInjectConstructorParameter: Any? = null){
        when (androidViewModel){
            is SideMenuViewModel -> appComponent.inject(androidViewModel)
            is GraphicListViewModel -> appComponent.inject(androidViewModel)
            is FlatViewModel -> appComponent.inject(androidViewModel)
            is FlatListViewModel -> appComponent.inject(androidViewModel)
            is TaskViewModel -> appComponent.inject(androidViewModel)
            is FlatPaymentListViewModel -> appComponent.inject(androidViewModel)
            is CreditDiagramViewModel -> appComponent.inject(androidViewModel)
            is ExchangeViewModel -> appComponent.inject(androidViewModel)
            is CreditListViewModel -> appComponent.inject(androidViewModel)
            is FlatMainViewModel -> appComponent.inject(androidViewModel)
        }
    }
}

/**
 * Application Component providing inject() methods for the instances where we are supposed to inject data.
 */
@Singleton
@Component(modules = [
    AppModule::class,
    UseCaseModule::class,
    ServiceRemoteModule::class,
    NetworkModule::class])
interface AppComponent {

    fun inject(target: SideMenuViewModel)

    fun inject(target: GraphicListViewModel)

    fun inject(target: FlatViewModel)

    fun inject(target: FlatListViewModel)

    fun inject(target: FlatMainViewModel)

    fun inject(target: TaskViewModel)

    fun inject(target: FlatPaymentListViewModel)

    fun inject(target: CreditDiagramViewModel)

    fun inject(target: ExchangeViewModel)

    fun inject(target: CreditListViewModel)

}