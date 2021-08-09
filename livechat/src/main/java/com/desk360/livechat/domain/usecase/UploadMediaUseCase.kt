package com.desk360.livechat.domain.usecase

import com.google.gson.Gson
import com.desk360.base.data.entity.MessageEntity
import com.desk360.base.domain.repository.MessageRepository
import com.desk360.base.domain.usecase.BaseUseCase
import com.desk360.base.util.ChatUtils
import com.desk360.base.util.DateUtils
import com.desk360.livechat.data.model.firebase.MessageModel
import com.desk360.livechat.data.model.media.MediaModel
import com.desk360.livechat.domain.repository.FirebaseRepository
import com.desk360.livechat.domain.repository.LiveChatRepository
import com.desk360.livechat.manager.FileManager
import com.desk360.livechat.manager.LiveChatFirebaseHelper
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class UploadMediaUseCase(
    private val conversationId: String,
    private val model: MessageModel,
    private val mediaType: String,
    private val file: File,
    private val name: String?,
    private val type: String
) :
    BaseUseCase<String>() {

    private val firebaseRepository = FirebaseRepository
    private val messageRepository = MessageRepository.getInstance()

    override fun buildUseCaseObservable(): Observable<String>? {
        val fileName = getFileName()
        val filePart = MultipartBody.Part.createFormData(
            "attachments[]",
            name ?: file.name,
            file.asRequestBody(type.toMediaTypeOrNull())
        )

        val reference = firebaseRepository.messages()
        val key = reference?.push()?.key
        val entity = createMessageEntity(key)

        return messageRepository?.insert(entity)?.doOnNext {

            val disposable =
                LiveChatRepository.instance.uploadMedia(filePart)?.doOnNext { response ->
                    key?.let {
                        model.attachments = response.data

                        reference.child(key)
                            .setValue(model)
                            .addOnFailureListener {
                                entity.status = ChatUtils.Status.ERROR
                                updateMessage(entity)
                            }
                    }
                    FileManager.copyFile(file, fileName)
                }?.doOnError {
                    entity.status = ChatUtils.Status.ERROR
                    updateMessage(entity)
                }?.subscribeOn(Schedulers.io())
                    ?.observeOn(AndroidSchedulers.mainThread())
                    ?.subscribe()

            disposable?.let { it1 -> compositeDisposable.add(it1) }
        }?.map { id ->
            return@map key
        }
    }

    private fun getFileName(): String {
        val fileNameAndExtension = (name ?: file.name).split(".")
        return fileNameAndExtension[0] + "_" + model.created_at + "." + fileNameAndExtension[1]
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
        type = when (mediaType) {
            FileManager.MediaType.IMAGES -> ChatUtils.Message.PHOTO
            FileManager.MediaType.FILES -> ChatUtils.Message.DOCUMENT
            else -> ChatUtils.Message.VIDEO
        },
        sender = LiveChatFirebaseHelper.userId,
        receiver = model.receiver.toString(),
        body = model.content.toString().trim(),
        status = ChatUtils.Status.SENDING,
        time = DateUtils.now(),
        conversationId = conversationId,
        attachments = Gson().toJson(
            arrayListOf(
                MediaModel(
                    name = getFileName(),
                    url = file.path
                )
            )
        )
    )
}