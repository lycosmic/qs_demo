package com.example.app.presentation.game.components.witch

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
 * 女巫毒人选人层
 */
@Composable
fun WitchPoisonSelectOverlay(
    players: List<Player>,
    onSelectTarget: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(OverlayBackground),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "你要毒谁",
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(40.dp))

            // 复用之前的选人网格逻辑
            val rows = players.chunked(2)
            rows.forEach { rowPlayers ->
                Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                    rowPlayers.forEach { player ->
                        Button(
                            onClick = { onSelectTarget(player.id) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xffac8e68)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.size(width = 110.dp, height = 70.dp)
                        ) {
                            Text("${player.seatNumber}", fontSize = 28.sp, color = Color.White)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}