package presentation.screens.paywall

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.flow.collectLatest
import org.koin.compose.viewmodel.koinViewModel
import presentation.screens.paywall.PaywallContract.Effect
import presentation.screens.paywall.PaywallContract.Event
import presentation.theme.CustomColor
import presentation.theme.DMSans18Medium
import presentation.theme.iosTitle1

@Composable
fun PaywallScreen(onPurchaseComplete: () -> Unit, onCancel: () -> Unit) {
    val viewModel: PaywallViewModel = koinViewModel()
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest {
            when (it) {
                Effect.OnPurchaseCompleted -> onPurchaseComplete.invoke()
            }
        }
    }

    if (state.error != null) {
//        AppAlertDialog(
//            title = "Something went wrong",
//            onDismissRequest = { viewModel.setEvent(Event.DismissErrorDialog) },
//            message = state.error?.message ?: ""
//        )
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surface,
        bottomBar = {
            Footer(
                isProcessing = state.isProcessing,
                needsReload = state.needsReload,
                offerings = state.offerings,
                showAllPans = state.showAllPlans,
                onActionButtonClicked = {
                    if (state.needsReload || state.offerings.isEmpty()) {
                        viewModel.setEvent(Event.GetOfferings)
                    } else {
                        viewModel.setEvent(Event.OnPurchaseItemClicked(state.offerings.first()))
                    }
                },
                onFooterItemClicked = { viewModel.setEvent(it) })
        }
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp).verticalScroll(
                rememberScrollState()
            ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(56.dp))
            Text(
                text = "How your free \ntrial works",
                modifier = Modifier.padding(horizontal = 32.dp),
                style = iosTitle1.copy(
                    color = MaterialTheme.colorScheme.inversePrimary,
                    fontWeight = FontWeight.SemiBold
                ),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(56.dp))
            SubscriptionsSteps()
            Spacer(modifier = Modifier.height(56.dp))
            HorizontalDivider()
            DismissContainer(onDismiss = onCancel)
            Spacer(modifier = Modifier.height(200.dp))
        }
    }
}