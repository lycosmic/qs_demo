package com.example.domain.usecase.witch

import com.example.domain.repository.WerewolfRepository
import javax.inject.Inject

/**
 * 女巫跳过
 * 规则：女巫可以选择不用药（压药/压毒）
 */
class WitchSkipActionUseCase @Inject constructor(
    private val repository: WerewolfRepository
) {
    suspend operator fun invoke() {
        repository.actionWitchSkip()
    }
}