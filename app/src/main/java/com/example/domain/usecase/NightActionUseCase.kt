package com.example.domain.usecase

import com.example.domain.model.NightActionResult
import com.example.domain.repository.WerewolfRepository

/**
 * 夜间操作
 */
class NightActionUseCase(
    private val repository: WerewolfRepository
) {
    /**
     * 触发后端执行夜间逻辑（狼人刀人、女巫用药等）
     * 移动端只是一个触发器，具体逻辑由后端 Django 处理
     */
    suspend operator fun invoke(roomId: String, roundNum: Int): Result<NightActionResult> {
        return repository.performNightAction(roomId, roundNum)
    }
}
