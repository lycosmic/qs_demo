package com.example.app.presentation.chat

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.domain.model.GamePhase
import com.example.domain.model.Role


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(viewModel: ChatViewModel = hiltViewModel()) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()
    val snackbarHostState = remember { SnackbarHostState() }

    // 自动滚动到底部
    LaunchedEffect(uiState.messages.size) {
        if (uiState.messages.isNotEmpty()) {
            listState.animateScrollToItem(uiState.messages.size - 1)
        }
    }

    // 错误提示
    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearError()
        }
    }

    // 背景颜色动画
    val bgColor by animateColorAsState(
        targetValue = if (uiState.phase.name.startsWith("NIGHT")) Color(0xFF121212) else Color(
            0xFFF0F0F0
        ),
        label = "BgColor"
    )

    Scaffold(
        containerColor = bgColor,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("房间: ${uiState.roomId}")
                        Text(
                            text = "阶段: ${uiState.phase} | 身份: ${uiState.myRole}",
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = bgColor.copy(alpha = 0.8f),
                    titleContentColor = if (uiState.phase.name.startsWith("NIGHT")) Color.White else Color.Black
                )
            )
        },
        bottomBar = {
            if (uiState.phase == GamePhase.WAITING) {
                Button(
                    onClick = { viewModel.startGame() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text("开始游戏")
                }
            } else {
                GameBottomBar(
                    uiState = uiState,
                    onSendChat = viewModel::sendMessage,
                    onActionClick = { viewModel.toggleActionDialog(true) }
                )
            }
        }
    ) { padding ->
        // --- 聊天列表 ---
        LazyColumn(
            state = listState,
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            contentPadding = PaddingValues(16.dp)
        ) {
            items(uiState.messages) { msg ->
                ChatBubble(message = msg, isMe = msg.senderId == uiState.myId)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        // --- 动作弹窗 (刀人/验人) ---
        if (uiState.showActionDialog) {
            val title = when (uiState.myRole) {
                Role.WOLF -> "选择袭击目标"
                Role.SEER -> "选择查验目标"
                Role.WITCH -> "选择用药目标"
                else -> "选择目标"
            }
            // 过滤：只能对活着的人操作 (根据规则可以细化，比如女巫救人可能要看死人)
            val targets = uiState.activePlayers.filter { !it.isMe } // 通常不能对自己操作(除了特殊规则)

            TargetSelectionDialog(
                players = targets,
                title = title,
                onDismiss = { viewModel.toggleActionDialog(false) },
                onConfirm = { id -> viewModel.onTargetSelected(id) }
            )
        }

        // --- 预言家结果弹窗 ---
        uiState.seerResult?.let { result ->
            AlertDialog(
                onDismissRequest = { viewModel.dismissSeerDialog() },
                title = { Text("查验结果") },
                text = {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // 这里可以用 Icon
                        Text(
                            if (result.isGood) "👍" else "🐺",
                            style = MaterialTheme.typography.displayMedium
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "${result.targetPlayerId} 号玩家是",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text = if (result.isGood) "好人" else "狼人",
                            style = MaterialTheme.typography.headlineMedium,
                            color = if (result.isGood) Color.Green else Color.Red,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                confirmButton = {
                    Button(onClick = { viewModel.dismissSeerDialog() }) {
                        Text("知道了")
                    }
                }
            )
        }
    }
}

//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//private fun ChatTopBar() {
//    TopAppBar(
//        title = {
//            Text("房间: 666号 (第2天)", fontWeight = FontWeight.Bold)
//        },
//        colors = TopAppBarDefaults.topAppBarColors(
//            containerColor = MaterialTheme.colorScheme.primaryContainer,
//            titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
//        )
//    )
//}
//
//@Composable
//private fun ChatBottomBar(
//    text: String,
//    onTextChange: (String) -> Unit,
//    onSendClick: (String) -> Unit,
//) {
//    Surface(
//        shadowElevation = 8.dp,
//        tonalElevation = 2.dp
//    ) {
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(8.dp)
//                .height(IntrinsicSize.Min),
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            TextField(
//                value = text,
//                onValueChange = onTextChange,
//                modifier = Modifier.weight(1f),
//                placeholder = { Text("请输入发言内容...") },
//                shape = RoundedCornerShape(24.dp),
//                colors = TextFieldDefaults.colors(
//                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
//                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
//                    focusedIndicatorColor = Color.Transparent,
//                    unfocusedIndicatorColor = Color.Transparent
//                ),
//                maxLines = 4,
//                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
//                keyboardActions = KeyboardActions(
//                    onSend = {
//                        onSendClick(text)
//                    }
//                )
//            )
//
//            Spacer(modifier = Modifier.width(8.dp))
//
//            IconButton(
//                onClick = { onSendClick(text) },
//                modifier = Modifier
//                    .size(48.dp)
//                    .background(MaterialTheme.colorScheme.primary, CircleShape)
//            ) {
//                Icon(
//                    Icons.AutoMirrored.Filled.Send,
//                    contentDescription = "发送",
//                    tint = Color.White
//                )
//            }
//        }
//    }
//}
//
//
//@Composable
//fun MessageItem(msg: GameMessage, roleColor: Color) {
//    val isMe = msg.isMe
//
//    Row(
//        modifier = Modifier.fillMaxWidth(),
//        horizontalArrangement = if (isMe) Arrangement.End else Arrangement.Start
//    ) {
//        if (!isMe) {
//            // 对方头像（简单用首字母代替）
//            Box(
//                modifier = Modifier
//                    .size(40.dp)
//                    .background(roleColor, CircleShape),
//                contentAlignment = Alignment.Center
//            ) {
//                Text(
//                    msg.playerName.first().toString(),
//                    color = Color.White,
//                    fontWeight = FontWeight.Bold
//                )
//            }
//            Spacer(modifier = Modifier.width(8.dp))
//        }
//
//        Column(
//            modifier = Modifier.widthIn(max = 260.dp)
//        ) {
//            // 玩家名字和身份标签
//            Row(verticalAlignment = Alignment.CenterVertically) {
//                Text(
//                    text = msg.playerName,
//                    fontSize = 12.sp,
//                    color = if (isMe) MaterialTheme.colorScheme.primary else Color.Gray
//                )
//                Spacer(modifier = Modifier.width(4.dp))
//                Surface(
//                    color = roleColor.copy(alpha = 0.2f),
//                    shape = RoundedCornerShape(4.dp)
//                ) {
//                    Text(
//                        text = msg.role,
//                        fontSize = 10.sp,
//                        color = roleColor,
//                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
//                    )
//                }
//            }
//
//            Spacer(modifier = Modifier.height(4.dp))
//
//            // 消息气泡
//            Box(
//                modifier = Modifier
//                    .background(
//                        color = if (isMe) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
//                        shape = RoundedCornerShape(
//                            topStart = 12.dp,
//                            topEnd = 12.dp,
//                            bottomStart = if (isMe) 12.dp else 2.dp,
//                            bottomEnd = if (isMe) 2.dp else 12.dp
//                        )
//                    )
//                    .padding(12.dp)
//            ) {
//                Text(
//                    text = msg.content,
//                    color = if (isMe) Color.White else Color.Black,
//                    fontSize = 14.sp
//                )
//            }
//        }
//
//        if (isMe) {
//            Spacer(modifier = Modifier.width(8.dp))
//            // 我的头像
//            Box(
//                modifier = Modifier
//                    .size(40.dp)
//                    .background(MaterialTheme.colorScheme.primary, CircleShape),
//                contentAlignment = Alignment.Center
//            ) {
//                Text("我", color = Color.White, fontWeight = FontWeight.Bold)
//            }
//        }
//    }
//}