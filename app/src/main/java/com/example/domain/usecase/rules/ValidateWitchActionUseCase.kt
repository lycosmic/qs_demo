package com.example.domain.usecase.rules

import com.example.domain.model.GameState
import com.example.domain.model.Role
import javax.inject.Inject


class ValidateWitchActionUseCase @Inject constructor() {

    // 校验解药
    fun checkCanSave(gameState: GameState, targetId: String): Boolean {
        // 1. 药必须还在
        if (!gameState.witchInventory.hasAntidote) return false

        // 2. 目标必须是昨晚被刀的人
        if (gameState.nightCache.wolfKillTargetId != targetId) return false

        // 3. 规则：不可自救
        // 找到女巫的ID
        val witchId = gameState.players.find { it.role == Role.WITCH }?.id
        if (witchId == targetId) {
            throw IllegalArgumentException("规则限制：女巫不可自救！")
        }

        // 4. 如果当晚已经使用了解药，则不能使用毒药
        if (gameState.nightCache.witchSaveTargetId != null) {
            throw IllegalArgumentException("规则限制：同一晚不可使用两瓶药！")
        }

        return true
    }

    // 校验毒药
    fun checkCanPoison(gameState: GameState, targetId: String): Boolean {
        // 1. 药必须还在
        if (!gameState.witchInventory.hasPoison) return false

        // 2. 如果当晚已经使用了毒药，则不能使用解药
        if (gameState.nightCache.witchPoisonTargetId != null) {
            throw IllegalArgumentException("规则限制：同一晚不可使用两瓶药！")
        }

        // 3. 不能毒死人
        val target = gameState.players.find { it.id == targetId }
        return !(target == null || !target.isAlive)
    }
}