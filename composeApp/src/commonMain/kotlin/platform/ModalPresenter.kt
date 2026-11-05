package platform

import domain.model.AppTheme

interface ModalPresenter {
    fun presentAddEvent(appTheme: AppTheme, onSave: () -> Unit)
}