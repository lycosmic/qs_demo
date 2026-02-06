package com.example.domain.usecase.base

import com.example.domain.model.Player
import com.example.domain.model.Role
import com.example.domain.model.WinResult
import javax.inject.Inject




class CheckWinConditionUseCase @Inject constructor() {

    operator fun invoke(players: List<Player>): WinResult {
        val alivePlayers = players.filter { it.isAlive }

        val wolfCount = alivePlayers.count { it.role == Role.WOLF }
        val goodCount = alivePlayers.count { it.role != Role.WOLF }

        return when {
            // 规则：狼人被投出局 -> 好人赢
            wolfCount == 0 -> WinResult.VILLAGER_WIN

            // 规则：好人阵营仅剩 1 人 -> 狼人赢
            // 或者：好人全灭 -> 狼人赢
            goodCount <= 1 -> WinResult.WOLF_WIN

            else -> WinResult.PLAYING
        }
    }
}