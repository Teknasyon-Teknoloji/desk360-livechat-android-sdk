package com.desk360.livechat.presentation.activity.livechat

import androidx.lifecycle.MutableLiveData
import com.desk360.base.manager.SharedPreferencesManager
import com.desk360.livechat.data.HeaderCompanyScreenModel
import com.desk360.livechat.data.model.chatsettings.Feedback
import com.desk360.livechat.data.model.feedback.FeedbackRequest
import com.desk360.livechat.domain.usecase.FeedbackUseCase
import com.desk360.livechat.manager.LiveChatHelper
import com.desk360.livechat.presentation.viewmodel.BaseViewModel

class FeedbackChatViewModel : BaseViewModel() {
    val screenModel: MutableLiveData<Feedback> by lazy {
        MutableLiveData(LiveChatHelper.settings?.data?.config?.feedback)
    }

    val headerCompanyScreenModel: MutableLiveData<HeaderCompanyScreenModel> by lazy {
        MutableLiveData(HeaderCompanyScreenModel())
    }

    private val _isSuccessful: MutableLiveData<Boolean> = MutableLiveData(false)
    val isSuccessful get() = _isSuccessful

    fun feedback(type: Int) {
        FeedbackUseCase(FeedbackRequest(feedback = type)).execute(onSuccess = { isSuccess ->
            SharedPreferencesManager.userId = ""
            isSuccessful.value = isSuccess
        }, onError = {
            handleError(it)
        })
    }

    object Type {
        const val LIKE = 1
        const val UNLIKE = 2
    }
}