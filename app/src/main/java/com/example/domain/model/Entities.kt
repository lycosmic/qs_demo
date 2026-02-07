package com.example.domain.model


/**
 * 玩家
 */
data class Player(
    val id: String,
    val seatNumber: Int,     // 座位号 1-5
    val role: Role,          // 底牌
    val isAlive: Boolean = true,
    val isMe: Boolean = false // 标记是否是当前用户
)


/**
 * 女巫药水背包
 */
data class WitchInventory(
    val hasAntidote: Boolean = true, // 是否还有解药
    val hasPoison: Boolean = true    // 是否还有毒药
)


/**
 * 夜间发生的临时动作缓存
 */
data class NightCache(
    val wolfKillTargetId: String? = null, // 狼人刀了谁
    val witchSaveTargetId: String? = null, // 女巫救了谁
    val witchPoisonTargetId: String? = null, // 女巫毒了谁
    val seerVerifyTargetId: String? = null // 预言家验了谁
)

/**
 * 预言家验人结果
 */
data class SeerVerificationResult(
    val targetId: String,
    val isGood: Boolean
)

/**
 * 全局游戏状态
 */
data class GameState(
    val phase: GamePhase = GamePhase.WAITING, // 游戏阶段
    val players: List<Player> = emptyList(), // 所有玩家列表
    // 聊天记录
    val chatHistory: List<ChatMessage> = emptyList(),
    // 女巫的药水状态
    val witchInventory: WitchInventory = WitchInventory(),
    val seerResult: SeerVerificationResult? = null,
    val currentSpeakerId: String? = null,
    val isPKPhase: Boolean = false, // 是否是平票PK阶段
    // 0 = 正常投票, 1 = PK 轮投票
    val votingRound: Int = 0,
    // 记录上一轮平票的玩家 ID
    val pkTargetIds: List<String> = emptyList(),
    // --- 夜间临时缓存 (用于结算) ---
    // 必须记录每一晚发生了什么，天亮结算后重置
    val nightCache: NightCache = NightCache(),
    val dayCount: Int = 1, // 第几天
    // 游戏胜负
    val winResult: WinResult = WinResult.PLAYING,
) {
    // 方便获取我的角色
    val myRole: Role
        get() = players.find { it.isMe }?.role ?: Role.VILLAGER

    // 方便获取我的 ID
    val myId: String
        get() = players.find { it.isMe }?.id ?: ""

    // 方便获取我的座位号
    val mySeat: Int
        get() = players.find { it.isMe }?.seatNumber ?: 0
}


/**
 * 聊天消息
 */
data class ChatMessage(
    val id: String,
    val senderId: String, // 发送者ID，如果是系统消息，是 "SYSTEM"
    val senderName: String, // 发送者名称
    val content: String, // 消息内容
    // 可见性名单
    // 空列表 = 所有人可见 (Public)
    // 非空 = 仅列表内的 ID 可见 (Private/Whisper)
    val visibleToIds: List<String> = emptyList()
) {
    // 聊天消息是否可见
    fun isVisibleTo(playerId: String): Boolean {
        return visibleToIds.isEmpty() || visibleToIds.contains(playerId)
    }

    // 是否是系统消息
    fun isSystemMessage(): Boolean {
        return senderId == SYSTEM_MESSAGE_SENDER_ID
    }

    companion object {
        const val SYSTEM_MESSAGE_SENDER_ID = "SYSTEM"
    }
}
