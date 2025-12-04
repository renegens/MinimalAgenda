package domain.use_case

import data.local.PaywallLocalDataSource
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class SavePaywallTimestampUseCase(private val paywallLocalDataSource: PaywallLocalDataSource) {

    @OptIn(ExperimentalTime::class)
    suspend operator fun invoke() {
        paywallLocalDataSource.save(Clock.System.now().toEpochMilliseconds())
    }
}