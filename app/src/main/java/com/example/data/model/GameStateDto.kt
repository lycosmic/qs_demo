package com.example.data.model

import com.google.gson.annotations.SerializedName

/**
 * 游戏全局状态 DTO
 */
data class GameStateDto(
    @SerializedName("room_id") val roomId: String,
    val phase: String, // "NIGHT_WOLF", "DAY_DISCUSSION"
    @SerializedName("players")
    val players: List<PlayerDto>,
    @SerializedName("messages")
    val messages: List<MessageDto>,
    @SerializedName("winner")
    val winner: String?, // null, "WOLF", "VILLAGER"
    @SerializedName("night_info")
    val nightInfo: NightActionInfoDto?
)