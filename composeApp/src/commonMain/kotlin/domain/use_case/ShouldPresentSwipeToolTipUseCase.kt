package domain.use_case

import data.local.LaunchCounterDataSource

class ShouldPresentSwipeToolTipUseCase(private val launchCounterDataSource: LaunchCounterDataSource) {

    private val MAX_SHOW = 1

    suspend operator fun invoke(): Boolean {
        val count = launchCounterDataSource.getCount()
        return count == MAX_SHOW
    }
}