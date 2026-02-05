package com.example.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay
import com.example.app.presentation.chat.ChatScreen
import com.example.app.presentation.welcome.WelcomeScreen


@Composable
fun MyNavDisplay(
    modifier: Modifier = Modifier,
    viewModel: NavigatorViewModel = hiltViewModel(),
) {
    NavDisplay(
        backStack = viewModel.appRouteStacks,
        modifier = modifier,
        onBack = { viewModel.back() },
        entryProvider = { navKey ->
            NavEntry(navKey) {
                when (navKey) {
                    is AppRoute.Welcome -> WelcomeScreen(
                        onStartGameClick = {
                            viewModel.navigate(AppRoute.Chat)
                        }
                    )

                    AppRoute.Chat -> ChatScreen()
                }
            }
        }
    )
}