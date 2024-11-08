package presentation.screens.paywall

import SubscriptionOptions
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.revenuecat.purchases.kmp.models.Package
import extensions.getOfferContinueButtonText
import extensions.getOfferText
import presentation.components.DotsPulsing
import presentation.screens.paywall.PaywallContract.Event
import presentation.theme.iosBody
import presentation.theme.iosFootnote
import presentation.theme.iosHeadline

@Composable
fun Footer(
    isProcessing: Boolean,
    needsReload: Boolean,
    offerings: List<Package>,
    onActionButtonClicked: () -> Unit,
    onFooterItemClicked: (Event) -> Unit,
    showAllPans: Boolean,
    selectedOffering: Package? = null,
    onOfferingSelected: (Package) -> Unit
) {

    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        if (offerings.isEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
        } else {

            if (showAllPans.not()) {
                Text(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    text = selectedOffering?.getOfferText() ?: "",
                    style = iosBody,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        if (showAllPans && offerings.isNotEmpty()) {
            SubscriptionOptions(
                offerings = offerings,
                onOfferingSelected = onOfferingSelected,
                selectedOffering = selectedOffering
            )
        }

        // Call to Action Button
        Button(
            onClick = onActionButtonClicked,
            enabled = isProcessing.not(),
            modifier = Modifier.fillMaxWidth().height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.inversePrimary)
        ) {

            if (isProcessing) {
                DotsPulsing()
            } else {
                val text =
                    if (needsReload || selectedOffering == null) "Please reload" else selectedOffering.getOfferContinueButtonText()
                Text(
                    text = text,
                    style = iosHeadline,
                    color = MaterialTheme.colorScheme.inverseSurface
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth()
                .padding(top = 8.dp, bottom = 32.dp, start = 16.dp, end = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                modifier = Modifier.clickable { onFooterItemClicked(Event.AllPlans) },
                text = "All Plans",
                style = iosFootnote,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "•", style = iosFootnote, color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                modifier = Modifier.clickable { onFooterItemClicked(Event.RestorePurchases) },
                text = "Restore",
                style = iosFootnote,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "•", style = iosFootnote, color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                modifier = Modifier.clickable { onFooterItemClicked(Event.OpenPrivacy) },
                text = "Privacy",
                style = iosFootnote,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "•", style = iosFootnote, color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                modifier = Modifier.clickable { onFooterItemClicked(Event.OpenTermsOfUse) },
                text = "Terms & Conditions",
                style = iosFootnote,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}