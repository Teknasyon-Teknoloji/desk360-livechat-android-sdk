package com.desk360.livechat.data.model.chatsettings

import com.google.gson.annotations.SerializedName

data class Feedback(
    @SerializedName("bottom_link") val bottomLink: String? = null,
    @SerializedName("bottom_link_color") val bottomLinkColor: String? = null,
    @SerializedName("bottom_text") val bottomText: String? = null,
    @SerializedName("header_text") val headerText: String? = null,
    @SerializedName("header_title") val headerTitle: String? = null,
    @SerializedName("icon_down_color") val iconDownColor: String? = null,
    @SerializedName("icon_up_color") val iconUpColor: String? = null
)