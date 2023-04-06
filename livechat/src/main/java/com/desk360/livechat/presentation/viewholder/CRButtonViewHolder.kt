package com.desk360.livechat.presentation.viewholder

import android.view.LayoutInflater
import android.view.ViewGroup
import com.desk360.livechat.data.model.cannedresponse.CannedActionType
import com.desk360.livechat.databinding.ItemCrButtonBinding
import com.desk360.livechat.manager.LiveChatHelper
import com.desk360.livechat.presentation.activity.livechat.CannedResponseViewModel


class CRButtonViewHolder(
    private val binding: ItemCrButtonBinding,
    onClick: OnCannedViewHolderClickListener
) : BaseCannedResponseViewHolder(binding.root, onClick) {

    companion object {
        fun create(parent: ViewGroup, onClick: OnCannedViewHolderClickListener) = CRButtonViewHolder(
            ItemCrButtonBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ), onClick
        )
    }


    override fun bindTo(response: CannedActionType, position: Int) {
        val hold = response as CannedActionType.CRButton
        val viewModel = CannedResponseViewModel()
        binding.viewModel = viewModel

        val bgColor = LiveChatHelper.settings?.data?.config?.chat?.messageBackgroundColor
        if (bgColor != null)
            viewModel.backgroundColor.value = bgColor

        binding.textViewLoginBg.text = hold.content
        binding.textViewLoginBg.setOnClickListener {
            if (hold.isClickable && hold.actionableType != null) {
                onClick(hold.actionId, hold.id, hold.actionableType)
            }
        }

        if (response.isSelected) {
            viewModel.isSelected.value = true
        }
        super.bindTo(response, position)
    }


}