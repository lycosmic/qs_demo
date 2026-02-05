package com.example.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay
import com.example.app.presentation.chat.ChatScreen
import com.example.app.presentation.welcome.WelcomeScreen


@Composable
fun MyNavDisplay(
    modifier: Modifier = Modifier,
    navigator: Navigator
) {
    NavDisplay(
        backStack = navigator.routeStack,
        modifier = modifier,
        onBack = { navigator.back() },
        entryProvider = { navKey ->
            NavEntry(navKey) {
                when (navKey) {
                    is Route.Welcome -> WelcomeScreen(
                        navigateToChat = {
                            navigator.navigate(Route.Chat)
                        }
                    )

                    Route.Chat -> ChatScreen(
                        navigateBack = {
                            navigator.back()
                        }
                    )
                }
            }
        }
    )
}