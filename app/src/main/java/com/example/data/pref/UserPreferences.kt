package com.example.data.pref

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

val Context.dataStore by preferencesDataStore(name = "bizryno_prefs")

class UserPreferences(private val context: Context) {

    companion object {
        val MASTER_AI_ENABLED = booleanPreferencesKey("master_ai_enabled")
        val SELECTED_WHATSAPP_MODE = stringPreferencesKey("selected_whatsapp_mode") // "BOTH", "WHATSAPP", "BUSINESS"
        val REPLY_DELAY_SECONDS = intPreferencesKey("reply_delay_seconds")
        val COOLDOWN_SECONDS = intPreferencesKey("cooldown_seconds")
        val HOURLY_CAP = intPreferencesKey("hourly_cap")
        val ALLOW_GROUPS = booleanPreferencesKey("allow_groups")
        val OPENROUTER_API_KEY = stringPreferencesKey("openrouter_api_key")
        val SELECTED_MODEL = stringPreferencesKey("selected_model")
        val FALLBACK_MODEL = stringPreferencesKey("fallback_model")
        val FALLBACK_MESSAGE = stringPreferencesKey("fallback_message")
        val HANDOVER_KEYWORDS = stringPreferencesKey("handover_keywords")
        val BUSINESS_PROMPT = stringPreferencesKey("business_prompt")
    }

    val preferencesFlow: Flow<UserSettings> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { prefs ->
            UserSettings(
                isAiEnabled = prefs[MASTER_AI_ENABLED] ?: false,
                whatsappMode = prefs[SELECTED_WHATSAPP_MODE] ?: "BOTH",
                replyDelaySeconds = prefs[REPLY_DELAY_SECONDS] ?: 5,
                cooldownSeconds = prefs[COOLDOWN_SECONDS] ?: 10,
                hourlyCap = prefs[HOURLY_CAP] ?: 60,
                allowGroups = prefs[ALLOW_GROUPS] ?: false,
                openRouterApiKey = prefs[OPENROUTER_API_KEY] ?: "",
                selectedModel = prefs[SELECTED_MODEL] ?: "google/gemma-2-9b-it:free",
                fallbackModel = prefs[FALLBACK_MODEL] ?: "meta-llama/llama-3-8b-instruct:free",
                fallbackMessage = prefs[FALLBACK_MESSAGE] ?: "Thanks for your message! We will get back to you shortly.",
                handoverKeywords = prefs[HANDOVER_KEYWORDS] ?: "human, agent, owner, call me, बात कराओ",
                businessPrompt = prefs[BUSINESS_PROMPT] ?: "You are a helpful sales assistant for our business. Answer customer inquiries politely and professionally."
            )
        }

    suspend fun setAiEnabled(enabled: Boolean) {
        context.dataStore.edit { it[MASTER_AI_ENABLED] = enabled }
    }

    suspend fun setWhatsappMode(mode: String) {
        context.dataStore.edit { it[SELECTED_WHATSAPP_MODE] = mode }
    }

    suspend fun setReplyDelay(delay: Int) {
        context.dataStore.edit { it[REPLY_DELAY_SECONDS] = delay }
    }

    suspend fun setCooldown(cooldown: Int) {
        context.dataStore.edit { it[COOLDOWN_SECONDS] = cooldown }
    }

    suspend fun setHourlyCap(cap: Int) {
        context.dataStore.edit { it[HOURLY_CAP] = cap }
    }

    suspend fun setAllowGroups(allow: Boolean) {
        context.dataStore.edit { it[ALLOW_GROUPS] = allow }
    }

    suspend fun setOpenRouterApiKey(key: String) {
        context.dataStore.edit { it[OPENROUTER_API_KEY] = key }
    }

    suspend fun setSelectedModel(model: String) {
        context.dataStore.edit { it[SELECTED_MODEL] = model }
    }

    suspend fun setFallbackModel(model: String) {
        context.dataStore.edit { it[FALLBACK_MODEL] = model }
    }

    suspend fun setFallbackMessage(message: String) {
        context.dataStore.edit { it[FALLBACK_MESSAGE] = message }
    }

    suspend fun setHandoverKeywords(keywords: String) {
        context.dataStore.edit { it[HANDOVER_KEYWORDS] = keywords }
    }

    suspend fun setBusinessPrompt(prompt: String) {
        context.dataStore.edit { it[BUSINESS_PROMPT] = prompt }
    }
}

data class UserSettings(
    val isAiEnabled: Boolean,
    val whatsappMode: String,
    val replyDelaySeconds: Int,
    val cooldownSeconds: Int,
    val hourlyCap: Int,
    val allowGroups: Boolean,
    val openRouterApiKey: String,
    val selectedModel: String,
    val fallbackModel: String,
    val fallbackMessage: String,
    val handoverKeywords: String,
    val businessPrompt: String
)
