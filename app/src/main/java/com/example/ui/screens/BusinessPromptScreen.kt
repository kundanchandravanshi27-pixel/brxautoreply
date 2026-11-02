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
fun BusinessPromptScreen(viewModel: BizRynoViewModel, onBack: () -> Unit) {
    val settings by viewModel.settings.collectAsState()
    var promptText by remember(settings.businessPrompt) { mutableStateOf(settings.businessPrompt) }
    var fallbackText by remember(settings.fallbackMessage) { mutableStateOf(settings.fallbackMessage) }
    var savedToast by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Business Prompt & AI Training", color = BizTextPrimary) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = BizTextPrimary)
                    }
                },
                actions = {
                    IconButton(onClick = {
                        viewModel.setBusinessPrompt(promptText)
                        viewModel.setFallbackMessage(fallbackText)
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
            Text(
                text = "Teach your AI about your business, products, prices, timing, and rules.",
                fontSize = 13.sp,
                color = BizTextSecondary
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = promptText,
                onValueChange = { promptText = it },
                label = { Text("Business Details & Instructions") },
                modifier = Modifier.fillMaxWidth().height(300.dp),
                shape = RoundedCornerShape(14.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            OutlinedTextField(
                value = fallbackText,
                onValueChange = { fallbackText = it },
                label = { Text("Fallback Message (when AI is uncertain)") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp)
            )

            if (savedToast) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(text = "Saved successfully & synced to cloud!", color = BizSuccess, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    viewModel.setBusinessPrompt(promptText)
                    viewModel.setFallbackMessage(fallbackText)
                    savedToast = true
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BizPrimary)
            ) {
                Text("Save & Sync", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }
}
