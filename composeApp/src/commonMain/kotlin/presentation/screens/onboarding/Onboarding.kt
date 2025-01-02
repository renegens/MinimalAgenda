package presentation.screens.onboarding

import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch

@Composable
fun Onboarding(dismiss: () -> Unit) {
    val pagerState = rememberPagerState(
        pageCount = { Onboarding.entries.size },
        initialPage = Onboarding.StepOne.position,
    )

    val coroutineScope = rememberCoroutineScope()
    HorizontalPager(state = pagerState) { page ->
        when (page) {
            Onboarding.StepOne.position -> StepOne {
                coroutineScope.launch {
                    pagerState.animateScrollToPage(Onboarding.StepTwo.position)
                }
            }

            Onboarding.StepTwo.position -> StepTwo {
                coroutineScope.launch {
                    pagerState.animateScrollToPage(Onboarding.StepThree.position)
                }
            }

            Onboarding.StepThree.position -> StepThree(dismiss)
        }
    }
}

enum class Onboarding(val position: Int) {
    StepOne(0), StepTwo(1), StepThree(2);
}