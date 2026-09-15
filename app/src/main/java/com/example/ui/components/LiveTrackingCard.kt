package com.example.ui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TripBookingEntity
import com.example.data.model.TripStatus
import com.example.ui.theme.Slate400
import com.example.ui.theme.TomnayaGold
import com.example.ui.theme.TomnayaGoldDark
import com.example.ui.theme.TomnayaNavy
import com.example.ui.theme.TomnayaTeal

@Composable
fun LiveTrackingCard(
    trip: TripBookingEntity,
    onCompleteTrip: () -> Unit,
    onCancelTrip: () -> Unit,
    onRateTrip: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val statusEnum = try {
        TripStatus.valueOf(trip.status)
    } catch (e: Exception) {
        TripStatus.ACCEPTED
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("live_tracking_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Live Status Banner
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .clip(CircleShape)
                            .background(
                                when (statusEnum) {
                                    TripStatus.COMPLETED -> Color(0xFF10B981)
                                    TripStatus.CANCELLED -> Color(0xFFEF4444)
                                    else -> TomnayaGoldDark
                                }
                            )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = statusEnum.arabicName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(TomnayaGold.copy(alpha = 0.2f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "${String.format("%.0f", trip.farePriceEgp)} ج.م",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 14.sp,
                        color = TomnayaGoldDark
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Interactive Live Map Canvas Simulation
            TomnayaMapCanvas(status = statusEnum)

            Spacer(modifier = Modifier.height(14.dp))

            // Route details
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = TomnayaTeal,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "الركوب: ${trip.pickupLocation}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = Color(0xFFEF4444),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "النزول: ${trip.dropoffLocation}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "نوع الرحلة: ${trip.selectedSeatsDesc}",
                    fontSize = 12.sp,
                    color = Slate400,
                    modifier = Modifier.padding(start = 26.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Driver Information Card
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(TomnayaNavy)
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(TomnayaGold),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.DirectionsCar,
                            contentDescription = null,
                            tint = TomnayaNavy,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = trip.driverName,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = Color.White
                        )
                        Text(
                            text = "سوزوكي ${trip.carColor} • لوحة ${trip.carPlate}",
                            fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = TomnayaGold,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "4.9 تقييم ممتاز",
                                fontSize = 11.sp,
                                color = TomnayaGold
                            )
                        }
                    }
                }

                // Phone Call Driver Button
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(TomnayaTeal)
                        .testTag("call_driver_button"),
                    contentAlignment = Alignment.Center
                ) {
                    IconButton(onClick = {
                        val intent = Intent(Intent.ACTION_DIAL).apply {
                            data = Uri.parse("tel:${trip.driverPhone}")
                        }
                        context.startActivity(intent)
                    }) {
                        Icon(
                            imageVector = Icons.Default.Call,
                            contentDescription = "اتصال بالسائق",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Action Buttons (Complete, Safety, Cancel)
            if (statusEnum != TripStatus.COMPLETED && statusEnum != TripStatus.CANCELLED) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = onCompleteTrip,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("complete_trip_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = TomnayaTeal,
                            contentColor = Color.White
                        )
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "وصلت بالسلامة", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = onCancelTrip,
                        modifier = Modifier
                            .height(48.dp)
                            .testTag("cancel_active_trip_button"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(text = "إلغاء", fontSize = 13.sp, color = Color(0xFFEF4444))
                    }
                }
            } else if (statusEnum == TripStatus.COMPLETED) {
                // Trip rating prompt
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(TomnayaGold.copy(alpha = 0.1f))
                        .padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "حمد الله على السلامة! قيّم كابتن السوزوكي:",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        (1..5).forEach { starIndex ->
                            IconButton(
                                onClick = { onRateTrip(starIndex.toFloat()) },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = "$starIndex نجوم",
                                    tint = if (starIndex <= trip.rating) TomnayaGold else Slate400,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TomnayaMapCanvas(
    status: TripStatus,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "car_movement")
    val carProgress by infiniteTransition.animateFloat(
        initialValue = 0.1f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 6000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "car_progress"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(130.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF0F2432))
            .border(1.dp, Color(0xFF1E3A52), RoundedCornerShape(16.dp))
    ) {
        Canvas(modifier = Modifier.fillMaxWidth().height(130.dp)) {
            val width = size.width
            val height = size.height

            // Simulated city street grid
            val gridColor = Color(0x22FFFFFF)
            drawLine(gridColor, Offset(0f, height * 0.3f), Offset(width, height * 0.3f), strokeWidth = 2f)
            drawLine(gridColor, Offset(0f, height * 0.7f), Offset(width, height * 0.7f), strokeWidth = 2f)
            drawLine(gridColor, Offset(width * 0.25f, 0f), Offset(width * 0.25f, height), strokeWidth = 2f)
            drawLine(gridColor, Offset(width * 0.75f, 0f), Offset(width * 0.75f, height), strokeWidth = 2f)

            // Dynamic Road Polyline
            val path = Path().apply {
                moveTo(width * 0.15f, height * 0.75f)
                cubicTo(
                    width * 0.35f, height * 0.25f,
                    width * 0.65f, height * 0.85f,
                    width * 0.85f, height * 0.25f
                )
            }

            // Road Glow and Path
            drawPath(
                path = path,
                color = Color(0xFFF59E0B).copy(alpha = 0.3f),
                style = Stroke(width = 14f, cap = StrokeCap.Round)
            )
            drawPath(
                path = path,
                color = Color(0xFFF59E0B),
                style = Stroke(width = 5f, cap = StrokeCap.Round)
            )

            // Start Pickup Pin
            drawCircle(
                color = Color(0xFF0D9488),
                radius = 12f,
                center = Offset(width * 0.15f, height * 0.75f)
            )
            drawCircle(
                color = Color.White,
                radius = 5f,
                center = Offset(width * 0.15f, height * 0.75f)
            )

            // Dropoff Destination Pin
            drawCircle(
                color = Color(0xFFEF4444),
                radius = 12f,
                center = Offset(width * 0.85f, height * 0.25f)
            )
            drawCircle(
                color = Color.White,
                radius = 5f,
                center = Offset(width * 0.85f, height * 0.25f)
            )

            // Animated Suzuki Minivan Icon along the curve
            val currentT = if (status == TripStatus.COMPLETED) 0.85f else carProgress
            val carX = width * (0.15f + currentT * 0.7f)
            val carY = height * (0.75f - currentT * 0.5f + (Math.sin(currentT * Math.PI * 2) * 0.2f).toFloat())

            // Glowing halo around car
            drawCircle(
                color = Color(0xFFF59E0B).copy(alpha = 0.4f),
                radius = 18f,
                center = Offset(carX, carY)
            )
            drawCircle(
                color = Color.White,
                radius = 10f,
                center = Offset(carX, carY)
            )
            drawCircle(
                color = Color(0xFF0F172A),
                radius = 6f,
                center = Offset(carX, carY)
            )
        }

        // Live badge overlay
        Box(
            modifier = Modifier
                .padding(10.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color.Black.copy(alpha = 0.65f))
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.DirectionsCar,
                    contentDescription = null,
                    tint = TomnayaGold,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "تتبع مسار التمناية المباشر",
                    fontSize = 11.sp,
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
