package com.desk360.livechat.manager

import android.content.Context

abstract class BaseChatManager {
    var token: String = ""
    var domainAddress: String = ""
    var userName: String = ""
    var userEmailAddress: String = ""
    var languageCode: String = ""

    abstract fun init(context: Context, isActive: (Boolean) -> Unit)
    abstract fun start(context: Context)
}