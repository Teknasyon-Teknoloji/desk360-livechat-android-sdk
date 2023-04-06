package com.desk360.livechat.presentation.activity.livechat

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.desk360.livechat.data.HeaderCompanyScreenModel
import com.desk360.livechat.data.model.cannedresponse.CannedActionType
import com.desk360.livechat.data.model.cannedresponse.CannedScreenType
import com.desk360.livechat.data.model.cannedresponse.request.CRRequest
import com.desk360.livechat.data.model.cannedresponse.response.CannedResponse
import com.desk360.livechat.data.model.cannedresponse.response.GroupedUnits
import com.desk360.livechat.data.model.feedback.FeedbackRequest
import com.desk360.livechat.domain.usecase.CannedResponseUseCase
import com.desk360.livechat.domain.usecase.FeedbackUseCase
import com.desk360.livechat.manager.CannedResponseHelper
import com.desk360.livechat.manager.LiveChatHelper
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

    private val _newCannedResponse = MutableLiveData<List<CannedActionType>>()
    val newCannedResponse: LiveData<List<CannedActionType>>
        get() = _newCannedResponse

    private val _startCannedResponse = MutableLiveData<List<CannedActionType>>()
    val startCannedResponse: LiveData<List<CannedActionType>>
        get() = _startCannedResponse

    private val cannedResponseModel: List<CannedResponse>
        get() = CannedResponseHelper.cannedResponseModel

    private val bindEntity: MutableList<CannedActionType> = mutableListOf()

    fun clearBindEntity() = bindEntity.clear()

    fun startCannedResponse() {
        CannedResponseHelper.clearPayloadLog()
        CannedResponseHelper.modelInitiate()
        cannedResponseModel
            .find { it.isStart == 1 }
            ?.let { getHandleActionType(it, true) }
    }

    private fun getHandleActionType(cannedResponse: CannedResponse, isStart: Boolean = false) {
        when (cannedResponse.actionableType) {
            SURVEY -> handleSurvey()
            REPLY_BUTTONS -> cannedResponse.groupedUnits?.let { handleReplyButtons(it, isStart) }
            USER -> handleStartSession(true)
            GROUP -> handleStartSession(true)
        }
    }

    private fun handleStartSession(isPath: Boolean = false) {
        viewModelScope.launch {
            var pathID: Int? = null
            if (isPath) {
                pathID = CannedResponseHelper.getPathId()
            }
            if (!LiveChatHelper.isOffline && isAutoLoginControl()) {
                autoLogin(pathID)
            } else {
                delay(500)
                screenType.value = if (isPath)
                    CannedScreenType.LoginScreen(pathID)
                else
                    CannedScreenType.LoginScreen(null)
            }
        }
    }

    private fun handleReplyButtons(groupedUnits: GroupedUnits, isStart: Boolean) {
        groupedUnits.message?.forEach { msg ->
            bindEntity.add(CannedActionType.CRMessage(msg.id, msg.content))
        }

        groupedUnits.button?.forEach { btn ->
            bindEntity.add(
                CannedActionType.CRButton(
                    btn.id,
                    btn.actionableId,
                    btn.content,
                    btn.orderId,
                    btn.actionableType
                )
            )
        }

        groupedUnits.closeMenuButton?.let { cmb ->
            cmb.forEach { btn ->
                bindEntity.add(
                    CannedActionType.CRMenuButton(
                        btn.id,
                        btn.actionableType,
                        btn.icon,
                        btn.orderId,
                        btn.content
                    )
                )
            }
            cannedResponseUseCase()
        }

        if (isStart)
            _startCannedResponse.value = bindEntity
        else
            _newCannedResponse.value = bindEntity
    }

    fun cannedResponseUseCase() {
        if (CannedResponseHelper.payloadLogData.isEmpty())
            return

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

    private fun handleSurvey() {
        bindEntity.add(CannedActionType.CRSurvey)
        _newCannedResponse.value = bindEntity
    }

    fun actionCannedResponse(id: Int) {
        val model = cannedResponseModel.find { it.id == id } ?: return
        getHandleActionType(model)
    }

    fun actionCannedResponseMenu(type: String) {
        when (type) {
            ACTION_SURVEY -> handleSurvey()
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
        if (id != null)
            CannedResponseHelper.addPayloadLog(id)
        if (type == ACTIONABLE_TYPE_PATH && actionId != null && id != null) {
            CannedResponseHelper.getButton(id)?.content?.let { content ->
                val message = CannedActionType.CRMessage(null, content, true)
                bindEntity.add(message)
            }
            _newCannedResponse.value = bindEntity
        }
        if (type == ACTION_SURVEY && actionId == 0) {
            LiveChatHelper.settings?.data?.language?.crFeedBackSuccessTitle?.let { title ->
                val message = CannedActionType.CRMessage(null, title, true)
                bindEntity.add(message)
            }
            _newCannedResponse.value = bindEntity
        }
        if (actionId == null) {
            CannedResponseHelper.getMenuButton(type)?.content?.let { content ->
                val message = CannedActionType.CRMessage(null, content, true)
                bindEntity.add(message)
            }
            _newCannedResponse.value = bindEntity
        }
    }
}