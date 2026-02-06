package com.example.app.presentation.game.components.seer

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
 * 预言家行动层
 */
@Composable
fun SeerActionOverlay(
    players: List<Player>, // 传入活着的、除自己以外的玩家
    onVerify: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(OverlayBackground), // 半透明黑底
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                "今晚你要验谁",
                color = Color.White,
                fontSize = 24.sp,
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
                            onClick = { onVerify(player.id) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xffac8e68)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.size(width = 100.dp, height = 60.dp)
                        ) {
                            Text(
                                "${player.seatNumber}",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}