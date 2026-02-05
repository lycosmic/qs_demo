package com.example.app.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed interface AppRoute : NavKey {
    // 欢迎页
    @Serializable
    object Welcome : AppRoute

    // AI聊天页
    @Serializable
    object Chat : AppRoute

    companion object {
        val default = Welcome
    }
}