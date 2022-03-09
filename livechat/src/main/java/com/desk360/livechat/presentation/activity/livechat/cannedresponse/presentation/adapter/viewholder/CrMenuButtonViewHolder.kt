package com.desk360.livechat.presentation.activity.livechat.cannedresponse.presentation.adapter.viewholder

import android.view.LayoutInflater
import android.view.ViewGroup
import com.desk360.base.presentation.endDrawable
import com.desk360.livechat.R
import com.desk360.livechat.databinding.ItemCrMenuButtonBinding
import com.desk360.livechat.presentation.activity.livechat.cannedresponse.data.model.CannedActionType
import com.desk360.livechat.presentation.activity.livechat.cannedresponse.presentation.viewmodel.CannedResponseViewModel


class CrMenuButtonViewHolder(
    private val binding: ItemCrMenuButtonBinding,
    onClick: (Int?, Int, String) -> Unit
) :
    BaseCannedResponseViewHolder(binding.root, onClick) {

    companion object {
        const val HOME = "canned-home"
        const val SURVEY = "canned-survey"
        const val CONNECT = "canned-connect"

        fun create(parent: ViewGroup, onClick: (Int?, Int, String) -> Unit) =
            CrMenuButtonViewHolder(
                ItemCrMenuButtonBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                ), onClick
            )
    }

    override fun bindTo(response: CannedActionType, position: Int) {
        super.bindTo(response, position)
        val viewModel = CannedResponseViewModel()
        binding.viewModel = viewModel
        val hold = response as CannedActionType.CRMenuButton
        binding.crMenuButton.text = hold.content
        when (hold.icon) {
            HOME -> {
                binding.crMenuButton.endDrawable(R.drawable.ic_menu_icon_home)
            }
            SURVEY -> {
                binding.crMenuButton.endDrawable(R.drawable.ic_menu_icon_survey)
            }
            CONNECT -> {
                binding.crMenuButton.endDrawable(R.drawable.ic_menu_icon_live_help)
            }
        }
        binding.crMenuButton.setOnClickListener {
            if (hold.isClickable) {
                onClick(null, hold.id, hold.actionableType)
            }
        }

        if (response.isSelected) {
            viewModel.isSelected.value = true
        }
    }
}