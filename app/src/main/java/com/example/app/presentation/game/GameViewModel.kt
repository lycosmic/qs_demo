package com.example.app.presentation.game

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.model.ChatMessage
import com.example.domain.model.ChatMessage.Companion.SYSTEM_MESSAGE_SENDER_ID
import com.example.domain.model.GamePhase
import com.example.domain.model.GameState
import com.example.domain.model.NightCache
import com.example.domain.model.Role
import com.example.domain.model.SeerVerificationResult
import com.example.domain.model.WinResult
import com.example.domain.repository.AiActorRepository
import com.example.domain.repository.WitchAction
import com.example.domain.usecase.base.CalculateNightResultUseCase
import com.example.domain.usecase.base.CalculateVoteResultUseCase
import com.example.domain.usecase.base.CheckWinConditionUseCase
import com.example.domain.usecase.base.InitializeGameUseCase
import com.example.domain.usecase.rules.ValidateWitchActionUseCase
import com.example.domain.usecase.rules.ValidateWolfKillUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class GameViewModel @Inject constructor(
    // 1. 核心用例
    private val initializeGameUseCase: InitializeGameUseCase,
    private val validateWolfKillUseCase: ValidateWolfKillUseCase,
    private val validateWitchActionUseCase: ValidateWitchActionUseCase,
    private val calculateNightResultUseCase: CalculateNightResultUseCase,
    private val calculateVoteResultUseCase: CalculateVoteResultUseCase,
    private val checkWinConditionUseCase: CheckWinConditionUseCase,

    // 2. AI 仓库 (用于获取 AI 决策)
    private val aiRepository: AiActorRepository
) : ViewModel() {

    // --- UI 状态 ---
    private val _uiState = MutableStateFlow(GameState()) // 初始为空状态
    val uiState: StateFlow<GameState> = _uiState.asStateFlow()

    // --- 辅助状态：用户交互控制器 ---
    // 用于在协程中“挂起”等待用户点击按钮
    private var userActionDeferred: CompletableDeferred<String>? = null

    // 用于等待用户输入文本
    private var userSpeechDeferred: CompletableDeferred<String>? = null

    // --- 错误处理 ---
    private val _errorFlow = MutableSharedFlow<String>()
    val errorFlow = _errorFlow.asSharedFlow()

    companion object {
        const val TAG = "GameViewModel"
    }

    fun startGame() {
        if (_uiState.value.phase != GamePhase.WAITING) return

        viewModelScope.launch {
            // 1. 初始化数据
            val initialState = initializeGameUseCase()
            _uiState.value = initialState

            Log.d(TAG, "当前游戏阶段: 等待中（${_uiState.value.phase}）")

            delay(1500)

            // 2. 启动游戏主循环 (法官逻辑)
            runGameLoop()
        }
    }

    // 用户点击了操作目标 (刀人、验人、投票)
    fun onUserAction(targetId: String) {
        // 如果协程正在等待结果，则提交结果
        if (userActionDeferred?.isActive == true) {
            userActionDeferred?.complete(targetId)
        }
    }

    // 用户发送了发言
    fun onUserSpeech(content: String) {
        if (userSpeechDeferred?.isActive == true) {
            userSpeechDeferred?.complete(content)
        }
    }

    /**
     * 玩家点击了“关闭验人结果”
     */
    fun dismissSeerResult() {
        updateState { state ->
            state.copy(seerResult = null) // 清空结果，弹窗消失
        }
    }

    private suspend fun runGameLoop() {
        while (true) {
            // === 1. 夜间流程 ===
            runNightPhase()

            // === 2. 天亮结算 ===
            val nightResult = calculateNightResultUseCase(_uiState.value)
            announceNightResult(nightResult)

            // === 3. 胜负判定 (天亮后) ===
            if (checkGameOver()) break

            // === 4. 白天流程 (发言) ===
            runDayDiscussionPhase(nightResult)

            // === 5. 投票流程 ===
            runVotingPhase()

            // === 6. 胜负判定 (投票后) ===
            if (checkGameOver()) break

            // 准备进入下一夜
            updateState { it.copy(dayCount = it.dayCount + 1) }
        }
    }

    /**
     * 天亮结算方法
     * 职责：
     * 1. 切换阶段 UI
     * 2. 发送系统公告（平安夜 vs 谁死了）
     * 3. 更新内存中的玩家存活状态 (isAlive = false)
     */
    private suspend fun announceNightResult(nightResult: CalculateNightResultUseCase.NightResult) {
        // 1. 切换到“天亮宣布”阶段 (此时 UI 背景通常会变亮)
        updateState { it.copy(phase = GamePhase.DAY_ANNOUNCE) }
        Log.d(
            TAG,
            "当前游戏阶段: 白天死讯通告（${_uiState.value.phase}）"
        )

        appendSystemMessage("=== 天亮了 ===")
        delay(1500) // 模拟天亮动画时间

        val deadIds = nightResult.deadPlayerIds

        // 2. 根据结果判定
        if (deadIds.isEmpty()) {
            // A. 平安夜
            appendSystemMessage("昨夜平安夜，无人死亡。请开始依次发言")
        } else {
            // B. 有人死亡
            // 获取死者名字用于显示 (例如 "3号、5号")
            val deadNames = deadIds.joinToString("、") { id ->
                val p = _uiState.value.players.find { it.id == id }
                if (p?.isMe == true) "你" else "${p?.seatNumber}号"
            }

            appendSystemMessage("昨夜 $deadNames 死亡。请开始依次发言")

            // --- 核心逻辑：真正处死玩家 ---
            updateState { state ->
                val newPlayers = state.players.map { player ->
                    if (player.id in deadIds) {
                        player.copy(isAlive = false) // 标记为死亡
                    } else {
                        player
                    }
                }
                state.copy(players = newPlayers)
            }
        }

        // 留给用户一点阅读时间
        delay(2500)
    }

    private fun checkGameOver(): Boolean {
        val winResult = checkWinConditionUseCase(_uiState.value.players)
        if (winResult != WinResult.PLAYING) {
            updateState { it.copy(phase = GamePhase.GAME_OVER, winResult = winResult) }
            val winnerText = if (winResult == WinResult.WOLF_WIN) "狼人胜利！" else "好人胜利！"
            appendSystemMessage("游戏结束，$winnerText")
            return true
        }
        return false
    }


    private suspend fun runNightPhase() {
        // 清理昨夜缓存，进入天黑
        updateState { it.copy(phase = GamePhase.NIGHT_START, nightCache = NightCache()) }
        Log.d(
            TAG,
            "当前游戏阶段: 进入天黑（${_uiState.value.phase}），今天是第 ${_uiState.value.dayCount} 天"
        )


        // 公开消息：天黑了
        appendSystemMessage("=== 第 ${_uiState.value.dayCount} 夜，天黑请闭眼 ===")
        delay(2000)

        // ----------------- 1. 狼人行动 -----------------
        updateState { it.copy(phase = GamePhase.NIGHT_WOLF) }

        val wolfIds = getPlayerIdsByRole(Role.WOLF)
        val goodIds = getOtherPlayerIds(Role.WOLF)

        Log.d(
            TAG,
            "当前游戏阶段: 狼人开始行动（${_uiState.value.phase}），狼人为 ${wolfIds.joinToString("、")}"
        )

        val myRole = _uiState.value.players.find { it.isMe }?.role
        var wolfKillId: String?

        if (myRole == Role.WOLF && _uiState.value.players.find { it.isMe }?.isAlive == true) {
            // A. 我是狼人：等待用户操作
            appendSystemMessage("狼人请睁眼。请选择今晚的击杀目标（禁止空刀）。", wolfIds)
            // 循环直到用户选择合法的目标
            while (true) {
                val inputId = waitForUserActionInput()
                try {
                    if (validateWolfKillUseCase(inputId)) {
                        wolfKillId = inputId
                        break
                    }
                } catch (e: Exception) {
                    _errorFlow.emit(e.message ?: "无效目标")
                }
            }
        } else {
            // B. 我是好人：调用 AI
            appendSystemMessage("狼人正在行动...", goodIds)
            val wolfId = _uiState.value.players.find { it.role == Role.WOLF }?.id ?: ""
            wolfKillId = aiRepository.getWolfKillTarget(_uiState.value, wolfId)
        }

        Log.d(TAG, "狼人刀杀了 $wolfKillId")

        // 记录狼刀
        updateState { it.copy(nightCache = it.nightCache.copy(wolfKillTargetId = wolfKillId)) }
        delay(Random.nextLong(1500, 2000))

        // ----------------- 2. 女巫行动 -----------------
        Log.d(
            TAG,
            "当前游戏阶段: 女巫开始行动（${_uiState.value.phase}）"
        )

        updateState { it.copy(phase = GamePhase.NIGHT_WITCH) }

        if (myRole == Role.WITCH && _uiState.value.players.find { it.isMe }?.isAlive == true) {
            // A. 我是女巫
            appendSystemMessage(
                "昨夜 ${getPlayerName(wolfKillId)} 被袭击了。",
                listOf(getMyId())
            )
            // 这里 UI 会根据 State 显示“救/毒/跳过”按钮，逻辑较复杂，简化为：等待用户指令
            // 我们假设 UI 返回的 inputId 格式： "SAVE:id" 或 "POISON:id" 或 "SKIP"
            while (true) {
                val actionCmd = waitForUserActionInput() // 解析命令
                try {
                    val (type, target) = parseWitchCommand(actionCmd)
                    // 调用 UseCase 校验
                    if (type == "SAVE" && validateWitchActionUseCase.checkCanSave(
                            _uiState.value,
                            target!!
                        )
                    ) {
                        // 1. 先展示动画
                        updateState {
                            it.copy(
                                nightCache = it.nightCache.copy(witchSaveTargetId = target),
                                witchInventory = it.witchInventory.copy(hasAntidote = false)
                            )
                        }
                        break
                    } else if (type == "POISON" && validateWitchActionUseCase.checkCanPoison(
                            _uiState.value,
                            target!!
                        )
                    ) {
                        updateState {
                            it.copy(
                                nightCache = it.nightCache.copy(witchPoisonTargetId = target),
                                witchInventory = it.witchInventory.copy(hasPoison = false)
                            )
                        }
                        break
                    } else if (type == "SKIP") {
                        break
                    }
                } catch (e: Exception) {
                    _errorFlow.emit(e.message ?: "操作无效")
                }
            }
        } else {
            // B. AI 女巫
            appendSystemMessage("女巫正在行动...", getOtherPlayerIds(Role.WITCH))
            val witchId = _uiState.value.players.find { it.role == Role.WITCH }?.id
                ?: throw IllegalStateException("无女巫")
            when (val action = aiRepository.getWitchAction(_uiState.value, wolfKillId, witchId)) {
                is WitchAction.Save -> updateState {
                    it.copy(
                        nightCache = it.nightCache.copy(
                            witchSaveTargetId = action.targetId,
                        ),
                        witchInventory = it.witchInventory.copy(hasAntidote = false)
                    )
                }

                is WitchAction.Poison -> updateState {
                    it.copy(
                        nightCache = it.nightCache.copy(
                            witchPoisonTargetId = action.targetId
                        ),
                        witchInventory = it.witchInventory.copy(hasPoison = false)
                    )
                }

                is WitchAction.Skip -> {}
            }
        }

        Log.d(
            TAG,
            "女巫行动结束，新的药水状态为：${_uiState.value.witchInventory}"
        )
        delay(Random.nextLong(1000, 2000))

        // ----------------- 3. 预言家行动 -----------------
        updateState { it.copy(phase = GamePhase.NIGHT_SEER) }
        Log.d(
            TAG,
            "当前游戏阶段: 预言家开始行动（${_uiState.value.phase}），预言家为 ${
                _uiState.value.players.filter { it.role == Role.SEER }.joinToString("、")
            }"
        )

        if (myRole == Role.SEER && _uiState.value.players.find { it.isMe }?.isAlive == true) {
            appendSystemMessage("请选择查验目标...", listOf(getMyId()))
            val targetId = waitForUserActionInput()

            val targetPlayer = _uiState.value.players.find { it.id == targetId }
            if (targetPlayer != null) {
                val isGood = targetPlayer.role != Role.WOLF
                // 更新 UI 显示验人结果
                updateState {
                    it.copy(
                        nightCache = it.nightCache.copy(seerVerifyTargetId = targetId),
                        seerResult = SeerVerificationResult(targetId, isGood),
                    )
                }

                if (isGood) {
                    appendSystemMessage(
                        "${getPlayerName(targetId)} 是好人",
                        listOf(getMyId())
                    )
                } else {
                    appendSystemMessage(
                        "${getPlayerName(targetId)} 是狼人",
                        listOf(getMyId())
                    )
                }

                // 暂停一下让用户看结果
                delay(3000)
            }
        } else {
            appendSystemMessage("预言家正在行动...", getOtherPlayerIds(Role.SEER))
            val seerId = _uiState.value.players.find { it.role == Role.SEER }?.id ?: ""
            val targetId = aiRepository.getSeerVerifyTarget(_uiState.value, seerId)
            updateState { it.copy(nightCache = it.nightCache.copy(seerVerifyTargetId = targetId)) }
        }
        delay(1000)
    }

    private suspend fun runDayDiscussionPhase(nightResult: CalculateNightResultUseCase.NightResult) {
        updateState { it.copy(phase = GamePhase.DAY_DISCUSSION) }

        // 1. 处理遗言 (仅首夜死亡有遗言)
        // 此时玩家状态已经是 isAlive=false 了，但我们需要根据 nightResult 知道是谁刚死的
        if (_uiState.value.dayCount == 1 && nightResult.deadPlayerIds.isNotEmpty()) {
            for (deadId in nightResult.deadPlayerIds) {
                updateState { it.copy(currentSpeakerId = deadId) }
                appendSystemMessage("请 ${getPlayerName(deadId)} 发表遗言...", listOf())

                // 调用之前的通用发言方法
                processSpeech(deadId, isLastWords = true)
            }
        }

        // 2. 正常轮流发言 (只让活着的人发言)
        val alivePlayers = _uiState.value.players
            .filter { it.isAlive }
            .sortedBy { it.seatNumber } // 按座位号顺序

        if (alivePlayers.isEmpty()) return // 假如全死光了(触发结束判定)，直接跳过

        for (player in alivePlayers) {
            // 1. 标记当前发言者
            updateState { it.copy(currentSpeakerId = player.id) }

            // 2. 处理发言 (挂起等待用户输入 或 AI思考)
            processSpeech(player.id, isLastWords = false)
        }

        // 循环结束，清空发言者
        updateState { it.copy(currentSpeakerId = null) }
    }

    // 统一处理发言逻辑 (用户输入 OR AI 生成)
    private suspend fun processSpeech(
        playerId: String,
        isLastWords: Boolean // 是否有最后的遗言
    ) {
        val player = _uiState.value.players.find { it.id == playerId } ?: return
        val prefix = if (isLastWords) "【遗言】" else "【发言】"

        if (player.isMe) {
            appendSystemMessage("$prefix 请发言...")
            // UI 会检测到是用户的回合，解锁输入框
            val content = waitForUserSpeechInput()
            // 发送消息
            val msg = ChatMessage(uuid(), player.id, player.seatNumber.toString(), content)
            updateState { it.copy(chatHistory = it.chatHistory + msg) }
        } else {
            // AI 发言
            // UI 可以显示 "2号 正在输入..."
            val content = aiRepository.getDaySpeech(_uiState.value, player.id)
            delay(Random.nextLong(1000, 3000)) // 模拟思考打字
            val msg = ChatMessage(uuid(), player.id, player.seatNumber.toString(), content)
            updateState { it.copy(chatHistory = it.chatHistory + msg) }
        }
        delay(500)
    }

    private suspend fun runVotingPhase() {
        updateState { it.copy(phase = GamePhase.DAY_VOTING) }
        appendSystemMessage("=== 开始公投 ===")
        delay(1000)

        // 1. 收集所有人的投票
        val votes = mutableMapOf<String, String>() // VoterId -> TargetId
        val alivePlayers = _uiState.value.players.filter { it.isAlive }

        for (player in alivePlayers) {
            if (player.isMe) {
                appendSystemMessage("请你选择投票对象...")
                val targetId = waitForUserActionInput()
                votes[player.id] = targetId
            } else {
                val targetId = aiRepository.getVoteTarget(_uiState.value, player.id)
                votes[player.id] = targetId
            }
        }

        // 2. 公布票型
        val voteSummary = votes.entries.joinToString("\n") { (voter, target) ->
            "${getPlayerName(voter)} -> ${getPlayerName(target)}"
        }
        appendSystemMessage("投票结果：\n$voteSummary")
        delay(2000)

        // 3. 计算出局
        when (val result = calculateVoteResultUseCase(_uiState.value, votes)) {
            is CalculateVoteResultUseCase.VoteOutcome.PlayerOut -> {
                val outId = result.playerId
                appendSystemMessage("${getPlayerName(outId)} 被放逐。")

                // 处死玩家
                updateState { state ->
                    val newPlayers = state.players.map {
                        if (it.id == outId) it.copy(isAlive = false) else it
                    }
                    state.copy(players = newPlayers)
                }

                // 遗言判定 (首日出局)
                if (_uiState.value.dayCount == 1) {
                    updateState { it.copy(currentSpeakerId = outId) }

                    processSpeech(outId, isLastWords = true)
                }
            }

            is CalculateVoteResultUseCase.VoteOutcome.TiePK -> {
                appendSystemMessage("平票 PK，无人出局。(五人本简化，直接进入下一夜)")
            }

            is CalculateVoteResultUseCase.VoteOutcome.TieNoOut -> {
                appendSystemMessage("再次平票，平安日。")
            }
        }
        delay(2000)
    }

    private fun getPlayerName(id: String?): String {
        if (id == null) return "未知"
        val p = _uiState.value.players.find { it.id == id }
        return if (p?.isMe == true) "你" else "${p?.seatNumber}号"
    }

    // 简单的解析器，用于处理 UI 传回来的女巫指令字符串
    private fun parseWitchCommand(cmd: String): Pair<String, String?> {
        if (cmd == "SKIP") return "SKIP" to null
        val parts = cmd.split(":")
        return parts[0] to parts.getOrNull(1)
    }

    // ----------------------------------------------------
    //  Helper Methods
    // ----------------------------------------------------

    private fun updateState(block: (GameState) -> GameState) {
        _uiState.update(block)
    }

    /**
     * 获取所有玩家ID列表
     */
    private fun getAllPlayerIds(): List<String> {
        return _uiState.value.players.map { it.id }
    }

    /**
     * 获取我的玩家ID
     */
    private fun getMyId(): String {
        return _uiState.value.myId
    }

    /**
     * 获取除指定角色外的所有玩家ID
     */
    private fun getOtherPlayerIds(role: Role): List<String> {
        return _uiState.value.players.filter { it.role != role }.map { it.id }
    }

    /**
     * 获取指定角色的玩家ID
     */
    private fun getPlayerIdsByRole(role: Role): List<String> {
        return _uiState.value.players.filter { it.role == role }.map { it.id }
    }

    /**
     * 添加系统消息
     */
    private fun appendSystemMessage(text: String, visibleTo: List<String> = emptyList()) {
        val msg = ChatMessage(
            uuid(),
            SYSTEM_MESSAGE_SENDER_ID,
            "法官",
            text,
            visibleTo
        )
        updateState { it.copy(chatHistory = it.chatHistory + msg) }
    }

    private fun uuid() = UUID.randomUUID().toString()

    // 关键：挂起函数，等待用户操作
    private suspend fun waitForUserActionInput(): String {
        val deferred = CompletableDeferred<String>()
        userActionDeferred = deferred
        // 这里会一直挂起，直到 onUserAction 被调用
        val result = deferred.await()
        userActionDeferred = null
        return result
    }

    // 关键：挂起函数，等待用户发言
    private suspend fun waitForUserSpeechInput(): String {
        val deferred = CompletableDeferred<String>()
        userSpeechDeferred = deferred
        val result = deferred.await()
        userSpeechDeferred = null
        return result
    }

    /**
     * 退出游戏，清空状态
     */
    fun exitGame() {
        updateState { GameState() }
    }


}