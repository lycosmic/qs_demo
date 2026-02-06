package com.example.app.presentation.game

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.domain.model.GamePhase
import com.example.domain.model.GameState

@Composable
fun GameBottomBar(
    uiState: GameState,
    onSendChat: (String) -> Unit,
    onActionClick: () -> Unit // 点击技能按钮
) {
    var text by remember { mutableStateOf("") }
    val isNight = uiState.phase.name.startsWith("NIGHT")
    val canChat =
        uiState.phase == GamePhase.DAY_DISCUSSION
//                || uiState.phase == GamePhase.DAY_VOTING // 简单规则：白天能聊
    val myRole = uiState.players.find {
        it.isMe
    }

    // 是否轮到我行动 (且不是聊天阶段)
//    val isActionPhase = uiState.isMyTurn(myRole) && !canChat

    Surface(
        shadowElevation = 8.dp,
        color = if (isNight) Color(0xFF2C2C2C) else Color.White
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (canChat) {
                // --- 聊天输入框 ---
                TextField(
                    value = text,
                    onValueChange = {
                        text = it
                    },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("发言...") },
                    singleLine = true
                )
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = {
                        if (text.isNotBlank()) {
                            onSendChat(text)
                            text = ""
                        }
                    },
                    enabled = text.isNotBlank()
                ) {
                    Text("发送")
                }
            }

//            else if (isActionPhase) {
//                // --- 技能按钮 ---
//                val actionText = when {
//                    uiState.phase == GamePhase.DAY_VOTING -> "投票"
//                    uiState.myRole == Role.WOLF -> "🔪 袭击玩家"
//                    uiState.myRole == Role.SEER -> "🔮 查验身份"
//                    uiState.myRole == Role.WITCH -> "🧪 使用药水" // 简化，暂只处理一个按钮
//                    else -> "行动"
//                }
//
//                Button(
//                    onClick = onActionClick,
//                    modifier = Modifier.fillMaxWidth(),
//                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
//                ) {
//                    Text(actionText, style = MaterialTheme.typography.titleMedium)
//                }
//            }
            else {
                // --- 等待提示 ---
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Text(
                        text = if (isNight) "🌙 长夜漫漫，请保持安静..." else "等待其他玩家...",
                        color = Color.Gray
                    )
                }
            }
        }
    }
}