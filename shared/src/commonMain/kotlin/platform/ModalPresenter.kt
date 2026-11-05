package platform

import domain.model.AppTheme
import kotlinx.datetime.LocalDate

interface ModalPresenter {
    fun presentAddEvent(appTheme: AppTheme, date: LocalDate? = null, onSave: () -> Unit)
}