package com.example.app.presentation.game.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * 游戏顶部导航栏
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameTopBar(
    title: String,
    titleColor: Color = Color.Black,
    subTitle: String,
    isVotingEnabled: Boolean,
    containerColor: Color,
    onExit: () -> Unit,
    onVoteClick: () -> Unit // 投票
) {
    CenterAlignedTopAppBar(
        title = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = title,
                    color = titleColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Text(
                    text = subTitle,
                    color = Color.Gray,
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 11.sp
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = containerColor
        ),
        navigationIcon = {
            TextButton(onClick = onExit) {
                Text("结束", color = Color.Red, fontSize = 16.sp)
            }
        },
        actions = {
            val voteColor =
                if (isVotingEnabled) Color(0xFF2196F3) else Color.Gray.copy(alpha = 0.6f) // 蓝色 vs 灰色

            TextButton(onClick = onVoteClick, enabled = isVotingEnabled) {
                Text(
                    "投票",
                    color = voteColor,
                    fontSize = 16.sp,
                    fontWeight = if (isVotingEnabled) FontWeight.Bold else FontWeight.Normal
                )
            }
        }
    )
}