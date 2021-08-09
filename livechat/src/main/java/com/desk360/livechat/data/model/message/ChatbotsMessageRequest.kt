package com.desk360.livechat.data.model.message

import com.google.gson.annotations.SerializedName

data class ChatbotsMessageRequest(
    @SerializedName("session_key") val sessionKey: String,
    @SerializedName("message") val message: String,
    @SerializedName("language_code") val languageCode: String
)
