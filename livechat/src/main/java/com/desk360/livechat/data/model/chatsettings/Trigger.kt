package com.desk360.livechat.data.model.chatsettings

import com.google.gson.annotations.SerializedName

data class Trigger(
    @SerializedName("answer") val answer: String? = null,
    @SerializedName("language_id") val languageId: Int? = null,
    @SerializedName("message") val message: String? = null,
    @SerializedName("time") val time: Int? = null,
    @SerializedName("type") val type: Int? = null
)