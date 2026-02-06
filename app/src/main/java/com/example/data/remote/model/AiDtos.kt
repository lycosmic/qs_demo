package com.example.data.remote.model

import com.google.gson.annotations.SerializedName

// 请求：获取 AI 动作
data class AiActionRequest(
    @SerializedName("my_role") val myRole: String, // "WOLF", "WITCH"...
    @SerializedName("phase") val phase: String,
    @SerializedName("alive_players") val alivePlayerIds: List<String>,
    @SerializedName("valid_target_ids") val validTargetIds: List<String>, // 客户端计算好的合法目标
    @SerializedName("context_info") val contextInfo: Map<String, Any>?
)

// 响应：AI 动作结果
data class AiActionResponse(
    @SerializedName("action_type") val actionType: String, // "KILL", "SAVE", "SKIP"
    @SerializedName("target_id") val targetId: String?,
    @SerializedName("speech_content") val speechContent: String? // 如果是发言接口用
)