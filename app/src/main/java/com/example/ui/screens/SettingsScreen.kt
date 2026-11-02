package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.viewmodel.BizRynoViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(viewModel: BizRynoViewModel, onLogout: () -> Unit, onBack: () -> Unit) {
    val settings by viewModel.settings.collectAsState()
    val scope = rememberCoroutineScope()
    var apiKey by remember(settings.openRouterApiKey) { mutableStateOf(settings.openRouterApiKey) }
    var selectedModel by remember(settings.selectedModel) { mutableStateOf(settings.selectedModel) }
    var testResult by remember { mutableStateOf<String?>(null) }
    var testing by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings & API", color = BizTextPrimary) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = BizTextPrimary)
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
            Text(text = "OpenRouter AI Configuration", fontWeight = FontWeight.Bold, color = BizTextPrimary, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = apiKey,
                onValueChange = { apiKey = it },
                label = { Text("OpenRouter API Key") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = selectedModel,
                onValueChange = { selectedModel = it },
                label = { Text("AI Model") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = {
                        viewModel.setOpenRouterApiKey(apiKey)
                        viewModel.setSelectedModel(selectedModel)
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BizPrimary)
                ) {
                    Text("Save")
                }

                Button(
                    onClick = {
                        testing = true
                        testResult = null
                        scope.launch {
                            val res = viewModel.testApiConnection(apiKey, selectedModel)
                            testing = false
                            testResult = if (res.isSuccess) res.getOrNull() else "Error: ${res.exceptionOrNull()?.localizedMessage}"
                        }
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BizSurfaceCardHover)
                ) {
                    if (testing) {
                        CircularProgressIndicator(color = BizTextPrimary, modifier = Modifier.size(20.dp))
                    } else {
                        Text("Test Connection", color = BizTextPrimary)
                    }
                }
            }

            if (testResult != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = testResult!!, color = if (testResult!!.startsWith("Success")) BizSuccess else BizError, fontSize = 13.sp)
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(text = "OEM Battery & Auto-Start Help", fontWeight = FontWeight.Bold, color = BizTextPrimary, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = BizSurfaceCard)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "Xiaomi / Redmi / Poco:", fontWeight = FontWeight.Bold, color = BizTextSecondary, fontSize = 13.sp)
                    Text(text = "Settings → Apps → Permissions → Autostart → Enable BizRyno.", color = BizTextPrimary, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Samsung / OnePlus:", fontWeight = FontWeight.Bold, color = BizTextSecondary, fontSize = 13.sp)
                    Text(text = "Battery → Background usage limits → Never sleeping apps → Add BizRyno.", color = BizTextPrimary, fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    viewModel.logout()
                    onLogout()
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BizError)
            ) {
                Text("Logout", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }
}
