package presentation.screens.add_event

import domain.model.AgendaEvent
import kotlinx.datetime.LocalDate
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow

/** What the shared event editor is opened for. */
sealed interface EventEditorRequest {
    data class Add(val date: LocalDate? = null) : EventEditorRequest
    class Edit(val event: AgendaEvent) : EventEditorRequest
}

/**
 * Bridge between the platform presenters (`ModalPresenter`, `EventEditViewHandler`) and the Compose
 * navigation in `App`: on Android they call [open], `App` navigates to the editor route and reads [current].
 * iOS never uses it (it presents the editor with its own view controllers).
 */
class EventEditorRequests {
    class Pending(val request: EventEditorRequest, val onSave: () -> Unit)

    /** The editor currently requested; kept until [close] so the editor survives recomposition/recreation. */
    var current: Pending? = null
        private set

    private val _opened = MutableSharedFlow<Pending>(extraBufferCapacity = 1)

    /** Emits once per [open]; `App` navigates to the editor route when it fires. */
    val opened: SharedFlow<Pending> = _opened

    fun open(request: EventEditorRequest, onSave: () -> Unit) {
        val pending = Pending(request, onSave)
        current = pending
        _opened.tryEmit(pending)
    }

    fun close() {
        current = null
    }
}
