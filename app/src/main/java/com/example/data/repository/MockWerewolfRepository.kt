package com.example.data.repository

import com.example.domain.model.ChatMessage
import com.example.domain.model.GamePhase
import com.example.domain.model.GameState
import com.example.domain.model.MessageType
import com.example.domain.model.NightActionInfo
import com.example.domain.model.Player
import com.example.domain.model.Role
import com.example.domain.model.SeerVerificationResult
import com.example.domain.repository.WerewolfRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

/**
 * 本地 Mock 仓库
 */
@Singleton
class MockWerewolfRepository @Inject constructor() : WerewolfRepository {

    // 模拟服务器协程
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private val myId = "player_me"
    private var currentRoomId: String? = null

    // 初始玩家配置，我是狼人
    private val initialPlayers = listOf(
        Player(
            id = myId,
            name = "我(狼王)",
            seatNumber = 1,
            role = Role.WOLF,
            isMe = true,
            isAlive = true
        ),
        Player(id = "ai_2", name = "AI 预言家", seatNumber = 2, role = Role.SEER, isAlive = true),
        Player(id = "ai_3", name = "AI 女巫", seatNumber = 3, role = Role.WITCH, isAlive = true),
        Player(
            id = "ai_4",
            name = "AI 平民A",
            seatNumber = 4,
            role = Role.VILLAGER,
            isAlive = true
        ),
        Player(id = "ai_5", name = "AI 平民B", seatNumber = 5, role = Role.VILLAGER, isAlive = true)
    )

    // 游戏核心状态
    private val _gameState = MutableStateFlow(
        GameState(
            roomId = "mock_room_001",
            myId = myId,
            phase = GamePhase.WAITING,
            players = initialPlayers,
            messages = listOf(
                ChatMessage(
                    UUID.randomUUID().toString(),
                    "system",
                    "系统",
                    "欢迎来到五人本测试服。",
                    MessageType.SYSTEM
                )
            ),
            nightActionInfo = NightActionInfo()
        )
    )

    override fun observeGameState(): Flow<GameState> = _gameState.asStateFlow()

    override fun observeErrors(): Flow<Throwable> = flow {

    }

    override suspend fun connectToGame(roomId: String) {
        currentRoomId = roomId
    }

    override suspend fun startGame() {
        delay(300)

        currentRoomId = "mock_room_001"
        addSystemMessage("游戏开始！分配身份中...")
        delay(1000)

        // 进入第一个夜晚
        startNightPhase()
    }

    override suspend fun sendChatMessage(content: String) {
        // 发送我的消息
        val myMsg =
            ChatMessage(UUID.randomUUID().toString(), myId, "我", content, MessageType.USER_TEXT)
        appendMessage(myMsg)

        // 模拟 AI 回复
        scope.launch {
            delay(Random.nextLong(1000, 3000))
            val randomAi = _gameState.value.players.filter { !it.isMe && it.isAlive }.randomOrNull()
            if (randomAi != null) {
                val aiText = if (content.contains("查杀")) "我是好人啊！" else "这就过了吧。"
                appendMessage(
                    ChatMessage(
                        UUID.randomUUID().toString(),
                        randomAi.id,
                        randomAi.name,
                        aiText,
                        MessageType.USER_TEXT
                    )
                )
            }
        }
    }

    override suspend fun actionWolfKill(targetPlayerId: String) {
        delay(300)

        if (_gameState.value.phase != GamePhase.NIGHT_WOLF) {
            throw IllegalStateException("当前不是狼人行动时间")
        }

        // 记录刀人目标
        updateState { state ->
            state.copy(
                nightActionInfo = state.nightActionInfo?.copy(wolfKillTargetId = targetPlayerId)
                    ?: NightActionInfo(wolfKillTargetId = targetPlayerId)
            )
        }

        addSystemMessage("【系统(仅狼可见)】你选择了袭击 $targetPlayerId 号玩家。")

        // 触发后续流程：狼人行动结束 -> 唤醒女巫
        scope.launch {
            delay(1500)
            transitionToWitch()
        }
    }

    override suspend fun actionWitchSave(targetPlayerId: String) {
        delay(300)

        // 假设女巫救了人，更新 nightInfo 并记录解药已用
        addSystemMessage("【系统】你使用了解药。")
    }

    override suspend fun actionWitchPoison(targetPlayerId: String) {
        delay(300)

        addSystemMessage("【系统】你使用了毒药。")
    }

    override suspend fun actionWitchSkip() {
        delay(300)

        addSystemMessage("【系统】你选择不使用药水。")
    }

    override suspend fun actionSeerVerify(targetPlayerId: String): SeerVerificationResult {
        delay(300)

        // 查找目标
        val target = _gameState.value.players.find { it.id == targetPlayerId }
            ?: throw IllegalArgumentException("玩家不存在")

        // 生成结果
        val isGood = target.role != Role.WOLF
        val result = SeerVerificationResult(targetPlayerId, isGood)

        // 更新状态
        updateState { state ->
            state.copy(
                nightActionInfo = state.nightActionInfo?.copy(seerResult = result)
                    ?: NightActionInfo(seerResult = result)
            )
        }

        addSystemMessage("【系统】验人完成。")

        return result
    }

    override suspend fun actionVote(targetPlayerId: String) {
        delay(300)

        addSystemMessage("你投票给了 $targetPlayerId 号。")
        // 简单演示：随便投
        scope.launch {
            delay(2000)
            addSystemMessage("投票结束，AI_4 被放逐。")
            // 简单处理：直接把某人投死
            killPlayer("ai_4")
            delay(2000)
            startNightPhase() // 再次入夜
        }
    }


    // --- 内部逻辑：模拟游戏引擎---
    private suspend fun startNightPhase() {
        updateState {
            it.copy(
                phase = GamePhase.NIGHT_START,
                nightActionInfo = NightActionInfo()
            )
        } // 清空昨夜信息
        addSystemMessage("=== 天黑请闭眼 ===")
        delay(2000)

        // 1. 狼人环节
        updateState { it.copy(phase = GamePhase.NIGHT_WOLF) }
        addSystemMessage("狼人请睁眼，请选择击杀目标。")

        // 如果我是狼，等待 UI 调用 actionWolfKill
        // 如果我不是狼，这里模拟 AI 刀人
        if (_gameState.value.players.find { it.isMe }?.role != Role.WOLF) {
            delay(3000)
            // AI 随机刀一个好人
            val victim = _gameState.value.players.filter { it.role != Role.WOLF }.random()
            updateState { s ->
                s.copy(
                    nightActionInfo = s.nightActionInfo?.copy(wolfKillTargetId = victim.id)
                        ?: NightActionInfo(wolfKillTargetId = victim.id)
                )
            }
            transitionToWitch()
        }
    }

    private suspend fun transitionToWitch() {
        updateState { it.copy(phase = GamePhase.NIGHT_WITCH) }

        // 如果我是狼人，我看不到女巫行动，但我需要等待这一阶段过去
        if (_gameState.value.players.find { it.isMe }?.role == Role.WOLF) {
            addSystemMessage("（等待女巫行动中...）")
            delay(Random.nextLong(3000, 5000))

            // 模拟 AI 女巫逻辑：50% 概率救人
            val killId = _gameState.value.nightActionInfo?.wolfKillTargetId
            if (killId != null && Random.nextBoolean()) {
                // 女巫救了，清空刀人ID (或者标记为已救，这里简化为清空代表平安夜)
                // 注意：为了逻辑严谨，通常是标记 isSaved=true，这里简单处理
                updateState { s -> s.copy(nightActionInfo = s.nightActionInfo?.copy(wolfKillTargetId = null)) } // 救活了
            }
            transitionToSeer()
        }
    }

    private suspend fun transitionToSeer() {
        updateState { it.copy(phase = GamePhase.NIGHT_SEER) }

        if (_gameState.value.players.find { it.isMe }?.role == Role.WOLF) {
            addSystemMessage("（等待预言家行动中...）")
            delay(Random.nextLong(2000, 4000))
            transitionToDay()
        }
    }

    private suspend fun transitionToDay() {
        updateState { it.copy(phase = GamePhase.DAY_ANNOUNCE) }
        addSystemMessage("=== 天亮了 ===")
        delay(1500)

        // 结算死亡
        val deadId = _gameState.value.nightActionInfo?.wolfKillTargetId
        if (deadId != null) {
            killPlayer(deadId)
            val deadName = _gameState.value.players.find { it.id == deadId }?.name ?: "未知"
            addSystemMessage("昨夜 $deadName 死亡。")
        } else {
            addSystemMessage("昨夜平安夜。")
        }

        delay(2000)
        updateState { it.copy(phase = GamePhase.DAY_DISCUSSION) }
        addSystemMessage("请开始发言。")
    }


    // --- 辅助方法 ---
    private fun killPlayer(targetId: String) {
        updateState { s ->
            val newPlayers = s.players.map {
                if (it.id == targetId) it.copy(isAlive = false) else it
            }
            s.copy(players = newPlayers)
        }
    }

    private fun updateState(reducer: (GameState) -> GameState) {
        _gameState.update(reducer)
    }

    private fun appendMessage(msg: ChatMessage) {
        updateState { s -> s.copy(messages = s.messages + msg) }
    }

    private fun addSystemMessage(text: String) {
        appendMessage(
            ChatMessage(
                UUID.randomUUID().toString(),
                "system",
                "法官",
                text,
                MessageType.SYSTEM
            )
        )
    }
}