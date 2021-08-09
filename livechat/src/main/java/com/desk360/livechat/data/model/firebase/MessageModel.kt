package com.desk360.livechat.data.model.firebase

import com.desk360.livechat.data.model.BaseModel
import com.desk360.livechat.data.model.media.Data

data class MessageModel(
    @field:JvmField var is_customer: Boolean = true,
    @field:JvmField var receiver: Any? = null,
    @field:JvmField var updated_at: Long? = null,
    @field:JvmField var sender: Any? = null,
    @field:JvmField var session: Any? = null,
    @field:JvmField var created_at: Long? = null,
    @field:JvmField var sender_name: String? = null,
    @field:JvmField var content: Any? = null,
    @field:JvmField var status: String? = null,
    @field:JvmField var source: String = "Android",
    @field:JvmField var attachments: Data? = null,
    @field:JvmField var timestamp: Boolean = true,
) : BaseModel()