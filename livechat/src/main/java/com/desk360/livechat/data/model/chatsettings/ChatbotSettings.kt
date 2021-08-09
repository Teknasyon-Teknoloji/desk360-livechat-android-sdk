package com.desk360.livechat.data.model.chatsettings

import com.google.gson.annotations.SerializedName

data class ChatbotSettings(
    @SerializedName("languages") val languages: ArrayList<String>? = null,
    @SerializedName("default_languages") val defaultLanguages: String? = null
)
