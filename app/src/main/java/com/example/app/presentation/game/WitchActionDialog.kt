package com.example.app.presentation.game

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.domain.model.Player

// presentation/game/components/WitchActionDialog.kt

@Composable
fun WitchActionDialog(
    killedPlayerId: String?,
    players: List<Player>,
    hasAntidote: Boolean,
    hasPoison: Boolean,
    onDismiss: () -> Unit,
    onAction: (String) -> Unit // 返回命令字符串，如 "SAVE:id"
) {
    // 找到被刀玩家的名字
    val killedName = players.find { it.id == killedPlayerId }?.seatNumber?.toString() ?: "未知"

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("女巫行动") },
        text = {
            Column {
                if (killedPlayerId != null) {
                    Text("昨晚 $killedName 号玩家被袭击了。", fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                } else {
                    Text("昨晚平安夜（逻辑上女巫不应该看到这个，除非空刀规则）。")
                }

                Text("你剩余药水：")
                Row {
                    Text("解药: ${if (hasAntidote) "✅" else "❌"}  ")
                    Text("毒药: ${if (hasPoison) "✅" else "❌"}")
                }
            }
        },
        confirmButton = {
            Column(modifier = Modifier.fillMaxWidth()) {
                // 1. 救人按钮
                if (hasAntidote && killedPlayerId != null) {
                    Button(
                        onClick = { onAction("SAVE:$killedPlayerId") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Green)
                    ) {
                        Text("使用解药 (救 $killedName 号)")
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                }

                // 2. 毒人按钮 (点击后还需要再选人，这里简化：点击弹出二级列表，或者先点击毒药再选人)
                // 为简化实现，我们这里仅展示逻辑：若要毒人，通常 UI 需变成选人列表。
                // 简单起见：我们在底部加一个“跳过”按钮，毒人逻辑复用通用的选人 Dialog
                // 这里我们做个特殊的处理：如果点击毒药，回调一个特殊标记，让主界面弹选人框
                if (hasPoison) {
                    Button(
                        onClick = { onAction("POISON_SELECT") }, // 让上层 UI 处理选人
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Magenta)
                    ) {
                        Text("使用毒药")
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                }

                // 3. 啥也不做
                Button(
                    onClick = { onAction("SKIP") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
                ) {
                    Text("什么都不做")
                }
            }
        }
    )
}