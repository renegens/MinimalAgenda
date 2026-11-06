package repository

import domain.model.AppColor
import domain.model.AppTheme
import domain.model.AppThemeMode
import domain.model.Settings
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class SettingsMigrationTest {
    private val expected = Settings(
        appThemeMode = AppThemeMode.DefaultDark,
        appColor = AppColor.Lila,
        privateNotifications = true,
        notificationTimeInMinutes = 45,
    )

    private fun legacyJson(extra: String = "", theme: String = "DefaultDark", color: String = "Lila", minutes: Int = 45) =
        """{"appTheme":"$theme","appColor":"$color","privateNotifications":true,"notificationTimeInMinutes":$minutes$extra}"""

    @Test
    fun removedImmersiveFlagIsIgnoredWithoutResettingPreferences() {
        for (value in listOf(true, false)) {
            assertEquals(expected, SettingsJson.decode(legacyJson(",\"hideBottomNavigation\":$value")))
        }
    }

    @Test
    fun olderFormatWithoutTheRemovedFlagKeepsAllPreferences() {
        assertEquals(expected, SettingsJson.decode(legacyJson()))
        assertEquals(expected.copy(notificationTimeInMinutes = 10_080), SettingsJson.decode(legacyJson(minutes = 10_080)))
    }

    @Test
    fun savingAfterUpgradeDropsTheRemovedFieldAndRoundTripsRemainingValues() {
        val decoded = SettingsJson.decode(legacyJson(",\"hideBottomNavigation\":true"))
        val saved = SettingsJson.encode(decoded)
        assertFalse("hideBottomNavigation" in saved)
        assertEquals(expected, SettingsJson.decode(saved))
    }

    @Test
    fun themeStreamReadsBothLegacyFlagValuesAndTheOlderFormat() = runTest {
        val themes = flowOf(
            legacyJson(",\"hideBottomNavigation\":true"),
            legacyJson(",\"hideBottomNavigation\":false", theme = "DefaultLight", color = "Blue"),
            legacyJson(theme = "FollowSystem", color = "Mint"),
        ).appThemeValues().toList()
        assertEquals(listOf(
            AppTheme(appThemeMode = AppThemeMode.DefaultDark, appColor = AppColor.Lila),
            AppTheme(appThemeMode = AppThemeMode.DefaultLight, appColor = AppColor.Blue),
            AppTheme(appThemeMode = AppThemeMode.FollowSystem, appColor = AppColor.Mint),
        ), themes)
    }

    @Test
    fun emptySettingsStillUseTheAppDefaults() {
        assertEquals(Settings(), SettingsJson.decode(""))
    }
}
