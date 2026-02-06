package com.example.app.presentation.game.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * 支持禁用状态的聊天栏
 */
@Composable
fun ChatBottomBar(
    enabled: Boolean,
    onSendChat: (String) -> Unit
) {
    var text by remember { mutableStateOf("") }

    // 整个底栏的背景色
    val barBackgroundColor = if (enabled) Color.White else Color(0xFFF5F5F5)
    // 输入框的背景色
    val inputBoxColor = if (enabled) Color(0xFFF0F0F0) else Color(0xFFE0E0E0)

    Surface(
        shadowElevation = 8.dp,
        color = barBackgroundColor,
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding() // 避开底部手势条
    ) {
        Row(
            modifier = Modifier
                // 底部留出 12dp，避免紧贴键盘
                .padding(start = 16.dp, end = 16.dp, top = 10.dp, bottom = 12.dp)
                .height(IntrinsicSize.Min),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BasicTextField(
                value = text,
                onValueChange = { if (enabled) text = it },
                enabled = enabled,
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp) // 强制高度 48dp
                    .background(inputBoxColor, RoundedCornerShape(24.dp)), // 自定义圆角背景
                textStyle = TextStyle(
                    fontSize = 16.sp,
                    color = Color.Black, // 必须手动指定颜色
                    textAlign = TextAlign.Start
                ),
                singleLine = true,
                cursorBrush = SolidColor(Color.Black), // 必须手动设置光标颜色
                // decorationBox 用于自定义输入框内部布局（占位符 + 内容）
                decorationBox = { innerTextField ->
                    Box(
                        contentAlignment = Alignment.CenterStart, // 强制内容垂直居中
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp) // 左右内边距
                    ) {
                        // 占位符逻辑
                        if (text.isEmpty()) {
                            Text(
                                text = if (enabled) "请发言" else "等待其他玩家发言...",
                                style = TextStyle(
                                    fontSize = 14.sp,
                                    color = Color.Gray,
                                    textAlign = TextAlign.Start
                                )
                            )
                        }
                        // 渲染实际输入内容
                        innerTextField()
                    }
                }
            )

            // 发送按钮
            if (enabled && text.isNotBlank()) {
                Spacer(modifier = Modifier.width(8.dp))
                TextButton(
                    onClick = {
                        if (text.isNotBlank()) {
                            onSendChat(text)
                            text = ""
                        }
                    },
                    contentPadding = PaddingValues(horizontal = 8.dp),
                    enabled = text.isNotBlank()
                ) {
                    Text("发送", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
        }
    }
}