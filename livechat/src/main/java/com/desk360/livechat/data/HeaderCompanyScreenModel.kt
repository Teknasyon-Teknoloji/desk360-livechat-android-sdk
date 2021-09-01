package com.desk360.livechat.data

import com.desk360.base.util.Utils
import com.desk360.livechat.manager.LiveChatHelper

data class HeaderCompanyScreenModel(
    val logo: String? = Utils.getBrandLogo(),
    val title: String? = Utils.getBrandName(),
    val subTitle: String? = LiveChatHelper.settings?.data?.config?.offline?.headerText,
    val titleColor: String? = LiveChatHelper.settings?.data?.config?.general?.headerTitleColor,
    val titleBackgroundColor: String? = LiveChatHelper.settings?.data?.config?.general?.backgroundHeaderColor
)
