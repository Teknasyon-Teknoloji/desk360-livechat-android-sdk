package com.desk360.livechat.domain.usecase

import com.desk360.base.data.entity.MessageEntity
import com.desk360.base.domain.repository.MessageRepository
import com.desk360.base.domain.usecase.BaseUseCase
import com.desk360.base.util.ChatUtils
import com.desk360.base.util.DateUtils
import com.desk360.livechat.data.model.firebase.MessageModel
import com.desk360.livechat.domain.repository.FirebaseRepository
import com.desk360.livechat.manager.LiveChatFirebaseHelper
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class AddMessageUseCase(
    private val conversationId: String,
    private val model: MessageModel,
    private val type: Int
) :
    BaseUseCase<String>() {

    private val messageRepository = MessageRepository.getInstance()
    private val firebaseRepository = FirebaseRepository

    override fun buildUseCaseObservable(): Observable<String>? {
        val reference = firebaseRepository.messages()
        val key = reference?.push()?.key
        val entity = createMessageEntity(key)

        return messageRepository?.insert(entity)?.doOnNext {
            key?.let {
                reference.child(it).setValue(model).addOnFailureListener {
                    entity.status = ChatUtils.Status.ERROR
                    updateMessage(entity)
                }
            }
        }?.map { id ->
            return@map key
        }
    }

    private fun updateMessage(entity: MessageEntity) {
        val disposable = messageRepository?.insert(entity)
            ?.subscribeOn(Schedulers.io())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribe()
        disposable?.let { it1 -> compositeDisposable.add(it1) }
    }

    private fun createMessageEntity(key: String?) = MessageEntity(
        id = key ?: "",
        isOutgoing = model.is_customer,
        type = type,
        sender = LiveChatFirebaseHelper.userId,
        receiver = model.receiver.toString(),
        body = model.content.toString().trim(),
        status = ChatUtils.Status.SENDING,
        time = DateUtils.now(),
        conversationId = conversationId
    )
}