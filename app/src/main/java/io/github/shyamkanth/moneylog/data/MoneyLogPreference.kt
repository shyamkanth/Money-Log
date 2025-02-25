package io.github.shyamkanth.moneylog.data

import android.content.Context
import android.content.SharedPreferences

object MoneyLogPreference {
    private const val PREFERENCE_NAME = "moneylog_preferences"
    const val IS_LOGGED_IN = "isLoggedIn"
    const val USER_ID = "userId"

    private fun getPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)
    }

    fun setString(context: Context, key: String, value: String) {
        val editor = getPreferences(context).edit()
        editor.putString(key, value)
        editor.apply()
    }

    fun getString(context: Context, key: String, defaultValue: String = ""): String {
        return getPreferences(context).getString(key, defaultValue) ?: defaultValue
    }

    fun setInt(context: Context, key: String, value: Int) {
        val editor = getPreferences(context).edit()
        editor.putInt(key, value)
        editor.apply()
    }

    fun getInt(context: Context, key: String, defaultValue: Int = 0): Int {
        return getPreferences(context).getInt(key, defaultValue)
    }

    fun setBoolean(context: Context, key: String, value: Boolean) {
        val editor = getPreferences(context).edit()
        editor.putBoolean(key, value)
        editor.apply()
    }

    fun getBoolean(context: Context, key: String, defaultValue: Boolean = false): Boolean {
        return getPreferences(context).getBoolean(key, defaultValue)
    }

    fun setFloat(context: Context, key: String, value: Float) {
        val editor = getPreferences(context).edit()
        editor.putFloat(key, value)
        editor.apply()
    }

    fun getFloat(context: Context, key: String, defaultValue: Float = 0f): Float {
        return getPreferences(context).getFloat(key, defaultValue)
    }

    fun setLong(context: Context, key: String, value: Long) {
        val editor = getPreferences(context).edit()
        editor.putLong(key, value)
        editor.apply()
    }

    fun getLong(context: Context, key: String, defaultValue: Long = 0L): Long {
        return getPreferences(context).getLong(key, defaultValue)
    }

    fun remove(context: Context, key: String) {
        val editor = getPreferences(context).edit()
        editor.remove(key)
        editor.apply()
    }

    fun clear(context: Context) {
        val editor = getPreferences(context).edit()
        editor.clear()
        editor.apply()
    }
}
