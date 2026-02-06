package com.example.domain.usecase.base

import com.example.domain.model.GameState
import javax.inject.Inject


class CalculateNightResultUseCase @Inject constructor() {

    data class NightResult(
        val deadPlayerIds: List<String>, // 死亡名单
        val isPeaceNight: Boolean        // 是否平安夜
    )

    operator fun invoke(state: GameState): NightResult {
        val cache = state.nightCache
        val deadIds = mutableSetOf<String>()

        // 1. 处理狼刀
        val wolfTarget = cache.wolfKillTargetId
        var wolfKillSuccess = true

        // 2. 处理女巫解药
        if (cache.witchSaveTargetId != null) {
            if (cache.witchSaveTargetId == wolfTarget) {
                // 救活了
                wolfKillSuccess = false
            }
        }

        if (wolfKillSuccess && wolfTarget != null) {
            deadIds.add(wolfTarget)
        }

        // 3. 处理女巫毒药
        if (cache.witchPoisonTargetId != null) {
            deadIds.add(cache.witchPoisonTargetId)
        }

        return NightResult(
            deadPlayerIds = deadIds.toList(),
            isPeaceNight = deadIds.isEmpty()
        )
    }
}