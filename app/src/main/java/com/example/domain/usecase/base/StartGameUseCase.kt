package com.example.domain.usecase.base

import com.example.domain.repository.WerewolfRepository
import javax.inject.Inject

/**
 * 开始游戏，向服务器发送开始请求
 */
class StartGameUseCase @Inject constructor(
    private val repository: WerewolfRepository
) {
    suspend operator fun invoke() {
        repository.startGame()
    }
}