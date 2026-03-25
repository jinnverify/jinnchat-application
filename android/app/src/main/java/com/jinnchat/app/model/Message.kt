package com.jinnchat.app.model

data class Message(
    val id: String = System.currentTimeMillis().toString(),
    val text: String,
    val isSent: Boolean,
    val isSystem: Boolean = false,
    val timestamp: String = java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault()).format(java.util.Date())
)
