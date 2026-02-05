package com.example.domain.usecase

import com.example.domain.model.VoteRequest
import com.example.domain.model.VoteResult
import com.example.domain.repository.WerewolfRepository

/**
 * 投票
 */
class VotePlayerUseCase(
    private val repository: WerewolfRepository
) {
    suspend operator fun invoke(request: VoteRequest): Result<VoteResult> {
        // 校验：不能投给自己（虽然后端可能也会校验，但前端做校验体验更好）
        if (request.voterId == request.targetId) {
            return Result.failure(IllegalArgumentException("不能投给自己"))
        }
        return repository.submitVote(request)
    }
}
