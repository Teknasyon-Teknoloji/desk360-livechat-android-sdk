package com.desk360.livechat.presentation.viewholder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.desk360.livechat.data.model.cannedresponse.CannedActionType


abstract class BaseViewHolderCr<T : CannedActionType>(itemView: View) :
    RecyclerView.ViewHolder(itemView) {

    open fun bindTo(message: T) {

    }
}