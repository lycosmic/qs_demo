package com.example.app.presentation.chat

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.domain.model.ChatMessage
import com.example.domain.model.MessageType

@Composable
fun ChatBubble(message: ChatMessage, isMe: Boolean) {
    val alignment =
        if (message.type == MessageType.SYSTEM) Alignment.CenterHorizontally else if (isMe) Alignment.End else Alignment.Start

    val color = if (message.type == MessageType.SYSTEM) Color.Gray
    else if (isMe) Color(0xFFDCF8C6) else Color.White

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp),
        horizontalAlignment = alignment
    ) {
        if (message.type != MessageType.SYSTEM && !isMe) {
            // 发送者
            Text(
                text = message.senderName,
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray
            )
        }

        Surface(
            shape = RoundedCornerShape(8.dp),
            color = if (message.type == MessageType.SYSTEM) Color.LightGray.copy(alpha = 0.3f) else color,
            shadowElevation = 1.dp
        ) {
            Column {


                // 消息
                Text(
                    text = message.content,
                    modifier = Modifier.padding(8.dp),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}