package com.example.data.repository

import com.example.domain.model.GameState
import com.example.domain.model.Role
import com.example.domain.repository.AiActorRepository
import com.example.domain.repository.WitchAction
import kotlinx.coroutines.delay
import javax.inject.Inject
import kotlin.random.Random

/**
 * 本地模拟 AI 决策仓库
 */
class MockAiActorRepository @Inject constructor() : AiActorRepository {

    // 模拟网络延迟 (1-2秒)，让 UI 显示 "AI 思考中..."
    private suspend fun simulateThinking() = delay(Random.nextLong(1000, 2500))

    // --- 1. 狼人 AI ---
    override suspend fun getWolfKillTarget(gameState: GameState, wolfId: String): String {
        simulateThinking()

        // 逻辑：随机选择一个活着的的玩家（包括狼人自己）
        val validTargets = gameState.players.filter {
            it.isAlive && it.role != Role.WOLF
        }

        // 兜底：如果没目标了，随便返一个空串或抛错
        return validTargets.randomOrNull()?.id
            ?: throw IllegalStateException("AI 狼人找不到可杀目标")
    }

    // --- 2. 女巫 AI ---
    override suspend fun getWitchAction(
        gameState: GameState,
        wolfKillTargetId: String?,
        witchId: String
    ): WitchAction {
        simulateThinking()

        // 逻辑A: 解药判定
        // 如果还有解药，且昨晚有人被刀 -> 50% 概率救
        if (gameState.witchInventory.hasAntidote && wolfKillTargetId != null) {
            // 规则限制：不可自救 (找到女巫ID)
            val witchId = gameState.players.find { it.role == Role.WITCH }?.id
            if (wolfKillTargetId != witchId) {
                // 模拟 70% 概率救人 (AI 比较善良)
                if (Random.nextInt(100) < 70) {
                    return WitchAction.Save(wolfKillTargetId)
                }
            }
        }

        // 逻辑B: 毒药判定
        // 如果没用解药(或没触发救人)，且有毒药 -> 30% 概率盲毒一个人
        if (gameState.witchInventory.hasPoison) {
            if (Random.nextInt(100) < 30) {
                // 毒一个活着的、不是自己的玩家
                val validTargets = gameState.players.filter {
                    it.isAlive && it.role != Role.WITCH
                }
                validTargets.randomOrNull()?.let {
                    return WitchAction.Poison(it.id)
                }
            }
        }

        // 逻辑C: 啥也不干
        return WitchAction.Skip
    }

    // --- 3. 预言家 AI ---
    override suspend fun getSeerVerifyTarget(gameState: GameState, seerId: String): String {
        simulateThinking()

        // 逻辑：优先查验活着的人，排除自己
        val validTargets = gameState.players.filter {
            it.isAlive && it.id != seerId && it.role != Role.SEER
        }

        return validTargets.randomOrNull()?.id ?: ""
    }

    // --- 4. 白天 AI 发言 ---
    override suspend fun getDaySpeech(gameState: GameState, speakerId: String): String {
        simulateThinking()

        val player = gameState.players.find { it.id == speakerId } ?: return "..."

        // 简单的发言模板库
        val commonPhrases = listOf(
            "我是好人，昨晚平安夜，我觉得不错。",
            "有人跳预言家吗？没有我就过了。",
            "我觉得 ${gameState.players.filter { !it.isMe }.random().seatNumber} 号 像狼。",
            "我这边是闭眼玩家，听大家分析吧。",
            "过。"
        )

        // 狼人伪装发言
        if (player.role == Role.WOLF) {
            return "我是平民，我觉得好人面很大，先过。"
        }

        // 预言家发言
        if (player.role == Role.SEER) {
            // 模拟预言家报验人信息
            return "我是预言家，今晚验人，暂无信息。"
        }

        return commonPhrases.random()
    }

    // --- 5. AI 投票 ---
    override suspend fun getVoteTarget(gameState: GameState, voterId: String): String {
        simulateThinking()

        // 逻辑：随机投一个活着的别人
        val validTargets = gameState.players.filter {
            it.isAlive && it.id != voterId
        }
        return validTargets.randomOrNull()?.id ?: ""
    }
}