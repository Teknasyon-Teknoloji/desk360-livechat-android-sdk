package com.desk360.livechat.data.model.chatsettings

import com.google.gson.annotations.SerializedName

data class Chat(
    @SerializedName("add_file_status") val addFileStatus: Boolean? = null,
    @SerializedName("button_text") val buttonText: String? = null,
    @SerializedName("feedback_message") val feedbackMessage: String? = null,
    @SerializedName("message_background_color") val messageBackgroundColor: String? = null,
    @SerializedName("message_text_color") val messageTextColor: String? = null,
    @SerializedName("placeholder_color") val placeholderColor: String? = null,
    @SerializedName("typing_status") val typingStatus: Boolean? = null,
    @SerializedName("welcome_message") val welcomeMessage: String? = null,
    @SerializedName("write_message_icon_color") val writeMessageIconColor: String? = null,
    @SerializedName("write_message_text_color") val writeMessageTextColor: String? = null
)
