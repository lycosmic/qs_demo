package com.example.app.presentation.game.components.witch

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
 * 女巫行动层
 */
@Composable
fun WitchActionOverlay(
    deadPlayerId: String?, // 昨晚谁死了，null代表平安夜
    players: List<Player>, // 用于查座位号
    hasAntidote: Boolean,
    hasPoison: Boolean,
    onUseAntidote: () -> Unit,
    onUsePoison: () -> Unit,
    onSkip: () -> Unit
) {
    // 获取死者座位号
    val deadSeat = players.find { it.id == deadPlayerId }?.seatNumber

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(OverlayBackground),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // 1. 标题信息
            if (deadSeat != null) {
                Text(
                    text = "今晚${deadSeat}号死了",
                    color = Color.White,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold
                )
            } else {
                Text(
                    text = "今晚平安夜",
                    color = Color.White,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold
                )
            }


            Text(
                text = "解药、毒药要用吗",
                color = Color.LightGray,
                fontSize = 20.sp,
                modifier = Modifier.padding(top = 12.dp, bottom = 40.dp)
            )

            // 药水按钮行
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // 解药按钮 (仅当有人死且有药时可用)
                PotionButton(
                    label = "解药",
                    enabled = hasAntidote && deadSeat != null,
                    // TODO: 替换为解药图片 R.drawable.antidote
                    color = Color(0xFFFFF176), // 金色光晕
                    onClick = onUseAntidote
                )

                // 毒药按钮
                PotionButton(
                    label = "毒药",
                    enabled = hasPoison,
                    // TODO: 替换为毒药图片 R.drawable.poison
                    color = Color(0xFFBA68C8), // 紫色光晕
                    onClick = onUsePoison
                )
            }

            Spacer(modifier = Modifier.height(48.dp))

            // 不用按钮
            Button(
                onClick = onSkip,
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.size(width = 140.dp, height = 100.dp) // 方块大按钮
            ) {
                Text("不用", color = Color.Black, fontSize = 28.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

/**
 * 药水按钮
 */
@Composable
private fun PotionButton(label: String, enabled: Boolean, color: Color, onClick: () -> Unit) {
    // 模拟图片按钮
    Card(
        modifier = Modifier
            .size(110.dp)
            .clickable(enabled = enabled, onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = if(enabled) Color.DarkGray else Color.Black.copy(alpha=0.5f)),
        border = if(enabled) BorderStroke(2.dp, color) else null
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            // 现在用占位符代替
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.Face, // 占位图
                    contentDescription = null,
                    tint = if(enabled) color else Color.Gray,
                    modifier = Modifier.size(48.dp)
                )
                Text(label, color = Color.White, fontSize = 14.sp)
            }
        }
    }
}
