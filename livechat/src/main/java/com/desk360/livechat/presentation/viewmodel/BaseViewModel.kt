package com.desk360.livechat.presentation.viewmodel

import androidx.lifecycle.*
import com.desk360.livechat.data.model.chatsettings.General
import com.google.firebase.FirebaseNetworkException
import com.desk360.livechat.data.model.chatsettings.Language
import com.desk360.livechat.manager.LiveChatHelper
import java.net.ConnectException
import java.net.UnknownHostException

abstract class BaseViewModel : ViewModel(), LifecycleObserver {
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

    val isRunningProgress: MutableLiveData<Boolean> by lazy {
        MutableLiveData(false)
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

    open fun pause() {

    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun onPause() {
        pause()
    }

    fun registerLifecycle(lifecycle: Lifecycle) {
        lifecycle.addObserver(this)
    }
}