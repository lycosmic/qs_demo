package com.example.app.presentation.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.model.GamePhase
import com.example.domain.model.Role
import com.example.domain.usecase.base.ObserveGameStateUseCase
import com.example.domain.usecase.base.SendMessageUseCase
import com.example.domain.usecase.base.StartGameUseCase
import com.example.domain.usecase.base.VotePlayerUseCase
import com.example.domain.usecase.seer.SeerVerifyUseCase
import com.example.domain.usecase.wolf.WolfKillUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val observeGameStateUseCase: ObserveGameStateUseCase,
    private val startGameUseCase: StartGameUseCase,
    private val sendMessageUseCase: SendMessageUseCase,
    private val wolfKillUseCase: WolfKillUseCase,
    private val seerVerifyUseCase: SeerVerifyUseCase,
    private val votePlayerUseCase: VotePlayerUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    init {
        observeGame()
    }

    private fun observeGame() {
        viewModelScope.launch {
            observeGameStateUseCase()
                .collect { state ->
                    _uiState.update { current ->
                        current.copy(
                            roomId = state.roomId,
                            myId = state.myId,
                            phase = state.phase,
                            players = state.players,
                            messages = state.messages,
                            // 如果有验人结果，自动更新到 State 以触发弹窗
                            seerResult = state.nightActionInfo?.seerResult
                        )
                    }
                }
        }
    }

    // --- 用户交互事件 ---

    fun startGame() {
        viewModelScope.launch {
            try {
                startGameUseCase()
            } catch (e: Exception) {
                showError(e.message)
            }
        }
    }

    fun sendMessage(content: String) {
        viewModelScope.launch {
            sendMessageUseCase(content) // 错误处理省略
        }
    }

    // 打开/关闭 选人弹窗
    fun toggleActionDialog(show: Boolean) {
        _uiState.update { it.copy(showActionDialog = show) }
    }

    // 关闭预言家结果弹窗
    fun dismissSeerDialog() {
        // 注意：这里可能需要通知 Repository 清除 nightActionInfo，或者本地暂时隐藏
        // 简单处理：我们只负责 UI 层的隐藏，如果不依赖后端清除，下次重组可能还会出来。
        // 理想做法是发个 action 给后端 "ACK_SEER_RESULT"，这里简化为本地置空 UI 绑定
        _uiState.update { it.copy(seerResult = null) }
    }

    // 执行目标动作 (刀/验/投)
    fun onTargetSelected(targetId: String) {
        viewModelScope.launch {
            try {
                toggleActionDialog(false) // 先关弹窗

                val role = uiState.value.myRole
                val phase = uiState.value.phase

                when {
                    phase == GamePhase.NIGHT_WOLF && role == Role.WOLF ->
                        wolfKillUseCase(targetId)

                    phase == GamePhase.NIGHT_SEER && role == Role.SEER ->
                        seerVerifyUseCase(targetId) // 结果会通过 Flow 回来，或者直接弹窗

                    phase == GamePhase.DAY_VOTING ->
                        votePlayerUseCase(targetId)
                }
            } catch (e: Exception) {
                showError(e.message)
            }
        }
    }

    private fun showError(msg: String?) {
        _uiState.update { it.copy(errorMessage = msg) }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}