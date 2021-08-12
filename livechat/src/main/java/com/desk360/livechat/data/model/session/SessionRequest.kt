package com.desk360.livechat.data.model.session

import com.google.gson.annotations.SerializedName

data class SessionRequest(
    val name: String? = "",
    val email: String? = "",
    @SerializedName("uuid") var deviceId: String? = "",
    @SerializedName("push_token") var pushToken: String? = "",
    private val source: String = "Android"
)