package com.example.data.remote

import com.example.data.model.GameStateDto
import com.example.data.model.VerifyResultDto
import com.example.data.remote.model.ApiResponse
import com.example.data.remote.model.ChatRequest
import com.example.data.remote.model.SkipActionRequest
import com.example.data.remote.model.StartGameRequest
import com.example.data.remote.model.TargetActionRequest
import com.example.data.remote.model.WitchSaveRequest
import retrofit2.http.Body
import retrofit2.http.POST


interface WerewolfApi {

    // --- 游戏流程控制 ---

    /**
     * 开始游戏
     * 返回：最新的游戏状态 GameStateDto (或者仅返回成功，状态通过 Socket 推送)
     */
    @POST("game/start")
    suspend fun startGame(
        @Body request: StartGameRequest
    ): ApiResponse<GameStateDto>

    /**
     * 发送聊天
     * 返回：Boolean (是否发送成功) 或 Unit
     */
    @POST("game/chat")
    suspend fun sendChat(
        @Body request: ChatRequest
    ): ApiResponse<Unit>

    // --- 角色技能接口 ---

    /**
     * 狼人刀人
     */
    @POST("game/action/wolf_kill")
    suspend fun wolfKill(
        @Body request: TargetActionRequest
    ): ApiResponse<Unit>

    /**
     * 女巫使用解药
     */
    @POST("game/action/witch_save")
    suspend fun witchSave(
        @Body request: WitchSaveRequest
    ): ApiResponse<Unit>

    /**
     * 女巫使用毒药
     */
    @POST("game/action/witch_poison")
    suspend fun witchPoison(
        @Body request: TargetActionRequest
    ): ApiResponse<Unit>

    /**
     * 女巫跳过/压药
     */
    @POST("game/action/witch_skip")
    suspend fun witchSkip(
        @Body request: SkipActionRequest
    ): ApiResponse<Unit>

    /**
     * 预言家验人
     * 注意：验人结果通常是即时返回的，或者通过 Socket 私聊推送。
     */
    @POST("game/action/seer_verify")
    suspend fun seerVerify(
        @Body request: TargetActionRequest
    ): ApiResponse<VerifyResultDto>

    // --- 公共接口 ---

    /**
     * 投票
     */
    @POST("game/vote")
    suspend fun votePlayer(
        @Body request: TargetActionRequest
    ): ApiResponse<Unit>
}