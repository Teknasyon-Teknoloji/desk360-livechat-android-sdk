package com.desk360.livechat.presentation.activity.livechat

import androidx.lifecycle.MutableLiveData
import com.desk360.livechat.data.HeaderCompanyScreenModel
import com.desk360.livechat.presentation.viewmodel.BaseViewModel

class SentMessageInfoViewModel : BaseViewModel() {
    val headerCompanyScreenModel: MutableLiveData<HeaderCompanyScreenModel> by lazy {
        MutableLiveData(HeaderCompanyScreenModel())
    }
}