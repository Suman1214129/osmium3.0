package com.osmiumai.app

import android.content.Context
import android.content.SharedPreferences

object SessionManager {
    private const val PREF_NAME = "OsmiumSession"
    private const val KEY_IS_LOGGED_IN = "isLoggedIn"
    
    private fun getPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }
    
    fun setLoggedIn(context: Context, isLoggedIn: Boolean) {
        getPreferences(context).edit().putBoolean(KEY_IS_LOGGED_IN, isLoggedIn).apply()
    }
    
    fun isLoggedIn(context: Context): Boolean {
        return getPreferences(context).getBoolean(KEY_IS_LOGGED_IN, false)
    }
    
    fun logout(context: Context) {
        getPreferences(context).edit().clear().apply()
    }
}
