package presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import presentation.screens.calendar.CalendarContract.Section
import presentation.theme.DMSans18Bold60
import presentation.theme.DMSans18Medium

@Composable
fun FloatingBottomNavigation(
    currentSection: Section,
    onSectionSelected: (tappedSection: Section, currentSection: Section) -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current
    
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 20.dp)
    ) {
        FloatingBackground(
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                NavigationItem(
                    section = Section.Day,
                    currentSection = currentSection,
                    label = "Day",
                    onClick = {
                        haptic.addHapticFeedback()
                        onSectionSelected(Section.Day, currentSection)
                    }
                )

                NavigationItem(
                    section = Section.Month,
                    currentSection = currentSection,
                    label = "Month",
                    onClick = {
                        haptic.addHapticFeedback()
                        onSectionSelected(Section.Month, currentSection)
                    }
                )

                NavigationItem(
                    section = Section.Year,
                    currentSection = currentSection,
                    label = "Year",
                    onClick = {
                        haptic.addHapticFeedback()
                        onSectionSelected(Section.Year, currentSection)
                    }
                )

//            NavigationItem(
//                section = Section.Settings,
//                currentSection = currentSection,
//                label = "Settings",
//                onClick = {
//                    haptic.addHapticFeedback()
//                    onSectionSelected(Section.Settings)
//                }
//            )
            }
        }
    }
}

@Composable
fun NavigationItem(
    section: Section,
    currentSection: Section,
    label: String,
    onClick: () -> Unit
) {
    val isSelected = currentSection == section

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .clickable { onClick() }
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = label,
            style = if (isSelected) DMSans18Bold60 else DMSans18Medium,
            color = if (isSelected) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            },
            textAlign = TextAlign.Center
        )
    }
} 