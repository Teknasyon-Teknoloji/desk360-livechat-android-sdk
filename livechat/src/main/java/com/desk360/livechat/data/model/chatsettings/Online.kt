package com.desk360.livechat.data.model.chatsettings

import com.google.gson.annotations.SerializedName

data class Online(
    @SerializedName("header_text") val headerText: String? = null,
    @SerializedName("triggers_status") val triggersStatus: Boolean? = null
)