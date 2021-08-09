package com.desk360.base.data.dao

import androidx.room.*
import com.desk360.base.data.entity.ConversationEntity
import com.desk360.base.data.entity.ConversationItem
import io.reactivex.Completable
import io.reactivex.Flowable

@Dao
interface ConversationDao {
    @Query("SELECT * FROM table_conversation")
    fun getAll(): Flowable<List<ConversationEntity>>

    @Query("SELECT EXISTS(SELECT * FROM table_conversation WHERE id = :id)")
    fun isExist(id : String) : Boolean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(entity: ConversationEntity): Long

    @Query("UPDATE table_conversation SET name = :name ,avatar= :avatar WHERE id = :id ")
    fun update(id: String, name: String?, avatar: String?): Int

    @Delete
    fun delete(entity: ConversationEntity): Completable

    @Query("DELETE FROM table_conversation")
    fun deleteAll()

    @Query("SELECT c.*, m.type as messageType, m.body AS messageBody, max(time) AS messageTime  FROM table_conversation c LEFT JOIN table_message m ON c.id = m.conversation_id WHERE c.type IN (:types) GROUP BY c.id ORDER BY m.time DESC")
    fun getConversations(types: IntArray): Flowable<List<ConversationItem>>
}