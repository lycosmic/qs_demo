package com.example.app.presentation.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.domain.model.Player

@Composable
fun TargetSelectionDialog(
    players: List<Player>,
    title: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            LazyColumn {
                items(players) { player ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onConfirm(player.id) }
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // 显示头像/号码
                        Box(
                            modifier = Modifier
                                .size(30.dp)
                                .background(Color.Blue, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("${player.seatNumber}", color = Color.White)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = if (player.isMe) "${player.name} (你)" else player.name,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                    HorizontalDivider()
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("取消") }
        }
    )
}