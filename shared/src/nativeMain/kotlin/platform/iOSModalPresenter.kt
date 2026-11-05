package platform

import androidx.compose.ui.window.ComposeUIViewController
import domain.model.AppTheme
import platform.UIKit.UIApplication
import presentation.screens.add_event.AddEventScreen
import presentation.theme.AppTheme

@Suppress("ClassName")
class iOSModalPresenter : ModalPresenter {

    override fun presentAddEvent(appTheme: AppTheme, onSave: () -> Unit) {
        val rootVC = UIApplication.sharedApplication.keyWindow?.rootViewController
        val s = ComposeUIViewController(
            content = {
                AppTheme(appThemeMode = appTheme.appThemeMode, appColor = appTheme.appColor) {
                    AddEventScreen(onCancel = {
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