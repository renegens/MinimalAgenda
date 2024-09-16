package platform

import domain.model.AppTheme

interface ModalPresenter {
    fun presentAddEvent(appTheme: AppTheme, onCompletion: () -> Unit)
    fun presentPaywall(appTheme: AppTheme, onCompletion: () -> Unit)
}