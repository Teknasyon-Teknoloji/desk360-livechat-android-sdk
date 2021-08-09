package com.desk360.base.domain.repository

import android.content.Context
import com.desk360.base.data.entity.ConversationEntity
import com.desk360.base.data.entity.ConversationItem
import com.desk360.livechat.manager.Desk360LiveChat
import io.reactivex.Flowable
import io.reactivex.Observable

class ConversationRepository private constructor(context: Context) {
    private val conversationDao: com.desk360.base.data.dao.ConversationDao

    init {
        val db = com.desk360.base.data.database.Desk360ChatDatabase.getDatabase(context)
        conversationDao = db.conversationDao()
    }

    fun getConversations(types: IntArray): Flowable<List<ConversationItem>> {
        return conversationDao.getConversations(types)
    }

    fun isExist(id: String): Observable<Boolean> {
        return Observable.fromCallable {
            conversationDao.isExist(id)
        }
    }

    fun insert(entity: ConversationEntity): Observable<Long> {
        return Observable.fromCallable {
            conversationDao.deleteAll()
            conversationDao.insert(entity)
        }
    }

    fun update(entity: ConversationEntity): Int {
        return conversationDao.update(entity.id, entity.name, entity.avatar)
    }

    fun deleteAll(): Observable<Unit> {
        return Observable.fromCallable {
            conversationDao.deleteAll()
        }
    }

    companion object {
        private var instance: ConversationRepository? = null

        fun getInstance() = instance ?: synchronized(this) {
            instance = Desk360LiveChat.getContext()?.let { ConversationRepository(it) }
            instance
        }
    }
}