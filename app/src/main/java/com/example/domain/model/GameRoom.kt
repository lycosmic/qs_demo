package com.example.domain.model

/**
 * 游戏房间
 */
data class GameRoom(
    val id: String,
    val totalPlayers: Int,
    val status: RoomStatus,
    val speakTime: Int,           // 发言时长（秒）
    val currentRound: Int = 1,    // 当前轮次
    val players: List<Player> = emptyList()
)
