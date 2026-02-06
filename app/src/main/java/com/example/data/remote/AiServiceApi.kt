package com.example.data.remote

import com.example.data.remote.model.AiActionRequest
import com.example.data.remote.model.AiActionResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AiServiceApi {

    @POST("ai/action")
    suspend fun getAction(@Body request: AiActionRequest): Response<AiActionResponse>

    @POST("ai/speech")
    suspend fun getSpeech(@Body request: AiActionRequest): Response<AiActionResponse>
}