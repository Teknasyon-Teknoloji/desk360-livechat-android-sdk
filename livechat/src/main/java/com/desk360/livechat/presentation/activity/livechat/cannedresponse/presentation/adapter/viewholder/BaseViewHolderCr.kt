package com.desk360.livechat.presentation.activity.livechat.cannedresponse.presentation.adapter.viewholder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.desk360.livechat.presentation.activity.livechat.cannedresponse.data.model.CannedActionType


abstract class BaseViewHolderCr<T : CannedActionType>(itemView: View) :
    RecyclerView.ViewHolder(itemView) {

    open fun bindTo(message: T) {

    }
}