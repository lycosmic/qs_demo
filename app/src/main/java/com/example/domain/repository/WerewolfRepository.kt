package com.example.domain.repository

import com.example.domain.model.GameRoom
import com.example.domain.model.NightActionResult
import com.example.domain.model.Player
import com.example.domain.model.Role
import com.example.domain.model.VoteRequest
import com.example.domain.model.VoteResult

interface WerewolfRepository {

    // 创建房间
    suspend fun createRoom(totalPlayers: Int, speakTime: Int): Result<GameRoom>

    // 加入房间并分配角色
    suspend fun assignRole(roomId: String, userRolePreference: Role?): Result<Player>

    // 获取房间状态（轮询使用）
    suspend fun getRoomStatus(roomId: String): Result<GameRoom>

    // 发送消息
    suspend fun sendMessage(roomId: String, content: String, roundNum: Int): Result<Unit>

    // 获取发言历史 (通常是 getRoomStatus 的一部分，也可单独获取)
    // suspend fun getMessages(roomId: String): Result<List<GameMessage>>

    // 执行夜间操作 (触发后端计算)
    suspend fun performNightAction(roomId: String, roundNum: Int): Result<NightActionResult>

    // 投票
    suspend fun submitVote(request: VoteRequest): Result<VoteResult>

    // 判定胜负
    suspend fun judgeWinner(roomId: String): Result<String>
}