package com.desk360.livechat.data.model.chatsettings

import com.google.gson.annotations.SerializedName

data class Offline(
    @SerializedName("custom_fields") val customFields: List<CustomField>? = null,
    @SerializedName("header_text") val headerText: String? = null,
    @SerializedName("triggers_status") val triggersStatus: Boolean? = null
)