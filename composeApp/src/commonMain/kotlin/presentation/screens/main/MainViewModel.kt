package presentation.screens.main

import androidx.lifecycle.viewModelScope
import data.DateGeneratorAsync
import kotlinx.coroutines.launch
import presentation.base.BaseViewModel

class MainViewModel : BaseViewModel<MainContract.Event, MainContract.State, MainContract.Effect>() {

    private val dateGeneratorAsync = DateGeneratorAsync()

    init {
        setEvent(MainContract.Event.LoadData)
    }

    private fun loadData() {
        viewModelScope.launch {
            val data = dateGeneratorAsync.run()
            setState {
                MainContract.State(calendar = data)
            }
        }
    }

    override fun createInitialState() = MainContract.State(null)

    override fun handleEvent(event: MainContract.Event) {
        when (event){
            MainContract.Event.LoadData -> loadData()
        }
    }
}