package com.example.equipotres.utils

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("MyAppSession", Context.MODE_PRIVATE)
    private val editor = prefs.edit()

    companion object {
        const val USER_EMAIL = "user_email"
    }

    fun saveUserEmail(email: String) {
        editor.putString(USER_EMAIL, email)
        editor.apply()
    }

    fun getUserEmail(): String? {
        return prefs.getString(USER_EMAIL, null)
    }

    fun logout() {
        editor.remove(USER_EMAIL)
        editor.apply()
    }

    fun isLoggedIn(): Boolean {
        return getUserEmail() != null
    }
}
