package com.example.data.model

import com.google.gson.annotations.SerializedName

/**
 * 夜间信息 DTO
 */
data class NightActionInfoDto(
    @SerializedName("wolf_kill_target_id") val wolfKillTargetId: String?,
    @SerializedName("seer_target_id") val seerTargetId: String?,
    @SerializedName("seer_is_good") val seerIsGood: Boolean?
)