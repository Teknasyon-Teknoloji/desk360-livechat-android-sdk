package com.desk360.livechat.data.model.message

data class MessageRequest(
    val name: String? = null,
    val email: String? = null,
    val message: String? = null,
    val source: String = "Android"
)
