package com.desk360.livechat.presentation.activity.livechat.cannedresponse.presentation.adapter.viewholder

import android.view.View
import com.desk360.livechat.presentation.activity.livechat.cannedresponse.data.model.CannedActionType


abstract class BaseCannedResponseViewHolder(itemView: View, val onClick: (Int?, Int,String) -> Unit) : BaseViewHolderCr<CannedActionType>(itemView) {

    open fun bindTo(response: CannedActionType, position: Int) {
        bindTo(response)
    }
}