package com.desk360.base.data.dao

import androidx.room.*
import com.desk360.base.data.entity.MessageEntity
import io.reactivex.Completable
import io.reactivex.Flowable

@Dao
interface MessageDao {
    @Query("SELECT * FROM table_message WHERE conversation_id == :id ORDER BY time")
    fun getMessages(id: String): Flowable<List<MessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(message: MessageEntity): Long

    @Delete
    fun delete(message: MessageEntity): Completable
}