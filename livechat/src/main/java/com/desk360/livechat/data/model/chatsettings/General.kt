package com.desk360.livechat.data.model.chatsettings

import com.google.gson.annotations.SerializedName

data class General(
    @SerializedName("agent_picture_status") val agentPictureStatus: Boolean? = null,

    @SerializedName("background_color") val backgroundColor: String? = null,
    @SerializedName("background_header_color") val backgroundHeaderColor: String? = null,
    @SerializedName("background_main_color") val backgroundMainColor: String? = null,

    @SerializedName("brand_logo") val brandLogo: String? = null,
    @SerializedName("brand_name") val brandName: String? = "brand_name",

    @SerializedName("header_sub_title") val headerSubTitle: String? = "header_sub_title",
    @SerializedName("header_sub_title_color") val headerSubTitleColor: String? = "#FFF",

    @SerializedName("header_title") val headerTitle: String? = "header_title",
    @SerializedName("header_title_color") val headerTitleColor: String? = "#FFF",

    @SerializedName("section_header_text") val sectionHeaderText: String? = "section_header_text",
    @SerializedName("section_header_text_color") val sectionHeaderTextColor: String? = "#9b9b9b",

    @SerializedName("section_header_title") val sectionHeaderTitle: String? = "section_header_title",
    @SerializedName("section_header_title_color") val sectionHeaderTitleColor: String? = "#1a233b",

    @SerializedName("send_button_background_color") val sendButtonBackgroundColor: String? = "#3447d5",
    @SerializedName("send_button_icon_color") val sendButtonIconColor: String? = "#FFFFFF",
    @SerializedName("send_button_text") val sendButtonText: String? = "send_button_text",
    @SerializedName("send_button_text_color") val sendButtonTextColor: String? = "#FFFFFF",

    @SerializedName("widget_icon") val widgetIcon: String? = null
)