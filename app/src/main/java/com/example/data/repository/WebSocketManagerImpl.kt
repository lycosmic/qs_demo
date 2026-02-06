package com.example.data.repository

import android.util.Log
import com.example.domain.repository.WebSocketManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import javax.inject.Inject

/**
 * WebSocket 管理器的实现
 */
class WebSocketManagerImpl @Inject constructor(
    private val okHttpClient: OkHttpClient
) : WebSocketManager {

    companion object {
        private const val TAG = "WebSocketManagerImpl"
    }

    private val _incomingMessages = MutableSharedFlow<String>(replay = 0) // replay=0 表示新订阅者不接收历史消息
    override val incomingMessages: Flow<String> = _incomingMessages

    private val _errors = MutableSharedFlow<Throwable>(replay = 0)
    override val errors: Flow<Throwable> = _errors

    private var currentSocket: WebSocket? = null

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)


    // 定义 WebSocket 监听器
    private val listener = object : WebSocketListener() {

        override fun onOpen(webSocket: WebSocket, response: Response) {
            Log.i(TAG, "onOpen: ${response.message}")
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            Log.i(TAG, "onMessage: $text")
            // 收到服务器消息 -> 发射到 Flow
            scope.launch {
                _incomingMessages.emit(text)
            }
        }

        override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
            Log.i(TAG, "onClosing: $code, $reason")
            webSocket.close(code, reason)
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            Log.e(TAG, "onFailure: ${t.message}")
            // 连接失败或发生异常 -> 发射错误
            scope.launch {
                _errors.emit(t)
            }
        }
    }

    override suspend fun connect(roomId: String) {
        // 1. 如果之前有连接，先关闭
        currentSocket?.cancel()

        // 2. 构建请求 URL
        val url = "ws://192.168.124.27:8000/game/ws/room/$roomId"

        val request = Request.Builder()
            .url(url)
            .build()

        // 3. 建立连接
        currentSocket = okHttpClient.newWebSocket(request, listener)
    }

    override suspend fun disconnect() {
        currentSocket?.close(1000, "User disconnect")
        currentSocket = null
    }
}