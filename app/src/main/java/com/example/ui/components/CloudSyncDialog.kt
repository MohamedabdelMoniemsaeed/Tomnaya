package com.example.ui.components

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudQueue
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.cloud.CloudSyncStatus
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate600
import com.example.ui.theme.TomnayaGold
import com.example.ui.theme.TomnayaGoldDark
import com.example.ui.theme.TomnayaNavy
import com.example.ui.theme.TomnayaNavySurface
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.filled.PhoneIphone
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField

@Composable
fun CloudSyncDialog(
    status: CloudSyncStatus,
    onDismiss: () -> Unit,
    onSyncNow: () -> Unit,
    onPhoneAuth: (String) -> Unit = {}
) {
    var phoneNumberInput by remember(status.currentUserEmail) {
        mutableStateOf(status.currentUserEmail ?: "01012345678")
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.testTag("cloud_sync_dialog"),
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(if (status.isConnected) EmeraldGreen.copy(alpha = 0.2f) else TomnayaGold.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (status.isConnected) Icons.Default.CloudDone else Icons.Default.CloudQueue,
                        contentDescription = "Cloud",
                        tint = if (status.isConnected) EmeraldGreen else TomnayaGoldDark,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Text(
                    text = "سحابة تمناية • Firebase ☁️",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = TomnayaNavy
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Status Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (status.isConnected) EmeraldGreen.copy(alpha = 0.1f) else Slate100
                    )
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(if (status.isConnected) EmeraldGreen else TomnayaGoldDark)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = status.statusMessage,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = TomnayaNavy
                            )
                        }

                        if (status.lastSyncTime != null) {
                            Spacer(modifier = Modifier.height(4.dp))
                            val sdf = SimpleDateFormat("yyyy/MM/dd - hh:mm a", Locale("ar"))
                            val dateStr = sdf.format(Date(status.lastSyncTime))
                            Text(
                                text = "آخر مزامنة ناجحة: $dateStr",
                                fontSize = 11.sp,
                                color = Slate600
                            )
                        }
                    }
                }

                // Phone Authentication Section
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.PhoneIphone,
                                contentDescription = null,
                                tint = TomnayaNavy,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "تسجيل الدخول بالهاتف (Firebase Auth)",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = TomnayaNavy
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = phoneNumberInput,
                                onValueChange = { phoneNumberInput = it },
                                modifier = Modifier.weight(1f),
                                placeholder = {
                                    Text(text = "01xxxxxxxxx", fontSize = 11.sp)
                                },
                                singleLine = true,
                                shape = RoundedCornerShape(8.dp)
                            )

                            Button(
                                onClick = { onPhoneAuth(phoneNumberInput) },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = TomnayaNavy)
                            ) {
                                Text("دخول", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        if (status.currentUserEmail != null) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "الحساب الحالي: ${status.currentUserEmail}",
                                fontSize = 11.sp,
                                color = EmeraldGreen,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                // Security & Persistence Info
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Slate100)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.Top) {
                            Icon(
                                imageVector = Icons.Default.Security,
                                contentDescription = null,
                                tint = TomnayaGoldDark,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "حماية البيانات سحابياً: يتم حفظ كافة الرحلات، تفاصيل الكباتن، وحجوزات الكراسي على خوادم Google Firebase السحابية لضمان عدم ضياعها مطلقاً.",
                                fontSize = 12.sp,
                                color = Slate600,
                                lineHeight = 18.sp
                            )
                        }

                        Row(verticalAlignment = Alignment.Top) {
                            Icon(
                                imageVector = Icons.Default.Sync,
                                contentDescription = null,
                                tint = TomnayaNavy,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "تزامن لحظي ومحلي (Hybrid): التطبيق يعمل حتى لو انقطع الإنترنت، ويقوم برفع وحفظ البيانات في السحابة بمجرد توفر الاتصال.",
                                fontSize = 12.sp,
                                color = Slate600,
                                lineHeight = 18.sp
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSyncNow()
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = TomnayaNavy,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(Icons.Default.CloudSync, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("مزامنة السحابة الآن", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("إغلاق", color = Slate600)
            }
        }
    )
}
