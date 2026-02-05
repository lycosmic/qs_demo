package com.example.domain.usecase.witch

import com.example.domain.repository.WerewolfRepository
import javax.inject.Inject

/**
 * 女巫使用毒药
 * 规则限制：
 * 1. 仅限夜间女巫阶段
 * 2. 只有一瓶毒药
 * 3. 毒药和解药不能同一晚使用 (由后端控制互斥，这里只负责发指令)
 */
class WitchPoisonUseCase @Inject constructor(
    private val repository: WerewolfRepository
) {
    suspend operator fun invoke(targetPlayerId: String) {
        if (targetPlayerId.isBlank()) return
        repository.actionWitchPoison(targetPlayerId)
    }
}