package presentation.screens.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import domain.model.AppThemeMode
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel
import presentation.components.PaperBackground
import presentation.components.noRippleClickable
import presentation.theme.DMSans24Medium60
import com.revenuecat.purchases.kmp.models.CustomerInfo
import com.revenuecat.purchases.kmp.models.StoreTransaction
import com.revenuecat.purchases.kmp.ui.revenuecatui.Paywall
import com.revenuecat.purchases.kmp.ui.revenuecatui.PaywallListener
import com.revenuecat.purchases.kmp.ui.revenuecatui.PaywallOptions
import org.jetbrains.compose.ui.tooling.preview.Preview

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
                OnboardingContract.Effect.ShowPaywall -> coroutineScope.launch {
                    pagerState.animateScrollToPage(Onboarding.StepPaywall.position)
                }
            }
        }
    }
    PaperBackground(appThemeMode = AppThemeMode.FollowSystem) {

        Column(modifier = Modifier.fillMaxSize().statusBarsPadding()) {
            if (pagerState.currentPage != Onboarding.StepPaywall.position) {
                Row(
                    modifier = Modifier.wrapContentHeight().fillMaxWidth().padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    repeat(pagerState.pageCount) { iteration ->
                        val color =
                            if (pagerState.currentPage == iteration) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.secondary
                        Box(
                            modifier = Modifier.padding(2.dp).clip(CircleShape).background(color)
                                .size(12.dp)
                        )
                    }
                    Spacer(modifier = Modifier.weight(1F))
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
                        onContinue = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(Onboarding.StepPaywall.position)
                            }
                        },
                        openSettings = openSettings,
                        requestCalendarPermission = { viewModel.setEvent(OnboardingContract.Event.OnRequestCalendarPermission) })

                    Onboarding.StepPaywall.position -> {
                        val listener = object : PaywallListener {
                            override fun onPurchaseCompleted(
                                customerInfo: CustomerInfo, storeTransaction: StoreTransaction
                            ) {
                                viewModel.setEvent(OnboardingContract.Event.OnDismissOnboarding)
                            }

                            override fun onPurchaseCancelled() {
                                viewModel.setEvent(OnboardingContract.Event.OnDismissOnboarding)
                            }
                        }

                        val options = PaywallOptions(
                            dismissRequest = {
                                viewModel.setEvent(OnboardingContract.Event.OnDismissOnboarding)
                            }) {
                            this.listener = listener
                            shouldDisplayDismissButton = true
                        }

                       Paywall(options = options)
                    }
                }
            }
        }
    }
}

enum class Onboarding(val position: Int) {
    StepOne(0), StepTwo(1), StepThree(2), StepPaywall(3);
}

@Preview
@Composable
fun OnboardingPreview() {
    Onboarding(dismiss = {}, openSettings = {})
}