package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.EventSeat
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.DriverOffer
import com.example.data.model.RideType
import com.example.ui.components.DriverOffersList
import com.example.ui.components.PriceBidderCard
import com.example.ui.components.SuzukiSeatSelector
import com.example.ui.theme.Slate400
import com.example.ui.theme.TomnayaGold
import com.example.ui.theme.TomnayaGoldDark
import com.example.ui.theme.TomnayaNavy
import com.example.ui.theme.TomnayaTeal
import com.example.viewmodel.TomnayaUiState

@Composable
fun BookingScreen(
    uiState: TomnayaUiState,
    onSeatClick: (Int) -> Unit,
    onRideTypeChange: (RideType) -> Unit,
    onLocationsChange: (String, String) -> Unit,
    onAdjustFare: (Double) -> Unit,
    onRequestTomnaya: () -> Unit,
    onCancelSearch: () -> Unit,
    onAcceptOffer: (DriverOffer) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    var pickupText by remember(uiState.pickupLocation) { mutableStateOf(uiState.pickupLocation) }
    var dropoffText by remember(uiState.dropoffLocation) { mutableStateOf(uiState.dropoffLocation) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Visual Banner (Suzuki Minivan in Cairo)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(145.dp)
                .testTag("hero_banner_card"),
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                Image(
                    painter = painterResource(id = R.drawable.img_tomnaya_hero),
                    contentDescription = "سوزوكي تمناية 7 راكب",
                    modifier = Modifier.fillMaxWidth(),
                    contentScale = ContentScale.Crop
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(145.dp)
                        .background(
                            androidx.compose.ui.graphics.Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color(0xCC0A192F))
                            )
                        )
                )
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(14.dp)
                ) {
                    Text(
                        text = "احجز تمنايتك بالسعر اللي يناسبك",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TomnayaGold
                    )
                    Text(
                        text = "كرسي في خط السير أو عربية كاملة 7 ركاب مخصوص",
                        fontSize = 11.sp,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }
            }
        }

        // Ride Type Tabs: Seat vs Whole Van
        Card(
            modifier = Modifier.fillMaxWidth().testTag("ride_type_card"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Shared Seat Button
                val isSeat = uiState.rideType == RideType.SEAT
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSeat) TomnayaGold else MaterialTheme.colorScheme.surfaceVariant)
                        .clickable { onRideTypeChange(RideType.SEAT) }
                        .padding(vertical = 12.dp)
                        .testTag("tab_seat_ride"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.EventSeat,
                            contentDescription = null,
                            tint = if (isSeat) TomnayaNavy else Slate400,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "حجز كرسي",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = if (isSeat) TomnayaNavy else MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "مشاركة بالخط",
                                fontSize = 10.sp,
                                color = if (isSeat) TomnayaNavy.copy(alpha = 0.8f) else Slate400
                            )
                        }
                    }
                }

                // Whole Van Button
                val isWhole = uiState.rideType == RideType.WHOLE_VAN
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isWhole) TomnayaGold else MaterialTheme.colorScheme.surfaceVariant)
                        .clickable { onRideTypeChange(RideType.WHOLE_VAN) }
                        .padding(vertical = 12.dp)
                        .testTag("tab_whole_van"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.DirectionsCar,
                            contentDescription = null,
                            tint = if (isWhole) TomnayaNavy else Slate400,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "مشوار مخصوص",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = if (isWhole) TomnayaNavy else MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "عربية كاملة (7 ركاب)",
                                fontSize = 10.sp,
                                color = if (isWhole) TomnayaNavy.copy(alpha = 0.8f) else Slate400
                            )
                        }
                    }
                }
            }
        }

        // Pickup & Dropoff Inputs
        Card(
            modifier = Modifier.fillMaxWidth().testTag("location_inputs_card"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Pickup
                OutlinedTextField(
                    value = pickupText,
                    onValueChange = {
                        pickupText = it
                        onLocationsChange(it, dropoffText)
                    },
                    label = { Text("مكان الركوب / الموقف") },
                    leadingIcon = {
                        Icon(Icons.Default.LocationOn, contentDescription = null, tint = TomnayaTeal)
                    },
                    modifier = Modifier.fillMaxWidth().testTag("pickup_input"),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                // Swap Button
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    IconButton(
                        onClick = {
                            val temp = pickupText
                            pickupText = dropoffText
                            dropoffText = temp
                            onLocationsChange(pickupText, dropoffText)
                        },
                        modifier = Modifier.size(36.dp).testTag("swap_locations_button")
                    ) {
                        Icon(Icons.Default.SwapVert, contentDescription = "تبديل المحطات", tint = TomnayaGoldDark)
                    }
                }

                // Dropoff
                OutlinedTextField(
                    value = dropoffText,
                    onValueChange = {
                        dropoffText = it
                        onLocationsChange(pickupText, it)
                    },
                    label = { Text("الوجهة / المحطة الأخيرة") },
                    leadingIcon = {
                        Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color(0xFFEF4444))
                    },
                    modifier = Modifier.fillMaxWidth().testTag("dropoff_input"),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )
            }
        }

        // 7-Passenger Suzuki Seat Selector (Visible when SEAT mode is chosen)
        if (uiState.rideType == RideType.SEAT) {
            SuzukiSeatSelector(
                seats = uiState.seats,
                onSeatClick = onSeatClick
            )
        }

        // InDrive Style Price Bidder Card
        val selectedCount = if (uiState.rideType == RideType.WHOLE_VAN) 7 else maxOf(1, uiState.seats.count { it.isSelected })
        PriceBidderCard(
            rideType = uiState.rideType,
            seatsCount = selectedCount,
            baseFare = if (uiState.rideType == RideType.WHOLE_VAN) uiState.baseFareWholeVan else uiState.baseFarePerSeat,
            offeredFare = uiState.userOfferedFare,
            isSearching = uiState.isSearchingDrivers,
            onAdjustFare = onAdjustFare,
            onSubmitRequest = onRequestTomnaya,
            onCancelSearch = onCancelSearch
        )

        // Incoming Driver Offers (Shown when searching or offers arrive)
        if (uiState.isSearchingDrivers || uiState.driverOffers.isNotEmpty()) {
            DriverOffersList(
                isSearching = uiState.isSearchingDrivers,
                offers = uiState.driverOffers,
                onAcceptOffer = onAcceptOffer
            )
        }
    }
}
