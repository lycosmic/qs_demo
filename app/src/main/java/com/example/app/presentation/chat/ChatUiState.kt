package com.example.app.presentation.chat

import com.example.domain.model.ChatMessage
import com.example.domain.model.GamePhase
import com.example.domain.model.Player
import com.example.domain.model.Role
import com.example.domain.model.SeerVerificationResult

data class ChatUiState(
    val roomId: String = "",
    val myId: String = "",
    val phase: GamePhase = GamePhase.WAITING,
    val players: List<Player> = emptyList(),
    val messages: List<ChatMessage> = emptyList(),

    // 辅助状态
    val errorMessage: String? = null, // 用于显示错误
    val isLoading: Boolean = false,

    // 弹窗控制
    val showActionDialog: Boolean = false, // 是否显示选人弹窗(刀人/验人/投票)
    val seerResult: SeerVerificationResult? = null // 预言家验人结果(用于弹窗显示)
) {
    // 方便 UI 判断当前是不是该我操作
    // 逻辑：当前阶段匹配我的角色，且我还活着
    fun isMyTurn(myRole: Role): Boolean {
        return when (phase) {
            GamePhase.NIGHT_WOLF -> myRole == Role.WOLF
            GamePhase.NIGHT_SEER -> myRole == Role.SEER
            GamePhase.NIGHT_WITCH -> myRole == Role.WITCH
            GamePhase.DAY_VOTING -> true // 投票阶段所有人都能动
            else -> false
        }
    }

    // 获取我的角色
    val myRole: Role
        get() = players.find { it.id == myId }?.role ?: Role.UNKNOWN

    // 获取我还活着的队友/可操作目标
    val activePlayers: List<Player>
        get() = players.filter { it.isAlive }
}