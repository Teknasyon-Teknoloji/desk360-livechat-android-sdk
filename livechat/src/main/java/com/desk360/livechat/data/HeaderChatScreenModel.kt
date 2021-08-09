package com.desk360.livechat.data

import com.desk360.livechat.manager.LiveChatHelper

data class HeaderChatScreenModel(
    var id: String = "",
    var isOffline: Boolean = true,
    val isActivateChatbot: Boolean = LiveChatHelper.settings?.data?.chatbot == true,
    val companyLogo: String? = "",
    val isAvatarExists: Boolean? = false,
    val initial: String? = "",
    val title: String? = "",
    val titleColor: String? = LiveChatHelper.settings?.data?.config?.general?.headerTitleColor,
    val titleBackgroundColor: String? = LiveChatHelper.settings?.data?.config?.general?.backgroundHeaderColor,
    val lastMessageType: Int? = null,
    val lastMessageDrawable: Int? = null,
    val lastMessageBody: String? = null,
    val statusText: String? = LiveChatHelper.settings?.data?.language?.online,
    val typingText: String? = LiveChatHelper.settings?.data?.language?.onlineTyping
)