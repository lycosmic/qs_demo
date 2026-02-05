package com.example.data.model.mappers

import com.example.data.model.MessageDto
import com.example.domain.model.ChatMessage
import com.example.domain.model.MessageType
import com.example.domain.model.Player

fun MessageDto.toDomain(playerMap: Map<String, Player>): ChatMessage {
    val senderName = playerMap[senderId]?.name ?: "系统"
    val typeEnum = try { MessageType.valueOf(type) } catch (e: Exception) { MessageType.SYSTEM }

    return ChatMessage(
        id = id,
        senderId = senderId,
        senderName = senderName,
        content = content,
        type = typeEnum,
        timestamp = timestamp
    )
}