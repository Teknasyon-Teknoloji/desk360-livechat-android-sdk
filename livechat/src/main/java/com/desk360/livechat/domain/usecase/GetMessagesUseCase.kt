package com.desk360.livechat.domain.usecase

import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ktx.getValue
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.desk360.base.data.entity.MessageEntity
import com.desk360.base.domain.repository.MessageRepository
import com.desk360.base.domain.usecase.BaseUseCase
import com.desk360.base.util.ChatUtils
import com.desk360.base.util.DateUtils
import com.desk360.livechat.data.model.chat.Message
import com.desk360.livechat.data.model.firebase.MessageModel
import com.desk360.livechat.data.model.media.MediaModel
import com.desk360.livechat.domain.repository.FirebaseRepository
import com.desk360.livechat.manager.LiveChatFirebaseHelper
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.*

class GetMessagesUseCase(private val conversationId: String) : BaseUseCase<ArrayList<Message>>() {
    private val messageRepository = MessageRepository.getInstance()
    private val firebaseRepository = FirebaseRepository

    override fun buildUseCaseObservable(): Observable<ArrayList<Message>>? {
        syncMessages()

        return messageRepository?.getMessages(conversationId)?.map {
            val list = arrayListOf<Message>()
            it.forEach { item ->
                val attachments = arrayListOf<MediaModel>()
                item.attachments?.let { files ->
                    val myType = object : TypeToken<ArrayList<MediaModel>>() {}.type
                    attachments.addAll(
                        Gson().fromJson<ArrayList<MediaModel>>(files, myType) ?: arrayListOf()
                    )
                }

                item.let { entity ->
                    val message = Message(
                        id = entity.id,
                        type = entity.type,
                        body = entity.body.toString()
                            .trim() + if (entity.type == ChatUtils.Message.TEXT) "\u0020\u0020\u0020\u0020\u0020\u0020\u0020\u0020\u0020\u0020\u0020\u0020\u0020\u0020\u200E" else "",
                        senderName = entity.sender.toString(),
                        receiverId = entity.receiver,
                        isMine = entity.isOutgoing,
                        time = entity.time,
                        status = entity.status,
                        attachments = attachments,
                        percentage = entity.percentage
                    )

                    list.add(message)
                }
            }
            list.sortBy { message -> message.time }

            val newList = arrayListOf<Message>()

            var isYesterday = false
            if (list.isNotEmpty()) {
                list[0].time?.let {
                    isYesterday = DateUtils.isYesterday(Date(it))
                }
            }

            if (isYesterday) {
                newList.add(Message(body = "Yesterday", type = ChatUtils.Message.TIME))

                var isToday = false

                list.forEach { message ->
                    if (message.time?.let { DateUtils.isToday(Date(it)) } == true && !isToday) {
                        newList.add(Message(body = "Today", type = ChatUtils.Message.TIME))

                        isToday = true
                    }

                    newList.add(message)
                }

            } else {
                newList.addAll(list)
            }

            return@map newList
        }?.toObservable()
    }

    private fun syncMessages() {
        firebaseRepository.conversation()
            ?.addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    if (snapshot.key != "session") {

                        LiveChatFirebaseHelper.database?.getReference("messages/${LiveChatFirebaseHelper.userId}/${snapshot.key}")
                            ?.addChildEventListener(object : ChildEventListener {
                                override fun onChildAdded(
                                    messageSnapshot: DataSnapshot,
                                    previousChildName: String?
                                ) {
                                    convertMessageFromSnapshot(
                                        messageSnapshot,
                                        snapshot.key
                                    )?.let { message ->
                                        updateMessage(message)

                                        if (!message.isMine && message.status == ChatUtils.Status.SENT) {
                                            LiveChatFirebaseHelper.database?.getReference("messages/${LiveChatFirebaseHelper.userId}/${snapshot.key}/${messageSnapshot.key}/status")
                                                ?.setValue("delivered")
                                        }
                                    }
                                }

                                override fun onChildChanged(
                                    messageSnapshot: DataSnapshot,
                                    previousChildName: String?
                                ) {
                                    convertMessageFromSnapshot(
                                        messageSnapshot,
                                        snapshot.key
                                    )?.let { message ->
                                        updateMessage(message)
                                    }
                                }

                                override fun onChildRemoved(snapshot: DataSnapshot) {}
                                override fun onChildMoved(
                                    snapshot: DataSnapshot,
                                    previousChildName: String?
                                ) {
                                }

                                override fun onCancelled(error: DatabaseError) {}
                            })
                    }
                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
                override fun onChildRemoved(snapshot: DataSnapshot) {}
                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
                override fun onCancelled(error: DatabaseError) {}
            })
    }

    private fun updateMessage(message: Message) {
        if (message.time == null)
            return

        val entity = MessageEntity(
            id = message.id.toString(),
            isOutgoing = message.isMine,
            type = message.type,
            sender = message.senderName,
            receiver = message.receiverId,
            body = message.body,
            status = message.status,
            time = message.time ?: 0L,
            conversationId = conversationId,
            attachments = Gson().toJson(message.attachments),
            percentage = 0
        )

        messageRepository?.insert(entity)
            ?.subscribeOn(Schedulers.io())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribe()?.let { messageDisposable ->
                compositeDisposable.add(messageDisposable)
            }
    }

    private fun convertMessageFromSnapshot(
        messageDataSnapshot: DataSnapshot,
        receiverId: String?
    ): Message? {
        val message = messageDataSnapshot.getValue<MessageModel>()
        message?.is_customer = messageDataSnapshot.child("is_customer")
            .getValue(Boolean::class.java) ?: true
        val content = if (message?.content is String) message.content.toString() else ""

        val newMessage = Message(
            id = messageDataSnapshot.key,
            body = content,
            senderName = message?.sender_name.toString(),
            receiverId = receiverId.toString(),
            isMine = message?.is_customer != false,
            time = message?.created_at,
            status = when (message?.status) {
                "read" -> ChatUtils.Status.SEEN
                "delivered" -> ChatUtils.Status.FORWARDED
                else -> ChatUtils.Status.SENT
            }
        )

        when {
            message?.attachments?.images?.isNotEmpty() == true -> {
                newMessage.apply {
                    type = ChatUtils.Message.PHOTO
                    attachments = message.attachments?.images
                }
            }
            message?.attachments?.videos?.isNotEmpty() == true -> {
                newMessage.apply {
                    type = ChatUtils.Message.VIDEO
                    attachments = message.attachments?.videos
                }
            }
            message?.attachments?.files?.isNotEmpty() == true -> {
                newMessage.apply {
                    type = ChatUtils.Message.DOCUMENT
                    attachments = message.attachments?.files
                }
            }
        }

        return newMessage
    }
}