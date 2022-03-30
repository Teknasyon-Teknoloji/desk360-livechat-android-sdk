package com.desk360.livechat.presentation.activity.livechat

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.desk360.livechat.data.HeaderCompanyScreenModel
import com.desk360.livechat.data.model.feedback.FeedbackRequest
import com.desk360.livechat.domain.usecase.FeedbackUseCase
import com.desk360.livechat.manager.LiveChatHelper
import com.desk360.livechat.manager.CannedResponseHelper
import com.desk360.livechat.data.model.cannedresponse.CannedActionType
import com.desk360.livechat.data.model.cannedresponse.CannedScreenType
import com.desk360.livechat.data.model.cannedresponse.request.CRRequest
import com.desk360.livechat.data.model.cannedresponse.response.CannedResponse
import com.desk360.livechat.data.model.cannedresponse.response.GroupedUnits
import com.desk360.livechat.domain.usecase.CannedResponseUseCase
import com.desk360.livechat.presentation.viewmodel.BaseViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class CannedResponseViewModel : BaseViewModel() {

    companion object {
        const val SURVEY = "Close_and_survey"
        const val REPLY_BUTTONS = "Reply_buttons"
        const val GROUP = "Group"
        const val USER = "User"

        const val ACTIONABLE_TYPE_PATH = "Path"

        const val ACTION_SURVEY = "Close_and_survey"
        const val ACTION_LIVE_HELP = "Live_help"
        const val ACTION_START_PATH = "Return_start_path"
    }

    val headerCompanyScreenModel: MutableLiveData<HeaderCompanyScreenModel> by lazy {
        MutableLiveData(HeaderCompanyScreenModel())
    }

    val isMine: MutableLiveData<Boolean> by lazy {
        MutableLiveData(false)
    }

    val messageTextColor: MutableLiveData<String> by lazy {
        MutableLiveData(LiveChatHelper.settings?.data?.config?.chat?.messageTextColor)
    }

    val backgroundColor: MutableLiveData<String> by lazy {
        MutableLiveData(LiveChatHelper.settings?.data?.config?.general?.backgroundHeaderColor)
    }

    val time: MutableLiveData<String> by lazy {
        MutableLiveData("")
    }

    val isSelected: MutableLiveData<Boolean> by lazy {
        MutableLiveData(false)
    }

    val screenType: MutableLiveData<CannedScreenType> by lazy {
        MutableLiveData(null)
    }

    private val _newCannedResponse: MutableLiveData<MutableList<CannedActionType>> =
        MutableLiveData()
    val newCannedResponse get() = _newCannedResponse

    private val _startCannedResponse: MutableLiveData<MutableList<CannedActionType>> =
        MutableLiveData()
    val startCannedResponse get() = _startCannedResponse

    private val cannedResponseModel = CannedResponseHelper.cannedResponseModel

    private val bindEntity: MutableList<CannedActionType> = mutableListOf()

    fun clearBindEntity() = bindEntity.clear()

    fun startCannedResponse() {
        CannedResponseHelper.modelInitiate()
        cannedResponseModel?.forEach { data ->
            if (data.isStart == 1) {
                getHandleActionType(data, true)
                return@forEach
            }
        }
    }

    private fun getHandleActionType(cannedResponse: CannedResponse, isStart: Boolean = false) {
        cannedResponse.actionableType?.let { type ->
            when (type) {
                SURVEY -> cannedResponse.groupedUnits?.let { handleSurvey(it) }
                REPLY_BUTTONS -> cannedResponse.groupedUnits?.let {
                    handleReplyButtons(
                        it,
                        isStart
                    )
                }
                USER -> {
                    handleStartSession(true)
                }
                GROUP -> {
                    handleStartSession(true)
                }
                else -> return
            }
        }
    }

    private fun handleStartSession(isPath:Boolean = false) {
        viewModelScope.launch {
            var pathID:Int? = null
            if (isPath) { pathID = CannedResponseHelper.getPathId()}
            if (!LiveChatHelper.isOffline && isAutoLoginControl()) autoLogin(pathID)
            else {
                delay(500)
                if (isPath) screenType.value = CannedScreenType.LoginScreen(pathID)
                else screenType.value = CannedScreenType.LoginScreen(null)
            }
        }
    }

    private fun handleReplyButtons(groupedUnits: GroupedUnits, isStart: Boolean) {
        groupedUnits.message?.let { msg ->
            msg.forEach { i ->
                bindEntity.add(CannedActionType.CRMessage(i.id, i.content))
            }
        }
        groupedUnits.button?.let { btn ->
            btn.forEach { i ->
                bindEntity.add(
                    CannedActionType.CRButton(
                        i.id,
                        i.actionableId,
                        i.content,
                        i.orderId,
                        i.actionableType
                    )
                )
            }
        }
        groupedUnits.closeMenuButton?.let { cBtn ->
            cBtn.forEach { i ->
                bindEntity.add(
                    CannedActionType.CRMenuButton(
                        i.id,
                        i.actionableType,
                        i.icon,
                        i.orderId,
                        i.content
                    )
                )
            }
            cannedResponseUseCase(false)
        }
        if (isStart) _startCannedResponse.value = bindEntity
        else _newCannedResponse.value = bindEntity
    }

    fun cannedResponseUseCase(isBackPress:Boolean = true) {
        if (CannedResponseHelper.payloadLogData.isNullOrEmpty()) return
        CannedResponseUseCase(
            CRRequest(
                CannedResponseHelper.payloadLogData,
                CannedResponseHelper.getPathId(),
                "Android"
            )
        ).execute(onSuccess = {

        }, onError = {

        })
    }

    private fun handleSurvey(groupedUnits: GroupedUnits) {
        groupedUnits.message?.let { msg ->
            msg.forEach { i ->
                bindEntity.add(CannedActionType.CRSurvey(i.id, i.content, ACTION_SURVEY))
            }
        }
        _newCannedResponse.value = bindEntity
    }

    fun actionCannedResponse(id: Int) {
        cannedResponseModel?.forEach {
            if (it.id == id) {
                getHandleActionType(it)
                return@forEach
            }
        }
    }

    fun actionCannedResponseMenu(type: String) {
        when (type) {
            ACTION_SURVEY -> {
                cannedResponseModel?.find { model ->
                    model.actionableType == type
                }?.groupedUnits?.let { handleSurvey(it) }
            }
            ACTION_LIVE_HELP -> handleStartSession()
            ACTION_START_PATH -> startCannedResponse()

        }
    }

    fun surveyFeedBackUseCase(feedBack: Int) {
        viewModelScope.launch {
            FeedbackUseCase(
                FeedbackRequest(
                    feedBack,
                    payload = CannedResponseHelper.payloadLogData,
                    pathId = null,
                    source = "Android"
                )
            ).execute(onSuccess = {
                screenType.postValue(CannedScreenType.OnSurveyInfoScreen)
            }, onError = {
                handleError(it)
            })
        }
    }

    fun writeActionMessage(actionId: Int?, id: Int?, type: String) {
        if (type == ACTIONABLE_TYPE_PATH && actionId != null && id != null) {
            CannedResponseHelper.addPayloadLog(id)
            val content = CannedResponseHelper.getButton(id)?.content
            val message: CannedActionType.CRMessage? =
                content?.let { it1 -> CannedActionType.CRMessage(null, it1, true) }
            message?.let { bindEntity.add(it) }
            _newCannedResponse.value = bindEntity
        }
        if (type == ACTION_SURVEY && actionId == 0) {
            val message: CannedActionType.CRMessage? = LiveChatHelper.settings?.data?.language?.crFeedBackSuccessTitle?.let { c ->
                CannedActionType.CRMessage(null, c, true)
            }
            message?.let { bindEntity.add(it) }
            _newCannedResponse.value = bindEntity
        }
        if (actionId == null) {
            val content = CannedResponseHelper.getMenuButton(type)?.content
            val message: CannedActionType.CRMessage? = content?.let { c ->
                CannedActionType.CRMessage(null, c, true)
            }
            message?.let { bindEntity.add(it) }
            _newCannedResponse.value = bindEntity
        }
    }
}