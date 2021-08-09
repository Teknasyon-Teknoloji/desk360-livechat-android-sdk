package com.desk360.livechat.data.model.session

data class SessionRequest(
    val name: String? = "",
    val email: String? = "",
    private val source: String = "Android"
)