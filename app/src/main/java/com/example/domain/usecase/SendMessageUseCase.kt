package com.example.domain.usecase

import com.example.domain.repository.WerewolfRepository

/**
 * 发送发言
 */
class SendMessageUseCase(
    private val repository: WerewolfRepository
) {
    suspend operator fun invoke(roomId: String, content: String, roundNum: Int): Result<Unit> {
        if (content.isBlank()) {
            return Result.failure(IllegalArgumentException("发言内容不能为空"))
        }
        return repository.sendMessage(roomId, content, roundNum)
    }
}
