package com.example.app.presentation.game

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.app.presentation.game.components.ChatBottomBar
import com.example.app.presentation.game.components.ChatBubble
import com.example.app.presentation.game.components.GameTopBar
import com.example.app.presentation.game.components.NightBottomBar
import com.example.app.presentation.game.components.ResultOverlay
import com.example.app.presentation.game.components.RoleInfoCard
import com.example.app.presentation.game.components.SilenceBottomBar
import com.example.app.presentation.game.components.TurnNotificationPill
import com.example.app.presentation.game.components.VoteActionOverlay
import com.example.app.presentation.game.components.WelcomeBanner
import com.example.app.presentation.game.components.seer.SeerActionOverlay
import com.example.app.presentation.game.components.witch.WitchActionOverlay
import com.example.app.presentation.game.components.witch.WitchFeedbackOverlay
import com.example.app.presentation.game.components.witch.WitchPoisonSelectOverlay
import com.example.app.presentation.game.components.wolf.WolfActionOverlay
import com.example.domain.model.GamePhase
import com.example.domain.model.Player
import com.example.domain.model.Role
import kotlinx.coroutines.delay


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(viewModel: GameViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()

    // 滚动到最新
    LaunchedEffect(uiState.chatHistory.size) {
        listState.scrollToItem(uiState.chatHistory.size)
    }

    // 狼人是否确定人选
    var showWolfKillConfirmation by remember { mutableStateOf(false) }


    // --- 是否弹出狼人视角 ---
    val shouldShowWolfOverlay = uiState.phase == GamePhase.NIGHT_WOLF &&
            uiState.myRole == Role.WOLF &&
            (uiState.players.find { it.isMe }?.isAlive == true) &&
            !showWolfKillConfirmation

    // --- 是否弹出预言家视角 ---
    val shouldShowSeerOverlay = uiState.phase == GamePhase.NIGHT_SEER &&
            uiState.myRole == Role.SEER &&
            (uiState.players.find { it.isMe }?.isAlive == true) &&
            uiState.nightCache.seerVerifyTargetId == null

    // 判断是否是夜间模式
    val isNight = uiState.phase.name.startsWith("NIGHT")
    val bgColor = if (isNight) Color(0xff1c1c1e) else Color(0xfff2f1f6)

    // 判断底部显示什么：聊天框、禁言条、还是夜间提示
    val bottomContentType = when {
        uiState.phase == GamePhase.DAY_DISCUSSION -> "CHAT"
        isNight -> "NIGHT_TEXT"
        else -> "SILENCE" // 默认（包括初始 WAITING 阶段）显示禁言
    }

    // --- 判断是否轮到我发言 ---
    val isMyTurn = uiState.phase == GamePhase.DAY_DISCUSSION &&
            uiState.currentSpeakerId == uiState.myId

    // --- Toast 控制状态 ---
    var showTurnToast by remember { mutableStateOf(false) }

    // --- 投票逻辑状态 ---
    // 是否可以投票：阶段是 DAY_VOTING 且 我活着
    val canVote = uiState.phase == GamePhase.DAY_VOTING &&
            (uiState.players.find { it.isMe }?.isAlive == true)

    // 控制投票弹窗显示
    var showVoteOverlay by remember { mutableStateOf(false) }

    // 监听：当轮到我发言时，触发 Toast 显示，并在 2秒 后自动隐藏
    LaunchedEffect(uiState.currentSpeakerId) {
        if (uiState.currentSpeakerId == uiState.myId) {
            showTurnToast = true
            delay(2000) // 停留2秒
            showTurnToast = false
        }
    }


    // --- 女巫局部状态管理 ---
    // 0:不仅入, 1:主面板, 2:毒药选人, 3:解药反馈动画, 4:毒药反馈动画
    var witchSubPhase by remember { mutableIntStateOf(0) }

    // 监听全局阶段，自动激活女巫面板
    LaunchedEffect(uiState.phase, uiState.myRole) {
        witchSubPhase = if (uiState.phase == GamePhase.NIGHT_WITCH &&
            uiState.myRole == Role.WITCH &&
            uiState.players.find { it.isMe }?.isAlive == true
        ) {
            1 // 进入主面板
        } else {
            0 // 重置
        }
    }

    BackHandler(enabled = true) {
        return@BackHandler
    }
    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            containerColor = bgColor,
            topBar = {
                GameTopBar(
                    title = "狼人杀 (5人)",
                    subTitle = "内容由AI生成",
                    titleColor = if (isNight) Color.White else Color.Black,
                    containerColor = bgColor,
                    onExit = {
                        // TODO: 退出游戏
                    },
                    isVotingEnabled = canVote,
                    onVoteClick = {
                        Log.d("GameScreen", "点击了投票按钮")
                        // 点击右上角按钮，显示弹窗
                        showVoteOverlay = true
                    }
                )
            },
            bottomBar = {
                when (bottomContentType) {
                    "CHAT" -> ChatBottomBar(
                        enabled = isMyTurn,
                        onSendChat = { content ->
                            viewModel.onUserSpeech(content)
                        }
                    )

                    "NIGHT_TEXT" -> NightBottomBar("长夜漫漫，请保持安静~")
                    "SILENCE" -> SilenceBottomBar() // 显示禁言条
                }
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                // 聊天列表区域
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    item {
                        WelcomeBanner(
                            backgroundColor = if (isNight) Color(0xff29282b) else Color(0xffe9e8ed),
                        )
                    }

                    item {
                        // 身份卡片
                        RoleInfoCard(mySeat = uiState.mySeat, myRole = uiState.myRole)
                    }

                    items(uiState.chatHistory) { msg ->
                        ChatBubble(message = msg, isMe = msg.senderId == uiState.myId)
                    }
                }
            }

            // 女巫行动弹窗
            // 1. 主面板 (救/毒/过)
            AnimatedVisibility(
                visible = witchSubPhase == 1,
                enter = fadeIn(), exit = fadeOut()
            ) {
                // 排除不能救的情况：如果是自救规则限制 (根据五人本规则：女巫不可自救)
                val deadId = uiState.nightCache.wolfKillTargetId
                val isMeDead = deadId == uiState.myId
                // 根据规则判断是否能用解药：有药 + (没人死 OR 有人死且不是自己)
                val canUseAntidote = uiState.witchInventory.hasAntidote &&
                        deadId != null &&
                        !isMeDead

                WitchActionOverlay(
                    deadPlayerId = deadId,
                    players = uiState.players,
                    hasAntidote = canUseAntidote,
                    hasPoison = uiState.witchInventory.hasPoison,
                    onUseAntidote = {
                        // 点击解药 -> 播放反馈动画 -> 2秒后提交
                        witchSubPhase = 3
                    },
                    onUsePoison = {
                        // 点击毒药 -> 进入选人
                        witchSubPhase = 2
                    },
                    onSkip = {
                        // 点击不用 -> 直接提交
                        viewModel.onUserAction("SKIP")
                        witchSubPhase = 0
                    }
                )
            }

            // 2. 毒药选人面板
            AnimatedVisibility(
                visible = witchSubPhase == 2,
                enter = fadeIn(), exit = fadeOut()
            ) {
                // 排除自己和死人
                val targets = uiState.players.filter { !it.isMe && it.isAlive }

                WitchPoisonSelectOverlay(
                    players = targets,
                    onSelectTarget = { targetId ->
                        // 选中目标 -> 提交毒药指令 -> 播放反馈动画
                        viewModel.onUserAction("POISON:$targetId")
                        witchSubPhase = 4
                    }
                )
            }

            // 3. 解药反馈动画 (成功复活)
            if (witchSubPhase == 3) {
                WitchFeedbackOverlay(
                    type = "SAVE",
                    onDismiss = {
                        // 动画结束 -> 提交解药指令 -> 退出
                        val deadId = uiState.nightCache.wolfKillTargetId
                        if (deadId != null) {
                            viewModel.onUserAction("SAVE:$deadId")
                        }
                        witchSubPhase = 0
                    }
                )
            }

            // 预言家验人弹窗
            AnimatedVisibility(
                visible = shouldShowSeerOverlay,
                enter = fadeIn() + scaleIn(),
                exit = fadeOut()
            ) {
                // 筛选目标：活着的、非自己的玩家
                val targets = uiState.players.filter { !it.isMe && it.isAlive }

                SeerActionOverlay(
                    players = targets,
                    onVerify = { targetId ->
                        viewModel.onUserAction(targetId)
                    }
                )
            }

            // 预言家验人结果反馈
            if (uiState.nightCache.seerVerifyTargetId != null
                && uiState.myRole == Role.SEER
                && uiState.seerResult != null
            ) {
                uiState.seerResult?.let {
                    ResultOverlay(
                        title = if (it.isGood) "他是好人" else "他是狼人",
                        titleColor = if (it.isGood) Color(0xFFFBC46F) else Color(0xFFFF3B30),
                        onDismiss = {
                            viewModel.dismissSeerResult()
                        }, // 确认后清除弹窗
                        subContent = {
                            // 大头像
                            Icon(
                                Icons.Default.Person,
                                contentDescription = null,
                                tint = Color.LightGray,
                                modifier = Modifier.size(120.dp)
                            )
                        }
                    )
                }
            }

            // 狼人行动弹窗
            AnimatedVisibility(
                visible = shouldShowWolfOverlay,
                enter = fadeIn() + scaleIn(),
                exit = fadeOut()
            ) {
                // 筛选目标：活着的的玩家，包括自己
                val targets = uiState.players.filter { it.isAlive }

                WolfActionOverlay(
                    players = targets,
                    onSelectTarget = { targetId ->
                        // 1. 立即切换本地状态，让选人界面消失，进入“刀具展示”阶段
                        showWolfKillConfirmation = true

                        // 2. 通知 ViewModel 执行业务逻辑 (API请求等)
                        viewModel.onUserAction(targetId)
                    }
                )
            }

            //  狼人刀具展示
            if (showWolfKillConfirmation) {
                // 启动一个副作用：2秒后自动隐藏
                LaunchedEffect(Unit) {
                    delay(2000)
                    showWolfKillConfirmation = false
                }

                ResultOverlay(
                    title = "已确定人选",
                    titleColor = Color(0xFFFF3B30),
                    autoDismiss = true,
                    onDismiss = {},
                    subContent = {
                        // TODO: 显示刀具图标
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = Color.LightGray,
                            modifier = Modifier
                                .size(140.dp)
                                .rotate(45f)
                        )
                    }
                )
            }
        }

        // ================== 轮到你发言提示 (Toast 模式) ==================
        Box(Modifier.fillMaxSize()) {
            AnimatedVisibility(
                visible = showTurnToast, // 由临时状态控制
                enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                exit = fadeOut(), // 淡出消失
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 50.dp) // 稍微提得更高一点，完全避开输入框
            ) {
                TurnNotificationPill()
            }
        }

        // ================== 投票弹窗 (Overlay Layer) ==================
        AnimatedVisibility(
            visible = showVoteOverlay,
            enter = fadeIn() + scaleIn(),
            exit = fadeOut(),
            modifier = Modifier.zIndex(2f)
        ) {
            // 筛选目标：活着的、非自己的玩家
            val targets = uiState.players.filter { !it.isMe && it.isAlive }

            VoteActionOverlay(
                players = targets,
                onDismiss = { showVoteOverlay = false },
                onConfirmVote = { targetId ->
                    // 1. 关闭弹窗
                    showVoteOverlay = false

                    // 2. 提交投票
                    viewModel.onUserAction(targetId)
                }
            )
        }
    }

}

fun getSeatById(id: String, players: List<Player>): Int =
    players.find { it.id == id }?.seatNumber ?: 0

fun getIdBySeat(seat: Int, players: List<Player>): String =
    players.find { it.seatNumber == seat }?.id ?: ""

