package presentation.screens.main

import androidx.lifecycle.viewModelScope
import com.revenuecat.purchases.kmp.Purchases
import domain.use_case.ListenAppThemeUseCase
import presentation.base.BaseViewModel
import presentation.screens.main.AppContract.*
import extensions.initializeRevenueCat
import extensions.isSubscribed
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class AppViewModel(private val listenAppThemeUseCase: ListenAppThemeUseCase) :
    BaseViewModel<Event, State, Effect>() {

    init {
        initializeRevenueCat()
        Purchases.sharedInstance.getCustomerInfo(onError = { error ->
            setState { copy(isSubscribed = false) }
        }, onSuccess = { customerInfo ->
            setState { copy(isSubscribed = customerInfo.isSubscribed()) }
        })

        viewModelScope.launch {
            listenAppThemeUseCase().collectLatest {
                setState { copy(appTheme = it) }
            }
        }
    }

    override fun createInitialState() = State()

    override fun handleEvent(event: Event) {
        when (event) {
            Event.OnPurchaseCompleted -> setState { State(isSubscribed = true) }
        }
    }
}