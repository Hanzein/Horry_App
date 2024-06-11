package com.farhanadi.horryapp.preferences_manager

import android.content.Context
import androidx.fragment.app.FragmentActivity

class UserManager(context: FragmentActivity) {
    private val preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun setToken(token: String?) {
        preferences.edit().putString(TOKEN, token).apply()
    }

    fun getToken(): String? {
        return preferences.getString(TOKEN, null)
    }

    companion object {
        private const val PREFS_NAME = "pref"
        private const val TOKEN = "token"
    }
}
