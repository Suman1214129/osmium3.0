package com.osmiumai.app.ui.ai_mentor

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

object ChatHistoryManager {

    private const val PREF_NAME = "chat_history"
    private const val KEY_SESSIONS = "sessions"
    private const val MAX_SESSIONS = 30

    fun saveSessions(context: Context, sessions: List<ChatSession>) {
        val arr = JSONArray()
        sessions.takeLast(MAX_SESSIONS).forEach { session ->
            val obj = JSONObject().apply {
                put("id", session.id)
                put("title", session.title)
                put("createdAt", session.createdAt)
                val msgs = JSONArray()
                session.messages.forEach { msg ->
                    msgs.put(JSONObject().apply {
                        put("text", msg.text)
                        put("isUser", msg.isUser)
                        put("timestamp", msg.timestamp)
                    })
                }
                put("messages", msgs)
            }
            arr.put(obj)
        }
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .edit().putString(KEY_SESSIONS, arr.toString()).apply()
    }

    fun loadSessions(context: Context): MutableList<ChatSession> {
        val json = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .getString(KEY_SESSIONS, null) ?: return mutableListOf()
        return try {
            val arr = JSONArray(json)
            val list = mutableListOf<ChatSession>()
            for (i in 0 until arr.length()) {
                val obj = arr.getJSONObject(i)
                val msgs = mutableListOf<ChatMessage>()
                val msgsArr = obj.getJSONArray("messages")
                for (j in 0 until msgsArr.length()) {
                    val m = msgsArr.getJSONObject(j)
                    msgs.add(ChatMessage(m.getString("text"), m.getBoolean("isUser"), m.getLong("timestamp")))
                }
                list.add(ChatSession(obj.getString("id"), obj.getString("title"), msgs, obj.getLong("createdAt")))
            }
            list.reversed().toMutableList()
        } catch (e: Exception) {
            mutableListOf()
        }
    }

    fun clearAll(context: Context) {
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit().clear().apply()
    }
}
