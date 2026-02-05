package com.example.app.presentation.welcome

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier


@Composable
fun WelcomeScreen(navigateToChat: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        Text(text = "欢迎页")

        Button(onClick = navigateToChat) {
            Text(text = "开始游戏")
        }
    }
}