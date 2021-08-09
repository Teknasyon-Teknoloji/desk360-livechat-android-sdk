package com.desk360.livechat.data.model.chatsettings

import com.google.gson.annotations.SerializedName

data class FirebaseConfig(
    @SerializedName("api_key") val apiKey: String? = null,
    @SerializedName("app_id") val appId: String? = null,
    @SerializedName("project_id") val projectId: String? = null,
    @SerializedName("database_url") val databaseUrl: String? = null,
    @SerializedName("messaging_sender_id") val messagingSenderId: String? = null,
    @SerializedName("aes_key") val aesKey: String? = null
)
