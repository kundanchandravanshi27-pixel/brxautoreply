package com.example.ui.screens

import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun PermissionWizardScreen(onAllGranted: () -> Unit) {
    val context = LocalContext.current
    var notificationListenerGranted by remember { mutableStateOf(false) }
    var batteryOptGranted by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BizDarkBackground)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier.fillMaxWidth().wrapContentHeight(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = BizSurfaceCard)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.Security,
                    contentDescription = null,
                    tint = BizSecondary,
                    modifier = Modifier.size(54.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Permission Wizard",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = BizTextPrimary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "BizRyno needs notification access to read incoming customer messages and reply automatically.",
                    fontSize = 14.sp,
                    color = BizTextSecondary,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                Spacer(modifier = Modifier.height(24.dp))

                PermissionStepCard(
                    title = "1. WhatsApp Notification Access",
                    description = "Required to read chat messages and send AI replies.",
                    granted = notificationListenerGranted,
                    onGrantClick = {
                        val intent = Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
                        context.startActivity(intent)
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                PermissionStepCard(
                    title = "2. Battery Optimization Exemption",
                    description = "Required to keep AI running 24x7 without closing.",
                    granted = batteryOptGranted,
                    onGrantClick = {
                        val intent = Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS)
                        context.startActivity(intent)
                    }
                )

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = {
                        notificationListenerGranted = true
                        batteryOptGranted = true
                        onAllGranted()
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BizPrimary)
                ) {
                    Text(text = "Continue to App", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun PermissionStepCard(title: String, description: String, granted: Boolean, onGrantClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = BizSurfaceCardHover)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, fontWeight = FontWeight.Bold, color = BizTextPrimary, fontSize = 15.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = description, color = BizTextSecondary, fontSize = 12.sp)
            }
            Spacer(modifier = Modifier.width(12.dp))
            if (granted) {
                Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = BizSuccess)
            } else {
                Button(
                    onClick = onGrantClick,
                    colors = ButtonDefaults.buttonColors(containerColor = BizSecondary),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text("Grant", fontSize = 12.sp, color = BizDarkBackground, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
