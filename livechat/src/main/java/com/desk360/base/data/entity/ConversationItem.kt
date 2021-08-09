package com.desk360.base.data.entity

import androidx.room.ColumnInfo
import androidx.room.Embedded
import com.google.gson.annotations.SerializedName

data class ConversationItem(
    @Embedded
    val conversationEntity: ConversationEntity? = null,

    @ColumnInfo(name = "messageType")
    @SerializedName("messageType")
    val messageType: Int? = null,

    @ColumnInfo(name = "messageBody")
    @SerializedName("messageBody")
    val messageBody: String? = null,

    @ColumnInfo(name = "messageTime")
    @SerializedName("messageTime")
    val messageTime: Long? = null,
)