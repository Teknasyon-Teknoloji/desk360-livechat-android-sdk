package com.desk360.livechat.domain.usecase

import com.desk360.base.manager.SharedPreferencesManager
import com.desk360.livechat.domain.repository.FirebaseRepository
import com.desk360.livechat.manager.LiveChatFirebaseHelper

object SignInWithTokenUseCase {
    fun execute(token: String) = FirebaseRepository.signInWithCustomToken(token)
        .addOnSuccessListener {
            SharedPreferencesManager.userId = LiveChatFirebaseHelper.auth?.currentUser?.uid
            SharedPreferencesManager.lastLoginTime =
                LiveChatFirebaseHelper.auth?.currentUser?.metadata?.lastSignInTimestamp ?: 0L
        }
}