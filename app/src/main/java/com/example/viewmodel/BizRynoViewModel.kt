package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.ai.ChatMessage
import com.example.data.ai.ChatRequest
import com.example.data.ai.OpenRouterClient
import com.example.data.auth.AuthRepository
import com.example.data.pref.UserPreferences
import com.example.data.pref.UserSettings
import com.example.data.room.BizRynoDatabase
import com.example.data.room.ChatEntity
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class BizRynoViewModel(application: Application) : AndroidViewModel(application) {

    private val userPrefs = UserPreferences(application)
    private val authRepo = AuthRepository(application)
    private val db = BizRynoDatabase.getDatabase(application)

    val settings: StateFlow<UserSettings> = userPrefs.preferencesFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UserSettings(
            false, "BOTH", 5, 10, 60, false, "", "google/gemma-4-31b-it:free", "google/gemma-4-26b-a4b-it:free", "", "", ""
        ))

    val chats: StateFlow<List<ChatEntity>> = db.chatDao().getAllChats()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    fun setAiEnabled(enabled: Boolean) = viewModelScope.launch {
        userPrefs.setAiEnabled(enabled)
    }

    fun setWhatsappMode(mode: String) = viewModelScope.launch {
        userPrefs.setWhatsappMode(mode)
    }

    fun setReplyDelay(delay: Int) = viewModelScope.launch {
        userPrefs.setReplyDelay(delay)
    }

    fun setCooldown(cooldown: Int) = viewModelScope.launch {
        userPrefs.setCooldown(cooldown)
    }

    fun setHourlyCap(cap: Int) = viewModelScope.launch {
        userPrefs.setHourlyCap(cap)
    }

    fun setAllowGroups(allow: Boolean) = viewModelScope.launch {
        userPrefs.setAllowGroups(allow)
    }

    fun setOpenRouterApiKey(key: String) = viewModelScope.launch {
        userPrefs.setOpenRouterApiKey(key)
    }

    fun setSelectedModel(model: String) = viewModelScope.launch {
        userPrefs.setSelectedModel(model)
    }

    fun setFallbackModel(model: String) = viewModelScope.launch {
        userPrefs.setFallbackModel(model)
    }

    fun setFallbackMessage(message: String) = viewModelScope.launch {
        userPrefs.setFallbackMessage(message)
    }

    fun setHandoverKeywords(keywords: String) = viewModelScope.launch {
        userPrefs.setHandoverKeywords(keywords)
    }

    fun setBusinessPrompt(prompt: String) = viewModelScope.launch {
        userPrefs.setBusinessPrompt(prompt)
    }

    fun toggleChatPaused(chatId: String, paused: Boolean) = viewModelScope.launch {
        db.chatDao().setChatPaused(chatId, paused)
    }

    fun toggleChatBlocked(chatId: String, blocked: Boolean) = viewModelScope.launch {
        db.chatDao().setChatBlocked(chatId, blocked)
    }

    suspend fun testApiConnection(apiKey: String, model: String): Result<String> {
        return try {
            val authHeader = "Bearer $apiKey"
            val request = ChatRequest(
                model = model,
                messages = listOf(ChatMessage("user", "Say OK")),
                temperature = 0.1,
                max_tokens = 10
            )
            val startTime = System.currentTimeMillis()
            val resp = OpenRouterClient.api.getChatCompletion(authHeader, request = request)
            val latency = System.currentTimeMillis() - startTime
            val content = resp.choices?.firstOrNull()?.message?.content ?: "OK"
            Result.success("Success ($latency ms): $content")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun login(username: String, pass: String, onResult: (Boolean, String?) -> Unit) = viewModelScope.launch {
        val res = authRepo.login(username, pass)
        if (res.isSuccess) {
            _isLoggedIn.value = true
            onResult(true, null)
        } else {
            onResult(false, res.exceptionOrNull()?.localizedMessage ?: "Login failed")
        }
    }

    fun signUp(username: String, mobile: String, pass: String, onResult: (Boolean, String?) -> Unit) = viewModelScope.launch {
        val res = authRepo.signUp(username, mobile, pass)
        if (res.isSuccess) {
            _isLoggedIn.value = true
            onResult(true, null)
        } else {
            onResult(false, res.exceptionOrNull()?.localizedMessage ?: "Sign up failed")
        }
    }

    fun logout() {
        _isLoggedIn.value = false
    }
}
