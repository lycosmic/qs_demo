package com.example.data.repository

import android.util.Log
import com.example.domain.model.GameState
import com.example.domain.model.Role

object AiPromptBuilder {

    // 生成系统提示词 - 注入世界观和规则
    fun buildSystemPrompt(role: Role): String {
        return """
            你正在玩一场5人局的狼人杀游戏。配置：1狼人、2村民、1预言家、1女巫。
            你的身份是：${role.roleName}。
            
            【游戏规则】
            1. 狼人：夜间必须杀一人（包括狼人玩家），不可空刀。
            2. 女巫：有一瓶解药和毒药。不可自救。同一晚不能双药。
            3. 预言家：每晚查验一人身份。
            4. 胜负：屠城规则（杀光好人或投出狼人）。
            
            【重要要求】
            - 请完全带入当前角色，像真人一样思考。
            - 不要过分强调自己的号码。
            - 回复尽可能口语化。
            - 你的输出必须是严格的 JSON 格式。
            - 不要输出 Markdown 标记，只输出 JSON 字符串。
        """.trimIndent()
    }

    // 生成当前局势描述
    fun buildGameContext(gameState: GameState, myId: String): String {
        if (gameState.players.isEmpty() || myId.isBlank()) {
            Log.e("AiPromptBuilder", "当前游戏可能已经结束，不再生成游戏上下文")
            return "游戏已经结束，不再生成游戏上下文，请不要返回空数据"
        }

        val me = gameState.players.find { it.id == myId }!!
        val alivePlayers = gameState.players.filter { it.isAlive }.map { "${it.seatNumber}号" }
        val deadPlayers = gameState.players.filter { !it.isAlive }.map { "${it.seatNumber}号" }

        // 构建最近的聊天记录 (取最后15条，避免 Token 溢出)
        val recentChats = gameState.chatHistory.takeLast(15).filter { msg ->
            // 过滤聊天记录
            // 规则：(是公开消息) OR (我是发送者) OR (我在可见名单里)
            msg.visibleToIds.isEmpty() || msg.senderId == myId || msg.isVisibleTo(myId)
        }.joinToString("\n") {
            "[${it.senderName}]: ${it.content}"
        }

        return """
            当前是第 ${gameState.dayCount} 天。
            当前阶段：${gameState.phase}。
            存活玩家：$alivePlayers
            已死玩家：$deadPlayers
            
            【你能看到的最近聊天记录】：
            $recentChats
            
            用户是 ${me.seatNumber}号玩家。
        """.trimIndent()
    }
}