package com.osmiumai.app

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

data class AppNotification(
    val id: String,
    val title: String,
    val message: String,
    val timestamp: Long,
    val isRead: Boolean = false,
    val type: String = "general",
    val iconName: String = "ic_phosphor_bell"
)

object NotificationStorageManager {
    private const val PREF_NAME = "OsmiumNotifications"
    private const val KEY_NOTIFICATIONS = "notifications"
    private val gson = Gson()
    
    private fun getPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }
    
    fun addNotification(context: Context, notification: AppNotification) {
        val notifications = getNotifications(context).toMutableList()
        notifications.add(0, notification)
        saveNotifications(context, notifications)
    }
    
    fun getNotifications(context: Context): List<AppNotification> {
        val json = getPreferences(context).getString(KEY_NOTIFICATIONS, null) ?: return emptyList()
        val type = object : TypeToken<List<AppNotification>>() {}.type
        return gson.fromJson(json, type)
    }
    
    fun clearAllNotifications(context: Context) {
        saveNotifications(context, emptyList())
    }
    
    fun markAllAsRead(context: Context) {
        val notifications = getNotifications(context).map { it.copy(isRead = true) }
        saveNotifications(context, notifications)
    }
    
    private fun saveNotifications(context: Context, notifications: List<AppNotification>) {
        val json = gson.toJson(notifications)
        getPreferences(context).edit().putString(KEY_NOTIFICATIONS, json).apply()
    }
}
