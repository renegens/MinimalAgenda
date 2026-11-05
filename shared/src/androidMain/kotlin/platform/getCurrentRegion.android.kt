package platform

import androidx.compose.ui.text.intl.Locale

actual fun getCurrentRegion(): String? {
    return Locale.current.region
}