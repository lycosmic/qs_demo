package com.example.data.remote

import com.google.gson.annotations.SerializedName
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

/**
 * 通用的 OpenAI 接口
 */
interface OpenAiApi {
    @POST("chat/completions")
    suspend fun chatCompletion(
        @Header("Authorization") apiKey: String,
        @Body request: ChatCompletionRequest
    ): ChatCompletionResponse
}

/**
 * 聊天请求体
 */
data class ChatCompletionRequest(
    val model: String,
    val messages: List<Message>, // 之前和AI的聊天记录
    val temperature: Double = 0.7,
    @SerializedName("response_format")
    val responseFormat: ResponseFormat = ResponseFormat(type = "json_object") // 强制 JSON
)

data class Message(
    val role: String, // "system" or "user"
    val content: String
)

data class ResponseFormat(
    val type: String
)

data class ChatCompletionResponse(
    val choices: List<Choice>
)

data class Choice(
    val message: Message
)