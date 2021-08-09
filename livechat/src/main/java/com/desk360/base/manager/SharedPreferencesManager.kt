package com.desk360.base.manager

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.google.gson.Gson
import com.desk360.livechat.manager.Desk360LiveChat
import java.lang.reflect.Type

class SharedPreferencesManager private constructor() {
    private val preferences: SharedPreferences = Desk360LiveChat.getContext()?.getSharedPreferences(
        PREFERENCE_NAME,
        Context.MODE_PRIVATE
    )!!
    private val gson: Gson = Gson()

    @SuppressLint("ApplySharedPref")
    internal fun setString(tag: String, value: String) {
        val editor = preferences.edit()
        editor.putString(tag, value)
        editor.commit()
    }

    internal fun getString(tag: String): String? {
        return preferences.getString(tag, null)
    }


    internal fun putObject(tag: String, targetObject: Any) {
        setString(tag, gson.toJson(targetObject))
    }

    private fun <T> getObject(key: String, type: Type? = null, clazz: Class<T>? = null): T? {
        if (preferences.contains(key)) {
            val preferenceTarget = preferences.getString(key, "")
            if (preferenceTarget != "") {
                return gson.fromJson<T>(preferenceTarget, type ?: clazz)
            }
        }
        return null
    }

    fun writeObject(key: String? = null, data: Any) {
        key?.let { safeKey ->
            write(safeKey, gson.toJson(data))
        } ?: write(data::class.java.simpleName, gson.toJson(data))
    }

    fun <T> readObject(key: String? = null, target: Class<T>): T? {
        key?.let { safeKey ->
            return gson.fromJson(read(safeKey, ""), target)
        } ?: return gson.fromJson(read(target.simpleName, ""), target) as T
    }

    fun <T> write(key: String, value: T) {
        when (value) {
            is String -> preferences.edit { putString(key, value).commit() }
            is Int -> preferences.edit { putInt(key, value).commit() }
            is Boolean -> preferences.edit { putBoolean(key, value).commit() }
            is Long -> preferences.edit { putLong(key, value).commit() }
            else -> Unit
        }
    }

    fun <T> read(key: String, defaultValue: T): T {
        return when (defaultValue) {
            is String -> preferences.getString(key, defaultValue as String) as? T ?: defaultValue
            is Int -> preferences.getInt(key, defaultValue as Int) as T ?: defaultValue
            is Boolean -> preferences.getBoolean(key, defaultValue as Boolean) as T ?: defaultValue
            is Long -> preferences.getLong(key, defaultValue as Long) as T ?: defaultValue
            else -> defaultValue
        }
    }

    fun clear(key: String) {
        preferences.edit { remove(key).clear().commit() }
    }

    companion object {
        private const val PREFERENCE_NAME = "chat_sdk"
        const val TOKEN = "TOKEN"
        const val USER_ID = "USER_ID"
        const val LAST_LOGIN_TIME = "first_login_time"
        const val USER_NAME = "user_name"
        const val USER_EMAIL_ADDRESS = "user_email_address"

        private var INSTANCE: SharedPreferencesManager? = null
        val instance: SharedPreferencesManager?
            get() {
                if (INSTANCE == null) {
                    INSTANCE = try {
                        SharedPreferencesManager()
                    } catch (ex: Exception) {
                        null
                    }
                }
                return INSTANCE
            }

        var token: String?
            get() = instance?.read(TOKEN, "")
            set(value) = instance?.write(TOKEN, value)!!

        var userId: String?
            get() = instance?.read(USER_ID, "")
            set(value) = instance?.write(USER_ID, value)!!

        var lastLoginTime: Long
            get() = try {
                instance?.readObject(LAST_LOGIN_TIME, Long::class.java) ?: 0L
            } catch (ex: Exception) {
                0L
            }
            set(value) = instance?.writeObject(LAST_LOGIN_TIME, value)!!

        var name: String?
            get() = instance?.read(USER_NAME, "")
            set(value) = instance?.write(
                USER_NAME,
                value
            )!!

        var emailAddress: String?
            get() = instance?.read(USER_EMAIL_ADDRESS, "")
            set(value) = instance?.write(
                USER_EMAIL_ADDRESS,
                value
            )!!

        fun setUser(newName: String?, email: String?) {
            name = newName
            emailAddress = email
        }
    }
}