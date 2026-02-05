package com.example.app.presentation.chat

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * 玩家发言
 */
data class GameMessage(
    val id: Int,
    val playerName: String,
    val role: String, // 比如 "狼人", "村民", "预言家"
    val content: String,
    val isMe: Boolean
)


@Composable
fun ChatScreen(navigateBack: () -> Unit, modifier: Modifier = Modifier) {

    val myRole = "预言家"

    // 消息列表
    var messages by remember {
        mutableStateOf(
            listOf(
                GameMessage(1, "玩家A", "未知", "我是好人，昨晚平安夜。", false),
                GameMessage(2, "玩家B", "狼人", "我觉得预言家应该出来带队。", false),
                GameMessage(3, "我", myRole, "今晚我验一下玩家B。", true)
            )
        )
    }

    // 输入框状态
    var inputText by remember { mutableStateOf("") }

    val context = LocalContext.current


    // 列表滚动状态
    val listState = rememberLazyListState()

    val coroutineScope = rememberCoroutineScope()

    // 角色颜色
    fun getRoleColor(role: String): Color {
        return when (role) {
            "狼人" -> Color(0xFFFF5252) // 红色
            "预言家" -> Color(0xFF448AFF) // 蓝色
            "村民" -> Color.Gray
            else -> Color.Gray
        }
    }

    Scaffold(
        topBar = {
            ChatTopBar()
        },
        bottomBar = {
            ChatBottomBar(
                onSendClick = { message ->
                    Toast.makeText(context, "发送成功", Toast.LENGTH_SHORT).show()
//                    if (message.isNotBlank()) {
//                        // 发送消息逻辑
//                        messages = messages + GameMessage(
//                            messages.size + 1,
//                            "我",
//                            myRole,
//                            inputText,
//                            true
//                        )
//                        inputText = ""
//                        // 滚动到底部
//                        coroutineScope.launch {
//                            listState.animateScrollToItem(messages.size - 1)
//                        }
//                    }

//                    if (text.isNotBlank()) {
//                        messages =
//                            messages + GameMessage(messages.size + 1, "我", myRole, inputText, true)
//                        inputText = ""
//                        coroutineScope.launch {
//                            listState.animateScrollToItem(messages.size - 1)
//                        }
//                    }
                },
                text = inputText,
                onTextChange = {
                    inputText = it
                }
            )
        }
    ) { paddingValues ->
        // 发言列表
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(messages) { msg ->
                MessageItem(msg, getRoleColor(msg.role))
            }
        }

        // 初始滚动到底部
        LaunchedEffect(key1 = messages.size) {
            if (messages.isNotEmpty()) {
                listState.animateScrollToItem(messages.size - 1)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ChatTopBar() {
    TopAppBar(
        title = {
            Text("房间: 666号 (第2天)", fontWeight = FontWeight.Bold)
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    )
}

@Composable
private fun ChatBottomBar(
    text: String,
    onTextChange: (String) -> Unit,
    onSendClick: (String) -> Unit,
) {
    Surface(
        shadowElevation = 8.dp,
        tonalElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .height(IntrinsicSize.Min),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = text,
                onValueChange = onTextChange,
                modifier = Modifier.weight(1f),
                placeholder = { Text("请输入发言内容...") },
                shape = RoundedCornerShape(24.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                maxLines = 4,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                keyboardActions = KeyboardActions(
                    onSend = {
                        onSendClick(text)
                    }
                )
            )

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                onClick = { onSendClick(text) },
                modifier = Modifier
                    .size(48.dp)
                    .background(MaterialTheme.colorScheme.primary, CircleShape)
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.Send,
                    contentDescription = "发送",
                    tint = Color.White
                )
            }
        }
    }
}


@Composable
fun MessageItem(msg: GameMessage, roleColor: Color) {
    val isMe = msg.isMe

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isMe) Arrangement.End else Arrangement.Start
    ) {
        if (!isMe) {
            // 对方头像（简单用首字母代替）
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(roleColor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    msg.playerName.first().toString(),
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
        }

        Column(
            modifier = Modifier.widthIn(max = 260.dp)
        ) {
            // 玩家名字和身份标签
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = msg.playerName,
                    fontSize = 12.sp,
                    color = if (isMe) MaterialTheme.colorScheme.primary else Color.Gray
                )
                Spacer(modifier = Modifier.width(4.dp))
                Surface(
                    color = roleColor.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = msg.role,
                        fontSize = 10.sp,
                        color = roleColor,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // 消息气泡
            Box(
                modifier = Modifier
                    .background(
                        color = if (isMe) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(
                            topStart = 12.dp,
                            topEnd = 12.dp,
                            bottomStart = if (isMe) 12.dp else 2.dp,
                            bottomEnd = if (isMe) 2.dp else 12.dp
                        )
                    )
                    .padding(12.dp)
            ) {
                Text(
                    text = msg.content,
                    color = if (isMe) Color.White else Color.Black,
                    fontSize = 14.sp
                )
            }
        }

        if (isMe) {
            Spacer(modifier = Modifier.width(8.dp))
            // 我的头像
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(MaterialTheme.colorScheme.primary, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text("我", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}