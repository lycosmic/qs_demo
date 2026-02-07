package com.example.data.repository

import android.util.Log
import com.example.data.remote.ChatCompletionRequest
import com.example.data.remote.Message
import com.example.data.remote.OpenAiApi
import com.example.domain.model.GameState
import com.example.domain.model.Role
import com.example.domain.repository.AiActorRepository
import com.example.domain.repository.WitchAction
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import javax.inject.Inject
import kotlin.random.Random


class DirectAiRepository @Inject constructor(
    private val api: OpenAiApi,
    private val gson: Gson
) : AiActorRepository {
    companion object {
        private const val TAG = "DirectAiRepository"
    }

    private val maxRetries = 3

    // API KEY
    private val apiKey = "sk-9d0b91af856843578409b1b91e9b8589"

    // 模型名称
    private val modelName = "deepseek-chat"

    // --- 通用请求方法 ---
    private suspend fun callAi(systemPrompt: String, userPrompt: String): JsonObject {
        val request = ChatCompletionRequest(
            model = modelName,
            messages = listOf(
                Message("system", systemPrompt),
                Message("user", userPrompt)
            )
        )

        try {
            val response = api.chatCompletion("Bearer $apiKey", request)
            val content = response.choices.first().message.content
            return gson.fromJson(content, JsonObject::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            if (e is CancellationException) {
                return JsonObject()
            }

            // 兜底：如果 AI 挂了，抛出异常
            throw RuntimeException("AI 连接失败: ${e.message}")
        }
    }

    // --- 带重试的请求方法 ---
    private suspend fun callAiWithRetry(
        systemPrompt: String,
        userPrompt: String,
        retryCount: Int = 0
    ): JsonObject {
        return try {
            callAi(systemPrompt, userPrompt)
        } catch (e: Exception) {
            if (retryCount < maxRetries) {
                // 等待随机时间后重试
                delay(Random.nextLong(0, 1000))
                callAiWithRetry(systemPrompt, userPrompt, retryCount + 1)
            } else {
                throw e
            }
        }
    }

    // 1. 狼人 AI 杀人
    override suspend fun getWolfKillTarget(gameState: GameState, wolfId: String): String {
        val system = AiPromptBuilder.buildSystemPrompt(Role.WOLF) +
                "\n任务：请选择一个击杀目标，可以是狼人玩家。必须是活着的玩家。返回JSON: {\"target_seat\": int, \"reason\": string}"

        val context = AiPromptBuilder.buildGameContext(gameState, wolfId) // 当前AI的ID

        (1..maxRetries).forEach { _ ->
            try {
                val json = callAiWithRetry(system, context)
                val targetSeat = json.get("target_seat").asInt
                // 将座位号转换为 PlayerId
                return gameState.players.find { it.seatNumber == targetSeat }?.id
                    ?: ""
            } catch (e: Exception) {
                Log.e(TAG, "获取狼人AI的技能失败: $e")
            }
        }
        return ""
    }

    // 2. 女巫 AI
    override suspend fun getWitchAction(
        gameState: GameState,
        wolfKillTargetId: String?,
        witchId: String
    ): WitchAction {
        val killedSeat = gameState.players.find { it.id == wolfKillTargetId }?.seatNumber ?: -1

        val system = AiPromptBuilder.buildSystemPrompt(Role.WITCH) + """
            任务：昨晚 ${killedSeat}号 被杀了。
            你是否拥有解药：${gameState.witchInventory.hasAntidote}。
            你是否拥有毒药：${gameState.witchInventory.hasPoison}。
            请决策。
            返回JSON格式：
            {"action": "SAVE" | "POISON" | "SKIP", "target_seat": int (如果用药)}
        """.trimIndent()

        val context = AiPromptBuilder.buildGameContext(gameState, witchId)


        (1..maxRetries).forEach { _ ->
            try {
                val json = callAiWithRetry(system, context)
                val action = json.get("action").asString
                val targetSeat = if (json.has("target_seat")) json.get("target_seat").asInt else -1
                val targetId = gameState.players.find { it.seatNumber == targetSeat }?.id ?: ""

                return when (action) {
                    "SAVE" -> WitchAction.Save(targetId) // 注意：这里通常是救被杀的人，ID应该是 wolfKillTargetId
                    "POISON" -> WitchAction.Poison(targetId)
                    else -> WitchAction.Skip
                }
            } catch (e: Exception) {
                Log.e(TAG, "获取女巫AI的技能失败: $e")
            }
        }

        return WitchAction.Skip
    }

    // 3. 预言家 AI
    override suspend fun getSeerVerifyTarget(gameState: GameState, seerId: String): String {
        val system = AiPromptBuilder.buildSystemPrompt(Role.SEER) +
                "\n任务：请选择一个查验目标。优先查验有嫌疑的玩家。返回JSON: {\"target_seat\": int}"
        val context = AiPromptBuilder.buildGameContext(gameState, seerId)

        (1..maxRetries).forEach { _ ->
            try {
                val json = callAiWithRetry(system, context)
                val seat = json.get("target_seat").asInt
                return gameState.players.find { it.seatNumber == seat }?.id
                    ?: ""
            } catch (e: Exception) {
                Log.e(TAG, "获取预言家AI的技能失败: $e")
            }
        }

        return ""
    }

    // 4. 白天发言
    override suspend fun getDaySpeech(gameState: GameState, speakerId: String): String {
        val player = gameState.players.find { it.id == speakerId }!!

        val system = AiPromptBuilder.buildSystemPrompt(player.role) + """
            任务：现在轮到你发言了。
            请根据局势分析，简短发言。
            如果是狼人，请伪装。
            如果是好人，请找狼。
            返回JSON: {"content": string}
        """.trimIndent()

        val context = AiPromptBuilder.buildGameContext(gameState, speakerId)
        val json = callAi(system, context)
        return json.get("content").asString
    }

    // 5. 投票
    override suspend fun getVoteTarget(gameState: GameState, voterId: String): String? {
        val player = gameState.players.find { it.id == voterId }!!

        // 构建提示词
        var taskPrompt = "现在是公投阶段。"

        if (gameState.isPKPhase) {
            // 获取PK台玩家号码
            val pkSeats = gameState.players
                .filter { it.id in gameState.pkTargetIds }
                .map { it.seatNumber }

            taskPrompt += "注意：发生了平票PK！你只能在 $pkSeats 号之间选择投谁。"
        } else {
            taskPrompt += "请选择你要投谁出局。"
        }

        val system = AiPromptBuilder.buildSystemPrompt(player.role) +
                "\n任务：$taskPrompt 返回JSON: {\"target_seat\": int}，如果弃权，请返回JSON: {\"target_seat\": -1}"

        val context = AiPromptBuilder.buildGameContext(gameState, voterId)
        val json = callAi(system, context)
        val seat = json.get("target_seat").asInt
        return gameState.players.find { it.seatNumber == seat }?.id
    }
}