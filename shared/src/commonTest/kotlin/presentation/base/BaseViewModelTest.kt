package presentation.base

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class BaseViewModelTest {
    private data class State(val count: Int = 0) : UiState
    private data object Increment : UiEvent
    private data object Effect : UiEffect
    private class Counter : BaseViewModel<Increment, State, Effect>() {
        override fun createInitialState() = State()
        override fun handleEvent(event: Increment) = setState { copy(count = count + 1) }
    }

    @Test
    fun simultaneousStateUpdatesDoNotLoseEvents() = runTest {
        val vm = Counter()
        List(8) { launch(Dispatchers.Default) { repeat(500) { vm.setEvent(Increment) } } }.joinAll()
        assertEquals(4000, vm.uiState.value.count)
    }
}
