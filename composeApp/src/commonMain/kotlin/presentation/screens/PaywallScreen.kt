package presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.revenuecat.purchases.kmp.Package
import com.revenuecat.purchases.kmp.Purchases
import com.revenuecat.purchases.kmp.availablePackages
import com.revenuecat.purchases.kmp.current
import com.revenuecat.purchases.kmp.entitlements
import com.revenuecat.purchases.kmp.get
import com.revenuecat.purchases.kmp.isActive
import presentation.theme.CustomColor
import presentation.theme.DMSans18Medium
import presentation.theme.DMSans24Black100
import presentation.theme.DMSans24Medium120
import presentation.theme.DMSans24Medium60
import presentation.theme.DMSansBlack250

@Composable
fun PaywallScreen(
    onPurchaseComplete: () -> Unit,
    onError: (Throwable) -> Unit
) {
    var offeringState by remember { mutableStateOf<List<Package>>(emptyList()) }

    LaunchedEffect(Unit) {
        Purchases.sharedInstance.getOfferings(
            onError = { error ->
                // An error occurred
            },
            onSuccess = { offerings ->
                offerings.current?.availablePackages?.takeUnless { it.isEmpty() }?.let {
                    offeringState = it
                }
            })
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Spacer(modifier = Modifier.height(32.dp))
        // Header
        Text(
            text = "Unlock Papely",
            style = DMSansBlack250,
            fontSize = 30.sp,
            letterSpacing = (-1).sp,
            color = MaterialTheme.colorScheme.tertiary
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Benefits
        Column(
            modifier = Modifier.padding(start = 16.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            BenefitItem("See only important events")
            BenefitItem("Clean, eye-friendly interface")
            BenefitItem("Event notification without hassle")
            BenefitItem("Integrated with your calendars")
            BenefitItem("Clutter-free daily view")
            BenefitItem("Less stress and fewer notifications")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Free Trial Promotion
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .background(MaterialTheme.colorScheme.secondaryContainer, RoundedCornerShape(4.dp))
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Try Papely free for 2 weeks!",
                style = DMSansBlack250,
                letterSpacing = (0).sp,
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.tertiary
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Subscription Details
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "$7.99/year after your free trial",
                style = DMSans18Medium,
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.tertiary

            )
            Text(
                text = "That’s less than $0.67/month!",
                style = DMSans18Medium,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.secondary
            )
        }
        Spacer(modifier = Modifier.height(32.dp))

        // Call to Action Button

        val colors = if (isSystemInDarkTheme()){
            CustomColor.White_500 to CustomColor.Black_900
        }else{
            CustomColor.Black_900 to CustomColor.White_500
        }

        Button(
            onClick = {  },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 10.dp,
            ),
            colors = ButtonDefaults.buttonColors(containerColor = colors.first)
        ) {
            Text(
                text = "Start Your Free Trial",
                style = DMSans24Black100,
                fontSize = 20.sp,
                color = colors.second
            )
        }
        Spacer(modifier = Modifier.height(24.dp))

        // Terms and Conditions
        Column {
            Text(
                text = "No commitment. Cancel anytime before your trial ends.",
                style = DMSans24Medium60,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.secondary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Subscription renews automatically unless canceled.",
                style = DMSans24Medium60,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.secondary
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
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

fun handlePurchase(
    packageItem: Package,
    onPurchaseComplete: () -> Unit,
    onError: (Throwable) -> Unit
) {

    Purchases.sharedInstance.purchase(
        packageToPurchase = packageItem,
        onError = { error, userCancelled ->
            // No purchase
        },
        onSuccess = { storeTransaction, customerInfo ->
            if (customerInfo.entitlements["my_entitlement_identifier"]?.isActive == true) {
                // Unlock that great "pro" content
                onPurchaseComplete.invoke()
            }
        }
    )
}


