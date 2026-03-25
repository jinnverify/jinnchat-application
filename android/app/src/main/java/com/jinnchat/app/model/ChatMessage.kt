package com.jinnchat.app.model

data class ChatMessage(
    val type: String,
    val userId: String? = null,
    val message: String? = null,
    val text: String? = null,
    val partnerId: String? = null,
    val from: String? = null,
    val timestamp: String? = null,
    val count: Int? = null,
    val isTyping: Boolean? = null
)
