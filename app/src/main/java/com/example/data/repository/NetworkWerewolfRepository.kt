package com.example.data.repository

import android.util.Log
import com.example.data.model.GameStateDto
import com.example.data.model.mappers.toDomain
import com.example.data.remote.WerewolfApi
import com.example.data.remote.model.ChatRequest
import com.example.data.remote.model.SkipActionRequest
import com.example.data.remote.model.StartGameRequest
import com.example.data.remote.model.TargetActionRequest
import com.example.data.remote.model.WitchSaveRequest
import com.example.domain.model.GameState
import com.example.domain.model.SeerVerificationResult
import com.example.domain.repository.WebSocketManager
import com.example.domain.repository.WerewolfRepository
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject


/**
 * 真实的网络仓库实现
 */
class NetworkWerewolfRepository @Inject constructor(
    private val api: WerewolfApi,
    private val socketManager: WebSocketManager,
    private val gson: Gson
) : WerewolfRepository {

    private var _currentRoomId: String? = null

    private val currentRoomId: String
        get() = _currentRoomId ?: throw IllegalStateException("尚未加入任何游戏房间，无法执行操作！")

    private val currentUserId: String
        get() = "my_user_id"

    companion object {
        private const val TAG = "NetworkWerewolfRepository"
    }

    override fun observeGameState(): Flow<GameState> {
        return socketManager.incomingMessages.map { jsonString ->
            try {
                val dto = gson.fromJson(jsonString, GameStateDto::class.java)
                dto.toDomain(currentUserId)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }.filterNotNull()
    }

    override fun observeErrors(): Flow<Throwable> {
        return socketManager.errors
    }

    override suspend fun connectToGame(roomId: String) {
        _currentRoomId = roomId
        socketManager.connect(roomId)
    }

    override suspend fun startGame() {
        val request = StartGameRequest(roomId = "NEW_ROOM")
        val response = api.startGame(request)

        if (response.isSuccess() && response.data != null) {
            // 获取房间 ID
            val newRoomId = response.data
            _currentRoomId = newRoomId

            Log.d(TAG, "startGame: 成功加入房间 $newRoomId")

            // 初始化 WebSocket 连接
            socketManager.connect(newRoomId)
        } else {
            Log.e(TAG, "startGame: 创建房间失败")
            throw Exception(response.message)
        }
    }

    override suspend fun sendChatMessage(content: String) {
        val request = ChatRequest(roomId = currentRoomId, content = content)
        val response = api.sendChat(request)
        if (!response.isSuccess()) throw Exception(response.message)
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
        val request = WitchSaveRequest(roomId = currentRoomId, targetPlayerId = targetPlayerId)
        val response = api.witchSave(request)
        if (!response.isSuccess()) throw Exception(response.message)
    }

    override suspend fun actionWitchPoison(targetPlayerId: String) {
        val request = TargetActionRequest(roomId = currentRoomId, targetPlayerId = targetPlayerId)
        val response = api.witchPoison(request)
        if (!response.isSuccess()) throw Exception(response.message)
    }

    override suspend fun actionWitchSkip() {
        val request = SkipActionRequest(roomId = currentRoomId)
        val response = api.witchSkip(request)
        if (!response.isSuccess()) throw Exception(response.message)
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
            val targetPlayerId = response.data.targetId
            return SeerVerificationResult(targetPlayerId, isGood)
        } else {
            throw Exception(response.message)
        }
    }

    override suspend fun actionVote(targetPlayerId: String) {
        val request = TargetActionRequest(roomId = currentRoomId, targetPlayerId = targetPlayerId)
        val response = api.votePlayer(request)
        if (!response.isSuccess()) throw Exception(response.message)
    }

}