package com.osmiumai.app.ui.ai_mentor

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import java.util.UUID

class AiMentorViewModel(app: Application) : AndroidViewModel(app) {

    private val _sessions = MutableLiveData<MutableList<ChatSession>>(mutableListOf())
    val sessions: LiveData<MutableList<ChatSession>> = _sessions

    private val _currentSession = MutableLiveData<ChatSession?>()
    val currentSession: LiveData<ChatSession?> = _currentSession

    private val _newMessage = MutableLiveData<ChatMessage>()
    val newMessage: LiveData<ChatMessage> = _newMessage

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    init {
        loadHistory()
    }

    private fun loadHistory() {
        val loaded = ChatHistoryManager.loadSessions(getApplication())
        _sessions.value = loaded
    }

    fun startNewSession() {
        _currentSession.value = null
    }

    fun loadSession(session: ChatSession) {
        _currentSession.value = session
    }

    fun sendMessage(userText: String, attachments: List<AttachmentItem> = emptyList()) {
        val userMsg = ChatMessage(userText, isUser = true, attachments = attachments)

        // Create session if none active
        if (_currentSession.value == null) {
            val title = userText.take(40).let { if (userText.length > 40) "$it…" else it }
            _currentSession.value = ChatSession(
                id = UUID.randomUUID().toString(),
                title = title
            )
        }

        _currentSession.value!!.messages.add(userMsg)
        _newMessage.value = userMsg
        _isLoading.value = true

        // Simulate AI response
        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
        val aiText = generateResponse(userText, attachments)
            val aiMsg = ChatMessage(aiText, isUser = false)
            _currentSession.value!!.messages.add(aiMsg)
            _newMessage.value = aiMsg
            _isLoading.value = false
            persistCurrentSession()
        }, 1000)
    }

    private fun persistCurrentSession() {
        val session = _currentSession.value ?: return
        val list = _sessions.value ?: mutableListOf()
        val existing = list.indexOfFirst { it.id == session.id }
        if (existing >= 0) list[existing] = session else list.add(0, session)
        _sessions.value = list
        ChatHistoryManager.saveSessions(getApplication(), list)
    }

    fun deleteSession(session: ChatSession) {
        val list = _sessions.value ?: return
        list.removeAll { it.id == session.id }
        _sessions.value = list
        ChatHistoryManager.saveSessions(getApplication(), list)
        if (_currentSession.value?.id == session.id) _currentSession.value = null
    }

    fun clearAllHistory() {
        _sessions.value = mutableListOf()
        _currentSession.value = null
        ChatHistoryManager.clearAll(getApplication())
    }

    private fun generateResponse(msg: String, attachments: List<AttachmentItem> = emptyList()): String {
        val images = attachments.count { it.type == AttachmentType.IMAGE }
        val files = attachments.filter { it.type == AttachmentType.FILE }

        if (images > 0 && msg.isEmpty()) return "**Image${if (images > 1) "s" else ""} Received**\n\nI can see the ${if (images > 1) "$images images" else "image"} you've shared. What would you like me to do?\n\n• Explain concepts shown\n• Solve problems in the image\n• Summarise the content"

        if (files.isNotEmpty() && msg.isEmpty()) {
            val names = files.joinToString(", ") { it.name }
            return "**File${if (files.size > 1) "s" else ""} Received**\n\nI've received: $names\n\nWhat would you like me to do with ${if (files.size > 1) "these files" else "this file"}?\n\n• Summarise the content\n• Answer questions from it\n• Explain key concepts"
        }

        if ((images > 0 || files.isNotEmpty()) && msg.isNotEmpty()) {
            val fileNames = files.joinToString(", ") { it.name }
            val context = buildString {
                if (images > 0) append("${if (images > 1) "$images images" else "an image"} ")
                if (files.isNotEmpty()) append("and files ($fileNames) ")
            }
            return "**Analysing your ${context.trim()}**\n\nBased on what you've shared and your question: \"$msg\"\n\n• I've reviewed the attached content\n• Here is my analysis and response\n• Feel free to ask follow-up questions"
        }

        return when {
        msg.contains("note", ignoreCase = true) || msg.contains("generate", ignoreCase = true) ->
            "**Notes Generated**\n\nHere are structured notes on your topic:\n\n• Key concept 1: Definition and explanation\n• Key concept 2: Important formulas\n• Key concept 3: Common applications\n\n**Summary:**\nThese notes cover the essential points for exam preparation."

        msg.contains("doubt", ignoreCase = true) || msg.contains("explain", ignoreCase = true) ->
            "**Clearing Your Doubt**\n\nGreat question! Let me break this down:\n\n1. First, understand the basic concept\n2. Apply the formula or principle\n3. Verify with an example\n\n**Key Takeaway:**\nPractice similar problems to reinforce understanding."

        msg.contains("math", ignoreCase = true) || msg.contains("solve", ignoreCase = true) || msg.contains("question", ignoreCase = true) ->
            "**Solution**\n\nLet me solve this step by step:\n\n**Step 1:** Identify what is given\n**Step 2:** Choose the right formula\n**Step 3:** Substitute values\n**Step 4:** Calculate and verify\n\n**Answer:** Please share the specific problem for a detailed solution."

        msg.contains("study plan", ignoreCase = true) || msg.contains("plan", ignoreCase = true) ->
            "**Your Study Plan**\n\n**Week 1-2:** Cover fundamentals\n• 2 hours daily theory\n• 1 hour practice problems\n\n**Week 3-4:** Deep dive topics\n• Focus on weak areas\n• Attempt mock tests\n\n**Week 5+:** Revision\n• Revise notes daily\n• Full-length tests"

        else ->
            "**Response**\n\nI understand your query: \"$msg\"\n\nHere's how I can help:\n\n• Generate structured notes on any topic\n• Clear doubts with step-by-step explanations\n• Solve maths and science problems\n• Create personalised study plans\n\nFeel free to ask anything specific!"
        }
    }
}
