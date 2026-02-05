package com.example.domain.usecase.base

import com.example.domain.repository.WerewolfRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * 监听游戏错误事件，弹出提示
 */
class ObserveGameErrorsUseCase @Inject constructor(
    private val repository: WerewolfRepository
) {
    operator fun invoke(): Flow<Throwable> = repository.observeErrors()
}