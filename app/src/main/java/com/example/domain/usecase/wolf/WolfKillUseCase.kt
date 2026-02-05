package com.example.domain.usecase.wolf

import com.example.domain.repository.WerewolfRepository
import javax.inject.Inject

/**
 * 狼人刀人
 * 规则限制：
 * 1. 仅限夜间狼人阶段使用
 * 2. 必须指定一名玩家（禁止空刀）
 */
class WolfKillUseCase @Inject constructor(
    private val repository: WerewolfRepository
) {
    suspend operator fun invoke(targetPlayerId: String?) {
        // 禁止空刀
        if (targetPlayerId.isNullOrBlank()) {
            throw IllegalArgumentException("狼人必须选择一名玩家进行刀杀，不可空刀！")
        }

        repository.actionWolfKill(targetPlayerId)
    }
}