package com.desk360.livechat.presentation.activity.livechat.cannedresponse.presentation.viewmodel

import androidx.lifecycle.MutableLiveData
import com.desk360.livechat.data.HeaderCompanyScreenModel

class CannedResponseViewModel : BaseCannedBindViewModel() {//BaseViewModel() {


    val headerCompanyScreenModel: MutableLiveData<HeaderCompanyScreenModel> by lazy {
        MutableLiveData(HeaderCompanyScreenModel())
    }
}