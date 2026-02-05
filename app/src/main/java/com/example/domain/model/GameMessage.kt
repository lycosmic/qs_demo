package com.example.domain.model

/**
 * 游戏发言
 */
data class GameMessage(
    val id: String,
    val playerId: String,
    val playerName: String,      // 显示名称
    val roleName: String,        // 角色显示名称
    val content: String,
    val roundNum: Int,
    val timestamp: Long          // 时间戳
)