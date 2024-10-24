import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.revenuecat.purchases.kmp.models.Package
import com.revenuecat.purchases.kmp.models.PackageType
import extensions.getOfferText
import presentation.theme.CustomColor
import presentation.theme.iosBody
import presentation.theme.iosFootnote

@Composable
fun SubscriptionOptions(
    offerings: List<Package>,
    selectedOffering: Package?,
    onOfferingSelected: (Package) -> Unit
) {
    var selectedOption by remember { mutableStateOf(selectedOffering?.identifier ?: "") }

    Column(modifier = Modifier.padding(vertical = 8.dp)) {
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
                        onClick = {
                            selectedOption = offering.identifier
                            onOfferingSelected.invoke(offerings.first { it.identifier == offering.identifier })
                        }
                    )
                    .border(
                        if (isSelected) 2.dp else 1.dp,
                        if (isSelected) MaterialTheme.colorScheme.inversePrimary else CustomColor.Gray_200,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (isSelected) {
                        Box(
                            modifier = Modifier
                                .size(24.dp) // Size of the circle
                                .background(
                                    color = MaterialTheme.colorScheme.inversePrimary,
                                    shape = CircleShape
                                )
                        ) {
                            Icon(
                                Icons.Default.Check,
                                contentDescription = "Selected",
                                tint = MaterialTheme.colorScheme.surface,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                    } else {
                        Box(
                            modifier = Modifier
                                .size(24.dp) // Size of the circle
                                .background(
                                    color = CustomColor.Gray_200,
                                    shape = CircleShape
                                )
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = offering.packageType.name,
                                style = iosBody.copy(fontWeight = FontWeight.SemiBold),
                                color = MaterialTheme.colorScheme.onSurface,
                            )

                            if (offering.packageType == PackageType.ANNUAL) {
                                Text(
                                    text = "85% OFF",
                                    color = MaterialTheme.colorScheme.inverseSurface,
                                    style = iosFootnote,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .wrapContentWidth()
                                        .wrapContentHeight()
                                        .background(color = MaterialTheme.colorScheme.inversePrimary)
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