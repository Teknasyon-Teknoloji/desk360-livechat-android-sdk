package com.desk360.livechat.data.model.chatsettings

import com.google.gson.annotations.SerializedName

data class CustomField(
    @SerializedName("is_active") val isActive: Boolean? = null,
    val key: String,
    val options: Any? = null,
    val title: String? = null,
    val type: String? = null,
    var value: String? = null
)