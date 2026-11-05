package platform

import domain.model.AppTheme
import presentation.screens.add_event.EventEditorRequest
import presentation.screens.add_event.EventEditorRequests

/** Opens the shared editor (a navigation route in `App`) in add mode. The theme is applied by `App`. */
class AndroidModalPresenter(private val editorRequests: EventEditorRequests) : ModalPresenter {

    override fun presentAddEvent(appTheme: AppTheme, onSave: () -> Unit) {
        editorRequests.open(EventEditorRequest.Add, onSave)
    }
}
