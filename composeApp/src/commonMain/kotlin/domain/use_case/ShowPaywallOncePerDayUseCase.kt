package domain.use_case

import data.local.PaywallLocalDataSource
import extensions.isPremium
import kotlin.time.Clock
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime

class ShowPaywallOncePerDayUseCase(
    private val paywallLocalDataSource: PaywallLocalDataSource,
    private val savePaywallTimestampUseCase: SavePaywallTimestampUseCase
) {

    suspend fun shouldPresentPaywall(): Boolean {
        if (isPremium()) return false

        val timestamp = paywallLocalDataSource.getTimestamp()

        if (timestamp == null) {
            // First time launch, save the current timestamp
            savePaywallTimestampUseCase()
            return false
        } else {
            val hoursPassed = hoursSinceTimestamp(timestamp)

            if (hoursPassed >= 24) {
                savePaywallTimestampUseCase()
                return true
            } else {
                return false
            }
        }
    }

    @OptIn(ExperimentalTime::class)
    private fun hoursSinceTimestamp(timestamp: Long): Long {
        val currentTime = Clock.System.now().toEpochMilliseconds()
        val timeDifference = (currentTime - timestamp).milliseconds
        return timeDifference.toLong(DurationUnit.HOURS)
    }
}