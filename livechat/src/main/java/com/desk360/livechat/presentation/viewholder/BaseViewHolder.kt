package com.desk360.livechat.presentation.viewholder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.desk360.livechat.data.model.BaseModel

abstract class BaseViewHolder<T : BaseModel>(itemView: View) :
    RecyclerView.ViewHolder(itemView) {

    open fun bindTo(message: T) {

    }
}

