package com.desk360.livechat.data.model.message

import com.google.gson.annotations.SerializedName

data class MessageRequest(
    val name: String? = null,
    val email: String? = null,
    val message: String? = null,
    val source: String = "Android",
    @SerializedName("settings") var settings:Map<String,String>? = mapOf()
)
