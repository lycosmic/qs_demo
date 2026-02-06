package com.example.domain.repository

import com.example.domain.model.GameState

/**
 * AI 决策仓库
 * 负责与后端 API 通信，获取 AI 的行动结果
 */
interface AiActorRepository {

    // 获取狼人 AI 的击杀目标，使用当前游戏状态告知 AI 上下文
    suspend fun getWolfKillTarget(gameState: GameState): String

    /**
     * 获取女巫 AI 的行动 (救人/毒人/跳过)
     * 传入昨夜被刀的玩家 ID，帮助女巫决策
     */
    suspend fun getWitchAction(gameState: GameState, wolfKillTargetId: String?): WitchAction

    // 获取预言家 AI 的验人目标
    suspend fun getSeerVerifyTarget(gameState: GameState): String

    // 获取 id 为 speakerId 的 AI 的白天发言
    suspend fun getDaySpeech(gameState: GameState, speakerId: String): String

    // 获取 AI 的投票目标
    suspend fun getVoteTarget(gameState: GameState, voterId: String): String
}

// 定义女巫 AI 的返回结构
sealed class WitchAction {
    data object Skip : WitchAction() // 不用药
    data class Save(val targetId: String) : WitchAction() // 用解药
    data class Poison(val targetId: String) : WitchAction() // 用毒药
}