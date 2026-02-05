package com.example.domain.usecase.seer

import com.example.domain.repository.WerewolfRepository
import javax.inject.Inject

/**
 * 预言家验人
 * 规则：
 * 1. 每晚查验一人
 * 2. 法官仅告知“好人/狼人”，不告知具体身份
 */
class SeerVerifyUseCase @Inject constructor(
    private val repository: WerewolfRepository
) {
    suspend operator fun invoke(targetPlayerId: String) {
        if (targetPlayerId.isBlank()) return
        repository.actionSeerVerify(targetPlayerId)
    }
}