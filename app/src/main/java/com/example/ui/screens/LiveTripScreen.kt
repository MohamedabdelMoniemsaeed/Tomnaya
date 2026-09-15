package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TripBookingEntity
import com.example.ui.components.LiveTrackingCard
import com.example.ui.theme.Slate400
import com.example.ui.theme.TomnayaGold
import com.example.ui.theme.TomnayaNavy

@Composable
fun LiveTripScreen(
    activeTrip: TripBookingEntity?,
    onCompleteTrip: (Long) -> Unit,
    onCancelTrip: (Long) -> Unit,
    onRateTrip: (Long, Float) -> Unit,
    onGoToBooking: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (activeTrip == null) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(24.dp)
                .testTag("no_active_trip_state"),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(TomnayaGold.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.DirectionsCar,
                        contentDescription = null,
                        tint = TomnayaGold,
                        modifier = Modifier.size(42.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "لا توجد رحلة نشطة حالياً",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "عندما تحجز كرسياً أو تطلب تمناية مخصوص، ستتمكن من متابعة مسار السوزوكي والكابتن هنا مباشرة على الخريطة الحية!",
                    fontSize = 12.sp,
                    color = Slate400,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = onGoToBooking,
                    modifier = Modifier.testTag("start_booking_from_live_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = TomnayaNavy, contentColor = TomnayaGold)
                ) {
                    Icon(Icons.Default.Navigation, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.size(6.dp))
                    Text("ابدأ حجز تمناية الآن", fontWeight = FontWeight.Bold)
                }
            }
        }
    } else {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            LiveTrackingCard(
                trip = activeTrip,
                onCompleteTrip = { onCompleteTrip(activeTrip.id) },
                onCancelTrip = { onCancelTrip(activeTrip.id) },
                onRateTrip = { rating -> onRateTrip(activeTrip.id, rating) }
            )
        }
    }
}
