package com.desk360.livechat.data.model.firebase

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class SessionModel(
    var receiver_status: String? = "",
    var receiver: Any? = null,
    var receiver_name: String? = "",
    var status: String? = "",
    var avatar: String? = null
)
