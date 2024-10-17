import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.border
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import com.revenuecat.purchases.kmp.Package
import com.revenuecat.purchases.kmp.PackageType
import extensions.getOfferText
import presentation.theme.iosBody

@Composable
fun SubscriptionOptions(offerings: List<Package>) {
    var selectedOption by remember { mutableStateOf(offerings.firstOrNull()?.identifier ?: "") }

    Column(modifier = Modifier.padding(16.dp)) {
        offerings.forEach { offering ->
            val isSelected = offering.identifier == selectedOption

            // Create the selectable container for each option
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .selectable(
                        selected = isSelected,
                        onClick = { selectedOption = offering.identifier }
                    )
                    .border(
                        if (isSelected) 2.dp else 1.dp,
                        if (isSelected) Color.Green else Color.LightGray,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (isSelected) {
                        Icon(
                            Icons.Default.Check,
                            contentDescription = "Selected",
                            tint = Color.Green,
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        Spacer(modifier = Modifier.size(24.dp)) // Empty space for alignment
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = offering.packageType.name,
                                style = iosBody.copy(fontWeight = FontWeight.SemiBold),
                            )

                            if (offering.packageType == PackageType.ANNUAL) {
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "63% OFF",
                                    color = Color.Green,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = offering.getOfferText(),
                            style = iosBody,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}