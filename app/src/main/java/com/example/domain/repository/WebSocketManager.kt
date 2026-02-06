package com.example.domain.repository

import kotlinx.coroutines.flow.Flow

/**
 * WebSocket 管理器接口定义
 */
interface WebSocketManager {
    // 来自服务器的原始 JSON 消息流
    val incomingMessages: Flow<String>

    // 连接错误或断开连接的流
    val errors: Flow<Throwable>

    suspend fun connect(roomId: String)
    suspend fun disconnect()
}