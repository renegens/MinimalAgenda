package presentation.screens.paywall

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.flow.collectLatest
import org.koin.compose.viewmodel.koinViewModel
import presentation.components.AppAlertDialog
import presentation.components.DotsPulsing
import presentation.components.clickableWithOpaqueText
import presentation.screens.paywall.PaywallContract.Effect
import presentation.screens.paywall.PaywallContract.Event
import presentation.theme.DMSans18Medium
import presentation.theme.DMSans24Black100
import presentation.theme.DMSans24Medium120
import presentation.theme.DMSans24Medium60
import presentation.theme.DMSansBlack250

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
            // Header
            Text(
                text = "Cancel",
                style = DMSans24Medium60,
                fontSize = 16.sp,
                textAlign = TextAlign.Start,
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.clickableWithOpaqueText {
                    if (state.isProcessing) {
                        return@clickableWithOpaqueText
                    } else {
                        onCancel.invoke()
                    }
                }.align(Alignment.Start)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Unlock Papely Premium",
                style = DMSansBlack250,
                fontSize = 30.sp,
                letterSpacing = (-1).sp,
                color = MaterialTheme.colorScheme.tertiary,
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Benefits
            Column(
                modifier = Modifier.padding(start = 16.dp),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                BenefitItem("See only important events")
                BenefitItem("Set alert time")
                BenefitItem("Add and Edit Events")
                BenefitItem("Pick your favorite theme color")
                BenefitItem("Private Notifications")
                BenefitItem("Support Papely Development.")
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Start,
                    text = "Papely is made by one person with no investors or corporate owner.",
                    style = DMSans24Medium60,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.secondary
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Free Trial Promotion
            Box(
                modifier = Modifier.fillMaxWidth().height(60.dp).background(
                    MaterialTheme.colorScheme.secondaryContainer, RoundedCornerShape(4.dp)
                ).padding(16.dp), contentAlignment = Alignment.Center
            ) {
                if (state.isProcessing) {
                    DotsPulsing()
                } else {

                    if (state.needsReload) {
                        Text(
                            modifier = Modifier.clickable { viewModel.setEvent(Event.GetOfferings) },
                            text = "Please reload",
                            style = DMSansBlack250,
                            letterSpacing = (0).sp,
                            fontSize = 18.sp,
                            color = MaterialTheme.colorScheme.tertiary
                        )
                    } else {
                        Text(
                            text = "Try Papely free for 1 week!",
                            style = DMSansBlack250,
                            letterSpacing = (0).sp,
                            fontSize = 18.sp,
                            color = MaterialTheme.colorScheme.tertiary
                        )
                    }
                }
            }
            Text(
                text = "No commitment.\nCancel anytime before your trial ends in the AppStore.",
                style = DMSans24Medium60,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.secondary
            )
            Spacer(modifier = Modifier.height(24.dp))

            // Subscription Details
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                if (state.offerings.isEmpty()) {
                    Spacer(modifier = Modifier.height(40.dp))
                } else {
                    val price = state.offerings.first().storeProduct.price

                    //todo price per month
                    val pricePerMonth =
                        ((price.formatted.filter { it.isDigit() }.toDouble() / 100) / 12)

                    Text(
                        text = "${price.formatted}/year after your free trial",
                        style = DMSans18Medium,
                        fontSize = 20.sp,
                        color = MaterialTheme.colorScheme.tertiary

                    )
//                    Text(
//                        text = "That’s less than ${price.currencyCode}${pricePerMonth}/month!",
//                        style = DMSans18Medium,
//                        fontSize = 16.sp,
//                        color = MaterialTheme.colorScheme.secondary
//                    )
                }
            }
            Spacer(modifier = Modifier.height(32.dp))

            // Call to Action Button

            Button(
                onClick = {
                    viewModel.setEvent(Event.OnPurchaseItemClicked(state.offerings.first()))
                },
                enabled = state.offerings.isNotEmpty() && state.isProcessing.not() && state.needsReload.not(),
                modifier = Modifier.fillMaxWidth().height(56.dp),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 10.dp,
                ),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.inversePrimary)
            ) {
                Text(
                    text = "Start Your Free Trial",
                    style = DMSans24Black100,
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.inverseSurface
                )
            }
            Text(
                modifier = Modifier.clickable { viewModel.setEvent(Event.RestorePurchases) },
                text = "Restore Purchases",
                style = DMSans24Medium60,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.secondary
            )
            Spacer(modifier = Modifier.height(24.dp))

            // Terms and Conditions
            Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Start,
                    text = "Subscription unlock for one year renews automatically unless canceled. By subscribing you are agreeing to our EULA",
                    style = DMSans24Medium60,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.secondary
                )

                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        modifier = Modifier.clickable { viewModel.setEvent(Event.OpenPrivacy) },
                        text = "Privacy Policy",
                        style = DMSans24Medium60,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Text(
                        modifier = Modifier.clickable { viewModel.setEvent(Event.OpenTermsOfUse) },
                        text = "Terms of Use",
                        style = DMSans24Medium60,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun BenefitItem(text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "•",
            style = DMSans24Medium120,
            fontSize = 20.sp,
            letterSpacing = 0.sp,
            color = MaterialTheme.colorScheme.tertiary
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            style = DMSans24Medium120,
            fontSize = 20.sp,
            letterSpacing = 0.sp,
            color = MaterialTheme.colorScheme.tertiary
        )
    }
}