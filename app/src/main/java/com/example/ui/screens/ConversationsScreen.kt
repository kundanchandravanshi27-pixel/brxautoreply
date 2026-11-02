package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
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
fun ConversationsScreen(viewModel: BizRynoViewModel, onBack: () -> Unit) {
    val chats by viewModel.chats.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Customer Conversations", color = BizTextPrimary) },
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(chats) { chat ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = BizSurfaceCard)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = chat.contactName, fontWeight = FontWeight.Bold, color = BizTextPrimary, fontSize = 16.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (chat.paused) "AI Paused (Manual Takeover)" else "AI Active",
                                color = if (chat.paused) BizError else BizSuccess,
                                fontSize = 12.sp
                            )
                        }

                        IconButton(onClick = { viewModel.toggleChatPaused(chat.id, !chat.paused) }) {
                            Icon(
                                imageVector = if (chat.paused) Icons.Default.PlayArrow else Icons.Default.Pause,
                                contentDescription = "Pause/Resume",
                                tint = BizSecondary
                            )
                        }

                        IconButton(onClick = { viewModel.toggleChatBlocked(chat.id, !chat.blocked) }) {
                            Icon(imageVector = Icons.Default.Block, contentDescription = "Block", tint = BizError)
                        }
                    }
                }
            }
        }
    }
}
