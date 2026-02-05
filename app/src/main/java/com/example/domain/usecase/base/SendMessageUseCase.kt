package com.example.domain.usecase.base

import com.example.domain.repository.WerewolfRepository
import javax.inject.Inject

/**
 * 发送消息，白天阶段公开发言，或遗言环节
 */
class SendMessageUseCase @Inject constructor(
    private val repository: WerewolfRepository
) {
    suspend operator fun invoke(content: String) {
        if (content.isBlank()) return
        repository.sendChatMessage(content)
    }
}