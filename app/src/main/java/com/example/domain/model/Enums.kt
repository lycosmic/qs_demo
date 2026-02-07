package com.example.domain.model

/**
 * 角色 (1狼 2民 1预 1巫)
 */
enum class Role(val roleName: String) {
    WOLF("狼人"),       // 狼人
    VILLAGER("平民"),   // 平民
    SEER("预言家"),       // 预言家
    WITCH("女巫"),      // 女巫
}



/**
 * 阵营
 */
enum class Camp {
    GOOD, // 好人
    WOLF  // 狼人
}


/**
 * 游戏阶段
 */
enum class GamePhase {
    WAITING,            // 等待开始
    NIGHT_START,        // 入夜动画
    NIGHT_WOLF,         // 狼人行动
    NIGHT_WITCH,        // 女巫行动
    NIGHT_SEER,         // 预言家行动
    DAY_ANNOUNCE,       // 天亮公布死讯
    DAY_DISCUSSION,     // 白天发言
    DAY_VOTING,         // 公投环节
    GAME_OVER           // 游戏结束
}


/**
 * 胜负判定
 */
enum class WinResult {
    PLAYING,        // 游戏进行中
    WOLF_WIN,       // 狼人胜利 (屠城)
    VILLAGER_WIN    // 好人胜利 (狼人出局)
}
