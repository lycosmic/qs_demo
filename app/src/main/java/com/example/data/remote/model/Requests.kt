package com.example.data.remote.model
import com.google.gson.annotations.SerializedName

// 开始游戏
data class StartGameRequest(
    @SerializedName("room_id") val roomId: String
)

// 发送消息
data class ChatRequest(
    @SerializedName("room_id") val roomId: String,
    @SerializedName("content") val content: String
)

// 动作：通用目标请求 (狼人刀、女巫毒、预言家验、投票)
data class TargetActionRequest(
    @SerializedName("room_id") val roomId: String,
    @SerializedName("target_player_id") val targetPlayerId: String
)

// 动作：女巫救人
data class WitchSaveRequest(
    @SerializedName("room_id") val roomId: String,
    @SerializedName("target_player_id") val targetPlayerId: String
)

// 动作：女巫跳过
data class SkipActionRequest(
    @SerializedName("room_id") val roomId: String
)