package com.example.data.model

import com.google.gson.annotations.SerializedName

/**
 * 玩家DTO
 */
data class PlayerDto(
    @SerializedName("id") val id: String,
    @SerializedName("nickname") val name: String,
    @SerializedName("seat_no") val seatNumber: Int,
    @SerializedName("role_code") val roleCode: String, // "WOLF", "VILLAGER"
    @SerializedName("is_alive") val isAlive: Boolean
)