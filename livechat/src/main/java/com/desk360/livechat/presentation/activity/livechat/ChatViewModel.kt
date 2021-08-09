package com.desk360.livechat.presentation.activity.livechat

import android.os.CountDownTimer
import androidx.lifecycle.MutableLiveData
import com.desk360.base.manager.SharedPreferencesManager
import com.desk360.base.util.ChatUtils
import com.desk360.livechat.data.HeaderChatScreenModel
import com.desk360.livechat.data.model.chat.Message
import com.desk360.livechat.data.model.firebase.MessageModel
import com.desk360.livechat.data.model.firebase.SessionModel
import com.desk360.livechat.domain.repository.FirebaseRepository
import com.desk360.livechat.domain.usecase.*
import com.desk360.livechat.manager.LiveChatFirebaseHelper
import com.desk360.livechat.manager.LiveChatHelper
import com.desk360.livechat.manager.LiveChatSharedPrefManager
import com.desk360.livechat.presentation.viewmodel.BaseViewModel
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import java.io.File
import java.util.*

class ChatViewModel : BaseViewModel() {
    private val timerTyping = object : CountDownTimer(2000, 1000) {
        override fun onTick(millisUntilFinished: Long) {

        }

        override fun onFinish() {
            isReceiverTyping.value = false
        }
    }

    val isOffline: MutableLiveData<Boolean> by lazy {
        MutableLiveData(false)
    }

    val isReceiverTyping: MutableLiveData<Boolean> by lazy {
        MutableLiveData(false)
    }

    val isTyping: MutableLiveData<Boolean> by lazy {
        MutableLiveData(false)
    }

    val isSending: MutableLiveData<Boolean> by lazy {
        MutableLiveData(false)
    }

    val headerScreenModel: MutableLiveData<HeaderChatScreenModel> by lazy {
        MutableLiveData(
            LiveChatSharedPrefManager.agent.apply {
                isOffline = false
            }
        )
    }

    private val _isOpenEmojiPopup: MutableLiveData<Boolean> = MutableLiveData(false)
    val isOpenEmojiPopup get() = _isOpenEmojiPopup

    private val _isOpenAttachment: MutableLiveData<Boolean> = MutableLiveData(false)
    val isOpenAttachment get() = _isOpenAttachment

    private val _isSentMessageSuccessful: MutableLiveData<Boolean> = MutableLiveData(false)
    val isSentMessageSuccessful get() = _isSentMessageSuccessful

    private val _isEndSessionSuccessful: MutableLiveData<Boolean> = MutableLiveData(false)
    val isEndSessionSuccessful get() = _isEndSessionSuccessful

    private val _newMessages: MutableLiveData<ArrayList<Message>> = MutableLiveData()
    val newMessages get() = _newMessages

    private var session: SessionModel? = null

    var conversationId: String? = null

    private fun getMessages() {
        conversationId?.let {
            GetMessagesUseCase(it).execute(onSuccess = { list ->
                _newMessages.value = list
            }, onError = {
                handleError(it)
            })
        }
    }

    fun checkStatus(companyId: Int) {
        getMessages()
        signInWithToken()

        LiveChatFirebaseHelper.isOffline(companyId)
            ?.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    isOffline.value = snapshot.getValue(Int::class.java) == 0
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    private fun signInWithToken() {
        if (LiveChatFirebaseHelper.userId.isNullOrEmpty() || LiveChatSharedPrefManager.isNeedRefreshToken()) {
            SharedPreferencesManager.token?.let { token ->
                SignInWithTokenUseCase.execute(token)
                    .addOnFailureListener { ex ->
                        when (ex) {
                            is FirebaseAuthInvalidCredentialsException -> {
                                deleteConversation()
                            }
                            else -> handleError(ex.fillInStackTrace())
                        }
                    }
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            getSession()
                        }
                    }
            }
        } else {
            getSession()
        }
    }

    private fun getSession() {
        FirebaseRepository.conversation()
            ?.get()
            ?.addOnSuccessListener { snapshot ->
                isEndedSession.value = snapshot.value == null
                if (snapshot.key == "session") {
                    convertSessionFromSnapshot(snapshot)
                }
            }

        FirebaseRepository.conversation()
            ?.addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    if (snapshot.key == "session") {
                        convertSessionFromSnapshot(snapshot)
                    }
                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                    if (snapshot.key == "session") {
                        convertSessionFromSnapshot(snapshot)
                    }
                }

                override fun onChildRemoved(snapshot: DataSnapshot) {
                    if (snapshot.key == "session") {
                        deleteConversation()
                    }
                }

                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
    }

    private fun convertSessionFromSnapshot(snapshot: DataSnapshot) {
        session = snapshot.getValue(SessionModel::class.java)

        if (session == null) {
            _isEndSessionSuccessful.value = true
            return
        }

        getReceiverTyping(session?.receiver)

        headerScreenModel.value = HeaderChatScreenModel(
            isOffline = false,
            companyLogo = session?.avatar,
            isAvatarExists = session?.avatar?.isNotEmpty() == true || LiveChatHelper.settings?.data?.chatbot == true,
            title = if (LiveChatHelper.settings?.data?.chatbot == true) "Chat Bot" else session?.receiver_name,
            initial = getInitial(session?.receiver_name),
            titleColor = LiveChatHelper.settings?.data?.config?.general?.headerTitleColor,
            titleBackgroundColor = LiveChatHelper.settings?.data?.config?.general?.backgroundHeaderColor
        )

        LiveChatSharedPrefManager.agent = headerScreenModel.value!!
    }

    private fun getInitial(receiverName: String?): String? {
        return if (receiverName?.isNotEmpty() == true) {
            var initial = ""
            receiverName.split(' ').forEachIndexed { index, s ->
                if (index < 2) {
                    initial += s.substring(0, 1)
                }
            }

            return initial.toUpperCase(Locale.getDefault())
        } else null
    }

    fun sendMessage(name: String, message: String) {
        val uid = LiveChatFirebaseHelper.userId

        val messageModel = MessageModel(
            content = message.trim(),
            created_at = com.desk360.base.util.DateUtils.now(),
            updated_at = com.desk360.base.util.DateUtils.now(),
            is_customer = true,
            receiver = session?.receiver?.toString() ?: 0,
            sender = uid,
            session = uid,
            sender_name = name,
            status = "sent"
        )

        conversationId?.let {
            AddMessageUseCase(
                it,
                messageModel,
                ChatUtils.Message.TEXT
            ).execute(onSuccess = { isSuccess ->
                _isSentMessageSuccessful.value = true
            }, onError = { th ->
                _isSentMessageSuccessful.value = false
                handleError(th)
            })
        }
    }

    fun sendMessageWithAttachment(
        file: File,
        type: String,
        mediaType: String,
        message: String? = null,
        name: String? = null
    ) {
        val uid = LiveChatFirebaseHelper.userId

        val messageModel = MessageModel(
            content = (message ?: "").trim(),
            created_at = com.desk360.base.util.DateUtils.now(),
            updated_at = com.desk360.base.util.DateUtils.now(),
            is_customer = true,
            receiver = session?.receiver?.toString() ?: 0,
            sender = uid,
            session = uid,
            sender_name = SharedPreferencesManager.instance?.read(
                SharedPreferencesManager.USER_NAME,
                ""
            ),
            status = "sent"
        )

        conversationId?.let { conversationId ->
            UploadMediaUseCase(
                conversationId,
                messageModel,
                mediaType,
                file,
                name,
                type
            ).execute(onSuccess = { response ->
                _isSentMessageSuccessful.value = true

            }, onError = {
                _isSentMessageSuccessful.value = false
                handleError(it)
            })
        }
    }

    fun sendChatbotMessage(message: String) {
        SendChatBotMessageUseCase(SendChatBotMessageUseCase.createRequest(message)).execute(
            onSuccess = { isSuccess ->
                _isSentMessageSuccessful.value = isSuccess
            },
            onError = {
                handleError(it)
            })
    }

    private fun getReceiverTyping(receiver: Any?) {
        if (receiver != null && receiver is Long) {
            LiveChatFirebaseHelper.database?.getReference("typing/$receiver/${LiveChatFirebaseHelper.userId}")
                ?.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        timerTyping.cancel()
                        val isTyping = snapshot.children.firstOrNull()
                            ?.child("typing")?.value == LiveChatFirebaseHelper.userId
                        isReceiverTyping.value = isReceiverTyping.value == true || isTyping
                        timerTyping.start()
                    }

                    override fun onCancelled(error: DatabaseError) {
                        isReceiverTyping.value = false
                    }
                })
        }
    }

    fun setTyping(message: String) {
        isTyping.value = message.isNotEmpty()
        if (message.isNotEmpty()) {
            LiveChatFirebaseHelper.database?.getReference("typing/${LiveChatFirebaseHelper.userId}")
                ?.push()
                ?.child("typing")
                ?.setValue(LiveChatFirebaseHelper.userId)
        } else {
            LiveChatFirebaseHelper.database?.getReference("typing/${LiveChatFirebaseHelper.userId}")
                ?.removeValue()
        }
    }

    private fun deleteConversation() {
        DeleteConversationUseCase().execute(onSuccess = {
            _isEndSessionSuccessful.value = true
        }, onError = {
            handleError(it)
            _isEndSessionSuccessful.value = false
        })
    }

    fun endSession() {
        EndSessionUseCase(conversationId = conversationId).execute(
            onSuccess = { isSuccess ->
                _isEndSessionSuccessful.value = isSuccess
            },
            onError = { th ->
                handleError(th)
                _isEndSessionSuccessful.value = false
            }
        )
    }
}