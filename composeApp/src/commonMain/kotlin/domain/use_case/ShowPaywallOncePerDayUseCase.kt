package domain.use_case

import data.local.PaywallLocalDataSource
import extensions.isPremium
import kotlinx.datetime.Clock
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.DurationUnit

class ShowPaywallOncePerDayUseCase(private val paywallLocalDataSource: PaywallLocalDataSource) {

    suspend fun shouldPresentPaywall(): Boolean {
        if (isPremium()) return false

        val timestamp = paywallLocalDataSource.getTimestamp()

        if (timestamp == null) {
            // First time launch, save the current timestamp
            saveCurrentTimestamp()
            return false
        } else {
            val hoursPassed = hoursSinceTimestamp(timestamp)

            if (hoursPassed >= 24) {
                saveCurrentTimestamp()
                return true
            } else {
                return false
            }
        }
    }

    private suspend fun saveCurrentTimestamp() {
        paywallLocalDataSource.save(Clock.System.now().toEpochMilliseconds())
    }

    private fun hoursSinceTimestamp(timestamp: Long): Long {
        val currentTime = Clock.System.now().toEpochMilliseconds()
        val timeDifference = (currentTime - timestamp).milliseconds
        return timeDifference.toLong(DurationUnit.HOURS)
    }
}