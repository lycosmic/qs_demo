package com.example.domain.usecase.base


import android.util.Log
import com.example.domain.model.ChatMessage
import com.example.domain.model.GamePhase
import com.example.domain.model.GameState
import com.example.domain.model.Player
import com.example.domain.model.Role
import java.util.UUID
import javax.inject.Inject
import kotlin.random.Random

class InitializeGameUseCase @Inject constructor() {

    operator fun invoke(): GameState {
        // 1. 定义牌堆 (1狼 2民 1预 1巫)
        val roles = mutableListOf(
            Role.WOLF,
            Role.SEER,
            Role.WITCH,
            Role.VILLAGER,
            Role.VILLAGER
        ).shuffled() // 洗牌

        // 2. 创建玩家
        val mySeatNumber = Random(System.currentTimeMillis()).nextInt(0, roles.size)
        val players = roles.mapIndexed { index, role ->
            Player(
                id = "player_${index + 1}",
                seatNumber = index + 1,
                role = role,
                isMe = (index == mySeatNumber) // 用户
            )
        }


        return GameState(
            phase = GamePhase.WAITING,
            players = players,
            chatHistory = listOf(
                ChatMessage(
                    UUID.randomUUID().toString(),
                    "SYSTEM",
                    "法官",
                    "身份已分配，游戏开始！",
                )
            )
        )
    }
}