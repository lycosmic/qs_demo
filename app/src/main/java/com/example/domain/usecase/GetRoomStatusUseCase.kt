package com.example.domain.usecase

import com.example.domain.model.GameRoom
import com.example.domain.repository.WerewolfRepository

/**
 * 获取房间状态
 */
class GetRoomStatusUseCase(
    private val repository: WerewolfRepository
) {
    suspend operator fun invoke(roomId: String): Result<GameRoom> {
        return repository.getRoomStatus(roomId)
    }
}
