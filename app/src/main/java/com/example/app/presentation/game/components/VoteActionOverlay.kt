package com.example.app.presentation.game.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
 * 投票选人层
 */
@Composable
fun VoteActionOverlay(
    players: List<Player>, // 传入活着的、除自己以外的玩家
    onConfirmVote: (String) -> Unit,
    onDismiss: () -> Unit // 点击空白处或返回可关闭
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(OverlayBackground) // 半透明黑底
            .clickable(onClick = onDismiss), // 点击背景关闭
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // 标题
            Text(
                text = "你要投谁",
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(40.dp))

            // 选人按钮网格
            val rows = players.chunked(2)
            rows.forEach { rowPlayers ->
                Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                    rowPlayers.forEach { player ->
                        Button(
                            onClick = { onConfirmVote(player.id) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFac8e68)),
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