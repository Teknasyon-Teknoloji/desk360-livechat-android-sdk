package com.desk360.livechat.domain.repository

import com.desk360.livechat.manager.LiveChatFirebaseHelper

object FirebaseRepository {
    private val auth
        get() = requireNotNull(LiveChatFirebaseHelper.auth)

    val database
        get() = requireNotNull(LiveChatFirebaseHelper.database)


    fun signInWithCustomToken(token: String) = auth.signInWithCustomToken(token)

    fun signOut() {
        LiveChatFirebaseHelper.auth?.signOut()
    }

    fun session() = database.getReference("messages/${LiveChatFirebaseHelper.userId}/session")
    fun messages() = LiveChatFirebaseHelper.database?.getReference("messages/${LiveChatFirebaseHelper.userId}/${LiveChatFirebaseHelper.userId}")
    fun status(key:String) = LiveChatFirebaseHelper.database?.getReference("messages/${LiveChatFirebaseHelper.userId}/${LiveChatFirebaseHelper.userId}/$key/status")
    fun conversation() = LiveChatFirebaseHelper.database?.getReference("messages/${LiveChatFirebaseHelper.userId}")
}