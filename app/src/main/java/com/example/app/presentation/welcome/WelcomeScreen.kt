package com.example.app.presentation.welcome

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun WelcomeScreen(onStartGameClick: () -> Unit, modifier: Modifier = Modifier) {
    // 简单的渐变背景，营造狼人杀氛围
    val backgroundBrush = Brush.verticalGradient(
        colors = listOf(Color(0xFF0F2027), Color(0xFF203A43), Color(0xFF2C5364))
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundBrush),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // 标题
        Text(
            text = "AI 狼人杀",
            style = MaterialTheme.typography.displayLarge.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = 4.sp
            ),
            color = Color.White
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "1 狼 · 1 预 · 1 巫 · 2 民",
            style = MaterialTheme.typography.titleMedium,
            color = Color.LightGray
        )

        Spacer(modifier = Modifier.height(64.dp))

        // 开始按钮
        Button(
            onClick = onStartGameClick,
            modifier = Modifier
                .height(56.dp)
                .width(200.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
            elevation = ButtonDefaults.buttonElevation(8.dp)
        ) {
            Text(
                text = "开始游戏",
                style = MaterialTheme.typography.titleLarge,
                color = Color.White
            )
        }
    }
}