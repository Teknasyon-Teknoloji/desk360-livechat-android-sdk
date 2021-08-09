package com.desk360.livechat.presentation.activity.livechat

import androidx.lifecycle.MutableLiveData
import com.desk360.livechat.data.HeaderChatScreenModel
import com.desk360.livechat.data.HeaderCompanyScreenModel
import com.desk360.livechat.data.model.chatsettings.Chat
import com.desk360.livechat.data.model.message.MessageRequest
import com.desk360.livechat.data.model.session.SessionRequest
import com.desk360.livechat.domain.usecase.*
import com.desk360.livechat.manager.Desk360LiveChat
import com.desk360.livechat.manager.LiveChatFirebaseHelper
import com.desk360.livechat.manager.LiveChatHelper
import com.desk360.livechat.manager.LiveChatSharedPrefManager
import com.desk360.livechat.presentation.viewmodel.BaseViewModel

class LoginNewChatViewModel : BaseViewModel() {
    val isOffline: MutableLiveData<Boolean> by lazy {
        MutableLiveData(LiveChatHelper.isOffline)
    }

    val conversationId: MutableLiveData<String> by lazy {
        MutableLiveData()
    }

    val headerCompanyScreenModel: MutableLiveData<HeaderCompanyScreenModel> by lazy {
        MutableLiveData(HeaderCompanyScreenModel())
    }

    val screenModel: MutableLiveData<Chat> by lazy {
        MutableLiveData(Chat())
    }

    val session: MutableLiveData<SessionRequest> by lazy {
        MutableLiveData()
    }

    private val _isSentOfflineMessage: MutableLiveData<Boolean> = MutableLiveData()
    val isSentOfflineMessage get() = _isSentOfflineMessage

    var messageRequest: MessageRequest? = null
    var getSessionUseCase: GetSessionUseCase? = null

    init {
        getSessionUseCase = GetSessionUseCase()

        session.value = SessionRequest(
            name = Desk360LiveChat.manager?.userName,
            email = Desk360LiveChat.manager?.userEmailAddress.toString().trim()
        )

        getConfig()
    }

    private val _token = MutableLiveData<String?>()
    val token get() = _token

    private val isOfflineUseCase = IsOfflineUseCase()

    fun checkStatus() {
        LiveChatFirebaseHelper.auth?.signOut()

        isOfflineUseCase.execute(onSuccess = { result ->
            isOffline.value = result
            getConfig()
        }, onError = {

        })
    }

    private fun getConfig() {
        headerCompanyScreenModel.value = HeaderCompanyScreenModel()
        screenModel.value = LiveChatHelper.settings?.data?.config?.chat
    }

    fun createNewChat() {
        isRunningProgress.value = true
        when {
            isOffline.value == true -> sendMessage()
            LiveChatHelper.settings?.data?.chatbot == true -> createChatbotSession()
            else -> createSession()
        }
    }

    private fun sendMessage() {
        messageRequest?.let { request ->
            SendOfflineMessageUseCase(request).execute(onSuccess = { isSuccess ->
                isSentOfflineMessage.value = isSuccess
            }, onError = {
                handleError(it)
            })
        }
    }

    private fun createSession() {
        LiveChatSharedPrefManager.isChatBot = false

        session.value?.let { request ->
            OnlineSessionUseCase(request).execute(onSuccess = { token ->
                _token.value = token
                singInWithToken(token)
            }, onError = {
                handleError(it)
            })
        }
    }

    private fun createChatbotSession() {
        LiveChatSharedPrefManager.isChatBot = true

        session.value?.let { request ->
            ChatBotSessionUseCase(request).execute(onSuccess = { token ->
                _token.value = token
            }, onError = {
                handleError(it)
            })
        }
    }

    private fun singInWithToken(token: String?) {
        token?.let {
            SignInWithTokenUseCase.execute(token).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    LiveChatFirebaseHelper.auth?.currentUser?.uid?.let { uid ->
                        getSession()
                    }
                }
            }
        }
    }

    private fun getSession() {
        getSessionUseCase = GetSessionUseCase()
        getSessionUseCase?.execute({ result ->
            if (result == null) {
                DeleteConversationUseCase().execute(onSuccess = {
                    isEndedSession.value = true
                }, onError = {
                    handleError(it)
                    isEndedSession.value = false
                })
            } else {
                LiveChatSharedPrefManager.agent = result
                createConversation(result)
            }
        }, {

        })
    }

    private fun createConversation(result: HeaderChatScreenModel) {
        CreateConversationUseCase(result).execute(onSuccess = { id ->
            conversationId.value = id
        }, onError = {
            handleError(it)
        })
    }

    fun isNicknameValid() = session.value?.name?.isNotEmpty() == true
    fun isEmailAddressValid() = session.value?.email?.isNotEmpty() == true
    fun isMessageValid() =
        (isOffline.value == true && messageRequest?.message?.isNotEmpty() == true) || isOffline.value != true

    fun setSessionRequest(name: String, emailAddress: String, message: String) {
        session.value = SessionRequest(
            name = name,
            email = emailAddress.trim()
        )

        messageRequest = MessageRequest(name = name, email = emailAddress, message = message)
    }

    override fun pause() {
        super.pause()
        getSessionUseCase?.removeListener()
    }

    override fun onCleared() {
        super.onCleared()
        isOfflineUseCase.removeListener()
    }
}