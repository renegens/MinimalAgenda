package platform

import androidx.compose.ui.window.ComposeUIViewController
import domain.model.AppTheme
import kotlinx.datetime.LocalDate
import platform.UIKit.UIApplication
import presentation.screens.add_event.AddEventScreen
import presentation.screens.add_event.EventEditorRequest
import presentation.theme.AppTheme

@Suppress("ClassName")
class iOSModalPresenter : ModalPresenter {

    override fun presentAddEvent(appTheme: AppTheme, date: LocalDate?, onSave: () -> Unit) {
        val request = EventEditorRequest.Add(date)
        val rootVC = UIApplication.sharedApplication.keyWindow?.rootViewController
        val s = ComposeUIViewController(
            content = {
                AppTheme(appThemeMode = appTheme.appThemeMode, appColor = appTheme.appColor) {
                    AddEventScreen(request = request, onCancel = {
                        rootVC?.dismissViewControllerAnimated(
                            true,
                            completion = null
                        )
                    }, onSave = {
                        rootVC?.dismissViewControllerAnimated(
                            true,
                            completion = null
                        )
                        onSave.invoke()
                    })
                }
            }
        )
        //val navigationController = UINavigationController(rootViewController = s)

        // Present the navigation controller
        rootVC?.presentViewController(s, animated = true, completion = null)
    }
}