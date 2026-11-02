package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ai.ChatMessage
import com.example.data.ai.ChatRequest
import com.example.data.ai.OpenRouterClient
import com.example.ui.theme.*
import com.example.viewmodel.BizRynoViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaygroundScreen(viewModel: BizRynoViewModel, onBack: () -> Unit) {
    val settings by viewModel.settings.collectAsState()
    val scope = rememberCoroutineScope()
    var inputMessage by remember { mutableStateOf("") }
    val chatHistory = remember { mutableStateListOf(ChatMessage("assistant", "Hello! I am your AI assistant. How can I help your customer today? Test me below.")) }
    var loading by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("AI Playground Simulator", color = BizTextPrimary) },
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
                .padding(16.dp)
        ) {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(chatHistory) { msg ->
                    val isUser = msg.role == "user"
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
                    ) {
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = if (isUser) BizPrimary else BizSurfaceCardHover,
                            modifier = Modifier.widthIn(max = 280.dp)
                        ) {
                            Text(
                                text = msg.content,
                                modifier = Modifier.padding(12.dp),
                                color = BizTextPrimary,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = inputMessage,
                    onValueChange = { inputMessage = it },
                    placeholder = { Text("Type customer message...") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = {
                        if (inputMessage.isNotBlank() && !loading) {
                            val userMsg = inputMessage
                            inputMessage = ""
                            chatHistory.add(ChatMessage("user", userMsg))
                            loading = true

                            scope.launch {
                                val authHeader = "Bearer ${settings.openRouterApiKey}"
                                val fullPrompt = "You are a helpful sales assistant.\nBUSINESS DETAILS:\n${settings.businessPrompt}"
                                val messages = listOf(ChatMessage("system", fullPrompt)) + chatHistory.map { ChatMessage(it.role, it.content) }
                                
                                // Exactly 2 models: Primary and Fallback
                                val modelsToTry = listOf(
                                    settings.selectedModel,
                                    settings.fallbackModel
                                )

                                var reply: String? = null

                                for (modelName in modelsToTry) {
                                    try {
                                        val req = ChatRequest(
                                            model = modelName,
                                            messages = messages,
                                            temperature = 0.5
                                        )
                                        val resp = OpenRouterClient.api.getChatCompletion(authHeader, request = req)
                                        reply = resp.choices?.firstOrNull()?.message?.content?.trim()
                                        if (!reply.isNullOrEmpty()) break
                                    } catch (_: Exception) {
                                        // Try fallback model
                                    }
                                }

                                val finalReply = if (!reply.isNullOrEmpty()) reply!! else settings.fallbackMessage
                                chatHistory.add(ChatMessage("assistant", finalReply))
                                loading = false
                            }
                        }
                    },
                    modifier = Modifier
                        .size(50.dp)
                        .background(BizPrimary, RoundedCornerShape(14.dp))
                ) {
                    if (loading) {
                        CircularProgressIndicator(color = BizTextPrimary, modifier = Modifier.size(24.dp))
                    } else {
                        Icon(imageVector = Icons.Default.Send, contentDescription = "Send", tint = BizTextPrimary)
                    }
                }
            }
        }
    }
}
