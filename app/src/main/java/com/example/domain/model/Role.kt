package com.example.domain.model

/**
 * 角色
 */
enum class Role {
    WEREWOLF,   // 狼人
    VILLAGER,   // 平民
    SEER,       // 预言家
    WITCH,      // 女巫
    HUNTER,     // 猎人
    GUARD;      // 守卫
}

// 获取角色的阵营
val Role.camp: Camp
    get() = when (this) {
        Role.WEREWOLF -> Camp.WOLF
        else -> Camp.GOOD
    }
