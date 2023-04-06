package com.desk360.livechat.presentation.viewholder

import android.view.View
import com.desk360.livechat.data.model.cannedresponse.CannedActionType


abstract class BaseCannedResponseViewHolder(
    itemView: View,
    val onClick: OnCannedViewHolderClickListener
) : BaseViewHolderCr<CannedActionType>(itemView) {

    open fun bindTo(response: CannedActionType, position: Int) {
        bindTo(response)
    }

    fun interface OnCannedViewHolderClickListener {
        operator fun invoke(action: Int?, id: Int, actionableType: String)
    }
}