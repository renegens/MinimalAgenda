package presentation.screens.main

import com.revenuecat.purchases.kmp.Purchases
import presentation.base.BaseViewModel
import presentation.screens.main.AppContract.*
import extensions.initializeRevenueCat
import extensions.isSubscribed

class AppViewModel() :
    BaseViewModel<Event, State, Effect>() {

    init {
        initializeRevenueCat()
        Purchases.sharedInstance.getCustomerInfo(
            onError = { error ->
                setState { State(isSubscribed = false) }
            },
            onSuccess = { customerInfo ->
                setState { State(isSubscribed = customerInfo.isSubscribed()) }
            })
    }

    override fun createInitialState() = State()

    override fun handleEvent(event: Event) {
        when (event) {
            Event.OnPurchaseCompleted -> setState { State(isSubscribed = true) }
        }
    }
}