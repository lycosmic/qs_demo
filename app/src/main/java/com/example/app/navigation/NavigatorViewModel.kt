package com.example.app.navigation

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import javax.inject.Inject

/**
 * 导航器
 */
class NavigatorViewModel @Inject constructor() : ViewModel() {

    val appRouteStacks = mutableStateListOf<AppRoute>(AppRoute.default)

    val currentRoute = derivedStateOf {
        appRouteStacks.lastOrNull() ?: throw IllegalStateException("No route in route stack")
    }

    /**
     * 导航至指定页面
     */
    fun navigate(appRoute: AppRoute) {
        appRouteStacks.add(appRoute)
    }

    /**
     * 回退上一个页面
     */
    fun back() {
        if (appRouteStacks.size <= 1) {
            return
        }
        appRouteStacks.removeAt(appRouteStacks.size - 1)
    }
}