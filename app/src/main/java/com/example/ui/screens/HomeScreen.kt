package com.example.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.service.WhatsAppNotificationListener
import com.example.ui.theme.*
import com.example.viewmodel.BizRynoViewModel

@Composable
fun HomeScreen(viewModel: BizRynoViewModel, onNavigate: (String) -> Unit) {
    val settings by viewModel.settings.collectAsState()
    val chats by viewModel.chats.collectAsState()

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(BizDarkBackground)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = "BizRyno", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = BizTextPrimary)
                    Text(text = "AI Sales Assistant", fontSize = 12.sp, color = BizTextSecondary)
                }
                IconButton(onClick = { onNavigate("settings") }) {
                    Icon(imageVector = Icons.Default.Settings, contentDescription = "Settings", tint = BizTextPrimary)
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }

        // Giant Power Toggle
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .background(
                        brush = Brush.linearGradient(
                            colors = if (settings.isAiEnabled) listOf(BizPrimary, BizSecondary) else listOf(BizSurfaceCard, BizSurfaceCardHover)
                        ),
                        shape = RoundedCornerShape(24.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(16.dp)
                ) {
                    IconButton(
                        onClick = { viewModel.setAiEnabled(!settings.isAiEnabled) },
                        modifier = Modifier
                            .size(72.dp)
                            .scale(if (settings.isAiEnabled) scale else 1f)
                            .background(BizDarkBackground, CircleShape)
                    ) {
                        Icon(
                            imageVector = if (settings.isAiEnabled) Icons.Default.PowerSettingsNew else Icons.Default.PowerOff,
                            contentDescription = "Toggle AI",
                            tint = if (settings.isAiEnabled) BizSuccess else BizTextSecondary,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = if (settings.isAiEnabled) "AI IS ACTIVE & REPLYING" else "AI IS PAUSED",
                        color = BizTextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }

        // Health Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = BizSurfaceCard)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "System Health", fontWeight = FontWeight.Bold, color = BizTextPrimary, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    HealthRow("Notification Listener", WhatsAppNotificationListener.isListenerConnected)
                    Spacer(modifier = Modifier.height(8.dp))
                    HealthRow("OpenRouter API Configured", settings.openRouterApiKey.isNotBlank())
                    Spacer(modifier = Modifier.height(8.dp))
                    HealthRow("Foreground Service Running", true)
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }

        // Quick Actions
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                QuickActionButton(
                    title = "Business Prompt",
                    icon = Icons.Default.EditNote,
                    modifier = Modifier.weight(1f)
                ) { onNavigate("business_prompt") }

                QuickActionButton(
                    title = "Playground",
                    icon = Icons.Default.SmartToy,
                    modifier = Modifier.weight(1f)
                ) { onNavigate("playground") }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                QuickActionButton(
                    title = "Conversations",
                    icon = Icons.Default.Chat,
                    modifier = Modifier.weight(1f)
                ) { onNavigate("conversations") }

                QuickActionButton(
                    title = "Rules & Limits",
                    icon = Icons.Default.Tune,
                    modifier = Modifier.weight(1f)
                ) { onNavigate("rules") }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }

        // Recent Chats Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Recent Chats (${chats.size})", fontWeight = FontWeight.Bold, color = BizTextPrimary, fontSize = 16.sp)
                TextButton(onClick = { onNavigate("conversations") }) {
                    Text("View All", color = BizSecondary)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        items(chats.take(5)) { chat ->
            Card(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = BizSurfaceCardHover)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = chat.contactName, fontWeight = FontWeight.Bold, color = BizTextPrimary)
                        Text(text = if (chat.paused) "Paused" else "Active", fontSize = 12.sp, color = if (chat.paused) BizError else BizSuccess)
                    }
                    IconButton(onClick = { viewModel.toggleChatPaused(chat.id, !chat.paused) }) {
                        Icon(
                            imageVector = if (chat.paused) Icons.Default.PlayArrow else Icons.Default.Pause,
                            contentDescription = "Toggle Pause",
                            tint = BizTextPrimary
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun HealthRow(label: String, ok: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, color = BizTextSecondary, fontSize = 14.sp)
        Icon(
            imageVector = if (ok) Icons.Default.CheckCircle else Icons.Default.Error,
            contentDescription = null,
            tint = if (ok) BizSuccess else BizError,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
fun QuickActionButton(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = modifier.height(80.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = BizSurfaceCard)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(12.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = BizPrimary, modifier = Modifier.size(28.dp))
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = title, color = BizTextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        }
    }
}
