package com.example.domain.repository

import com.example.domain.model.GameState
import com.example.domain.model.SeerVerificationResult
import kotlinx.coroutines.flow.Flow


/**
 * 狼人杀仓库
 */
interface WerewolfRepository {
    /**
     * 观察游戏状态：有人说话、天亮了、你被刀了
     */
    fun observeGameState(): Flow<GameState>

    /**
     * 错误流：网络断开、服务器报错
     */
    fun observeErrors(): Flow<Throwable>

    // --- 玩家的行为 ---

    /**
     * 连接游戏
     */
    suspend fun connectToGame(roomId: String)

    /**
     * 开始游戏
     */
    suspend fun startGame()

    /**
     * 发送聊天消息（白天发言）
     */
    suspend fun sendChatMessage(content: String)

    // --- 角色特定技能 ---

    /**
     * 狼人刀人，不可空刀
     */
    suspend fun actionWolfKill(targetPlayerId: String)

    /**
     * 女巫使用解药，不可自救
     */
    suspend fun actionWitchSave(targetPlayerId: String)

    /**
     * 女巫使用毒药
     */
    suspend fun actionWitchPoison(targetPlayerId: String)

    /**
     * 女巫什么也不做 (跳过)
     */
    suspend fun actionWitchSkip()

    /**
     * 预言家验人，法官告知目标玩家是好人/狼人
     */
    suspend fun actionSeerVerify(targetPlayerId: String): SeerVerificationResult

    /**
     * 白天投票，平票需PK，再次平票无人出局
     */
    suspend fun actionVote(targetPlayerId: String)
}