package com.example.data.model

import com.google.gson.annotations.SerializedName

/**
 * 验人结果 Dto
 */
data class VerifyResultDto(
    @SerializedName("target_id") val targetId: String,
    @SerializedName("is_good") val isGood: Boolean // true=好人, false=狼人
)