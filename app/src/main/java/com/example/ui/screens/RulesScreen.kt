package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.viewmodel.BizRynoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RulesScreen(viewModel: BizRynoViewModel, onBack: () -> Unit) {
    val settings by viewModel.settings.collectAsState()
    var delaySec by remember(settings.replyDelaySeconds) { mutableStateOf(settings.replyDelaySeconds.toString()) }
    var cooldownSec by remember(settings.cooldownSeconds) { mutableStateOf(settings.cooldownSeconds.toString()) }
    var hourlyCap by remember(settings.hourlyCap) { mutableStateOf(settings.hourlyCap.toString()) }
    var allowGroups by remember(settings.allowGroups) { mutableStateOf(settings.allowGroups) }
    var whatsappMode by remember(settings.whatsappMode) { mutableStateOf(settings.whatsappMode) }
    var handoverKeywords by remember(settings.handoverKeywords) { mutableStateOf(settings.handoverKeywords) }
    var savedToast by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Auto-Reply Rules & Limits", color = BizTextPrimary) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = BizTextPrimary)
                    }
                },
                actions = {
                    IconButton(onClick = {
                        viewModel.setReplyDelay(delaySec.toIntOrNull() ?: 5)
                        viewModel.setCooldown(cooldownSec.toIntOrNull() ?: 10)
                        viewModel.setHourlyCap(hourlyCap.toIntOrNull() ?: 60)
                        viewModel.setAllowGroups(allowGroups)
                        viewModel.setWhatsappMode(whatsappMode)
                        viewModel.setHandoverKeywords(handoverKeywords)
                        savedToast = true
                    }) {
                        Icon(imageVector = Icons.Default.Save, contentDescription = "Save", tint = BizSecondary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BizDarkBackground)
            )
        },
        containerColor = BizDarkBackground
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Text(text = "WhatsApp App Selection", fontWeight = FontWeight.Bold, color = BizTextPrimary)
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = whatsappMode == "BOTH",
                    onClick = { whatsappMode = "BOTH" },
                    label = { Text("Both") }
                )
                FilterChip(
                    selected = whatsappMode == "WHATSAPP",
                    onClick = { whatsappMode = "WHATSAPP" },
                    label = { Text("WhatsApp") }
                )
                FilterChip(
                    selected = whatsappMode == "BUSINESS",
                    onClick = { whatsappMode = "BUSINESS" },
                    label = { Text("WA Business") }
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            OutlinedTextField(
                value = delaySec,
                onValueChange = { delaySec = it },
                label = { Text("Reply Delay (seconds)") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = cooldownSec,
                onValueChange = { cooldownSec = it },
                label = { Text("Per-Contact Cooldown (seconds)") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = hourlyCap,
                onValueChange = { hourlyCap = it },
                label = { Text("Hourly Reply Cap") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = handoverKeywords,
                onValueChange = { handoverKeywords = it },
                label = { Text("Handover Keywords (comma separated)") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Allow Group Chats", color = BizTextPrimary)
                Switch(checked = allowGroups, onCheckedChange = { allowGroups = it })
            }

            if (savedToast) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(text = "Rules saved successfully!", color = BizSuccess, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    viewModel.setReplyDelay(delaySec.toIntOrNull() ?: 5)
                    viewModel.setCooldown(cooldownSec.toIntOrNull() ?: 10)
                    viewModel.setHourlyCap(hourlyCap.toIntOrNull() ?: 60)
                    viewModel.setAllowGroups(allowGroups)
                    viewModel.setWhatsappMode(whatsappMode)
                    viewModel.setHandoverKeywords(handoverKeywords)
                    savedToast = true
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BizPrimary)
            ) {
                Text("Save Rules", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }
}
