package com.example.domain.usecase.base

import com.example.domain.model.GameState
import com.example.domain.repository.WerewolfRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * 监听游戏全局状态更新界面
 */
class ObserveGameStateUseCase @Inject constructor(
    private val repository: WerewolfRepository
) {
    operator fun invoke(): Flow<GameState> = repository.observeGameState()
}