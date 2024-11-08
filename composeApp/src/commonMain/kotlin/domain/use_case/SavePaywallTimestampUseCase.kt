package domain.use_case

import data.local.PaywallLocalDataSource
import kotlinx.datetime.Clock

class SavePaywallTimestampUseCase(private val paywallLocalDataSource: PaywallLocalDataSource) {

    suspend operator fun invoke() {
        paywallLocalDataSource.save(Clock.System.now().toEpochMilliseconds())
    }
}