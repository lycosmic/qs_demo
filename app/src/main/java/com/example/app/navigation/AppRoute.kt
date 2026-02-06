package com.example.app.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed interface AppRoute : NavKey {
    // 欢迎页
    @Serializable
    object Welcome : AppRoute

    // 游戏页
    @Serializable
    object Game : AppRoute

    companion object {
        val default = Welcome
    }
}