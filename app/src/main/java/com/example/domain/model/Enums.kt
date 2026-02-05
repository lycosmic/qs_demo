package com.example.domain.model

/**
 * 角色身份
 */
enum class Role {
    WOLF,       // 狼人
    VILLAGER,   // 平民
    SEER,       // 预言家
    WITCH,      // 女巫
    UNKNOWN     // 当前未知身份
}


/**
 * 游戏阶段
 */
enum class GamePhase {
    WAITING,            // 等待开始
    NIGHT_START,        // 天黑请闭眼
    NIGHT_WOLF,         // 狼人行动（必杀，不可空刀）
    NIGHT_WITCH,        // 女巫行动（解药/毒药）
    NIGHT_SEER,         // 预言家行动（验人）
    DAY_ANNOUNCE,       // 天亮+公布死讯
    DAY_DISCUSSION,     // 白天发言/遗言环节
    DAY_VOTING,         // 公投环节
    GAME_OVER           // 游戏结束（屠城判定）
}

/**
 * 消息类型
 */
enum class MessageType {
    SYSTEM,     // 法官/系统提示
    USER_TEXT,  // 玩家发言
    WOLF_CHANNEL // 狼人夜间交流，当前暂时没有
}

/**
 * 胜负判定
 */
enum class WinResult {
    NONE,           // 游戏继续
    WOLF_WIN,       // 狼人屠城成功
    VILLAGER_WIN    // 狼人被放逐
}