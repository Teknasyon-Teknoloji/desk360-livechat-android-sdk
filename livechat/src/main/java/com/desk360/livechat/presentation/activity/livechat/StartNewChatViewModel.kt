package com.desk360.livechat.presentation.activity.livechat

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.desk360.base.manager.SharedPreferencesManager
import com.desk360.base.util.ChatUtils
import com.desk360.base.util.Utils
import com.desk360.livechat.data.HeaderChatScreenModel
import com.desk360.livechat.domain.usecase.*
import com.desk360.livechat.manager.LiveChatFirebaseHelper
import com.desk360.livechat.manager.LiveChatSharedPrefManager
import com.desk360.livechat.presentation.viewmodel.BaseViewModel

class StartNewChatViewModel : BaseViewModel() {
    private val _isNeedNewToken = MutableLiveData(LiveChatSharedPrefManager.isNeedNewToken())
    val isNeedNewToken: LiveData<Boolean>
        get() = _isNeedNewToken

    val companyName: MutableLiveData<String> by lazy {
        MutableLiveData(Utils.getBrandName())
    }

    val companyLogo: MutableLiveData<String> by lazy {
        MutableLiveData(Utils.getBrandLogo())
    }

    private val _conversations = MutableLiveData<List<HeaderChatScreenModel>>()
    val conversations: LiveData<List<HeaderChatScreenModel>>
        get() = _conversations

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
            if(LiveChatSharedPrefManager.isNeedNewToken() || it.isEmpty()){
                _isNeedNewToken.value = true
            } else {
                _conversations.value = it
            }
        }, onError = {

        })
    }

    private fun getAgent() {
        _isNeedNewToken.value = LiveChatSharedPrefManager.isNeedNewToken()
        if (isNeedNewToken.value == true)
            return

        if (LiveChatFirebaseHelper.userId.isNullOrEmpty()) {
            SharedPreferencesManager.token?.let { token ->
                SignInWithTokenUseCase.execute(token).addOnCompleteListener {
                    LiveChatFirebaseHelper.auth?.currentUser?.uid?.let {
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
                _isNeedNewToken.value = true
                DeleteConversationUseCase().execute(onSuccess = {

                }, onError = {
                    handleError(it)
                    isEndedSession.value = false
                })
            } else {
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