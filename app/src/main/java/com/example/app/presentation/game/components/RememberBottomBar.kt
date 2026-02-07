package com.example.app.presentation.game.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * 提示玩家记住当前角色信息
 */
@Composable
fun RememberBottomBar(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding(),
        color = Color.White,
        tonalElevation = 8.dp
    ) {
        Text(
            text = "\uD83C\uDF1E 请记住自己的号数和身份",
            color = Color.Black,
            fontSize = 16.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp)
        )
    }
}