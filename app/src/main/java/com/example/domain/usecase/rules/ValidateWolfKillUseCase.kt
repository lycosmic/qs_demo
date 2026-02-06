package com.example.domain.usecase.rules

import javax.inject.Inject

class ValidateWolfKillUseCase @Inject constructor() {

    /**
     * 校验狼人选择的目标是否合法
     * @return 如果合法返回 true，否则抛出异常
     */
    operator fun invoke(targetId: String?): Boolean {
        if (targetId.isNullOrBlank()) {
            throw IllegalArgumentException("规则限制：狼人必须选择一名目标，不可空刀！")
        }
        return true
    }
}