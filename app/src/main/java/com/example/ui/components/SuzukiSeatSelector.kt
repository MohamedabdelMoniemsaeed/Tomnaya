package com.example.ui.components

import androidx.compose.animation.animateColorAsState
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.EventSeat
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SeatInfo
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate700
import com.example.ui.theme.TomnayaGold
import com.example.ui.theme.TomnayaGoldDark
import com.example.ui.theme.TomnayaNavy
import com.example.ui.theme.TomnayaTeal

@Composable
fun SuzukiSeatSelector(
    seats: List<SeatInfo>,
    onSeatClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("suzuki_seat_selector_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.DirectionsCar,
                        contentDescription = null,
                        tint = TomnayaGoldDark,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "مخطط كراسي السوزوكي (7 راكب)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                val selectedCount = seats.count { it.isSelected }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(TomnayaGold.copy(alpha = 0.2f))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "$selectedCount محجوز",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = TomnayaGoldDark
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Van Cabin Visualization Frame
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp, bottomStart = 16.dp, bottomEnd = 16.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .border(2.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp, bottomStart = 16.dp, bottomEnd = 16.dp))
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Windshield indicator
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.7f)
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(TomnayaTeal.copy(alpha = 0.5f))
                    )
                    Text(
                        text = "الزجاج الأمامي (مقدمة التمناية)",
                        fontSize = 10.sp,
                        color = Slate400,
                        modifier = Modifier.padding(top = 2.dp, bottom = 12.dp)
                    )

                    // Front Row: Driver + Seat 1
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Driver (Permanent)
                        DriverSeatIndicator()

                        // Seat 1 (Front Passenger)
                        val seat1 = seats.find { it.seatNumber == 1 }
                        if (seat1 != null) {
                            SeatItem(seat = seat1, onClick = { onSeatClick(1) })
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Middle Row: Seats 2, 3, 4
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        seats.filter { it.seatNumber in 2..4 }.forEach { seat ->
                            SeatItem(seat = seat, onClick = { onSeatClick(seat.seatNumber) })
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Back Row: Seats 5, 6, 7
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        seats.filter { it.seatNumber in 5..7 }.forEach { seat ->
                            SeatItem(seat = seat, onClick = { onSeatClick(seat.seatNumber) })
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Legend
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                LegendItem(color = MaterialTheme.colorScheme.surface, borderColor = MaterialTheme.colorScheme.outline, label = "متاح")
                LegendItem(color = TomnayaGold, borderColor = TomnayaGoldDark, label = "محدد لك")
                LegendItem(color = Slate700.copy(alpha = 0.4f), borderColor = Color.Transparent, label = "مشغول")
            }
        }
    }
}

@Composable
private fun SeatItem(
    seat: SeatInfo,
    onClick: () -> Unit
) {
    val backgroundColor by animateColorAsState(
        targetValue = when {
            seat.isOccupied -> Slate700.copy(alpha = 0.25f)
            seat.isSelected -> TomnayaGold
            else -> MaterialTheme.colorScheme.surface
        },
        label = "seat_bg"
    )

    val borderColor by animateColorAsState(
        targetValue = when {
            seat.isOccupied -> Color.Transparent
            seat.isSelected -> TomnayaGoldDark
            else -> MaterialTheme.colorScheme.outline
        },
        label = "seat_border"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(enabled = !seat.isOccupied, onClick = onClick)
            .testTag("seat_item_${seat.seatNumber}")
    ) {
        Box(
            modifier = Modifier
                .size(54.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(backgroundColor)
                .border(1.5.dp, borderColor, RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            when {
                seat.isOccupied -> {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "مشغول",
                        tint = Slate400,
                        modifier = Modifier.size(24.dp)
                    )
                }
                seat.isSelected -> {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "تم الاختيار",
                        tint = TomnayaNavy,
                        modifier = Modifier.size(26.dp)
                    )
                }
                else -> {
                    Icon(
                        imageVector = Icons.Default.EventSeat,
                        contentDescription = "متاح",
                        tint = Slate400,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
        Text(
            text = "كرسي ${seat.seatNumber}",
            fontSize = 11.sp,
            fontWeight = if (seat.isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (seat.isSelected) TomnayaGoldDark else MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Composable
private fun DriverSeatIndicator() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(54.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(TomnayaNavy)
                .border(1.5.dp, TomnayaTeal, RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.DirectionsCar,
                contentDescription = "السائق",
                tint = TomnayaGold,
                modifier = Modifier.size(24.dp)
            )
        }
        Text(
            text = "الكابتن",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = TomnayaTeal,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Composable
private fun LegendItem(
    color: Color,
    borderColor: Color,
    label: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(14.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(color)
                .border(1.dp, borderColor, RoundedCornerShape(4.dp))
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(text = label, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
