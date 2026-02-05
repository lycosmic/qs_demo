package com.example.data.model.mappers

import com.example.data.model.NightActionInfoDto
import com.example.domain.model.NightActionInfo
import com.example.domain.model.SeerVerificationResult

fun NightActionInfoDto.toDomain(): NightActionInfo {
    val seerResult = if (seerTargetId != null && seerIsGood != null) {
        SeerVerificationResult(seerTargetId, seerIsGood)
    } else {
        null
    }

    return NightActionInfo(
        wolfKillTargetId = this.wolfKillTargetId,
        seerResult = seerResult
    )
}