package platform

import androidx.compose.ui.window.ComposeUIViewController
import domain.model.AppTheme
import platform.UIKit.UIApplication
import presentation.screens.add_event.AddEventScreen
import presentation.screens.paywall.PaywallScreen
import presentation.theme.AppTheme

@Suppress("ClassName")
class iOSModalPresenter : ModalPresenter {

    override fun presentAddEvent(appTheme: AppTheme, onCompletion: () -> Unit) {
        val rootVC = UIApplication.sharedApplication.keyWindow?.rootViewController
        val s = ComposeUIViewController(
            content = {
                AppTheme(appThemeMode = appTheme.appThemeMode, appColor = appTheme.appColor) {
                    AddEventScreen(onCancel = {
                        rootVC?.dismissViewControllerAnimated(
                            true,
                            onCompletion
                        )
                    }, onSave = {
                        rootVC?.dismissViewControllerAnimated(
                            true,
                            onCompletion
                        )
                    })
                }
            }
        )
        rootVC?.presentViewController(s, animated = true, completion = null)
    }

    override fun presentPaywall(appTheme: AppTheme, onCompletion: () -> Unit) {
        val rootVC = UIApplication.sharedApplication.keyWindow?.rootViewController
        val s = ComposeUIViewController(
            content = {
                AppTheme(appThemeMode = appTheme.appThemeMode, appColor = appTheme.appColor) {
                    PaywallScreen(onPurchaseComplete = {
                        rootVC?.dismissViewControllerAnimated(
                            true,
                            onCompletion
                        )
                    }, onCancel = {
                        rootVC?.dismissViewControllerAnimated(
                            true,
                            onCompletion
                        )
                    })
                }
            }
        )
        rootVC?.presentViewController(s, animated = true, completion = null)
    }
}