package com.example.app.presentation.game.components.witch

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Face
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.app.ui.theme.OverlayBackground
import kotlinx.coroutines.delay

/**
 * 女巫反馈动画层
 */
@Composable
fun WitchFeedbackOverlay(
    type: String, // "SAVE" or "POISON"
    onDismiss: () -> Unit // 自动消失回调
) {
    val title = if (type == "SAVE") "成功复活" else "确定毒药人选"
    val icon = if (type == "SAVE") Icons.Default.Face else Icons.Default.Delete // TODO: 替换为对应药水图
    val iconTint = if (type == "SAVE") Color(0xFFFFF176) else Color(0xFFBA68C8)

    // 自动停留逻辑
    LaunchedEffect(Unit) {
        delay(2000)
        onDismiss()
    }

    Box(
        modifier = Modifier.fillMaxSize().background(OverlayBackground),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("停留2秒自动消失", color = Color.Gray, fontSize = 12.sp)
            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = title,
                color = Color.White,
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(40.dp))

            // 药水发光大图
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(180.dp)
            )
        }
    }
}