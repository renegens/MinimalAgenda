package domain.use_case

import data.local.SwipeToolTipDataSource

class DidSeeSwipeTooltipUseCase(private val swipeToolTipDataSource: SwipeToolTipDataSource) {
    suspend operator fun invoke(): Boolean {
        val didSee = swipeToolTipDataSource.get()
        if (didSee.not()){
            swipeToolTipDataSource.markSeen()
        }
        return didSee
    }
}