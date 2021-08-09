package com.desk360.base.domain.repository

import android.content.Context
import com.desk360.base.data.dao.MessageDao
import com.desk360.base.data.database.Desk360ChatDatabase
import com.desk360.base.data.entity.MessageEntity
import com.desk360.livechat.manager.Desk360LiveChat
import io.reactivex.Flowable
import io.reactivex.Observable

class MessageRepository private constructor(context: Context) {
    private val messageDao: MessageDao

    init {
        val db = Desk360ChatDatabase.getDatabase(context)
        messageDao = db.messageDao()
    }

    fun getMessages(id: String): Flowable<List<MessageEntity>> {
        return messageDao.getMessages(id)
    }

    fun insert(entity: MessageEntity): Observable<Long> {
        return Observable.fromCallable { messageDao.insert(entity) }
    }

    companion object {
        private var instance: MessageRepository? = null

        fun getInstance() = instance ?: synchronized(this) {
            instance = Desk360LiveChat.getContext()?.let { MessageRepository(it) }
            instance
        }
    }
}