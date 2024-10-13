package presentation.screens.paywall

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.flow.collectLatest
import org.koin.compose.viewmodel.koinViewModel
import presentation.components.AppAlertDialog
import presentation.components.DotsPulsing
import presentation.screens.paywall.PaywallContract.Effect
import presentation.screens.paywall.PaywallContract.Event
import presentation.theme.CustomColor
import presentation.theme.DMSans18Medium
import presentation.theme.DMSans24Black100
import presentation.theme.iosBody
import presentation.theme.iosFootnote
import presentation.theme.iosTitle1
import presentation.theme.iosTitle3

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
        AppAlertDialog(
            title = "Something went wrong",
            onDismissRequest = { viewModel.setEvent(Event.DismissErrorDialog) },
            message = state.error?.message ?: ""
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "How your free \ntrial works",
                modifier = Modifier.padding(horizontal = 32.dp),
                style = iosTitle1.copy(
                    color = MaterialTheme.colorScheme.inversePrimary,
                    fontWeight = FontWeight.SemiBold
                ),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Benefits
            Column(
                modifier = Modifier.padding(start = 16.dp),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                BenefitItem(
                    title = "Today",
                    description = "Get instant access to all features and get back control of your time",
                    icon = Icons.Default.Lock,
                    iconColor = MaterialTheme.colorScheme.inversePrimary,
                )
                BenefitItem(
                    title = "Day 5",
                    description = "You will get a notification that your trial is ending",
                    icon = Icons.Default.Notifications,
                    iconColor = MaterialTheme.colorScheme.inversePrimary
                )
                BenefitItem(
                    title = "Day 7",
                    description = "Your subscription starts, cancel anytime before",
                    icon = Icons.Default.CheckCircle,
                    iconColor = CustomColor.Mint_500
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Subscription Details
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (state.offerings.isEmpty()) {
                    Spacer(modifier = Modifier.height(40.dp))
                } else {
                    val price = state.offerings.first().storeProduct.price
                    Text(
                        text = "First week free, then ${price.formatted}/yr",
                        style = DMSans18Medium,
                        fontSize = 20.sp,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                }
            }
            Spacer(modifier = Modifier.height(32.dp))

            // Call to Action Button
            Button(
                onClick = {
                    if (state.needsReload || state.offerings.isEmpty()) {
                        viewModel.setEvent(Event.GetOfferings)
                    } else {
                        viewModel.setEvent(Event.OnPurchaseItemClicked(state.offerings.first()))
                    }
                },
                enabled = state.isProcessing.not(),
                modifier = Modifier.fillMaxWidth().height(56.dp),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 10.dp,
                ),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.inversePrimary)
            ) {

                if (state.isProcessing) {
                    DotsPulsing()
                } else {
                    val text =
                        if (state.needsReload || state.offerings.isEmpty()) "Please reload" else "Start Your Free Trial"
                    Text(
                        text = text,
                        style = DMSans24Black100,
                        fontSize = 20.sp,
                        color = MaterialTheme.colorScheme.inverseSurface
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            // Terms and Conditions
            Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Start,
                    text = "Subscription unlock for one year renews automatically unless canceled. By subscribing you are agreeing to our EULA",
                    style = iosFootnote,
                    color = MaterialTheme.colorScheme.secondary
                )

                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        modifier = Modifier.clickable { viewModel.setEvent(Event.AllPlans) },
                        text = "All Plans",
                        style = iosFootnote,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Text(
                        text = "•",
                        style = iosFootnote,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Text(
                        modifier = Modifier.clickable { viewModel.setEvent(Event.RestorePurchases) },
                        text = "Restore",
                        style = iosFootnote,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Text(
                        text = "•",
                        style = iosFootnote,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Text(
                        modifier = Modifier.clickable { viewModel.setEvent(Event.OpenPrivacy) },
                        text = "Privacy",
                        style = iosFootnote,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Text(
                        text = "•",
                        style = iosFootnote,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Text(
                        modifier = Modifier.clickable { viewModel.setEvent(Event.OpenTermsOfUse) },
                        text = "Terms & Conditions",
                        style = iosFootnote,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun BenefitItem(
    title: String,
    description: String,
    icon: ImageVector,
    iconColor: Color = MaterialTheme.colorScheme.secondary
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconColor,
            modifier = Modifier.size(48.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(
                text = title,
                style = iosTitle3.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.inversePrimary,
            )
            Text(
                text = description,
                style = iosBody,
                color = MaterialTheme.colorScheme.inversePrimary
            )
        }
    }
}