package com.desk360.livechat.presentation.viewholder

import android.view.LayoutInflater
import android.view.ViewGroup
import com.desk360.livechat.databinding.ItemCrButtonBinding
import com.desk360.livechat.data.model.cannedresponse.CannedActionType
import com.desk360.livechat.manager.LiveChatHelper
import com.desk360.livechat.presentation.activity.livechat.CannedResponseViewModel


class CRButtonViewHolder(
    private val binding: ItemCrButtonBinding,
    onClick: (Int?, Int, String) -> Unit
) : BaseCannedResponseViewHolder(binding.root, onClick) {

    companion object {
        fun create(parent: ViewGroup, onClick: (Int?, Int, String) -> Unit) = CRButtonViewHolder(
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
        viewModel.backgroundColor.value =
            LiveChatHelper.settings?.data?.config?.chat?.messageBackgroundColor
        binding.textViewLoginBg.text = hold.content
        binding.textViewLoginBg.setOnClickListener {
            if (hold.isClickable) {
                onClick(hold.actionId, hold.id, hold.actionableType)
            }
        }

        if (response.isSelected) {
            viewModel.isSelected.value = true
        }
        super.bindTo(response, position)
    }


}