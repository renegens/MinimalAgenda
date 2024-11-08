package presentation.screens.paywall

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import com.revenuecat.purchases.kmp.models.Package
import kotlinx.coroutines.flow.collectLatest
import org.koin.compose.viewmodel.koinViewModel
import presentation.components.AppAlertDialog
import presentation.screens.paywall.PaywallContract.Effect
import presentation.screens.paywall.PaywallContract.Event
import presentation.theme.CustomColor
import presentation.theme.iosTitle1

@Composable
fun PaywallScreen(onPurchaseComplete: (Package?) -> Unit, onCancel: () -> Unit) {
    val viewModel: PaywallViewModel = koinViewModel()
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest {
            when (it) {
                is Effect.OnPurchaseCompleted -> onPurchaseComplete.invoke(it.purchase)
            }
        }
    }

    if (state.error != null) {
        AppAlertDialog(
            title = "Something went wrong",
            onContinueRequest = { viewModel.setEvent(Event.DismissErrorDialog) },
            message = state.error?.message ?: ""
        )
    }

    Scaffold(containerColor = MaterialTheme.colorScheme.surface, bottomBar = {
        Footer(isProcessing = state.isProcessing,
            needsReload = state.needsReload,
            offerings = state.offerings,
            selectedOffering = state.selectedPackage,
            showAllPans = state.showAllPlans,
            onActionButtonClicked = { viewModel.setEvent(Event.OnProceedButtonClicked) },
            onFooterItemClicked = { viewModel.setEvent(it) },
            onOfferingSelected = { viewModel.setEvent(Event.OnOfferingSelected(it)) }
        )
    }) {
        PaywallContent(state.isProcessing, onCancel = onCancel)
    }
}

@Composable
private fun PaywallContent(isProcessing: Boolean, onCancel: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().background(color = MaterialTheme.colorScheme.surface)
            .padding(
                horizontal = 16.dp
            ).verticalScroll(
                rememberScrollState(),
                enabled = isProcessing.not()
            ), horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(48.dp))
        Text(
            text = "How your free \ntrial works",
            modifier = Modifier.padding(horizontal = 32.dp),
            style = iosTitle1.copy(
                color = MaterialTheme.colorScheme.inversePrimary, fontWeight = FontWeight.SemiBold
            ),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(48.dp))
        SubscriptionsSteps()
        Spacer(modifier = Modifier.height(40.dp))
        HorizontalDivider(color = CustomColor.Gray_200)
        Spacer(modifier = Modifier.height(48.dp))
        DismissContainer(isProcessing = isProcessing, onDismiss = onCancel)
        Spacer(modifier = Modifier.height(200.dp))
    }
}

