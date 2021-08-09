package com.desk360.livechat.manager

import com.desk360.base.manager.SharedPreferencesManager
import com.desk360.livechat.data.HeaderChatScreenModel
import java.util.*

object LiveChatSharedPrefManager {
    private const val IS_CHAT_BOT = "is_chat_bot"
    private const val AGENT = "agent"

    var agent: HeaderChatScreenModel
        get() = SharedPreferencesManager.instance?.readObject(
            AGENT,
            HeaderChatScreenModel::class.java
        ) ?: HeaderChatScreenModel()
        set(value) = SharedPreferencesManager.instance?.writeObject(
            AGENT,
            value
        )!!

    var isChatBot: Boolean
        get() = SharedPreferencesManager.instance?.read(IS_CHAT_BOT, false) == true
        set(value) = SharedPreferencesManager.instance?.writeObject(IS_CHAT_BOT, value)!!

    private fun isExpiredToken(): Boolean {
        val calendar = Calendar.getInstance()
        val date = calendar.time

        val diff = date.time - SharedPreferencesManager.lastLoginTime
        val diffHours: Long = diff / (60 * 60 * 1000)

        return diffHours > 23
    }

    fun isNeedRefreshToken(): Boolean {
        val calendar = Calendar.getInstance()
        val date = calendar.time

        val diff = date.time - SharedPreferencesManager.lastLoginTime
        val diffHours: Long = diff / (60 * 60 * 1000)

        return diffHours > 1
    }

    fun isNeedNewToken() = SharedPreferencesManager.token.isNullOrEmpty() || isExpiredToken()

    fun clearSession() {
        LiveChatFirebaseHelper.signOut()
        SharedPreferencesManager.token = ""
        SharedPreferencesManager.lastLoginTime = 0L
        agent = HeaderChatScreenModel()
    }
}