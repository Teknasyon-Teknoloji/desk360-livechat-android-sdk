package com.desk360.livechat.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.*
import com.desk360.base.domain.usecase.AutoLoginTasksImpl
import com.desk360.livechat.data.model.chatsettings.General
import com.desk360.livechat.data.model.chatsettings.Language
import com.desk360.livechat.data.model.session.SessionRequest
import com.desk360.livechat.domain.usecase.AutoLoginTask
import com.desk360.livechat.domain.usecase.IsOfflineUseCase
import com.desk360.livechat.manager.Desk360LiveChat
import com.desk360.livechat.manager.LiveChatHelper
import com.desk360.livechat.manager.CannedResponseHelper
import com.google.firebase.FirebaseNetworkException
import java.net.ConnectException
import java.net.UnknownHostException

abstract class BaseViewModel : ViewModel(), LifecycleObserver {

    private val isOfflineUseCase = IsOfflineUseCase()

    val language: MutableLiveData<Language> by lazy {
        MutableLiveData(LiveChatHelper.settings?.data?.language)
    }

    val general: MutableLiveData<General> by lazy {
        MutableLiveData(LiveChatHelper.settings?.data?.config?.general)
    }

    private val _errorMessage: MutableLiveData<String> = MutableLiveData("")
    val errorMessage: LiveData<String> get() = _errorMessage

    private val _hasConnection: MutableLiveData<Boolean> = MutableLiveData(true)
    val hasConnection: LiveData<Boolean> get() = _hasConnection

    val error: MutableLiveData<Throwable> by lazy {
        MutableLiveData()
    }

    val isEndedSession: MutableLiveData<Boolean> by lazy {
        MutableLiveData(false)
    }

    val conversationId: MutableLiveData<String> by lazy {
        MutableLiveData()
    }

    val conversationDeskId: MutableLiveData<String> by lazy {
        MutableLiveData()
    }

    val isRunningProgress: MutableLiveData<Boolean> by lazy {
        MutableLiveData(false)
    }

    val isProgressDialogState: MutableLiveData<AutoLoginTasksImpl.LoginProgressDialogState> by lazy {
        MutableLiveData(AutoLoginTasksImpl.LoginProgressDialogState.DEFAULT)
    }

    fun checkOnlineStatus() {
        isOfflineUseCase.execute(onSuccess = { result ->
            if (result != null) {
                LiveChatHelper.isOffline = result
            }
        }, onError = {

        })
    }

    fun handleError(th: Throwable?) {
        isRunningProgress.value = false

        error.value = th
        _errorMessage.value = when (th) {
            is ConnectException -> LiveChatHelper.settings?.data?.language?.sdkCheckYourConnection
            is UnknownHostException -> LiveChatHelper.settings?.data?.language?.sdkCheckYourConnection
            is FirebaseNetworkException -> LiveChatHelper.settings?.data?.language?.sdkCheckYourConnection
            else -> LiveChatHelper.settings?.data?.language?.sdkEerrorHasOccurred
        }
    }

    fun setConnection(hasConnection: Boolean) {
        _hasConnection.value = hasConnection
    }

    open fun pause() {}

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun onPause() {
        pause()
    }

    fun registerLifecycle(lifecycle: Lifecycle) {
        lifecycle.addObserver(this)
    }


    fun autoLogin(pathId: Int? = null, request: SessionRequest? = null) {
        var session = SessionRequest()
        request?.let { session = request } ?: run {
            session = SessionRequest(
                Desk360LiveChat.manager?.userName,
                Desk360LiveChat.manager?.userEmailAddress
            )
        }
        pathId?.let {
            session.pathId = it
        }

        autoLoginTaskListener.execute(session)
    }

    fun isAutoLoginControl(): Boolean =
        (!Desk360LiveChat.manager?.userName.isNullOrEmpty() &&
                !Desk360LiveChat.manager?.userEmailAddress.isNullOrEmpty())

    private val autoLoginTaskListener = object : AutoLoginTask() {
        override fun onStartTask() {
            isProgressDialogState.value = LoginProgressDialogState.START
        }

        override fun onError(t: Throwable) {
            Log.d("onError", "message")
            isProgressDialogState.value = LoginProgressDialogState.STOP
            handleError(t)
        }

        override fun onComplete(id: String) {
            isProgressDialogState.value = LoginProgressDialogState.STOP
            conversationDeskId.value = id
            CannedResponseHelper.clearPayload()
            Log.d("onComplete", "message")
        }

        override fun onSession(state: Boolean) {
            isProgressDialogState.value = LoginProgressDialogState.STOP
            isEndedSession.value = state
            Log.d("onSession", "message")
        }

    }
}