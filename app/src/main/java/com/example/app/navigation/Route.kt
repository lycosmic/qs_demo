package com.example.app.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed interface Route : NavKey {
    // 欢迎页
    @Serializable
    object Welcome : Route

    // AI聊天页
    @Serializable
    object Chat : Route

    companion object {
        val default = Welcome
    }
}