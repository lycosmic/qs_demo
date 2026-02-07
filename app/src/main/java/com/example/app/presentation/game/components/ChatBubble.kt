package com.example.app.presentation.game.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.domain.model.ChatMessage


/**
 * 聊天气泡
 */
@Composable
fun ChatBubble(message: ChatMessage, isMe: Boolean) {
    val arrangement = if (isMe) Arrangement.End else Arrangement.Start

    val bubbleColor = if (isMe) Color(0xFFa2835e) else Color(0xFFFFFFFF) // 纯白
    val textColor = if (isMe) Color.White else Color(0xFF191919) // 深黑色字体

    // 气泡圆角
    val bubbleShape = RoundedCornerShape(8.dp)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp, horizontal = 12.dp),
        horizontalArrangement = arrangement,
        verticalAlignment = Alignment.Top // 头像和气泡顶部对齐
    ) {
        // --- 1. 别人的头像 (在左边) ---
        if (!isMe) {
            Avatar(isSystem = message.isSystemMessage())
            Spacer(modifier = Modifier.width(8.dp))
        }

        // --- 2. 消息气泡 ---
        // 限制气泡最大宽度，防止太宽
        Column(
            horizontalAlignment = if (isMe) Alignment.End else Alignment.Start,
            modifier = Modifier.weight(1f, fill = false) // fill=false 让气泡宽度自适应内容
        ) {
            // 显示发送者名字 (如果是系统法官，通常不显示名字，只看头像)
            if (!isMe && !message.isSystemMessage()) {
                Text(
                    text = message.senderName,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF999999),
                    modifier = Modifier.padding(bottom = 2.dp, start = 4.dp)
                )
            }

            Surface(
                shape = bubbleShape,
                color = bubbleColor,
                shadowElevation = 0.dp // 扁平化
            ) {
                Text(
                    text = message.content,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 15.sp,
                        lineHeight = 22.sp
                    ),
                    color = textColor
                )
            }
        }

        // --- 3. 自己的头像 (在右边) ---
        if (isMe) {
            Spacer(modifier = Modifier.width(8.dp))
            Avatar(isSystem = false)
        }
    }
}


/**
 * 头像组件
 * 根据名字或类型显示不同的图标/图片
 */
@Composable
fun Avatar(isSystem: Boolean) {
    // 头像大小
    val avatarSize = 40.dp

    val imageRes = when {
        isSystem -> R.drawable.avatar_judge
        else -> R.drawable.ic_launcher_background
    }

    Surface(
        shape = CircleShape,
        color = Color.White, // 头像底色
        modifier = Modifier.size(avatarSize)
    ) {
        // 如果有图片资源，使用 Image
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

//        // 普通玩家头像
//        Box(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(4.dp),
//            contentAlignment = Alignment.Center
//        ) {
//            Icon(
//                imageVector = Icons.Default.Person,
//                contentDescription = null,
//                tint = Color.Gray
//            )
//        }
    }
}