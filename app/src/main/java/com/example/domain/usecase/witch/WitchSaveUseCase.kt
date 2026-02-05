package com.example.domain.usecase.witch

import com.example.domain.repository.WerewolfRepository
import javax.inject.Inject

/**
 * 女巫使用解药
 * 规则限制：
 * 1. 仅限夜间女巫阶段
 * 2. 只有一瓶解药
 * 3. 不可自救 (五人本平衡设定)
 */
class WitchSaveUseCase @Inject constructor(
    private val repository: WerewolfRepository
) {
    suspend operator fun invoke(targetPlayerId: String, myPlayerId: String) {
        // 禁止自救
        if (targetPlayerId == myPlayerId) {
            throw IllegalArgumentException("根据规则，女巫无法使用解药自救！")
        }

        repository.actionWitchSave(targetPlayerId)
    }
}