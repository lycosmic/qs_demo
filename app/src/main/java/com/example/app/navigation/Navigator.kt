package com.example.app.navigation

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel

/**
 * 导航器
 */
class Navigator : ViewModel() {

    val routeStack = mutableStateListOf<Route>(Route.default)

    val currentRoute = derivedStateOf {
        routeStack.lastOrNull() ?: throw IllegalStateException("No route in route stack")
    }

    /**
     * 导航至指定页面
     */
    fun navigate(route: Route) {
        routeStack.add(route)
    }

    /**
     * 回退上一个页面
     */
    fun back() {
        if (routeStack.size <= 1) {
            return
        }
        routeStack.removeAt(routeStack.size - 1)
    }
}