package com.example.domain.model

/**
 * 玩家信息
 */
data class Player(
    val id: String,
    val name: String,
    val seatNumber: Int,    // 座位号，几号玩家
    val role: Role,
    val isAlive: Boolean = true,
    val isMe: Boolean = false // 辅助字段
)

/**
 * 对话消息
 */
data class ChatMessage(
    val id: String,
    val senderId: String,   // 发送者 ID
    val senderName: String,
    val content: String,
    val type: MessageType,
    val timestamp: Long = System.currentTimeMillis()
) {
    companion object {
        // 系统消息的发送者 ID
        const val SYSTEM_SENDER_ID = "SYSTEM"
    }
}

/**
 * 预言家验人结果
 */
data class SeerVerificationResult(
    val targetPlayerId: String,
    val isGood: Boolean // true=好人, false=狼人
)



/**
 * 夜间特殊信息
 */
data class NightActionInfo(
    val wolfKillTargetId: String? = null, // 女巫看到的被刀玩家 ID
    val seerResult: SeerVerificationResult? = null
)

/**
 * 整个游戏状态的快照
 */
data class GameState(
    val roomId: String,
    val myId: String,       // 当前用户的 ID
    val phase: GamePhase,   // 当前游戏阶段
    val players: List<Player>, // 所有玩家列表
    val messages: List<ChatMessage>, // 聊天记录
    val winResult: WinResult = WinResult.NONE, // 是否结束
    // 比如：女巫阶段，后端可能会告诉你“昨晚 X 号被刀了”
    val nightActionInfo: NightActionInfo? = null
)
