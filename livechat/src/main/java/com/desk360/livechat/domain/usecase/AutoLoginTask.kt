package com.desk360.livechat.domain.usecase

import com.desk360.base.domain.usecase.AutoLoginTasksImpl
import com.desk360.livechat.data.HeaderChatScreenModel
import com.desk360.livechat.data.model.session.SessionRequest
import com.desk360.livechat.manager.LiveChatFirebaseHelper
import com.desk360.livechat.manager.LiveChatSharedPrefManager

abstract class AutoLoginTask : AutoLoginTasksImpl<SessionRequest>() {

    private var getSessionUseCase: GetSessionUseCase? = null

    override fun execute(param: SessionRequest){
        onStartTask()
        onlineSessionUseCase(param)
    }

    private fun onlineSessionUseCase(request: SessionRequest) {
        OnlineSessionUseCase(request).execute(onSuccess = { token ->
            singInWithToken(token)
        }, onError = {
            onError(it)
        })
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
                    onSession(true)
                }, onError = {
                    onError(it)
                    onSession(false)
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
            onComplete(id)
            getSessionUseCase?.removeListener()
        }, onError = {
            onError(it)
        })
    }
}