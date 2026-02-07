package com.example.app.presentation.welcome

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.R
import com.example.app.presentation.game.GameViewModel

/**
 * 欢迎页
 */
@Composable
fun WelcomeScreen(onStartGame: () -> Unit, viewModel: GameViewModel = hiltViewModel()) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable(
                indication = null, onClick = {
                    viewModel.startGame()
                    onStartGame()
                },
                interactionSource = remember { MutableInteractionSource() },
                enabled = true
            ) // 点击任意处进入
    ) {
        Image(
            painter = painterResource(id = R.drawable.welcome_bg),
            contentDescription = "Background",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    }
}