package com.example.domain.usecase

import com.example.domain.model.GameRoom
import com.example.domain.repository.WerewolfRepository

/**
 * 创建房间
 */
class CreateRoomUseCase(
    private val repository: WerewolfRepository
) {
    /**
     * @param totalPlayers 5-12
     * @param speakTime 10-60秒
     */
    suspend operator fun invoke(totalPlayers: Int, speakTime: Int): Result<GameRoom> {
        // 业务规则校验
        if (totalPlayers < 5 || totalPlayers > 12) {
            return Result.failure(IllegalArgumentException("人数必须在5-12人之间"))
        }
        if (speakTime < 10 || speakTime > 60) {
            return Result.failure(IllegalArgumentException("发言时长必须在10-60秒之间"))
        }

        return repository.createRoom(totalPlayers, speakTime)
    }
}
