package com.example.domain.usecase.base

import com.example.domain.repository.WerewolfRepository
import javax.inject.Inject

/**
 * 投票
 * 规则：
 * 1. 白天发言结束后进行
 * 2. 指向要放逐的玩家
 */
class VotePlayerUseCase @Inject constructor(
    private val repository: WerewolfRepository
) {
    suspend operator fun invoke(targetPlayerId: String) {
        if (targetPlayerId.isBlank()) {
            throw IllegalArgumentException("请选择一名玩家进行投票！")
        }
        repository.actionVote(targetPlayerId)
    }
}