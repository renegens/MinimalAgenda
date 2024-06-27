import androidx.compose.runtime.*
import org.jetbrains.compose.ui.tooling.preview.Preview
import presentation.components.day.DayScreen

import presentation.components.months.MonthsScreen
import presentation.components.years.YearsScreen
import presentation.theme.AppTheme

@Composable
@Preview
fun App() {
    AppTheme {
        //YearsScreen()
        //MonthsScreen()
        DayScreen()
    }
}