package com.example.data.repository

import com.example.data.remote.WerewolfApi
import com.example.data.remote.model.StartGameRequest
import com.example.data.remote.model.TargetActionRequest
import com.example.domain.model.GameState
import com.example.domain.model.SeerVerificationResult
import com.example.domain.repository.WerewolfRepository
import kotlinx.coroutines.flow.Flow


class NetworkWerewolfRepository(
    private val api: WerewolfApi,
) : WerewolfRepository {

    private var _currentRoomId: String? = null

    private val currentRoomId: String
        get() = _currentRoomId ?: throw IllegalStateException("尚未加入任何游戏房间，无法执行操作！")

    override fun observeGameState(): Flow<GameState> {
        TODO("Not yet implemented")
    }

    override fun observeErrors(): Flow<Throwable> {
        TODO("Not yet implemented")
    }

    override suspend fun connectToGame(roomId: String) {
        TODO("Not yet implemented")
    }

    override suspend fun startGame() {
        val request = StartGameRequest(roomId = "NEW_ROOM")
        val response = api.startGame(request)

        if (response.isSuccess() && response.data != null) {
            // 获取房间 ID
            _currentRoomId = response.data.roomId

            // 初始化 WebSocket 连接
        } else {
            throw Exception(response.message)
        }
    }

    override suspend fun sendChatMessage(content: String) {
        TODO("Not yet implemented")
    }

    override suspend fun actionWolfKill(targetPlayerId: String) {
        val request = TargetActionRequest(roomId = currentRoomId, targetPlayerId = targetPlayerId)
        val response = api.wolfKill(request)

        if (!response.isSuccess()) {
            throw Exception(response.message)
        }
        // 如果成功，就等待 WebSocket 推送，更新 GameState
    }

    override suspend fun actionWitchSave(targetPlayerId: String) {
        TODO("Not yet implemented")
    }

    override suspend fun actionWitchPoison(targetPlayerId: String) {
        TODO("Not yet implemented")
    }

    override suspend fun actionWitchSkip() {
        TODO("Not yet implemented")
    }

    override suspend fun actionSeerVerify(targetPlayerId: String): SeerVerificationResult {
        val response =
            api.seerVerify(
                TargetActionRequest(
                    roomId = currentRoomId,
                    targetPlayerId = targetPlayerId
                )
            )
        if (response.isSuccess() && response.data != null) {
            val isGood = response.data.isGood
            return SeerVerificationResult(targetPlayerId, isGood)
        } else {
            throw Exception(response.message)
        }
    }

    override suspend fun actionVote(targetPlayerId: String) {
        TODO("Not yet implemented")
    }

}