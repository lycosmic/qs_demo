package com.example.app.presentation.game.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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

/**
 * 结果展示层
 */
@Composable
fun ResultOverlay(
    title: String, // "成功复活" 或 "他是好人"
    subContent: @Composable () -> Unit, // 中间的图片或头像
    titleColor: Color = Color.White,
    autoDismiss: Boolean = false, // 是否显示"停留2秒自动消失"
    onDismiss: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f)),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            if (autoDismiss) {
                Text("停留2秒自动消失", color = Color.LightGray, fontSize = 12.sp, modifier = Modifier.padding(bottom = 20.dp))
            }

            Text(title, color = titleColor, fontSize = 32.sp, fontWeight = FontWeight.Bold)

            Spacer(modifier = Modifier.height(32.dp))

            // 中间的内容插槽
            subContent()

            Spacer(modifier = Modifier.height(48.dp))

            if (!autoDismiss) {
                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                    modifier = Modifier.width(200.dp)
                ) {
                    Text("我知道了", color = Color.Black)
                }
            }
        }
    }
}