package com.example.domain.model

/**
 * 投票结果
 */
data class VoteResult(
    val eliminatedPlayerId: String?,
    val voteDetails: List<String> // 投票详情文本
)