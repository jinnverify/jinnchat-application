package com.jinnchat.app.websocket

import android.util.Log
import com.google.gson.Gson
import com.jinnchat.app.model.ChatMessage
import okhttp3.*
import java.util.concurrent.TimeUnit

class CustomWebSocketListener(private val onMessageReceived: (ChatMessage) -> Unit) : okhttp3.WebSocketListener() {
    override fun onOpen(webSocket: WebSocket, response: Response) {
        Log.d("WebSocket", "Connected to server")
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        Log.d("WebSocket", "Received: $text")
        try {
            val message = Gson().fromJson(text, ChatMessage::class.java)
            onMessageReceived(message)
        } catch (e: Exception) {
            Log.e("WebSocket", "Error parsing message: ${e.message}")
        }
    }

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        Log.d("WebSocket", "Closing: $reason")
        webSocket.close(1000, null)
    }

    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
        Log.d("WebSocket", "Closed")
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        Log.e("WebSocket", "Error: ${t.message}")
    }
}

class WebSocketManager(private val serverUrl: String) {
    private var webSocket: WebSocket? = null
    private val client = OkHttpClient.Builder()
        .readTimeout(0, TimeUnit.MILLISECONDS)
        .connectTimeout(10, TimeUnit.SECONDS)
        .build()

    private val gson = Gson()
    var isConnected = false
        private set

    fun connect(listener: CustomWebSocketListener) {
        val request = Request.Builder()
            .url(serverUrl)
            .build()

        webSocket = client.newWebSocket(request, listener)
    }

    fun sendMessage(type: String, text: String? = null, isTyping: Boolean? = null) {
        val message = ChatMessage(type = type, text = text, isTyping = isTyping)
        webSocket?.send(gson.toJson(message))
    }

    fun findPartner() {
        sendMessage("find_partner")
    }

    fun sendChatMessage(text: String) {
        sendMessage("chat_message", text)
    }

    fun endChat() {
        sendMessage("end_chat")
    }

    fun sendTypingStatus(isTyping: Boolean) {
        sendMessage("typing", isTyping = isTyping)
    }

    fun disconnect() {
        webSocket?.close(1000, "User disconnected")
        isConnected = false
    }
}
