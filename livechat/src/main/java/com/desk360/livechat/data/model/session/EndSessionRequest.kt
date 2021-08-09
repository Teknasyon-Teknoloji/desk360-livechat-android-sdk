package com.desk360.livechat.data.model.session

import com.google.gson.annotations.SerializedName
import com.desk360.livechat.manager.LiveChatFirebaseHelper

data class EndSessionRequest(@SerializedName("session_firebase_key") var userId: String? = LiveChatFirebaseHelper.userId)
