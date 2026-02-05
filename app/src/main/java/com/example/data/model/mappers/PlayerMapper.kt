package com.example.data.model.mappers

import com.example.data.model.PlayerDto
import com.example.domain.model.Player
import com.example.domain.model.Role

fun PlayerDto.toDomain(currentUserId: String): Player {
    val roleEnum = try { Role.valueOf(roleCode) } catch (e: Exception) { Role.UNKNOWN }

    return Player(
        id = id,
        name = name,
        seatNumber = seatNumber,
        role = roleEnum,
        isAlive = isAlive,
        isMe = (id == currentUserId)
    )
}