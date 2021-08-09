package com.desk360.base.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.desk360.base.util.ChatUtils

@Entity(tableName = "table_conversation")
class ConversationEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "companion_id") var companionId: String,
    @ColumnInfo(name = "name") var name: String?,
    @ColumnInfo(name = "avatar") var avatar: String?,
    @ColumnInfo(name = "type") @ChatUtils.Conversation.Type var type: Int,
    @ColumnInfo(name = "created_time") var createdTime: Long
)