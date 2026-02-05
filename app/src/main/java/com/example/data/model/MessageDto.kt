package com.example.data.model

import com.google.gson.annotations.SerializedName

/**
 * 消息 DTO
 */
data class MessageDto(
    @SerializedName("id") val id: String,
    @SerializedName("sender_id") val senderId: String,
    @SerializedName("content") val content: String,
    @SerializedName("type") val type: String, // "SYSTEM", "TEXT"
    @SerializedName("timestamp") val timestamp: Long
)