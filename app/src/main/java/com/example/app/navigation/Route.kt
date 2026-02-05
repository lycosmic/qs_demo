package com.example.app.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed interface Route : NavKey {
    // 主页
    @Serializable
    object Home : Route

    // 详情页
    @Serializable
    data class Detail(val id: String) : Route

    // AI聊天页
    @Serializable
    object Chat : Route

    companion object {
        val default = Home
    }
}