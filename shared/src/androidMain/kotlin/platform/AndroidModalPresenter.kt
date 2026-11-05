package platform

import domain.model.AppTheme
import kotlinx.datetime.LocalDate
import presentation.screens.add_event.EventEditorRequest
import presentation.screens.add_event.EventEditorRequests

/** Opens the shared editor (a navigation route in `App`) in add mode. The theme is applied by `App`. */
class AndroidModalPresenter(private val editorRequests: EventEditorRequests) : ModalPresenter {

    override fun presentAddEvent(appTheme: AppTheme, date: LocalDate?, onSave: () -> Unit) {
        editorRequests.open(EventEditorRequest.Add(date), onSave)
    }
}
