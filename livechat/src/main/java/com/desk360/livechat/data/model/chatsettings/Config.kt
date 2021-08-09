package com.desk360.livechat.data.model.chatsettings

data class Config(
    val chat: Chat? = null,
    val feedback: Feedback? = null,
    val general: General? = null,
    val offline: Offline? = null,
    val online: Online? = null
)