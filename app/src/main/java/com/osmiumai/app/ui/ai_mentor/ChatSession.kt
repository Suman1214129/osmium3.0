package com.osmiumai.app.ui.ai_mentor

data class ChatSession(
    val id: String,
    val title: String,
    val messages: MutableList<ChatMessage> = mutableListOf(),
    val createdAt: Long = System.currentTimeMillis()
)
