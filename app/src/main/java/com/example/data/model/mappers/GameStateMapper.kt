package com.example.data.model.mappers

import com.example.data.model.GameStateDto
import com.example.domain.model.GamePhase
import com.example.domain.model.GameState
import com.example.domain.model.WinResult
//
//fun GameStateDto.toDomain(currentUserId: String): GameState {
//    val domainPlayers = players.map { it.toDomain(currentUserId) }
//    // id -> Player
//    val playerMap = domainPlayers.associateBy { it.id }
//
//    return GameState(
//        roomId = roomId,
//        myId = currentUserId,
//        phase = try {
//            GamePhase.valueOf(phase)
//        } catch (e: Exception) {
//            GamePhase.WAITING
//        },
//        players = domainPlayers,
//        messages = messages.map { it.toDomain(playerMap) },
//        winResult = if (winner == "WOLF") WinResult.WOLF_WIN else if (winner == "GOOD") WinResult.VILLAGER_WIN else WinResult.NONE
//    )
//}