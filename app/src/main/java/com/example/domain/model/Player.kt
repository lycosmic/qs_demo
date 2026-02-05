package com.example.domain.model

/**
 * 玩家
 */
data class Player(
    val id: String,
    val roomId: String,
    val role: Role,
    val camp: Camp,
    val isAi: Boolean,
    val isAlive: Boolean,
    val skillUsed: Map<String, Boolean> = mapOf()
)


