package com.desk360.base.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "table_message", foreignKeys = [ForeignKey(
        entity = ConversationEntity::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("conversation_id"),
        onDelete = ForeignKey.CASCADE
    )]
)
class MessageEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "is_outgoing") var isOutgoing: Boolean,
    @ColumnInfo(name = "type") var type: Int,
    @ColumnInfo(name = "sender") var sender: String?,
    @ColumnInfo(name = "receiver") var receiver: String?,
    @ColumnInfo(name = "body") var body: String?,
    @ColumnInfo(name = "status") var status: Int,
    @ColumnInfo(name = "time") var time: Long,
    @ColumnInfo(name = "conversation_id") var conversationId: String,
    @ColumnInfo(name = "attachments") var attachments: String? = null,
    @ColumnInfo(name = "percentage") var percentage: Int? = 0
)
