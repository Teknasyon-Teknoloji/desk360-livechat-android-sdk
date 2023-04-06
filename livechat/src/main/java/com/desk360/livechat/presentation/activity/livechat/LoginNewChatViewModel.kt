package com.desk360.livechat.presentation.activity.livechat

import androidx.lifecycle.MutableLiveData
import com.desk360.livechat.data.HeaderCompanyScreenModel
import com.desk360.livechat.data.model.chatsettings.Chat
import com.desk360.livechat.data.model.chatsettings.CustomField
import com.desk360.livechat.data.model.message.MessageRequest
import com.desk360.livechat.data.model.session.SessionRequest
import com.desk360.livechat.domain.usecase.ChatBotSessionUseCase
import com.desk360.livechat.domain.usecase.GetSessionUseCase
import com.desk360.livechat.domain.usecase.IsOfflineUseCase
import com.desk360.livechat.domain.usecase.SendOfflineMessageUseCase
import com.desk360.livechat.manager.*
import com.desk360.livechat.presentation.viewmodel.BaseViewModel

class LoginNewChatViewModel : BaseViewModel() {

    val isOffline: MutableLiveData<Boolean> by lazy {
        MutableLiveData(LiveChatHelper.isOffline)
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

    val customFieldList get() = LiveChatHelper.settings?.data?.config?.offline?.customFields

    private val _isSentOfflineMessage: MutableLiveData<Boolean> = MutableLiveData()
    val isSentOfflineMessage get() = _isSentOfflineMessage

    var messageRequest: MessageRequest? = null
    var getSessionUseCase: GetSessionUseCase? = null
    var isPathId: Boolean = false

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

    fun createNewChat(activeCustomField: MutableList<CustomField>) {
        isRunningProgress.value = true
        when {
            isOffline.value == true -> sendMessage(activeCustomField)
            LiveChatHelper.settings?.data?.chatbot == true -> createChatbotSession()
            isPathId -> autoLogin(CannedResponseHelper.getPathId(), session.value)
            else -> autoLogin(null, session.value)
        }
    }

    private fun sendMessage(activeCustomField: MutableList<CustomField>) {
        messageRequest?.let { request ->
            SendOfflineMessageUseCase(request, activeCustomField).execute(onSuccess = { isSuccess ->
                isSentOfflineMessage.value = isSuccess
                CannedResponseHelper.clearPayload()
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

    fun isNicknameValid() = session.value?.name?.isNotEmpty() == true
    fun isEmailAddressValid() = session.value?.email?.isNotEmpty() == true
    fun isMessageValid() =
        (isOffline.value == true && messageRequest?.message?.isNotEmpty() == true) || isOffline.value != true

    fun setSessionRequest(name: String, emailAddress: String, message: String) {
        session.value = SessionRequest(
            name = name,
            email = emailAddress.trim()
        )

        messageRequest = if (isPathId) {
            MessageRequest(
                name = name,
                email = emailAddress,
                message = message,
                pathId = CannedResponseHelper.getPathId(),
            )
        } else MessageRequest(name = name, email = emailAddress, message = message)

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