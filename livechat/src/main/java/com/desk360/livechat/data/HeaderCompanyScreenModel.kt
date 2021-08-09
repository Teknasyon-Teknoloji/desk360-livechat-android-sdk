package com.desk360.livechat.data

import com.desk360.livechat.manager.LiveChatHelper

data class HeaderCompanyScreenModel(
    val logo: String? = LiveChatHelper.settings?.data?.config?.general?.brandLogo,
    val title: String? = LiveChatHelper.settings?.data?.config?.general?.brandName,
    val subTitle: String? = LiveChatHelper.settings?.data?.config?.offline?.headerText,
    val titleColor: String? = LiveChatHelper.settings?.data?.config?.general?.headerTitleColor,
    val titleBackgroundColor: String? = LiveChatHelper.settings?.data?.config?.general?.backgroundHeaderColor
)
