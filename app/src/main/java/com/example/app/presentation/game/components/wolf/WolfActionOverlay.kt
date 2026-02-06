package com.example.app.presentation.game.components.wolf

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.app.ui.theme.OverlayBackground
import com.example.domain.model.Player

/**
 * 狼人行动层
 */
@Composable
fun WolfActionOverlay(
    players: List<Player>, // 传入活着的玩家（包括自己）
    onSelectTarget: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(OverlayBackground), // 半透明黑底
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // 红色标题
            Text(
                text = "🔪 今晚你要杀谁",
                color = Color(0xFFFF5252), // 鲜艳的红色
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(40.dp))

            // 选人按钮网格 (2xN 布局)
            val rows = players.chunked(2)
            rows.forEach { rowPlayers ->
                Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                    rowPlayers.forEach { player ->
                        Button(
                            onClick = { onSelectTarget(player.id) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFac8e68)), // 金色按钮
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.size(width = 110.dp, height = 70.dp),
                            elevation = ButtonDefaults.buttonElevation(6.dp)
                        ) {
                            Text(
                                text = "${player.seatNumber}",
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}