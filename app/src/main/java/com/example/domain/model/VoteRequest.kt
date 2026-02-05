package com.example.domain.model

/**
 * 投票请求
 */
data class VoteRequest(
    val roomId: String,
    val voterId: String,
    val targetId: String,
    val roundNum: Int
)