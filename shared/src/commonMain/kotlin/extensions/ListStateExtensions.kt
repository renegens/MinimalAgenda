package extensions

import androidx.compose.foundation.lazy.LazyListState

suspend fun LazyListState.scrollToNowIfNeeded(todayIndex: Int, shouldAnimate: Boolean = false) {
    if (firstVisibleItemIndex != todayIndex || firstVisibleItemScrollOffset > 0) {
        if (shouldAnimate) {
            animateScrollToItem(todayIndex)
        } else {
            scrollToItem(todayIndex)
        }
    }
}