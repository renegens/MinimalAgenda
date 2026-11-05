package presentation.screens.calendar

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import domain.model.AppThemeMode
import presentation.components.BodyText
import presentation.components.MainCTAButton
import presentation.components.PaperBackground
import androidx.compose.ui.text.style.TextAlign

/** Shown when permission is granted but the device has no calendars. */
@Composable
fun NoCalendarsView(appThemeMode: AppThemeMode, onCreateCalendar: () -> Unit) {
    PaperBackground(appThemeMode) {
        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            BodyText(
                text = "No calendars on this device. Add an account (for example Google, or DAVx⁵ for CalDAV), " +
                        "or create a calendar that stays on this device.",
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(32.dp))
            MainCTAButton("Create device-only calendar", onCreateCalendar)
        }
    }
}
