package com.desk360.base.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.desk360.base.data.dao.ConversationDao
import com.desk360.base.data.dao.MessageDao
import com.desk360.base.data.entity.ConversationEntity
import com.desk360.base.data.entity.MessageEntity

@Database(
    entities = [ConversationEntity::class, MessageEntity::class],
    version = 1,
    exportSchema = false
)
abstract class Desk360ChatDatabase : RoomDatabase() {

    abstract fun conversationDao(): ConversationDao
    abstract fun messageDao(): MessageDao

    companion object {
        private const val DATABASE_NAME = "chat_db"
        private var instance: Desk360ChatDatabase? = null

        fun getDatabase(context: Context): Desk360ChatDatabase {
            return instance ?: synchronized(this) {
                val newInstance = Room.databaseBuilder(
                    context.applicationContext,
                    Desk360ChatDatabase::class.java,
                    DATABASE_NAME
                )
                    .build()
                instance = newInstance
                newInstance
            }
        }
    }
}