package com.desk360.livechat.presentation.activity.livechat

import androidx.lifecycle.MutableLiveData
import com.desk360.base.manager.SharedPreferencesManager
import com.desk360.base.util.ChatUtils
import com.desk360.livechat.data.HeaderChatScreenModel
import com.desk360.livechat.data.model.chatsettings.General
import com.desk360.livechat.domain.usecase.*
import com.desk360.livechat.manager.LiveChatFirebaseHelper
import com.desk360.livechat.manager.LiveChatHelper
import com.desk360.livechat.manager.LiveChatSharedPrefManager
import com.desk360.livechat.presentation.viewmodel.BaseViewModel

class StartNewChatViewModel : BaseViewModel() {
    val isNeedNewToken: MutableLiveData<Boolean> by lazy {
        MutableLiveData(
            LiveChatSharedPrefManager.isNeedNewToken()
        )
    }

    val agent: MutableLiveData<HeaderChatScreenModel> by lazy {
        MutableLiveData(LiveChatSharedPrefManager.agent)
    }

    val screenModel: MutableLiveData<General> by lazy {
        MutableLiveData(LiveChatHelper.settings?.data?.config?.general)
    }

    val conversations: MutableLiveData<List<HeaderChatScreenModel>> by lazy {
        MutableLiveData()
    }

    init {
        getDatas()
    }

    fun getDatas() {
        getAgent()
        getConversations()
    }

    private fun getConversations() {
        GetConversationListUseCase(
            types = intArrayOf(
                ChatUtils.Conversation.LIVE_CHAT,
                ChatUtils.Conversation.CHAT_BOT
            )
        ).execute(onSuccess = {
            if(!LiveChatSharedPrefManager.isNeedNewToken()) {
                conversations.value =it
            }
            isNeedNewToken.value = it.isEmpty()
        }, onError = {

        })
    }

    private fun getAgent() {
        isNeedNewToken.value = LiveChatSharedPrefManager.isNeedNewToken()
        if (isNeedNewToken.value == true)
            return

        if (LiveChatFirebaseHelper.userId.isNullOrEmpty()) {
            SharedPreferencesManager.token?.let { token ->
                SignInWithTokenUseCase.execute(token).addOnCompleteListener { task ->
                    LiveChatFirebaseHelper.auth?.currentUser?.uid?.let { uid ->
                        getSession()
                    }
                }
            }
        } else {
            getSession()
        }
    }

    private fun getSession() {
        GetSessionUseCase().execute({ result ->
            if (result == null) {
                isNeedNewToken.value = true
                DeleteConversationUseCase().execute(onSuccess = {

                }, onError = {
                    handleError(it)
                    isEndedSession.value = false
                })
            } else {
                agent.value = result
                LiveChatSharedPrefManager.agent = result
                updateConversation(result)
            }
        }, {

        })
    }

    private fun updateConversation(result: HeaderChatScreenModel) {
        UpdateConversationUseCase(result).execute(onSuccess = { id ->

        }, onError = {
            handleError(it)
        })
    }
}