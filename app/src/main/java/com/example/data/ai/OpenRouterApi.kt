package com.example.data.ai

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import java.util.concurrent.TimeUnit

@Serializable
data class ChatMessage(
    val role: String,
    val content: String
)

@Serializable
data class ReasoningConfig(
    val exclude: Boolean = true
)

@Serializable
data class ChatRequest(
    val model: String,
    val messages: List<ChatMessage>,
    val temperature: Double = 0.5,
    val max_tokens: Int = 300,
    val reasoning: ReasoningConfig = ReasoningConfig()
)

@Serializable
data class ChatChoice(
    val message: ChatMessage
)

@Serializable
data class ChatResponse(
    val choices: List<ChatChoice>? = null
)

interface OpenRouterApiService {
    @POST("chat/completions")
    suspend fun getChatCompletion(
        @Header("Authorization") authHeader: String,
        @Header("HTTP-Referer") referer: String = "https://bizryno.app",
        @Header("X-Title") title: String = "BizRyno Auto Reply",
        @Body request: ChatRequest
    ): ChatResponse
}

object OpenRouterClient {
    private val client by lazy {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(45, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    private val json = Json { ignoreUnknownKeys = true }

    val api: OpenRouterApiService by lazy {
        val contentType = "application/json".toMediaType()
        Retrofit.Builder()
            .baseUrl("https://openrouter.ai/api/v1/")
            .client(client)
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
            .create(OpenRouterApiService::class.java)
    }
}
