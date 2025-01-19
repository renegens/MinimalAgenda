package presentation.screens.onboarding

import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun Onboarding(dismiss: () -> Unit, openSettings: () -> Unit) {
    val viewModel: OnboardingViewModel = koinViewModel()
    val state by viewModel.uiState.collectAsState()

    val pagerState = rememberPagerState(
        pageCount = { Onboarding.entries.size },
        initialPage = Onboarding.StepOne.position,
    )

    val coroutineScope = rememberCoroutineScope()
    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest {
            when (it) {
                OnboardingContract.Effect.CloseOnboarding -> dismiss.invoke()
            }
        }
    }
    HorizontalPager(state = pagerState) { page ->
        when (page) {
            Onboarding.StepOne.position -> StepWelcome {
                coroutineScope.launch {
                    pagerState.animateScrollToPage(Onboarding.StepTwo.position)
                }
            }

            Onboarding.StepTwo.position -> StepHideDemo {
                coroutineScope.launch {
                    pagerState.animateScrollToPage(Onboarding.StepThree.position)
                }
            }

            Onboarding.StepThree.position -> StepCalendarPermission(
                calendarPermissionState = state.calendarPermissionState,
                onContinue = dismiss,
                openSettings = openSettings,
                requestCalendarPermission = { viewModel.setEvent(OnboardingContract.Event.OnRequestCalendarPermission) })
        }
    }
}

enum class Onboarding(val position: Int) {
    StepOne(0), StepTwo(1), StepThree(2);
}