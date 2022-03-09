package com.desk360.livechat.presentation.activity.livechat.cannedresponse.presentation.adapter.viewholder

import android.os.Build
import android.text.Html
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.text.HtmlCompat
import com.desk360.livechat.databinding.ItemCrMessageBinding
import com.desk360.livechat.manager.LiveChatHelper
import com.desk360.livechat.presentation.activity.livechat.cannedresponse.data.model.CannedActionType
import com.desk360.livechat.presentation.activity.livechat.cannedresponse.presentation.viewmodel.CannedResponseViewModel

class CRMessageViewHolder(
    private val binding: ItemCrMessageBinding,
    onClick: (Int?, Int, String) -> Unit
) : BaseCannedResponseViewHolder(binding.root, onClick) {

    companion object {
        fun create(parent: ViewGroup, onClick: (Int?, Int, String) -> Unit) = CRMessageViewHolder(
            ItemCrMessageBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ), onClick = onClick
        )
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun bindTo(response: CannedActionType, position: Int) {
        super.bindTo(response, position)
        val viewModel = CannedResponseViewModel()
        binding.viewModel = viewModel
        val hold = response as CannedActionType.CRMessage
        if (hold.isMine) {
            viewModel.isMine.value = true
            viewModel.backgroundColor.value =
                LiveChatHelper.settings?.data?.config?.chat?.messageBackgroundColor
            viewModel.messageTextColor.value =
                LiveChatHelper.settings?.data?.config?.chat?.messageTextColor

        } else {
            viewModel.backgroundColor.value =
                LiveChatHelper.settings?.data?.config?.general?.backgroundHeaderColor
            viewModel.messageTextColor.value =
                LiveChatHelper.settings?.data?.config?.general?.headerTitleColor
        }
        binding.textViewContent.text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml(hold.content, HtmlCompat.FROM_HTML_MODE_LEGACY)
        } else {
            Html.fromHtml(hold.content)
        }
        binding.textViewTime.text = hold.time
    }

}