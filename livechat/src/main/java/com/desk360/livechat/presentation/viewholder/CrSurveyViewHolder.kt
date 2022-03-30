package com.desk360.livechat.presentation.viewholder

import android.view.LayoutInflater
import android.view.ViewGroup
import com.desk360.livechat.databinding.ItemCrSurveyBinding
import com.desk360.livechat.data.model.cannedresponse.CannedActionType
import com.desk360.livechat.manager.LiveChatHelper
import com.desk360.livechat.presentation.activity.livechat.CannedResponseViewModel.Companion.ACTION_SURVEY
import com.desk360.livechat.presentation.activity.livechat.CannedResponseViewModel

class CrSurveyViewHolder(
    private val binding: ItemCrSurveyBinding,
    onClick: (Int?, Int, String) -> Unit
) :
    BaseCannedResponseViewHolder(binding.root, onClick) {

    companion object {
        fun create(parent: ViewGroup, onClick: (Int?, Int, String) -> Unit) = CrSurveyViewHolder(
            ItemCrSurveyBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ), onClick
        )
    }

    override fun bindTo(response: CannedActionType, position: Int) {
        super.bindTo(response, position)
        val viewModel = CannedResponseViewModel()
        val hold = response as CannedActionType.CRSurvey
        binding.viewModel = viewModel
        viewModel.backgroundColor.value =
            LiveChatHelper.settings?.data?.config?.chat?.messageBackgroundColor
        binding.clButtonPanelOne.setOnClickListener {
            binding.clButtonPanelOne.isClickable = false
            binding.clButtonPanelTwo.isClickable = false
            onClick(null,1,ACTION_SURVEY)
        }
        binding.clButtonPanelTwo.setOnClickListener {
            binding.clButtonPanelOne.isClickable = false
            binding.clButtonPanelTwo.isClickable = false
            onClick(null,2,ACTION_SURVEY)
        }
    }


}