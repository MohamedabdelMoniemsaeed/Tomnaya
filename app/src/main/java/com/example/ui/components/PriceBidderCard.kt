package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ElectricRickshaw
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.RideType
import com.example.ui.theme.Slate400
import com.example.ui.theme.TomnayaGold
import com.example.ui.theme.TomnayaGoldDark
import com.example.ui.theme.TomnayaNavy
import com.example.ui.theme.TomnayaTeal

@Composable
fun PriceBidderCard(
    rideType: RideType,
    seatsCount: Int,
    baseFare: Double,
    offeredFare: Double,
    isSearching: Boolean,
    onAdjustFare: (Double) -> Unit,
    onSubmitRequest: () -> Unit,
    onCancelSearch: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("price_bidder_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Title and Fare Mode
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocalOffer,
                        contentDescription = null,
                        tint = TomnayaGoldDark,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "الأجرة المقترحة (نظام إندرايف)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(TomnayaTeal.copy(alpha = 0.15f))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = if (rideType == RideType.SEAT) "$seatsCount كراسي" else "عربية كاملة",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TomnayaTeal
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Main Price Display with Minus / Plus
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(vertical = 12.dp, horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                FilledIconButton(
                    onClick = { onAdjustFare(-5.0) },
                    modifier = Modifier
                        .size(48.dp)
                        .testTag("decrease_fare_button"),
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.onSurface
                    )
                ) {
                    Icon(Icons.Default.Remove, contentDescription = "تقليل الأجرة")
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = String.format("%.0f", offeredFare),
                            fontSize = 34.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = TomnayaGoldDark
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "ج.م",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(bottom = 6.dp)
                        )
                    }
                    Text(
                        text = "السعر المقترح للكابتن",
                        fontSize = 11.sp,
                        color = Slate400
                    )
                }

                FilledIconButton(
                    onClick = { onAdjustFare(+5.0) },
                    modifier = Modifier
                        .size(48.dp)
                        .testTag("increase_fare_button"),
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = TomnayaGold,
                        contentColor = TomnayaNavy
                    )
                ) {
                    Icon(Icons.Default.Add, contentDescription = "زيادة الأجرة")
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Quick increment chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                listOf(5.0, 10.0, 20.0).forEach { delta ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(20.dp))
                            .clickable { onAdjustFare(delta) }
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "+$delta ج.م",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Action Button
            if (isSearching) {
                Button(
                    onClick = onCancelSearch,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("cancel_search_button"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFEF4444)
                    )
                ) {
                    Text(
                        text = "إلغاء البحث عن تمناية",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            } else {
                Button(
                    onClick = onSubmitRequest,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .testTag("request_tomnaya_button"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = TomnayaGold,
                        contentColor = TomnayaNavy
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Navigation,
                        contentDescription = null,
                        tint = TomnayaNavy,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (rideType == RideType.SEAT) "طلب كرسي في التمناية" else "طلب تمناية مخصوص",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
        }
    }
}
