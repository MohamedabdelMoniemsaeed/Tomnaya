package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.speech.tts.TextToSpeech
import android.widget.Toast
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.AlertDialog
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
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import java.util.Locale
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

    var showPaymentDialog by remember { mutableStateOf(false) }
    var ttsInstance by remember { mutableStateOf<TextToSpeech?>(null) }

    DisposableEffect(context) {
        val tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                try {
                    ttsInstance?.language = Locale("ar")
                } catch (e: Exception) {
                    // ignore fallback
                }
            }
        }
        ttsInstance = tts
        onDispose {
            tts.stop()
            tts.shutdown()
        }
    }

    fun playArrivalVoiceAlert() {
        val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator?.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            vibrator?.vibrate(500)
        }

        val speechText = "انتبه! سيارة السوزوكي التمناية اقتربت من نقطة ركوبك، لوحة أرقام ${trip.carPlate}. برجاء الاستعداد للركوب."
        ttsInstance?.speak(speechText, TextToSpeech.QUEUE_FLUSH, null, "TomnayaArrivalAudio")
        Toast.makeText(context, "📢 تم تشغيل التنبيه الصوتي لاقتراب التمناية!", Toast.LENGTH_LONG).show()
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

            Spacer(modifier = Modifier.height(8.dp))

            // Direct Real-time Google Maps Navigation Option
            OutlinedButton(
                onClick = {
                    val gmmIntentUri = Uri.parse(
                        "https://www.google.com/maps/dir/?api=1&origin=" +
                                Uri.encode(trip.pickupLocation) +
                                "&destination=" +
                                Uri.encode(trip.dropoffLocation) +
                                "&travelmode=driving"
                    )
                    val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri).apply {
                        setPackage("com.google.android.apps.maps")
                    }
                    try {
                        context.startActivity(mapIntent)
                    } catch (e: Exception) {
                        context.startActivity(Intent(Intent.ACTION_VIEW, gmmIntentUri))
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("open_live_google_maps_button"),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.NearMe,
                    contentDescription = null,
                    tint = TomnayaTeal,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "فتح الملاحة الحية على خرائط Google 🗺️",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = TomnayaNavy
                )
            }

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
                    androidx.compose.foundation.Image(
                        painter = androidx.compose.ui.res.painterResource(id = com.example.R.drawable.img_driver_avatar),
                        contentDescription = trip.driverName,
                        contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .border(2.dp, TomnayaGold, CircleShape)
                    )

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

            Spacer(modifier = Modifier.height(10.dp))

            // Quick Safety & Audio Alert Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Audio Arrival Alert Button
                Button(
                    onClick = { playArrivalVoiceAlert() },
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .testTag("voice_alert_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = TomnayaNavy,
                        contentColor = TomnayaGold
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.VolumeUp,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = TomnayaGold
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "تنبيه صوتي 📢",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // WhatsApp Safety Share Button
                Button(
                    onClick = {
                        val shareMessage = """
🛡️ تفاصيل رحلتي على تطبيق تمناية (Tomnaya):
📍 الركوب: ${trip.pickupLocation}
🏁 الوصول: ${trip.dropoffLocation}
🚐 كابتن السوزوكي: ${trip.driverName}
📞 تليفون: ${trip.driverPhone}
🔢 لوحة العربية: ${trip.carPlate} (${trip.carColor})
💰 الأجرة: ${String.format("%.0f", trip.farePriceEgp)} ج.م
🔒 أنا في طريقي الآن بأمان مع تمناية.
                        """.trimIndent()

                        val sendIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_TEXT, shareMessage)
                            `package` = "com.whatsapp"
                        }
                        try {
                            context.startActivity(sendIntent)
                        } catch (e: Exception) {
                            val genericIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_TEXT, shareMessage)
                            }
                            context.startActivity(Intent.createChooser(genericIntent, "مشاركة تفاصيل المشوار لأهلك 🛡️"))
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .testTag("whatsapp_safety_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF25D366),
                        contentColor = Color.White
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "أمان واتساب 🛡️",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Vodafone Cash & InstaPay Payment Button
            OutlinedButton(
                onClick = { showPaymentDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .testTag("open_vodafone_instapay_button"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = TomnayaNavy
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Payment,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = Color(0xFFE60000)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "📲 الدفع السريع بـ (فودافون كاش و InstaPay)",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = TomnayaNavy
                )
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

            if (showPaymentDialog) {
                VodafoneInstaPayPaymentDialog(
                    trip = trip,
                    onDismiss = { showPaymentDialog = false }
                )
            }
        }
    }
}

@Composable
fun VodafoneInstaPayPaymentDialog(
    trip: TripBookingEntity,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val amountStr = String.format("%.0f", trip.farePriceEgp)
    var selectedMethod by remember { mutableStateOf("VODAFONE") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.AccountBalanceWallet,
                    contentDescription = null,
                    tint = if (selectedMethod == "VODAFONE") Color(0xFFE60000) else Color(0xFF6B21A8),
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "دفع الأجرة إلكترونياً",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                // Method Switch Tabs
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFFF1F5F9))
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (selectedMethod == "VODAFONE") Color(0xFFE60000) else Color.Transparent)
                            .clickable { selectedMethod = "VODAFONE" }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "فودافون كاش 🔴",
                            color = if (selectedMethod == "VODAFONE") Color.White else Color(0xFF334155),
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (selectedMethod == "INSTAPAY") Color(0xFF6B21A8) else Color.Transparent)
                            .clickable { selectedMethod = "INSTAPAY" }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "InstaPay 🟣",
                            color = if (selectedMethod == "INSTAPAY") Color.White else Color(0xFF334155),
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Fare Amount Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "المبلغ المطلوب تحويله:", fontSize = 13.sp)
                        Text(
                            text = "$amountStr ج.م",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = TomnayaGoldDark
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Driver Payment Target
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Text(
                        text = if (selectedMethod == "VODAFONE") "رقم محفظة كابتن السوزوكي:" else "معرف InstaPay للكابتن:",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (selectedMethod == "VODAFONE") trip.driverPhone else "${trip.driverPhone}@instapay",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = TomnayaNavy
                        )

                        IconButton(
                            onClick = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("Payment Number", trip.driverPhone)
                                clipboard.setPrimaryClip(clip)
                                Toast.makeText(context, "تم نسخ الرقم بنجاح!", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = "نسخ الرقم",
                                tint = TomnayaTeal,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Direct Action Buttons
                if (selectedMethod == "VODAFONE") {
                    Button(
                        onClick = {
                            val ussd = "*9*7*${trip.driverPhone}*${trip.farePriceEgp.toInt()}%23"
                            val callIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$ussd"))
                            try {
                                context.startActivity(callIntent)
                            } catch (e: Exception) {
                                val fallback = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${trip.driverPhone}"))
                                context.startActivity(fallback)
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE60000)),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("تحويل سريع عبر فودافون كاش (*9*7#)", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                } else {
                    Button(
                        onClick = {
                            val launchIntent = context.packageManager.getLaunchIntentForPackage("com.emeint.android.mysrv")
                            if (launchIntent != null) {
                                context.startActivity(launchIntent)
                            } else {
                                val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.emeint.android.mysrv"))
                                context.startActivity(webIntent)
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6B21A8)),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("فتح تطبيق InstaPay للتحويل", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = TomnayaNavy)
            ) {
                Text("تم التحويل للكابتن ✓")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("إغلاق", color = Color.Gray)
            }
        }
    )
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
