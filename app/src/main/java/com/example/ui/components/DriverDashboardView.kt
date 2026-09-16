package com.example.ui.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.EventSeat
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.PhoneIphone
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Route
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.ui.theme.Slate400
import com.example.ui.theme.TomnayaGold
import com.example.ui.theme.TomnayaGoldDark
import com.example.ui.theme.TomnayaNavy
import com.example.ui.theme.TomnayaNavySurface
import com.example.ui.theme.TomnayaTeal
import com.example.viewmodel.PassengerRequestForDriver
import com.example.viewmodel.TomnayaUiState

@Composable
fun DriverDashboardView(
    uiState: TomnayaUiState,
    onToggleOnline: () -> Unit,
    onAcceptRequest: (PassengerRequestForDriver) -> Unit,
    onRejectRequest: (String) -> Unit,
    onResetSeats: () -> Unit,
    onUploadLicense: (Uri) -> Unit = {},
    onSimulateLicense: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            onUploadLicense(uri)
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Driver Profile Card with Custom Portrait Photo
        Card(
            modifier = Modifier.fillMaxWidth().testTag("driver_profile_card"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Driver Avatar Image
                Image(
                    painter = painterResource(id = R.drawable.img_driver_avatar),
                    contentDescription = "كابتن التمناية",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .border(2.dp, TomnayaGold, CircleShape)
                )

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "الأسطى المعلم إبراهيم سلامة",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = TomnayaNavy
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.VerifiedUser,
                            contentDescription = "موثق",
                            tint = TomnayaTeal,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = "كابتن تمناية معتمد • خط رمسيس / مدينة نصر",
                        fontSize = 11.sp,
                        color = Slate400
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = TomnayaGoldDark,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "4.9 (540 تقييم ركاب)",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TomnayaNavy
                        )
                    }
                }
            }
        }

        // Firebase Storage: Driver License & Vehicle Docs Verification
        Card(
            modifier = Modifier.fillMaxWidth().testTag("driver_license_storage_card"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (uiState.driverLicenseUploaded) Color(0xFFF0FDF4) else Color(0xFFFFFBEB)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (uiState.driverLicenseUploaded) Icons.Default.CheckCircle else Icons.Default.Badge,
                            contentDescription = null,
                            tint = if (uiState.driverLicenseUploaded) Color(0xFF10B981) else TomnayaGoldDark,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (uiState.driverLicenseUploaded) "رخصة القيادة موثقة سحابياً ☁️" else "توثيق رخصة السائق (Firebase Storage)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = if (uiState.driverLicenseUploaded) Color(0xFF065F46) else TomnayaNavy
                        )
                    }

                    if (uiState.isUploadingLicense) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp,
                            color = TomnayaGoldDark
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // License preview image
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.img_license_sample),
                        contentDescription = "رخصة القيادة المصرية",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .width(84.dp)
                            .height(54.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .border(1.dp, TomnayaGold.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                    )

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (uiState.driverLicenseUploaded) "تم حفظ وتوثيق الرخصة على Cloud Storage بنجاح ✓" else "ارفع صورة رخصة التمناية لتأكيد حسابك لدى إدارة المرور وركاب الخط",
                            fontSize = 11.sp,
                            color = if (uiState.driverLicenseUploaded) Color(0xFF047857) else Slate400,
                            lineHeight = 15.sp
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = { photoPickerLauncher.launch("image/*") },
                                modifier = Modifier.height(34.dp).testTag("upload_license_btn"),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (uiState.driverLicenseUploaded) Color(0xFF10B981) else TomnayaNavy
                                )
                            ) {
                                Icon(Icons.Default.CloudUpload, contentDescription = null, modifier = Modifier.size(15.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (uiState.driverLicenseUploaded) "تحديث الصورة" else "رفع الرخصة",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            if (!uiState.driverLicenseUploaded) {
                                OutlinedButton(
                                    onClick = onSimulateLicense,
                                    modifier = Modifier.height(34.dp),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("معاينة سريعة", fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Online / Offline Status Card
        Card(
            modifier = Modifier.fillMaxWidth().testTag("driver_status_card"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(14.dp)
                            .clip(CircleShape)
                            .background(if (uiState.driverIsOnline) Color(0xFF10B981) else Color(0xFFEF4444))
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = if (uiState.driverIsOnline) "أنت متصل بالخط (جاهز للتحميل)" else "غير متصل (استراحة)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "سوزوكي فان 7 راكب • لوحة ط ر ق ٥٨٢١",
                            fontSize = 11.sp,
                            color = Slate400
                        )
                    }
                }

                Switch(
                    checked = uiState.driverIsOnline,
                    onCheckedChange = { onToggleOnline() },
                    modifier = Modifier.testTag("driver_online_switch"),
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = TomnayaGold,
                        checkedTrackColor = TomnayaNavy
                    )
                )
            }
        }

        // Active Line & 7-Seat Capacity Manager
        Card(
            modifier = Modifier.fillMaxWidth().testTag("driver_capacity_card"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Route,
                            contentDescription = null,
                            tint = TomnayaGoldDark,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "خط السير الحالي",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(TomnayaGold.copy(alpha = 0.2f))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "${String.format("%.0f", uiState.driverLine.defaultSeatFareEgp)} ج.م / كرسي",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = TomnayaGoldDark
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "${uiState.driverLine.fromStation} ⇄ ${uiState.driverLine.toStation}",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Van Seats Capacity Visual
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.EventSeat, contentDescription = null, tint = TomnayaTeal, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "إشغال التمناية: ${uiState.driverOccupiedSeats} من 7 كراسي",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }

                    IconButton(
                        onClick = onResetSeats,
                        modifier = Modifier.size(32.dp).testTag("reset_seats_button")
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = "تفريغ الكراسي", tint = Slate400, modifier = Modifier.size(18.dp))
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                LinearProgressIndicator(
                    progress = { uiState.driverOccupiedSeats / 7f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .clip(RoundedCornerShape(5.dp)),
                    color = if (uiState.driverOccupiedSeats >= 7) Color(0xFFEF4444) else TomnayaGoldDark,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = if (uiState.driverOccupiedSeats >= 7) "التمناية مقفلة عدد - جاهز للانطلاق!" else "متبقي ${7 - uiState.driverOccupiedSeats} كراسي فاضية للتحميل",
                    fontSize = 11.sp,
                    color = Slate400
                )
            }
        }

        // Daily Earnings Stats
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Card(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(text = "دخل اليوم", fontSize = 12.sp, color = Slate400)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${String.format("%.0f", uiState.driverTodayEarnings)} ج.م",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TomnayaTeal
                    )
                }
            }

            Card(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(text = "رحلات اليوم", fontSize = 12.sp, color = Slate400)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${uiState.driverCompletedTripsCount} رحلات",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TomnayaGoldDark
                    )
                }
            }
        }

        // Incoming Passenger Requests (inDrive / Uber style for drivers)
        Card(
            modifier = Modifier.fillMaxWidth().testTag("incoming_requests_card"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "طلبات ركاب على خط سيرك",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(TomnayaTeal.copy(alpha = 0.2f))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "${uiState.driverIncomingRequests.size} طلبات",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = TomnayaTeal
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (uiState.driverIncomingRequests.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "لا توجد طلبات جديدة حالياً، جاري فحص الركاب القريبين...",
                            fontSize = 12.sp,
                            color = Slate400
                        )
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        uiState.driverIncomingRequests.forEach { req ->
                            PassengerRequestItem(
                                request = req,
                                onAccept = { onAcceptRequest(req) },
                                onReject = { onRejectRequest(req.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PassengerRequestItem(
    request: PassengerRequestForDriver,
    onAccept: () -> Unit,
    onReject: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(14.dp))
            .padding(12.dp)
            .testTag("driver_passenger_item_${request.id}")
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = request.passengerName,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )

                Text(
                    text = "${String.format("%.0f", request.offeredPriceEgp)} ج.م",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 16.sp,
                    color = TomnayaGoldDark
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = if (request.isWholeVan) "طلب عربية كاملة مخصوص (7 ركاب)" else "${request.seatsCount} كراسي محجوزة",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = TomnayaTeal
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "${request.pickup} ➔ ${request.dropoff}",
                fontSize = 11.sp,
                color = Slate400
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onAccept,
                    modifier = Modifier.weight(1f).height(38.dp).testTag("accept_passenger_${request.id}"),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = TomnayaTeal, contentColor = Color.White)
                ) {
                    Text(text = "قبول وتحميل", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = onReject,
                    modifier = Modifier.height(38.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(text = "تخطي", fontSize = 12.sp, color = Slate400)
                }
            }
        }
    }
}
